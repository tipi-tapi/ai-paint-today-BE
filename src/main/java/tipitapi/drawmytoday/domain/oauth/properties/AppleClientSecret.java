package tipitapi.drawmytoday.domain.oauth.properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.io.StringReader;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Component;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

@Component
@RequiredArgsConstructor
public class AppleClientSecret {

    private final ObjectMapper objectMapper;
    private String clientSecret;

    public String getClientSecret(String teamId, String keyId, String privateKey,
        String clientId) {
        if (isClientSecretNotValidOrExpired()) {
            clientSecret = generateClientSecret(teamId, keyId, privateKey, clientId, 29);
        }
        return clientSecret;
    }

    private String generateClientSecret(String teamId, String keyId, String privateKey,
        String clientId, int expireDays) {
        Date expirationDate = Date.from(
            LocalDateTime.now().plusDays(expireDays).atZone(ZoneId.systemDefault()).toInstant());
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", keyId);
        jwtHeader.put("alg", "ES256");

        return Jwts.builder()
            .setHeaderParams(jwtHeader)
            .setIssuer(teamId)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(expirationDate)
            .setAudience("https://appleid.apple.com")
            .setSubject(clientId)
            .signWith(getPrivateKey(privateKey), SignatureAlgorithm.ES256)
            .compact();
    }

    private PrivateKey getPrivateKey(String privateKey) {
        try (StringReader stringReader = new StringReader(privateKey);
            PEMParser pemParser = new PEMParser(stringReader)) {
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());
            return converter.getPrivateKey(privateKeyInfo);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.GENERATE_KEY_FAILED, e);
        }
    }

    private boolean isClientSecretNotValidOrExpired() {
        if (clientSecret == null) {
            return true;
        }
        String[] jsonToken = clientSecret.split("\\.");
        if (jsonToken.length != 3) {
            return true;
        }
        byte[] payloadBytes = Base64.getUrlDecoder().decode(jsonToken[1]);
        try {
            JsonNode payloadNode = objectMapper.readTree(payloadBytes);
            long expirationTime = payloadNode.get("exp").asLong() * 1000;
            long currentTime = System.currentTimeMillis();
            return expirationTime < currentTime;
        } catch (IOException e) {
            return true;
        }
    }

}

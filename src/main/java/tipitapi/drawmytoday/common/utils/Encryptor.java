package tipitapi.drawmytoday.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

@Component
public class Encryptor {

    private final String ALGORITHM = "AES";
    private final SecretKeySpec SECRET_KEY;

    public Encryptor(@Value("${encryptor.secret.key}") String stringKey) {
        this.SECRET_KEY = new SecretKeySpec(
            stringKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    public String encrypt(String plainText) {
        if (!StringUtils.hasText(plainText)) {
            return null;
        }
        byte[] encryptedBytes = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
            encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new BusinessException(ErrorCode.ENCRYPTION_ERROR, e);
        }
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedText) {
        if (!StringUtils.hasText(encryptedText)) {
            return null;
        }
        byte[] decryptedBytes = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            decryptedBytes = cipher.doFinal(decodedBytes);
        } catch (GeneralSecurityException e) {
            throw new BusinessException(ErrorCode.DECRYPTION_ERROR, e);
        }
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

}

package tipitapi.drawmytoday.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class EncryptorUtils {

    private static final String ALGORITHM = "AES";
    private static final SecretKey secretKey = generateSecretKey();

    private static SecretKey generateSecretKey() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    public static String encrypt(String plainText) {
        byte[] encryptedBytes = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }


    public static String decrypt(String encryptedText) {
        byte[] decryptedBytes = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            decryptedBytes = cipher.doFinal(decodedBytes);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

}

package tipitapi.drawmytoday.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import tipitapi.drawmytoday.common.exception.BusinessException;
import tipitapi.drawmytoday.common.exception.ErrorCode;

public class EncryptorUtils {

    private static final String ALGORITHM = "AES";
    private static final SecretKeySpec SECRET_KEY;

    static {
        String keyString = "ThisIsEncryptorSecretKey";
        SECRET_KEY = new SecretKeySpec(
            keyString.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    public static String encrypt(String plainText) {
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

    public static String decrypt(String encryptedText) {
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

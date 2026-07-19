package bg.sofia.uni.fmi.mjt.algorithm;

import bg.sofia.uni.fmi.mjt.logging.Logger;
import org.junit.jupiter.api.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RijndaelTest {

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int KEY_SIZE_IN_BITS = 128;

    private static SecretKey generateSecretKey() {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            Logger.logException("Error occurred while generating secret key", e);
            return null;
        }
        keyGenerator.init(KEY_SIZE_IN_BITS);
        return keyGenerator.generateKey();
    }

    @Test
    public void testEncryptDecryptString() {
        Rijndael rijndael = new Rijndael(generateSecretKey());
        String message = "New message to encrypt!";
        String encryptedMessage = rijndael.encryptString(message);
        String decryptedMessage = rijndael.decryptString(encryptedMessage);

        assertEquals(message, decryptedMessage,
            "The decrypted message must be the same as the original message");
    }

}
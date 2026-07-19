package bg.sofia.uni.fmi.mjt.algorithm;

import bg.sofia.uni.fmi.mjt.exception.CipherException;
import bg.sofia.uni.fmi.mjt.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Rijndael implements SymmetricBlockCipher {

    private static final String ENCRYPTION_ALGORITHM = "AES";

    // just a placeholder, the actual key is not hardcoded
    private static final String SECRET_KEY_STRING = "SECRET_KEY_WITH_LENGTH_OF_32BYTE";
    private static final byte[] SECRET_KEY_BYTES = SECRET_KEY_STRING.getBytes();
    private static final SecretKey SECRET_KEY = new SecretKeySpec(SECRET_KEY_BYTES, ENCRYPTION_ALGORITHM);
    private static final int BUFFER_SIZE = 1024;

    private final SecretKey secretKey;

    public Rijndael() {
        this.secretKey = SECRET_KEY;
    }

    public Rijndael(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String encryptString(String string) {

        if (string == null) {
            throw new IllegalArgumentException("String to encrypt cannot be null in encryptString method");
        }

        try (ByteArrayOutputStream encrypted = new ByteArrayOutputStream()) {
            encrypt(new ByteArrayInputStream(string.getBytes()), encrypted);
            byte[] encryptedBytes = encrypted.toByteArray();
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (CipherException e) {
            Logger.logException("Error while encrypting string", e);
            throw new IllegalStateException("Error while encrypting string", e);
        } catch (IOException e) {
            Logger.logException("Error while handling input/output during encryption", e);
            throw new IllegalStateException("Error while handling input/output during encryption", e);
        }
    }

    @Override
    public String decryptString(String string) {

        if (string == null) {
            throw new IllegalArgumentException("String to decrypt cannot be null in decryptString method");
        }

        try (ByteArrayOutputStream decrypted = new ByteArrayOutputStream()) {
            byte[] decodedBytes = Base64.getDecoder().decode(string);
            decrypt(new ByteArrayInputStream(decodedBytes), decrypted);
            return decrypted.toString();
        } catch (CipherException e) {
            Logger.logException("Error while decrypting string", e);
            throw new IllegalStateException("Error while decrypting string", e);
        } catch (IOException e) {
            Logger.logException("Error while handling input/output during decryption", e);
            throw new IllegalStateException("Error while handling input/output during decryption", e);
        }
    }

    private void transferData(Cipher cipher, InputStream inputStream, OutputStream outputStream) {

        if (cipher == null || inputStream == null || outputStream == null) {
            throw new IllegalArgumentException(
                "Cipher, input stream and output stream cannot be null in transferData method");
        }

        try (var cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                cipherOutputStream.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            Logger.logException("IO Error while transferring data during encryption", e);
            throw new IllegalStateException("IO Error while transferring data during encryption", e);
        }
    }

    private void encrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {

        if (inputStream == null || outputStream == null) {
            throw new IllegalArgumentException("Input stream and output stream cannot be null in encrypt method");
        }

        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);
        transferData(cipher, inputStream, outputStream);
    }

    private void decrypt(InputStream inputStream, OutputStream outputStream) throws CipherException {

        if (inputStream == null || outputStream == null) {
            throw new IllegalArgumentException("Input stream and output stream cannot be null in decrypt method");
        }

        Cipher cipher = initCipher(Cipher.DECRYPT_MODE);
        transferData(cipher, inputStream, outputStream);
    }

    private Cipher initCipher(int mode) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Logger.logException("Cannot get instance of encryption algorithm", e);
            throw new IllegalStateException("Cannot get instance of encryption algorithm", e);
        }
        try {
            cipher.init(mode, secretKey);
        } catch (InvalidKeyException e) {
            Logger.logException("Cannot init cipher", e);
            throw new IllegalStateException("Cannot init cipher", e);
        }
        return cipher;
    }

}

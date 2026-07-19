package bg.sofia.uni.fmi.mjt.algorithm;

import bg.sofia.uni.fmi.mjt.exception.CipherException;

public interface SymmetricBlockCipher {
    /**
     * Encrypts a string and returns the encrypted data as a Base64 encoded string.
     *
     * @param string the string to encrypt
     * @return the encrypted string encoded in Base64
     * @throws CipherException if the encrypt/decrypt operation cannot be completed successfully
     */
    String encryptString(String string) throws CipherException;

    /**
     * Decrypts a Base64 encoded string and returns the decrypted data as a string.
     *
     * @param string the Base64 encoded string to decrypt
     * @return the decrypted string
     * @throws CipherException if the encrypt/decrypt operation cannot be completed successfully
     */
    String decryptString(String string) throws CipherException;
}

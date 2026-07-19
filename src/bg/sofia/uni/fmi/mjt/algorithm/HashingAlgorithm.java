package bg.sofia.uni.fmi.mjt.algorithm;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingAlgorithm {
    private static final int HEXADECIMAL255 = 0xff;
    private final String algorithm;

    public HashingAlgorithm() {
        algorithm = "SHA-256";
    }

    public HashingAlgorithm(String algorithm) {
        if (algorithm == null) {
            throw new IllegalArgumentException("algorithm cannot be null");
        }

        this.algorithm = algorithm;
    }

    public String hash(String password) throws NoSuchAlgorithmException {

        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null in hash method");
        }

        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedHash);
    }

    private String bytesToHex(byte[] hash) {

        if (hash == null) {
            throw new IllegalArgumentException("Hash cannot be null in bytesToHex method");
        }

        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(HEXADECIMAL255 & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

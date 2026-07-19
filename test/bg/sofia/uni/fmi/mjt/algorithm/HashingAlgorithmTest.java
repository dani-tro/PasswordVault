package bg.sofia.uni.fmi.mjt.algorithm;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HashingAlgorithmTest {

    @Test
    void testHashingAlgorithmCorrectlyHashesUsingSHA256() {
        HashingAlgorithm hashingAlgorithm = new HashingAlgorithm("SHA-256");

        String message = "Message to be hashed123!";
        String hashedMessage;

        try {
            hashedMessage = hashingAlgorithm.hash(message);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException occurred while testing hash method of HashingAlgorithm", e);
        }

        assertEquals("148902ea2deff56580b5a6438a42f1017eb2226ef61fe295157b4200a56a6c1e", hashedMessage,
            "The hashed message must be the same as the expected SHA-256 hash");
    }

    @Test
    void testHashingAlgorithmCorrectlyHashesUsingSHA512() {
        HashingAlgorithm hashingAlgorithm = new HashingAlgorithm("SHA-512");

        String message = "Message to be hashed123!";
        String hashedMessage;

        try {
            hashedMessage = hashingAlgorithm.hash(message);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException occurred while testing hash method of HashingAlgorithm", e);
        }

        assertEquals(
            "88678e9dd2d8b11f893eb25bf88076da0becaa95e0a41f91ada235e91f813922b95db2ea278a321d4336e2c4f30c906147ba9966627deb3dcdcdb841f6d17d05",
            hashedMessage,
            "The hashed message must be the same as the expected SHA-256 hash");
    }

    @Test
    void testHashingAlgorithmThrowsExceptionWhenPasswordIsNull() {
        HashingAlgorithm hashingAlgorithm = new HashingAlgorithm();

        assertThrows(IllegalArgumentException.class, () -> hashingAlgorithm.hash(null),
            "Expected hash(null) to throw IllegalArgumentException");
    }

}


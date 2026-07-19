package bg.sofia.uni.fmi.mjt.server.vault;

import bg.sofia.uni.fmi.mjt.algorithm.HashingAlgorithm;
import bg.sofia.uni.fmi.mjt.logging.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Vault {

    private final Map<String, String> usersToPasswords = new HashMap<>();

    private static final String VAULT_FILE = "vault.txt";

    private static Vault instance = null;

    private Vault() {
        File vaultFile = new File(VAULT_FILE);
        try {
            vaultFile.createNewFile();
        } catch (IOException e) {
            Logger.logException("A problem occurred while creating the vault file", e);
        }
        try (var bufferedReader = Files.newBufferedReader(Path.of(VAULT_FILE))) {
            String username;
            String hashedPassword;

            while ((username = bufferedReader.readLine()) != null) {
                hashedPassword = bufferedReader.readLine();
                usersToPasswords.put(username, hashedPassword);
            }

        } catch (IOException e) {
            Logger.logException("A problem occurred while reading from the vault file", e);
        }
    }

    public static Vault getInstance() {
        if (instance == null) {
            instance = new Vault();
        }
        return instance;
    }

    public synchronized boolean addUser(String username, String password) throws NoSuchAlgorithmException {

        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }

        if (usersToPasswords.containsKey(username)) {
            return false;
        }
        try (var fileOutputStream = new FileOutputStream(VAULT_FILE, true)) {
            fileOutputStream.write((username + System.lineSeparator()).getBytes());
            fileOutputStream.write((new HashingAlgorithm().hash(password) + System.lineSeparator()).getBytes());
        } catch (IOException e) {
            Logger.logException("A problem occurred while writing to the vault file", e);
        }
        usersToPasswords.put(username, (new HashingAlgorithm().hash(password)));
        return true;
    }

    public boolean authenticate(String username, String password) throws NoSuchAlgorithmException {

        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }

        if (!usersToPasswords.containsKey(username)) {
            return false;
        }

        return usersToPasswords.get(username).equals(new HashingAlgorithm().hash(password));

    }
}

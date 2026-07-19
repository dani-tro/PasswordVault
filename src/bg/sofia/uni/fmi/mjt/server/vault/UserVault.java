package bg.sofia.uni.fmi.mjt.server.vault;

import bg.sofia.uni.fmi.mjt.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.exception.APIException;
import bg.sofia.uni.fmi.mjt.logging.Logger;
import bg.sofia.uni.fmi.mjt.server.PasswordAuthenticator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class UserVault {

    private static final String PASSWORD_FILES_DIRECTORY = "PasswordFiles";
    private static final String PASSWORD_FILE_EXTENSION = ".txt";
    private static final String TEMP_FILE_NAME = "temp.txt";
    private final File userVaultFile;
    private final PasswordAuthenticator passwordAuthenticator;
    private final Rijndael rijndael;

    public UserVault(String username) {
        this(username, new PasswordAuthenticator(), new Rijndael());
    }

    public UserVault(String username, PasswordAuthenticator passwordAuthenticator, Rijndael rijndael) {

        if (username == null || passwordAuthenticator == null || rijndael == null) {
            throw new IllegalArgumentException("username, passwordAuthenticator and rijndael cannot be null");
        }

        File passwordFilesDirectory = new File(PASSWORD_FILES_DIRECTORY);
        if (!passwordFilesDirectory.exists()) {
            passwordFilesDirectory.mkdirs();
        }

        this.userVaultFile = new File(PASSWORD_FILES_DIRECTORY + File.separator + username + PASSWORD_FILE_EXTENSION);
        try {
            userVaultFile.createNewFile();
        } catch (IOException e) {
            Logger.logException("A problem occurred while creating the user vault file", e);
        }
        this.passwordAuthenticator = passwordAuthenticator;
        this.rijndael = rijndael;
    }

    public Optional<String> retrieveCredentials(String website, String user) {

        if (website == null || user == null) {
            throw new IllegalArgumentException("Website and user cannot be null");
        }

        try (var bufferedReader = new BufferedReader(new FileReader(userVaultFile))) {
            String websiteString;
            String userString;
            String encryptedPasswordString;

            while ((userString = bufferedReader.readLine()) != null) {
                websiteString = bufferedReader.readLine();
                encryptedPasswordString = bufferedReader.readLine();
                if (userString.equals(user) && websiteString.equals(website)) {
                    return Optional.of(rijndael.decryptString(encryptedPasswordString));
                }
            }
            return Optional.empty();
        } catch (IOException e) {
            Logger.logException("A problem occurred while reading from a file", e);
            return Optional.empty();
        }
    }

    public synchronized boolean addPassword(String website, String user, String password)
        throws APIException, NoSuchAlgorithmException {

        if (website == null || user == null || password == null) {
            throw new IllegalArgumentException("Website, user and password cannot be null");
        }

        if (!passwordAuthenticator.isPasswordStrong(password)) {
            return false;
        }

        if (retrieveCredentials(website, user).isPresent()) {
            return false;
        }

        try (var fileOutputStream = new FileWriter(userVaultFile, true)) {
            fileOutputStream.write(user + System.lineSeparator());
            fileOutputStream.write(website + System.lineSeparator());
            fileOutputStream.write(rijndael.encryptString(password) + System.lineSeparator());
        } catch (IOException e) {
            Logger.logException("A problem occurred while writing to the user vault file", e);
            return false;
        }
        return true;
    }

    public synchronized void removePassword(String website, String user) {
        if (website == null || user == null) {
            throw new IllegalArgumentException("Website and user cannot be null");
        }
        if (retrieveCredentials(website, user).isEmpty()) {
            return;
        }

        File tempFile = new File(PASSWORD_FILES_DIRECTORY + File.separator + TEMP_FILE_NAME);
        try (var bufferedReader = new BufferedReader(new FileReader(userVaultFile));
             var fileWriter = new FileWriter(tempFile)) {
            String websiteString;
            String userString;
            String encryptedPasswordString;

            while ((userString = bufferedReader.readLine()) != null) {
                websiteString = bufferedReader.readLine();
                encryptedPasswordString = bufferedReader.readLine();
                if (!userString.equals(user) || !websiteString.equals(website)) {
                    fileWriter.write(userString + System.lineSeparator());
                    fileWriter.write(websiteString + System.lineSeparator());
                    fileWriter.write(encryptedPasswordString + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from a file", e);
        }
        userVaultFile.delete();
        tempFile.renameTo(userVaultFile);
    }
}

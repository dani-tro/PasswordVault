package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.exception.APIException;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordCreator {

    public static final String UPPER_CASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";

    public static final String DIGITS = "0123456789";

    public static final String SPECIAL_SYMBOLS = "!@#$%^&*()_+";

    public static final int PASSWORD_MIN_LENGTH = 8;

    public static final int PASSWORD_MAX_LENGTH = 20;

    public static final int PASSWORD_MANDATORY_SYMBOLS = 4;

    public static final String ALL_SYMBOLS = UPPER_CASE_LETTERS + LOWER_CASE_LETTERS + DIGITS + SPECIAL_SYMBOLS;

    private final PasswordAuthenticator passwordAuthenticator;

    public PasswordCreator() {
        this.passwordAuthenticator = new PasswordAuthenticator();
    }

    public PasswordCreator(PasswordAuthenticator passwordAuthenticator) {
        if (passwordAuthenticator == null) {
            throw new IllegalArgumentException("passwordAuthenticator cannot be null");
        }

        this.passwordAuthenticator = passwordAuthenticator;
    }

    public String generatePassword() throws APIException, NoSuchAlgorithmException {

        StringBuilder password;
        Random random = new Random();

        do {
            password = new StringBuilder();
            int passwordLength = random.nextInt(PASSWORD_MAX_LENGTH - PASSWORD_MIN_LENGTH - PASSWORD_MANDATORY_SYMBOLS
            ) + PASSWORD_MIN_LENGTH;
            password.append(UPPER_CASE_LETTERS.charAt(random.nextInt(UPPER_CASE_LETTERS.length())));
            password.append(LOWER_CASE_LETTERS.charAt(random.nextInt(LOWER_CASE_LETTERS.length())));
            password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
            password.append(SPECIAL_SYMBOLS.charAt(random.nextInt(SPECIAL_SYMBOLS.length())));
            for (int i = 0; i < passwordLength; i++) {
                int index = (int) (Math.random() * ALL_SYMBOLS.length());
                password.append(ALL_SYMBOLS.charAt(index));
            }

            for (int i = 0; i < password.length(); i++) {
                int index = (int) (Math.random() * password.length());
                char temp = password.charAt(i);
                password.setCharAt(i, password.charAt(index));
                password.setCharAt(index, temp);
            }
        }
        while (!passwordAuthenticator.isPasswordStrong(password.toString()));
        return password.toString();
    }
}

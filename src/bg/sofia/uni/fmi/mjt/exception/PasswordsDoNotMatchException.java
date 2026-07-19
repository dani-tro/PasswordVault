package bg.sofia.uni.fmi.mjt.exception;

public class PasswordsDoNotMatchException extends CommandException {
    public PasswordsDoNotMatchException(String message) {
        super(message);
    }

    public PasswordsDoNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}

package bg.sofia.uni.fmi.mjt.exception;

public class AlreadyLoggedInException extends CommandException {
    public AlreadyLoggedInException(String message) {
        super(message);
    }

    public AlreadyLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }
}

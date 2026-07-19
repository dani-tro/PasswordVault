package bg.sofia.uni.fmi.mjt.exception;

public class InvalidCommandException extends CommandException {
    public InvalidCommandException(String message) {
        super(message);
    }

    public InvalidCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}

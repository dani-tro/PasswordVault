package bg.sofia.uni.fmi.mjt.exception;

public class UnsuccessfulCommandException extends CommandException {
    public UnsuccessfulCommandException(String message) {
        super(message);
    }

    public UnsuccessfulCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}

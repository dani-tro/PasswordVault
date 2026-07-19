package bg.sofia.uni.fmi.mjt.exception;

public class InvalidNumberOfArgumentsException extends CommandException {
    public InvalidNumberOfArgumentsException(String message) {
        super(message);
    }

    public InvalidNumberOfArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }
}

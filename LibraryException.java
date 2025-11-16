/**
 * Custom exception for Library of Stuff application.
 * Provides error codes and clear messages.
 */
public class LibraryException extends RuntimeException {
    private String errorCode;

    public LibraryException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "[" + errorCode + "] " + getMessage();
    }
}
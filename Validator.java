/**
 * Input validation utility class.
 * Validates all user inputs before processing.
 */
public class Validator {

    private Validator() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String requireNonEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new LibraryException(
                    "VALIDATION_ERROR",
                    fieldName + " cannot be empty"
            );
        }
        return value.trim();
    }

    public static <T> T requireNonNull(T object, String fieldName) {
        if (object == null) {
            throw new LibraryException(
                    "VALIDATION_ERROR",
                    fieldName + " cannot be null"
            );
        }
        return object;
    }
}
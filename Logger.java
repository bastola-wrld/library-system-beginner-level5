import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logging utility for tracking operations.
 */
public class Logger {
    private String className;
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Logger(String className) {
        this.className = className;
    }

    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz.getSimpleName());
    }

    public void info(String message) {
        log("INFO", message);
    }

    public void warn(String message) {
        log("WARN", message);
    }

    public void error(String message, Exception e) {
        log("ERROR", message);
        if (e != null) {
            System.err.println("  Exception: " + e.getMessage());
        }
    }

    private void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        System.out.println(String.format("[%s] [%s] %s: %s",
                timestamp, level, className, message));
    }
}
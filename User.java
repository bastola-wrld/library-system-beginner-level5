import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User entity with loyalty system.
 */
public class User {
    private static final Logger logger = Logger.getLogger(User.class);

    // Core properties (3+ requirement)
    private final String userId;
    private final String name;
    private final String email;
    private final LocalDateTime createdAt;

    // Booking data
    private final List<Item> currentBookings = new ArrayList<>();
    private volatile int totalBookingsMade = 0;
    private volatile int loyaltyPoints = 0;

    public User(String userId, String name, String email) {
        this.userId = Validator.requireNonEmpty(userId, "User ID");
        this.name = Validator.requireNonEmpty(name, "Name");
        this.email = Validator.requireNonEmpty(email, "Email");
        this.createdAt = LocalDateTime.now();

        logger.info("Created user: " + userId + " - " + name);
    }

    // Getters (3+ methods requirement)
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public int getTotalBookingsMade() { return totalBookingsMade; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public synchronized List<Item> getCurrentBookings() {
        return new ArrayList<>(currentBookings);
    }

    // Booking management (thread-safe)
    public synchronized void addBooking(Item item) {
        Validator.requireNonNull(item, "Item");

        if (currentBookings.contains(item)) {
            logger.warn("User " + userId + " tried to book already booked item");
            return;
        }

        currentBookings.add(item);
        totalBookingsMade++;
        loyaltyPoints += 10;  // Loyalty reward!

        logger.info("User " + userId + " booked item " + item.getItemId());
    }

    public synchronized void removeBooking(Item item) {
        Validator.requireNonNull(item, "Item");

        if (currentBookings.remove(item)) {
            logger.info("User " + userId + " returned item " + item.getItemId());
        }
    }

    public synchronized boolean hasBooked(Item item) {
        return currentBookings.contains(item);
    }

    // Display method
    public void printProfile() {
        System.out.println("\n========================================");
        System.out.println("USER PROFILE");
        System.out.println("========================================");
        System.out.println("User ID:         " + userId);
        System.out.println("Name:            " + name);
        System.out.println("Email:           " + email);
        System.out.println("Loyalty Points:  " + loyaltyPoints);
        System.out.println("Total Bookings:  " + totalBookingsMade);
        System.out.println("Active Bookings: " + currentBookings.size());

        if (!currentBookings.isEmpty()) {
            System.out.println("\nCURRENT BOOKINGS:");
            for (Item item : currentBookings) {
                System.out.println("  • " + item.getName() + " (" + item.getItemId() + ")");
            }
        }
        System.out.println("========================================\n");
    }
}
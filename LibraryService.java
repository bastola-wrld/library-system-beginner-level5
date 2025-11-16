import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LibraryService - Central service class (Facade Pattern)
 * Manages all library operations with simplified interface.
 */
public class LibraryService {
    private static final Logger logger = Logger.getLogger(LibraryService.class);
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Core components
    private final BookingManager bookingManager;
    private final Map<String, Item> itemCatalog;
    private final Map<String, User> userRegistry;

    // Statistics
    private volatile int totalBookingsMade = 0;
    private volatile int totalReturns = 0;
    private volatile int failedBookingAttempts = 0;

    /**
     * Constructor
     */
    public LibraryService() {
        this.bookingManager = new BookingManager();
        this.itemCatalog = new HashMap<>();
        this.userRegistry = new HashMap<>();
        logger.info("LibraryService initialized");
    }

    // ============================================
    // ITEM MANAGEMENT
    // ============================================

    /**
     * Register a single item
     */
    public boolean registerItem(Item item) {
        try {
            Validator.requireNonNull(item, "Item");

            if (itemCatalog.containsKey(item.getItemId())) {
                logger.warn("Item already registered: " + item.getItemId());
                return false;
            }

            bookingManager.addItem(item);
            itemCatalog.put(item.getItemId(), item);
            logger.info("Item registered: " + item.getItemId());
            return true;
        } catch (LibraryException e) {
            logger.error("Failed to register item", e);
            return false;
        }
    }

    /**
     * Register multiple items
     */
    public int registerItems(List<Item> items) {
        int successCount = 0;
        for (Item item : items) {
            if (registerItem(item)) {
                successCount++;
            }
        }
        logger.info("Registered " + successCount + " out of " + items.size() + " items");
        return successCount;
    }

    /**
     * Get item by ID
     */
    public Item getItem(String itemId) {
        return itemCatalog.get(itemId);
    }

    /**
     * Search items by name
     */
    public List<Item> searchItemsByName(String searchTerm) {
        List<Item> results = new ArrayList<>();
        String lowerSearch = searchTerm.toLowerCase();

        for (Item item : itemCatalog.values()) {
            if (item.getName().toLowerCase().contains(lowerSearch)) {
                results.add(item);
            }
        }

        logger.info("Search for '" + searchTerm + "' found " + results.size() + " items");
        return results;
    }

    /**
     * Get items by category
     */
    public List<Item> getItemsByCategory(String category) {
        List<Item> results = new ArrayList<>();

        for (Item item : itemCatalog.values()) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                results.add(item);
            }
        }

        return results;
    }

    /**
     * Get all available items
     */
    public List<Item> getAvailableItems() {
        List<Item> available = new ArrayList<>();

        for (Item item : itemCatalog.values()) {
            if (item.isAvailable()) {
                available.add(item);
            }
        }

        return available;
    }

    /**
     * Get all borrowed items
     */
    public List<Item> getBorrowedItems() {
        List<Item> borrowed = new ArrayList<>();

        for (Item item : itemCatalog.values()) {
            if (!item.isAvailable()) {
                borrowed.add(item);
            }
        }

        return borrowed;
    }

    /**
     * Get all items
     */
    public Collection<Item> listAllItems() {
        return new ArrayList<>(itemCatalog.values());
    }

    // ============================================
    // USER MANAGEMENT
    // ============================================

    /**
     * Register a single user
     */
    public boolean registerUser(User user) {
        try {
            Validator.requireNonNull(user, "User");

            if (userRegistry.containsKey(user.getUserId())) {
                logger.warn("User already registered: " + user.getUserId());
                return false;
            }

            bookingManager.addUser(user);
            userRegistry.put(user.getUserId(), user);
            logger.info("User registered: " + user.getUserId());
            return true;
        } catch (LibraryException e) {
            logger.error("Failed to register user", e);
            return false;
        }
    }

    /**
     * Register multiple users
     */
    public int registerUsers(List<User> users) {
        int successCount = 0;
        for (User user : users) {
            if (registerUser(user)) {
                successCount++;
            }
        }
        logger.info("Registered " + successCount + " out of " + users.size() + " users");
        return successCount;
    }

    /**
     * Get user by ID
     */
    public User getUser(String userId) {
        return userRegistry.get(userId);
    }

    /**
     * Search users by name
     */
    public List<User> searchUsersByName(String searchTerm) {
        List<User> results = new ArrayList<>();
        String lowerSearch = searchTerm.toLowerCase();

        for (User user : userRegistry.values()) {
            if (user.getName().toLowerCase().contains(lowerSearch)) {
                results.add(user);
            }
        }

        return results;
    }

    /**
     * Get top users by loyalty points
     */
    public List<User> getTopUsersByLoyalty(int limit) {
        List<User> allUsers = new ArrayList<>(userRegistry.values());
        allUsers.sort((u1, u2) -> Integer.compare(u2.getLoyaltyPoints(), u1.getLoyaltyPoints()));

        return allUsers.subList(0, Math.min(limit, allUsers.size()));
    }

    /**
     * Get all users
     */
    public Collection<User> listAllUsers() {
        return new ArrayList<>(userRegistry.values());
    }

    // ============================================
    // BOOKING OPERATIONS
    // ============================================

    /**
     * Book an item for a user
     */
    public BookingResult bookItem(String itemId, String userId) {
        logger.info("Booking request: itemId=" + itemId + ", userId=" + userId);

        try {
            // Get item and user
            Item item = itemCatalog.get(itemId);
            if (item == null) {
                failedBookingAttempts++;
                return new BookingResult(false, "Item not found: " + itemId, null, null);
            }

            User user = userRegistry.get(userId);
            if (user == null) {
                failedBookingAttempts++;
                return new BookingResult(false, "User not found: " + userId, null, null);
            }

            // Check availability
            if (!item.isAvailable()) {
                failedBookingAttempts++;
                String message = String.format(
                        "Sorry, '%s' is currently borrowed by %s. Please check back later or choose another item.",
                        item.getName(), item.getBorrower()
                );
                return new BookingResult(false, message, item, user);
            }

            // Perform booking
            item.borrow(user.getName());
            user.addBooking(item);
            totalBookingsMade++;

            String successMessage = String.format(
                    "Success! %s has booked '%s'. You earned 10 loyalty points!",
                    user.getName(), item.getName()
            );

            logger.info("Booking successful: " + itemId + " by " + userId);
            return new BookingResult(true, successMessage, item, user);

        } catch (LibraryException e) {
            failedBookingAttempts++;
            logger.error("Booking failed", e);
            return new BookingResult(false, e.getMessage(), null, null);
        }
    }

    /**
     * Return an item
     */
    public BookingResult returnItem(String itemId, String userId) {
        logger.info("Return request: itemId=" + itemId + ", userId=" + userId);

        try {
            Item item = itemCatalog.get(itemId);
            if (item == null) {
                return new BookingResult(false, "Item not found: " + itemId, null, null);
            }

            User user = userRegistry.get(userId);
            if (user == null) {
                return new BookingResult(false, "User not found: " + userId, null, null);
            }

            // Check if user has this item booked
            if (!user.hasBooked(item)) {
                return new BookingResult(false, "You haven't booked this item.", item, user);
            }

            // Perform return
            item.returnItem();
            user.removeBooking(item);
            totalReturns++;

            String successMessage = String.format(
                    "Thank you! '%s' has been returned successfully.",
                    item.getName()
            );

            logger.info("Return successful: " + itemId + " by " + userId);
            return new BookingResult(true, successMessage, item, user);

        } catch (LibraryException e) {
            logger.error("Return failed", e);
            return new BookingResult(false, e.getMessage(), null, null);
        }
    }

    /**
     * Get user's current bookings
     */
    public List<Item> getUserBookings(String userId) {
        User user = userRegistry.get(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        return user.getCurrentBookings();
    }

    // ============================================
    // REPORTING & STATISTICS
    // ============================================

    /**
     * Print available items catalog
     */
    public void printAvailableItemsCatalog() {
        List<Item> available = getAvailableItems();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("AVAILABLE ITEMS CATALOG");
        System.out.println("=".repeat(60));

        if (available.isEmpty()) {
            System.out.println("No items currently available.");
        } else {
            System.out.println("\n" + available.size() + " item(s) available:\n");
            for (Item item : available) {
                System.out.println("• " + item.getName() + " (" + item.getItemId() + ")");
                System.out.println("  Category: " + item.getCategory());
                System.out.println("  Type: " + item.getClass().getSimpleName());
                System.out.println();
            }
        }

        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * Print borrowed items report
     */
    public void printBorrowedItemsReport() {
        List<Item> borrowed = getBorrowedItems();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("CURRENTLY BORROWED ITEMS");
        System.out.println("=".repeat(60));

        if (borrowed.isEmpty()) {
            System.out.println("No items currently borrowed.");
        } else {
            for (Item item : borrowed) {
                System.out.println("\n• " + item.getName() + " (" + item.getItemId() + ")");
                System.out.println("  Borrowed by: " + item.getBorrower());
                System.out.println("  Borrowed at: " + item.getBorrowedAt().format(formatter));
            }
        }

        System.out.println("\n" + "=".repeat(60) + "\n");
    }

    /**
     * Print comprehensive statistics report
     */
    public void printStatisticsReport() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("LIBRARY STATISTICS REPORT");
        System.out.println("Generated: " + LocalDateTime.now().format(formatter));
        System.out.println("=".repeat(60));

        // Item statistics
        int totalItems = itemCatalog.size();
        int availableItems = getAvailableItems().size();
        int borrowedItems = getBorrowedItems().size();

        System.out.println("\n--- ITEM STATISTICS ---");
        System.out.println("Total Items:         " + totalItems);
        System.out.println("Available:           " + availableItems +
                " (" + (totalItems > 0 ? (availableItems * 100 / totalItems) : 0) + "%)");
        System.out.println("Currently Borrowed:  " + borrowedItems +
                " (" + (totalItems > 0 ? (borrowedItems * 100 / totalItems) : 0) + "%)");

        // User statistics
        int totalUsers = userRegistry.size();
        int activeUsers = 0;
        int totalLoyaltyPoints = 0;

        for (User user : userRegistry.values()) {
            if (!user.getCurrentBookings().isEmpty()) {
                activeUsers++;
            }
            totalLoyaltyPoints += user.getLoyaltyPoints();
        }

        System.out.println("\n--- USER STATISTICS ---");
        System.out.println("Total Users:         " + totalUsers);
        System.out.println("Active Users:        " + activeUsers);
        System.out.println("Total Loyalty Points: " + totalLoyaltyPoints);
        System.out.println("Avg Points per User: " +
                (totalUsers > 0 ? (totalLoyaltyPoints / totalUsers) : 0));

        // Booking statistics
        System.out.println("\n--- BOOKING STATISTICS ---");
        System.out.println("Total Bookings:      " + totalBookingsMade);
        System.out.println("Total Returns:       " + totalReturns);
        System.out.println("Failed Attempts:     " + failedBookingAttempts);
        int totalAttempts = totalBookingsMade + failedBookingAttempts;
        System.out.println("Success Rate:        " +
                (totalAttempts > 0 ? (totalBookingsMade * 100 / totalAttempts) : 0) + "%");

        // Category breakdown
        Map<String, Integer> categoryCount = new HashMap<>();
        for (Item item : itemCatalog.values()) {
            categoryCount.put(item.getCategory(),
                    categoryCount.getOrDefault(item.getCategory(), 0) + 1);
        }

        System.out.println("\n--- ITEMS BY CATEGORY ---");
        for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        // Top users
        List<User> topUsers = getTopUsersByLoyalty(3);
        if (!topUsers.isEmpty()) {
            System.out.println("\n--- TOP 3 USERS (LOYALTY POINTS) ---");
            int rank = 1;
            for (User user : topUsers) {
                System.out.println(rank + ". " + user.getName() +
                        " - " + user.getLoyaltyPoints() + " points");
                rank++;
            }
        }

        System.out.println("=".repeat(60) + "\n");
    }

    // ============================================
    // UTILITY METHODS
    // ============================================

    public int getTotalItems() {
        return itemCatalog.size();
    }

    public int getTotalUsers() {
        return userRegistry.size();
    }

    public int getTotalBookingsMade() {
        return totalBookingsMade;
    }

    public int getTotalReturns() {
        return totalReturns;
    }

    public void resetStatistics() {
        totalBookingsMade = 0;
        totalReturns = 0;
        failedBookingAttempts = 0;
        logger.info("Statistics reset");
    }

    // ============================================
    // INNER CLASS: BookingResult
    // ============================================

    /**
     * Result object for booking/return operations
     */
    public static class BookingResult {
        private final boolean success;
        private final String message;
        private final Item item;
        private final User user;

        public BookingResult(boolean success, String message, Item item, User user) {
            this.success = success;
            this.message = message;
            this.item = item;
            this.user = user;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Item getItem() {
            return item;
        }

        public User getUser() {
            return user;
        }

        public void printResult() {
            if (success) {
                System.out.println("✓ " + message);
            } else {
                System.out.println("✗ " + message);
            }
        }
    }
}
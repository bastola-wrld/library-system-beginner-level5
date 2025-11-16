import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Booking manager with thread-safe operations.
 */
public class BookingManager {
    private static final Logger logger = Logger.getLogger(BookingManager.class);

    private final Map<String, Item> items = new ConcurrentHashMap<>();
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public BookingManager() {
        logger.info("BookingManager initialized");
    }

    // Item management
    public void addItem(Item item) {
        Validator.requireNonNull(item, "Item");

        String itemId = item.getItemId();
        if (items.containsKey(itemId)) {
            throw new LibraryException(
                    "DUPLICATE_ITEM",
                    "Item with ID " + itemId + " already exists"
            );
        }

        items.put(itemId, item);
        logger.info("Added item: " + itemId);
    }

    // User management
    public void addUser(User user) {
        Validator.requireNonNull(user, "User");

        String userId = user.getUserId();
        if (users.containsKey(userId)) {
            throw new LibraryException(
                    "DUPLICATE_USER",
                    "User with ID " + userId + " already exists"
            );
        }

        users.put(userId, user);
        logger.info("Added user: " + userId);
    }

    // Retrieval methods
    public Item getItem(String itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new LibraryException("ITEM_NOT_FOUND", "Item not found: " + itemId);
        }
        return item;
    }

    public User getUser(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new LibraryException("USER_NOT_FOUND", "User not found: " + userId);
        }
        return user;
    }

    // List methods
    public Collection<Item> listAllItems() {
        return new ArrayList<>(items.values());
    }

    public Collection<Item> listAvailableItems() {
        List<Item> availableItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.isAvailable()) {
                availableItems.add(item);
            }
        }
        return availableItems;
    }

    // Booking operations
    public boolean bookItem(String itemId, String userId) {
        Item item = getItem(itemId);
        User user = getUser(userId);

        item.borrow(user.getName());
        user.addBooking(item);

        logger.info("Booked item " + itemId + " for user " + userId);
        return true;
    }

    public boolean returnItem(String itemId, String userId) {
        Item item = getItem(itemId);
        User user = getUser(userId);

        if (!user.hasBooked(item)) {
            throw new LibraryException("UNAUTHORIZED",
                    "User has not booked this item");
        }

        item.returnItem();
        user.removeBooking(item);

        logger.info("Returned item " + itemId + " by user " + userId);
        return true;
    }
}
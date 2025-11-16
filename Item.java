import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all library items.
 * Implements Template Method pattern.
 */
public abstract class Item {
    private static final Logger logger = Logger.getLogger(Item.class);
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Core properties (3+ requirement)
    private final String itemId;
    private final String name;
    private final String category;

    // Status properties
    private volatile boolean available;
    private volatile String borrower;
    private volatile LocalDateTime borrowedAt;
    private final LocalDateTime createdAt;
    private volatile LocalDateTime updatedAt;

    // Association properties
    private final List<Hobby> hobbies = new ArrayList<>();
    private final List<Equipment> equipment = new ArrayList<>();
    private final List<Facility> facilities = new ArrayList<>();

    // Constructor
    public Item(String itemId, String name, String category) {
        this.itemId = Validator.requireNonEmpty(itemId, "Item ID");
        this.name = Validator.requireNonEmpty(name, "Name");
        this.category = Validator.requireNonEmpty(category, "Category");

        this.available = true;
        this.borrower = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;

        logger.info("Created item: " + itemId + " - " + name);
    }

    // Getters (3+ methods requirement)
    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return available; }
    public String getBorrower() { return borrower; }
    public LocalDateTime getBorrowedAt() { return borrowedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Booking operations (thread-safe)
    public synchronized boolean borrow(String borrowerName) {
        Validator.requireNonEmpty(borrowerName, "Borrower name");

        if (!available) {
            logger.warn("Cannot borrow " + itemId + " - already borrowed");
            throw new LibraryException(
                    "ITEM_NOT_AVAILABLE",
                    "Item is already borrowed by " + borrower
            );
        }

        this.available = false;
        this.borrower = borrowerName;
        this.borrowedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        logger.info("Item " + itemId + " borrowed by " + borrowerName);
        return true;
    }

    public synchronized void returnItem() {
        if (available) {
            logger.warn("Cannot return " + itemId + " - not borrowed");
            throw new LibraryException(
                    "INVALID_OPERATION",
                    "Item is not currently borrowed"
            );
        }

        String previousBorrower = this.borrower;
        this.available = true;
        this.borrower = null;
        this.borrowedAt = null;
        this.updatedAt = LocalDateTime.now();

        logger.info("Item " + itemId + " returned by " + previousBorrower);
    }

    // Association management
    public synchronized void addHobby(Hobby hobby) {
        Validator.requireNonNull(hobby, "Hobby");
        if (!hobbies.contains(hobby)) {
            hobbies.add(hobby);
            this.updatedAt = LocalDateTime.now();
            logger.info("Added hobby to item " + itemId);
        }
    }

    public synchronized void addEquipment(Equipment eq) {
        Validator.requireNonNull(eq, "Equipment");
        if (!equipment.contains(eq)) {
            equipment.add(eq);
            this.updatedAt = LocalDateTime.now();
            logger.info("Added equipment to item " + itemId);
        }
    }

    public synchronized void addFacility(Facility facility) {
        Validator.requireNonNull(facility, "Facility");
        if (!facilities.contains(facility)) {
            facilities.add(facility);
            this.updatedAt = LocalDateTime.now();
            logger.info("Added facility to item " + itemId);
        }
    }

    public synchronized List<Hobby> getHobbies() {
        return new ArrayList<>(hobbies);
    }

    public synchronized List<Equipment> getEquipment() {
        return new ArrayList<>(equipment);
    }

    public synchronized List<Facility> getFacilities() {
        return new ArrayList<>(facilities);
    }

    // Display methods
    public void printDetails() {
        System.out.println("\n========================================");
        System.out.println("ITEM DETAILS");
        System.out.println("========================================");
        System.out.println("ID:       " + itemId);
        System.out.println("Name:     " + name);
        System.out.println("Category: " + category);
        System.out.println("Status:   " + (available ? "✓ Available" : "✗ Borrowed"));

        if (!available) {
            System.out.println("Borrowed by: " + borrower);
            System.out.println("Borrowed at: " + borrowedAt.format(formatter));
        }

        System.out.println("Created:  " + createdAt.format(formatter));
        System.out.println("Updated:  " + updatedAt.format(formatter));

        System.out.println("\n--- Type Details ---");
        System.out.println(typeDetails());

        System.out.println("\n--- HOBBIES (" + hobbies.size() + ") ---");
        if (hobbies.isEmpty()) {
            System.out.println("  None");
        } else {
            for (Hobby h : hobbies) {
                System.out.println("  • " + h);
            }
        }

        System.out.println("\n--- EQUIPMENT (" + equipment.size() + ") ---");
        if (equipment.isEmpty()) {
            System.out.println("  None");
        } else {
            for (Equipment e : equipment) {
                System.out.println("  • " + e);
            }
        }

        System.out.println("\n--- FACILITIES (" + facilities.size() + ") ---");
        if (facilities.isEmpty()) {
            System.out.println("  None");
        } else {
            for (Facility f : facilities) {
                System.out.println("  • " + f);
            }
        }

        System.out.println("========================================\n");
    }

    // Abstract method (Strategy pattern)
    public abstract String typeDetails();

    @Override
    public String toString() {
        return String.format("%s (ID: %s) - %s",
                name, itemId,
                available ? "Available" : "Borrowed by " + borrower);
    }
}
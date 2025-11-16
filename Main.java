import java.util.*;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("LIBRARY OF STUFF - SPRINT 2 DEMONSTRATION");
        System.out.println("========================================\n");

        // Initialize service
        LibraryService service = new LibraryService();

        // Create 3+ items (different types)
        Tool hammer = new Tool("T001", "Claw Hammer", "Hand Tools", "Hand Tool", false);
        hammer.addHobby(new Hobby("H001", "Carpentry", "Building structures", "Intermediate"));
        hammer.addEquipment(new Equipment("E001", "Safety Gloves", true, "Hand protection"));
        hammer.addFacility(new Facility("F001", "Workshop", "Building A", false, "9AM-5PM"));

        Appliance blender = new Appliance("A001", "Blender", "Kitchen", "Electric", true);
        blender.addHobby(new Hobby("H002", "Cooking", "Healthy meals", "Beginner"));

        Accessory drillBits = new Accessory("AC001", "Drill Bits", "Accessories", "Bits", "All drills");

        List<Item> items = Arrays.asList(hammer, blender, drillBits);
        service.registerItems(items);
        System.out.println("✓ Registered 3 items\n");

        // Create 3+ users
        User alice = new User("U001", "Alice Johnson", "alice@email.com");
        User bob = new User("U002", "Bob Smith", "bob@email.com");
        User carol = new User("U003", "Carol Williams", "carol@email.com");

        List<User> users = Arrays.asList(alice, bob, carol);
        service.registerUsers(users);
        System.out.println("✓ Registered 3 users\n");

        // Show available items
        System.out.println("--- Available Items ---");
        service.printAvailableItemsCatalog();

        // Demonstrate booking
        System.out.println("--- Booking Demonstration ---\n");
        LibraryService.BookingResult r1 = service.bookItem("T001", "U001");
        r1.printResult();

        LibraryService.BookingResult r2 = service.bookItem("A001", "U002");
        r2.printResult();

        // Demonstrate unavailable message
        System.out.println("\n--- Unavailable Item Demo ---\n");
        LibraryService.BookingResult r3 = service.bookItem("T001", "U002");
        r3.printResult();
        System.out.println("(Polite message shown!)\n");

        // Show user profiles
        System.out.println("--- User Profiles ---\n");
        alice.printProfile();
        bob.printProfile();

        // Show statistics
        System.out.println("--- Statistics ---");
        service.printStatisticsReport();

        System.out.println("\n✓ ALL REQUIREMENTS DEMONSTRATED!");
        logger.info("Demo complete");
    }
}
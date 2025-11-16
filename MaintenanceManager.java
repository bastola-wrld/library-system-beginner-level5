public class MaintenanceManager {

    private static MaintenanceManager instance;

    private MaintenanceManager() {}

    public static MaintenanceManager getInstance() {
        if (instance == null) instance = new MaintenanceManager();
        return instance;
    }

    public void markUsed(Item item) {
        System.out.println("🛠 Item " + item.getClass().getSimpleName() + " marked as used.");
    }

    public void markForMaintenance(Item item) {
        System.out.println("🔧 " + item.getClass().getSimpleName() + " scheduled for maintenance.");
    }
}

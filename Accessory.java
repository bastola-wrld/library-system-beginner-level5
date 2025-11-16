/**
 * Accessory item type.
 */
public class Accessory extends Item {
    private final String accessoryType;
    private final String compatibility;

    public Accessory(String itemId, String name, String category,
                     String accessoryType, String compatibility) {
        super(itemId, name, category);
        this.accessoryType = Validator.requireNonEmpty(accessoryType, "Accessory Type");
        this.compatibility = Validator.requireNonEmpty(compatibility, "Compatibility");
    }

    public String getAccessoryType() {
        return accessoryType;
    }

    public String getCompatibility() {
        return compatibility;
    }

    @Override
    public String typeDetails() {
        return String.format("Accessory Type: %s\nCompatibility: %s",
                accessoryType, compatibility);
    }

    @Override
    public String toString() {
        return "[ACCESSORY] " + getName() + " (" + getItemId() + ")";
    }
}
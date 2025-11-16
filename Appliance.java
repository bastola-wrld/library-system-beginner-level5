/**
 * Appliance item type.
 */
public class Appliance extends Item {
    private final String powerSource;
    private final boolean instructionsIncluded;

    public Appliance(String itemId, String name, String category,
                     String powerSource, boolean instructionsIncluded) {
        super(itemId, name, category);
        this.powerSource = Validator.requireNonEmpty(powerSource, "Power Source");
        this.instructionsIncluded = instructionsIncluded;
    }

    public String getPowerSource() {
        return powerSource;
    }

    public boolean hasInstructions() {
        return instructionsIncluded;
    }

    @Override
    public String typeDetails() {
        return String.format("Power Source: %s\nInstructions Included: %s",
                powerSource, instructionsIncluded ? "Yes" : "No");
    }

    @Override
    public String toString() {
        return "[APPLIANCE] " + getName() + " (" + getItemId() + ")";
    }
}
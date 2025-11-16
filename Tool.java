/**
 * Tool item type.
 */
public class Tool extends Item {
    private final String toolType;
    private final boolean requiresSafety;

    public Tool(String itemId, String name, String category,
                String toolType, boolean requiresSafety) {
        super(itemId, name, category);
        this.toolType = Validator.requireNonEmpty(toolType, "Tool Type");
        this.requiresSafety = requiresSafety;
    }

    public String getToolType() {
        return toolType;
    }

    public boolean requiresSafety() {
        return requiresSafety;
    }

    @Override
    public String typeDetails() {
        return String.format("Tool Type: %s\nRequires Safety Gear: %s",
                toolType, requiresSafety ? "Yes" : "No");
    }

    @Override
    public String toString() {
        return "[TOOL] " + getName() + " (" + getItemId() + ")";
    }
}
/**
 * Equipment entity associated with items.
 */
public class Equipment {
    private final String id;
    private final String name;
    private final boolean included;
    private final String purpose;

    public Equipment(String id, String name, boolean included, String purpose) {
        this.id = Validator.requireNonEmpty(id, "Equipment ID");
        this.name = Validator.requireNonEmpty(name, "Equipment name");
        this.purpose = Validator.requireNonEmpty(purpose, "Purpose");
        this.included = included;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isIncluded() { return included; }
    public String getPurpose() { return purpose; }

    @Override
    public String toString() {
        return String.format("%s | Included: %s | Purpose: %s",
                name, included ? "Yes" : "No", purpose);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Equipment other = (Equipment) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
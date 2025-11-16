/**
 * Hobby entity associated with items.
 */
public class Hobby {
    private final String id;
    private final String name;
    private final String description;
    private final String skillLevel;

    public Hobby(String id, String name, String description, String skillLevel) {
        this.id = Validator.requireNonEmpty(id, "Hobby ID");
        this.name = Validator.requireNonEmpty(name, "Hobby name");
        this.description = Validator.requireNonEmpty(description, "Description");
        this.skillLevel = Validator.requireNonEmpty(skillLevel, "Skill level");
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getSkillLevel() { return skillLevel; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s", name, skillLevel, description);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Hobby other = (Hobby) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
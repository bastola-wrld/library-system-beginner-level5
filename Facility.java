/**
 * Facility entity where items can be used.
 */
public class Facility {
    private final String id;
    private final String name;
    private final String location;
    private final boolean requiresBooking;
    private final String openingHours;

    public Facility(String id, String name, String location,
                    boolean requiresBooking, String openingHours) {
        this.id = Validator.requireNonEmpty(id, "Facility ID");
        this.name = Validator.requireNonEmpty(name, "Facility name");
        this.location = Validator.requireNonEmpty(location, "Location");
        this.openingHours = Validator.requireNonEmpty(openingHours, "Opening hours");
        this.requiresBooking = requiresBooking;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public boolean requiresBooking() { return requiresBooking; }
    public String getOpeningHours() { return openingHours; }

    @Override
    public String toString() {
        return String.format("%s @ %s | Booking Required: %s | Hours: %s",
                name, location, requiresBooking ? "Yes" : "No", openingHours);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Facility other = (Facility) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
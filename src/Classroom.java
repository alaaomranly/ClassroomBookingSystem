/**
 * Represents a classroom in the booking system.
 *
 * OOP Concept: Encapsulation (private fields + getters/setters)
 *
 * @author Student 03306
 * @version 1.0
 */
public class Classroom {

    /** Unique classroom ID */
    private int id;

    /** Classroom name, e.g. "Room A101" */
    private String name;

    /** Maximum number of students */
    private int capacity;

    /**
     * Creates a new Classroom.
     * @param id       unique ID
     * @param name     room name
     * @param capacity seating capacity
     */
    public Classroom(int id, String name, int capacity) {
        this.id       = id;
        this.name     = name;
        this.capacity = capacity;
    }

    /** @return classroom ID */
    public int    getId()       { return id; }
    /** @return classroom name */
    public String getName()     { return name; }
    /** @return seating capacity */
    public int    getCapacity() { return capacity; }

    /** @param name new room name */
    public void setName(String name)      { this.name = name; }
    /** @param capacity new capacity */
    public void setCapacity(int capacity) { this.capacity = capacity; }

    /**
     * OOP: Method Overriding — overrides Object.toString().
     * @return formatted string for this classroom
     */
    @Override
    public String toString() {
        return "Room #" + id + " | " + name + " | Capacity: " + capacity;
    }
}

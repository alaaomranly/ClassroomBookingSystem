/**
 * Interface that defines management operations.
 * Any class that manages resources should implement this.
 *
 * OOP Concept: Interface
 *
 * @author Student 03306
 * @version 1.0
 */
public interface Manageable {

    /**
     * Add a new resource.
     * @param details description of the resource to add
     */
    void add(String details);

    /**
     * Delete an existing resource.
     * @param id the ID of the resource to delete
     */
    void delete(int id);

    /**
     * Update an existing resource.
     * @param id      the ID of the resource to update
     * @param details new details
     */
    void update(int id, String details);
}

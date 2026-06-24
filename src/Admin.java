/**
 * Represents an administrator in the system.
 * Extends User and implements Manageable interface.
 *
 * OOP Concepts: Inheritance, Interface Implementation, Method Overriding
 *
 * @author Student 03306
 * @version 1.0
 */
public class Admin extends User implements Manageable {

    /**
     * Creates a new Admin.
     * @param name     full name
     * @param email    email address
     * @param password login password
     */
    public Admin(String name, String email, String password) {
        super(name, email, password);
    }

    /**
     * OOP: Method Overriding — returns "Admin" as the role.
     * @return the string "Admin"
     */
    @Override
    public String getRole() {
        return "Admin";
    }

    /**
     * OOP: Interface Implementation — add a resource.
     * @param details description of what to add
     */
    @Override
    public void add(String details) {
        System.out.println("Admin adding: " + details);
    }

    /**
     * OOP: Interface Implementation — delete a resource.
     * @param id the ID to delete
     */
    @Override
    public void delete(int id) {
        System.out.println("Admin deleting ID: " + id);
    }

    /**
     * OOP: Interface Implementation — update a resource.
     * @param id      the ID to update
     * @param details new details
     */
    @Override
    public void update(int id, String details) {
        System.out.println("Admin updating ID " + id + " with: " + details);
    }
}

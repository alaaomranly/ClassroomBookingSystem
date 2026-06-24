/**
 * Represents a regular user (student/staff) in the system.
 * Extends Person and overrides getRole().
 *
 * OOP Concepts: Inheritance, Method Overriding, Encapsulation
 *
 * @author Student 03306
 * @version 1.0
 */
public class User extends Person {

    /**
     * Creates a new User.
     * @param name     full name
     * @param email    email address
     * @param password login password
     */
    public User(String name, String email, String password) {
        super(name, email, password); // calls Person constructor
    }

    /**
     * OOP: Method Overriding — returns "User" as the role.
     * @return the string "User"
     */
    @Override
    public String getRole() {
        return "User";
    }
}

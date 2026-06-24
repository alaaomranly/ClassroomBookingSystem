/**
 * Abstract base class for all persons in the system.
 * Provides common attributes shared by User and Admin.
 *
 * OOP Concept: Abstract Class + Encapsulation
 *
 * @author Student 03306
 * @version 1.0
 */
public abstract class Person {

    private String name;
    private String email;
    private String password;

    /**
     * Constructor for Person.
     * @param name     full name
     * @param email    email address
     * @param password login password
     */
    public Person(String name, String email, String password) {
        this.name     = name;
        this.email    = email;
        this.password = password;
    }

    /**
     * Returns the role of this person.
     * OOP: Abstract Method — every subclass must override this.
     * @return role name as String
     */
    public abstract String getRole();

    /** @return the person's name */
    public String getName()     { return name; }
    /** @return the person's email */
    public String getEmail()    { return email; }
    /** @return the person's password */
    public String getPassword() { return password; }

    /** @param name new name */
    public void setName(String name)         { this.name = name; }
    /** @param email new email */
    public void setEmail(String email)       { this.email = email; }
    /** @param password new password */
    public void setPassword(String password) { this.password = password; }

    /**
     * OOP: Method Overriding — overrides Object.toString().
     * @return formatted string describing this person
     */
    @Override
    public String toString() {
        return "[" + getRole() + "] " + name + " | " + email;
    }
}

import java.util.ArrayList;

/**
 * Core logic of the Classroom Booking System.
 * Manages users, classrooms, and bookings in memory and in the database.
 *
 * OOP Concepts: Encapsulation, Exception Handling, Polymorphism
 *
 * @author Student 03306
 * @version 1.0
 */
public class BookingSystem {

    /** In-memory list of all persons (User or Admin) */
    private ArrayList<Person>    persons    = new ArrayList<>();

    /** In-memory list of all classrooms */
    private ArrayList<Classroom> classrooms = new ArrayList<>();

    /** In-memory list of all bookings */
    private ArrayList<Booking>   bookings   = new ArrayList<>();

    /** Database manager for persistent storage */
    private DatabaseManager db;

    /**
     * Creates the BookingSystem and loads data from the database.
     * Seeds default admin and classrooms if the database is empty.
     *
     * @throws BookingException if database connection fails
     */
    public BookingSystem() throws BookingException {
        db = new DatabaseManager();

        // Load saved data from DB
        persons    = db.loadUsers();
        classrooms = db.loadClassrooms();
        bookings   = db.loadBookings(persons, classrooms);

        // If first run — seed default data
        if (persons.isEmpty()) {
            Admin defaultAdmin = new Admin("Admin", "admin@school.com", "admin123");
            persons.add(defaultAdmin);
            db.saveUser("Admin", "admin@school.com", "admin123", "Admin");
        }
        if (classrooms.isEmpty()) {
            addDefaultClassrooms();
        }
    }

    /** Adds sample classrooms on first run */
    private void addDefaultClassrooms() throws BookingException {
        Classroom[] defaults = {
            new Classroom(1, "Room A101", 30),
            new Classroom(2, "Room B202", 50),
            new Classroom(3, "Lab C301",  20),
            new Classroom(4, "Hall D401", 100)
        };
        for (Classroom c : defaults) {
            classrooms.add(c);
            db.saveClassroom(c);
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  AUTHENTICATION
    // ═══════════════════════════════════════════════════════════

    /**
     * Registers a new user account.
     *
     * @param name     full name (must not be blank)
     * @param email    email address (must be unique)
     * @param password password (must not be blank)
     * @throws BookingException if any field is blank or email is taken
     */
    public void register(String name, String email, String password)
            throws BookingException {
        // Exception Handling: validate inputs
        if (name.isBlank() || email.isBlank() || password.isBlank())
            throw new BookingException("All fields are required.");
        if (findPersonByEmail(email) != null)
            throw new BookingException("This email is already registered.");

        User newUser = new User(name, email, password);
        persons.add(newUser);
        db.saveUser(name, email, password, "User");
    }

    /**
     * Logs in a user with email and password.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return the Person object if credentials are correct
     * @throws BookingException if credentials are wrong
     */
    public Person login(String email, String password) throws BookingException {
        Person p = findPersonByEmail(email);
        if (p != null && p.getPassword().equals(password)) return p;
        throw new BookingException("Invalid email or password.");
    }

    // ═══════════════════════════════════════════════════════════
    //  CLASSROOMS
    // ═══════════════════════════════════════════════════════════

    /** @return all classrooms */
    public ArrayList<Classroom> getClassrooms() { return classrooms; }

    /**
     * Adds a new classroom (Admin only).
     * @param id       unique classroom ID
     * @param name     room name
     * @param capacity seating capacity
     * @throws BookingException if ID exists or inputs are invalid
     */
    public void addClassroom(int id, String name, int capacity)
            throws BookingException {
        if (name.isBlank())
            throw new BookingException("Room name cannot be empty.");
        if (capacity <= 0)
            throw new BookingException("Capacity must be greater than zero.");
        if (findClassroomById(id) != null)
            throw new BookingException("Classroom ID " + id + " already exists.");

        Classroom c = new Classroom(id, name, capacity);
        classrooms.add(c);
        db.saveClassroom(c);
    }

    /**
     * Deletes a classroom and all its bookings (Admin only).
     * @param id the classroom ID to delete
     * @throws BookingException if classroom not found
     */
    public void deleteClassroom(int id) throws BookingException {
        Classroom c = findClassroomById(id);
        if (c == null) throw new BookingException("Classroom not found.");
        classrooms.remove(c);
        bookings.removeIf(b -> b.getClassroom().getId() == id);
        db.deleteClassroom(id);
    }

    /**
     * Updates a classroom's name and capacity (Admin only).
     * @param id          the classroom ID
     * @param newName     new room name
     * @param newCapacity new seating capacity
     * @throws BookingException if classroom not found or inputs invalid
     */
    public void updateClassroom(int id, String newName, int newCapacity)
            throws BookingException {
        Classroom c = findClassroomById(id);
        if (c == null) throw new BookingException("Classroom not found.");
        if (newName.isBlank()) throw new BookingException("Name cannot be empty.");
        if (newCapacity <= 0)  throw new BookingException("Capacity must be > 0.");
        c.setName(newName);
        c.setCapacity(newCapacity);
        db.saveClassroom(c);
    }

    // ═══════════════════════════════════════════════════════════
    //  BOOKINGS
    // ═══════════════════════════════════════════════════════════

    /** @return all bookings */
    public ArrayList<Booking> getBookings() { return bookings; }

    /**
     * Returns all bookings belonging to a specific user.
     * @param user the user whose bookings to return
     * @return filtered list of bookings
     */
    public ArrayList<Booking> getBookingsForUser(User user) {
        ArrayList<Booking> result = new ArrayList<>();
        for (Booking b : bookings)
            if (b.getUser().getEmail().equals(user.getEmail()))
                result.add(b);
        return result;
    }

    /**
     * Books a classroom for a user.
     * Checks for conflicts before confirming.
     *
     * @param user        the user making the booking
     * @param classroomId the ID of the classroom
     * @param date        the date (YYYY-MM-DD)
     * @param timeSlot    the time slot
     * @throws BookingException if slot is taken or inputs are invalid
     */
    public void bookClassroom(User user, int classroomId,
                               String date, String timeSlot)
            throws BookingException {
        if (date.isBlank() || timeSlot.isBlank())
            throw new BookingException("Date and time slot are required.");

        Classroom room = findClassroomById(classroomId);
        if (room == null)
            throw new BookingException("Classroom not found.");

        // Check for conflicts
        for (Booking b : bookings) {
            if (b.getClassroom().getId() == classroomId
                    && b.getDate().equals(date)
                    && b.getTimeSlot().equals(timeSlot))
                throw new BookingException(
                    "This room is already booked at " + timeSlot + " on " + date);
        }

        int newId = db.saveBooking(user.getEmail(), classroomId, date, timeSlot);
        bookings.add(new Booking(newId, user, room, date, timeSlot));
    }

    /**
     * Cancels an existing booking.
     * Users can only cancel their own; Admins can cancel any.
     *
     * @param actor     the person requesting cancellation
     * @param bookingId the booking ID to cancel
     * @throws BookingException if booking not found or permission denied
     */
    public void cancelBooking(Person actor, int bookingId)
            throws BookingException {
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            if (b.getBookingId() == bookingId) {
                boolean isOwner = b.getUser().getEmail().equals(actor.getEmail());
                boolean isAdmin = actor instanceof Admin; // Polymorphism
                if (!isOwner && !isAdmin)
                    throw new BookingException("You can only cancel your own bookings.");
                bookings.remove(i);
                db.deleteBooking(bookingId);
                return;
            }
        }
        throw new BookingException("Booking #" + bookingId + " not found.");
    }

    // ═══════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════

    /**
     * Finds a person by email address.
     * @param email the email to search for
     * @return the Person, or null if not found
     */
    public Person findPersonByEmail(String email) {
        for (Person p : persons)
            if (p.getEmail().equalsIgnoreCase(email)) return p;
        return null;
    }

    /**
     * Finds a classroom by its ID.
     * @param id the ID to search for
     * @return the Classroom, or null if not found
     */
    public Classroom findClassroomById(int id) {
        for (Classroom c : classrooms)
            if (c.getId() == id) return c;
        return null;
    }

    /** @return all persons in the system */
    public ArrayList<Person> getPersons() { return persons; }

    /** Closes the database connection */
    public void close() { db.close(); }
}

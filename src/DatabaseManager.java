import java.sql.*;
import java.util.ArrayList;

/**
 * Manages all SQLite database operations for the Classroom Booking System.
 * Handles creating tables, saving, loading, and deleting records.
 *
 * OOP Concept: Encapsulation, Exception Handling
 *
 * @author Student 03306
 * @version 1.0
 */
public class DatabaseManager {

    /** SQLite database file path */
    private static final String URL = "jdbc:sqlite:classroom_booking.db";

    /** Active database connection */
    private Connection conn;

    /**
     * Creates a DatabaseManager and connects to SQLite.
     * Creates all tables if they don't exist yet.
     *
     * @throws BookingException if connection fails
     */
    public DatabaseManager() throws BookingException {
        try {
            conn = DriverManager.getConnection(URL);
            createTables();
        } catch (SQLException e) {
            throw new BookingException("Cannot connect to database: " + e.getMessage());
        }
    }

    /**
     * Creates the three main tables: users, classrooms, bookings.
     * @throws SQLException if SQL fails
     */
    private void createTables() throws SQLException {
        String usersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "email TEXT UNIQUE NOT NULL,"
                + "password TEXT NOT NULL,"
                + "role TEXT NOT NULL DEFAULT 'User'"
                + ")";

        String classroomsTable = "CREATE TABLE IF NOT EXISTS classrooms ("
                + "id INTEGER PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "capacity INTEGER NOT NULL"
                + ")";

        String bookingsTable = "CREATE TABLE IF NOT EXISTS bookings ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_email TEXT NOT NULL,"
                + "classroom_id INTEGER NOT NULL,"
                + "date TEXT NOT NULL,"
                + "time_slot TEXT NOT NULL"
                + ")";

        try (Statement st = conn.createStatement()) {
            st.execute(usersTable);
            st.execute(classroomsTable);
            st.execute(bookingsTable);
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  USER OPERATIONS
    // ═══════════════════════════════════════════════════════════

    /**
     * Saves a new user to the database.
     * @param name     user's name
     * @param email    user's email
     * @param password user's password
     * @param role     "User" or "Admin"
     * @throws BookingException if save fails
     */
    public void saveUser(String name, String email,
                         String password, String role) throws BookingException {
        String sql = "INSERT INTO users(name, email, password, role) VALUES(?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BookingException("Could not save user: " + e.getMessage());
        }
    }

    /**
     * Loads all users from the database.
     * @return list of Person objects (User or Admin)
     * @throws BookingException if load fails
     */
    public ArrayList<Person> loadUsers() throws BookingException {
        ArrayList<Person> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM users")) {
            while (rs.next()) {
                String name  = rs.getString("name");
                String email = rs.getString("email");
                String pass  = rs.getString("password");
                String role  = rs.getString("role");
                if ("Admin".equals(role)) list.add(new Admin(name, email, pass));
                else                      list.add(new User(name, email, pass));
            }
        } catch (SQLException e) {
            throw new BookingException("Could not load users: " + e.getMessage());
        }
        return list;
    }

    // ═══════════════════════════════════════════════════════════
    //  CLASSROOM OPERATIONS
    // ═══════════════════════════════════════════════════════════

    /**
     * Saves a classroom to the database.
     * @param c the classroom to save
     * @throws BookingException if save fails
     */
    public void saveClassroom(Classroom c) throws BookingException {
        String sql = "INSERT OR REPLACE INTO classrooms(id, name, capacity) VALUES(?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getId());
            ps.setString(2, c.getName());
            ps.setInt(3, c.getCapacity());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BookingException("Could not save classroom: " + e.getMessage());
        }
    }

    /**
     * Deletes a classroom from the database.
     * @param id the classroom ID to delete
     * @throws BookingException if delete fails
     */
    public void deleteClassroom(int id) throws BookingException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM classrooms WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BookingException("Could not delete classroom: " + e.getMessage());
        }
    }

    /**
     * Loads all classrooms from the database.
     * @return list of Classroom objects
     * @throws BookingException if load fails
     */
    public ArrayList<Classroom> loadClassrooms() throws BookingException {
        ArrayList<Classroom> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM classrooms")) {
            while (rs.next())
                list.add(new Classroom(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("capacity")
                ));
        } catch (SQLException e) {
            throw new BookingException("Could not load classrooms: " + e.getMessage());
        }
        return list;
    }

    // ═══════════════════════════════════════════════════════════
    //  BOOKING OPERATIONS
    // ═══════════════════════════════════════════════════════════

    /**
     * Saves a new booking to the database.
     * @param userEmail   the user's email
     * @param classroomId the classroom ID
     * @param date        the booking date
     * @param timeSlot    the time slot
     * @return the generated booking ID
     * @throws BookingException if save fails
     */
    public int saveBooking(String userEmail, int classroomId,
                            String date, String timeSlot) throws BookingException {
        String sql = "INSERT INTO bookings(user_email, classroom_id, date, time_slot)"
                   + " VALUES(?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, userEmail);
            ps.setInt(2, classroomId);
            ps.setString(3, date);
            ps.setString(4, timeSlot);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            throw new BookingException("Could not save booking: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Deletes a booking from the database.
     * @param bookingId the booking ID to delete
     * @throws BookingException if delete fails
     */
    public void deleteBooking(int bookingId) throws BookingException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM bookings WHERE id=?")) {
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new BookingException("Could not cancel booking: " + e.getMessage());
        }
    }

    /**
     * Loads all bookings from the database.
     * @param allUsers      list of all users (to match email)
     * @param allClassrooms list of all classrooms (to match ID)
     * @return list of Booking objects
     * @throws BookingException if load fails
     */
    public ArrayList<Booking> loadBookings(ArrayList<Person> allUsers,
                                            ArrayList<Classroom> allClassrooms)
            throws BookingException {
        ArrayList<Booking> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM bookings")) {
            while (rs.next()) {
                int    id    = rs.getInt("id");
                String email = rs.getString("user_email");
                int    cid   = rs.getInt("classroom_id");
                String date  = rs.getString("date");
                String slot  = rs.getString("time_slot");

                // Find matching user and classroom
                User      user = null;
                Classroom room = null;
                for (Person p : allUsers)
                    if (p.getEmail().equals(email) && p instanceof User)
                        user = (User) p;
                for (Classroom c : allClassrooms)
                    if (c.getId() == cid) room = c;

                if (user != null && room != null)
                    list.add(new Booking(id, user, room, date, slot));
            }
        } catch (SQLException e) {
            throw new BookingException("Could not load bookings: " + e.getMessage());
        }
        return list;
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
}

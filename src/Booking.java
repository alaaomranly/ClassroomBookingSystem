/**
 * Represents a booking record linking a User, a Classroom, date and time.
 *
 * OOP Concept: Encapsulation, Association (uses User and Classroom objects)
 *
 * @author Student 03306
 * @version 1.0
 */
public class Booking {

    /** Unique booking ID */
    private int bookingId;

    /** The user who made the booking */
    private User user;

    /** The booked classroom */
    private Classroom classroom;

    /** Booking date in YYYY-MM-DD format */
    private String date;

    /** Time slot e.g. "09:00-11:00" */
    private String timeSlot;

    /**
     * Creates a new Booking.
     * @param bookingId unique ID
     * @param user      the user making the booking
     * @param classroom the reserved classroom
     * @param date      the booking date (YYYY-MM-DD)
     * @param timeSlot  the time slot
     */
    public Booking(int bookingId, User user, Classroom classroom,
                   String date, String timeSlot) {
        this.bookingId = bookingId;
        this.user      = user;
        this.classroom = classroom;
        this.date      = date;
        this.timeSlot  = timeSlot;
    }

    /** @return booking ID */
    public int       getBookingId() { return bookingId; }
    /** @return the user */
    public User      getUser()      { return user; }
    /** @return the classroom */
    public Classroom getClassroom() { return classroom; }
    /** @return the date */
    public String    getDate()      { return date; }
    /** @return the time slot */
    public String    getTimeSlot()  { return timeSlot; }

    /**
     * OOP: Method Overriding — overrides Object.toString().
     * @return a readable summary of the booking
     */
    @Override
    public String toString() {
        return "Booking #" + bookingId + " | " + classroom.getName()
             + " | " + date + " | " + timeSlot + " | " + user.getName();
    }
}

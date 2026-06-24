/**
 * Custom exception for errors that occur in the Classroom Booking System.
 *
 * OOP Concept: Exception Handling (custom checked exception)
 *
 * @author Student 03306
 * @version 1.0
 */
public class BookingException extends Exception {

    /**
     * Creates a new BookingException with a message.
     * @param message description of the error
     */
    public BookingException(String message) {
        super(message);
    }
}

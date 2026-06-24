import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

/**
 * Entry point for the Classroom Booking System.
 * Creates the BookingSystem and launches the Login window.
 *
 * @author Student 03306
 * @version 1.0
 */
public class Main {

    /**
     * Main method — starts the application.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Launch GUI on the Event Dispatch Thread (required by Swing)
        SwingUtilities.invokeLater(() -> {
            try {
                BookingSystem system = new BookingSystem();
                new LoginFrame(system);

            } catch (BookingException e) {
                // Exception Handling: show error if system fails to start
                JOptionPane.showMessageDialog(null,
                    "Failed to start: " + e.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

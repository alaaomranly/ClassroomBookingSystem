import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Main dashboard window for regular Users.
 * Provides tabs for viewing classrooms, booking, and managing own bookings.
 *
 * OOP Concept: Encapsulation, Exception Handling
 *
 * @author Student 03306
 * @version 1.0
 */
public class UserFrame extends JFrame {

    private BookingSystem system;
    private User          currentUser;

    /**
     * Creates the User dashboard.
     * @param system      shared BookingSystem
     * @param currentUser the logged-in user
     */
    public UserFrame(BookingSystem system, User currentUser) {
        this.system      = system;
        this.currentUser = currentUser;

        setTitle("Welcome, " + currentUser.getName() + " [" + currentUser.getRole() + "]");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildUI();
        setVisible(true);
    }

    /** Builds the tabbed UI */
    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("📋 View Classrooms", buildClassroomsTab());
        tabs.addTab("📅 Book a Room",     buildBookTab());
        tabs.addTab("📌 My Bookings",     buildMyBookingsTab());

        // Logout button at bottom
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> { dispose(); new LoginFrame(system); });
        bottom.add(logout);

        add(tabs, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    // ── Tab 1: View all classrooms ────────────────────────────────────────

    private JPanel buildClassroomsTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("All Available Classrooms:"), BorderLayout.NORTH);

        String[] cols = {"ID", "Room Name", "Capacity"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Classroom c : system.getClassrooms())
            model.addRow(new Object[]{c.getId(), c.getName(), c.getCapacity()});

        panel.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        return panel;
    }

    // ── Tab 2: Book a room ────────────────────────────────────────────────

    private JPanel buildBookTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Room dropdown
        JComboBox<String> roomBox = new JComboBox<>();
        for (Classroom c : system.getClassrooms())
            roomBox.addItem(c.getId() + " - " + c.getName());

        // Date field
        JTextField dateField = new JTextField("2026-06-01", 15);

        // Time slot dropdown
        String[] slots = {"08:00-10:00","10:00-12:00","12:00-14:00",
                          "14:00-16:00","16:00-18:00"};
        JComboBox<String> slotBox = new JComboBox<>(slots);

        // Layout
        g.gridx=0; g.gridy=0; panel.add(new JLabel("Select Room:"), g);
        g.gridx=1;             panel.add(roomBox, g);

        g.gridx=0; g.gridy=1; panel.add(new JLabel("Date (YYYY-MM-DD):"), g);
        g.gridx=1;             panel.add(dateField, g);

        g.gridx=0; g.gridy=2; panel.add(new JLabel("Time Slot:"), g);
        g.gridx=1;             panel.add(slotBox, g);

        JButton bookBtn = new JButton("✅ Confirm Booking");
        g.gridx=0; g.gridy=3; g.gridwidth=2;
        panel.add(bookBtn, g);

        // Book action
        bookBtn.addActionListener(e -> {
            int idx = roomBox.getSelectedIndex();
            if (idx < 0) return;
            Classroom selected = system.getClassrooms().get(idx);
            String date = dateField.getText().trim();
            String slot = (String) slotBox.getSelectedItem();

            try {
                system.bookClassroom(currentUser, selected.getId(), date, slot);
                JOptionPane.showMessageDialog(this,
                    "Booking confirmed!\n" + selected.getName()
                    + " on " + date + " at " + slot,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (BookingException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Booking Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // ── Tab 3: My bookings ────────────────────────────────────────────────

    private JPanel buildMyBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Your Bookings:"), BorderLayout.NORTH);

        ArrayList<Booking> mine = system.getBookingsForUser(currentUser);

        String[] cols = {"Booking #", "Room", "Date", "Time Slot"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Booking b : mine)
            model.addRow(new Object[]{
                b.getBookingId(), b.getClassroom().getName(),
                b.getDate(), b.getTimeSlot()
            });

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Cancel button
        JButton cancelBtn = new JButton("❌ Cancel Selected Booking");
        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a booking first.");
                return;
            }
            int bookingId = (int) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel booking #" + bookingId + "?",
                "Confirm", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    system.cancelBooking(currentUser, bookingId);
                    model.removeRow(row);
                    JOptionPane.showMessageDialog(this, "Booking cancelled.");
                } catch (BookingException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(cancelBtn, BorderLayout.SOUTH);
        return panel;
    }
}

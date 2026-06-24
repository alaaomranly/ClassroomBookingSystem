import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Admin dashboard window.
 * Provides tabs for managing classrooms and viewing all bookings.
 *
 * OOP Concept: Encapsulation, Exception Handling, Polymorphism (instanceof)
 *
 * @author Student 03306
 * @version 1.0
 */
public class AdminFrame extends JFrame {

    private BookingSystem system;
    private Admin         admin;

    /**
     * Creates the Admin dashboard.
     * @param system shared BookingSystem
     * @param admin  the logged-in admin
     */
    public AdminFrame(BookingSystem system, Admin admin) {
        this.system = system;
        this.admin  = admin;

        setTitle("Admin Dashboard - " + admin.getName());
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildUI();
        setVisible(true);
    }

    /** Builds the tabbed admin UI */
    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("🏫 Manage Classrooms", buildClassroomsTab());
        tabs.addTab("➕ Add Classroom",     buildAddClassroomTab());
        tabs.addTab("📋 All Bookings",      buildAllBookingsTab());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> { dispose(); new LoginFrame(system); });
        bottom.add(logout);

        add(tabs, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    // ── Tab 1: Manage classrooms ──────────────────────────────────────────

    private JPanel buildClassroomsTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("All Classrooms — select one to edit or delete:"),
                  BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Capacity"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        refreshClassroomModel(model);

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));

        JButton editBtn = new JButton("✏️ Edit Selected");
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { msg("Select a classroom first."); return; }
            int id = (int) model.getValueAt(row, 0);
            Classroom c = system.findClassroomById(id);
            if (c == null) return;

            JTextField nameF = new JTextField(c.getName(), 15);
            JTextField capF  = new JTextField(String.valueOf(c.getCapacity()), 8);
            JPanel form = new JPanel(new GridLayout(4, 1, 4, 4));
            form.add(new JLabel("New Name:"));  form.add(nameF);
            form.add(new JLabel("New Capacity:")); form.add(capF);

            int res = JOptionPane.showConfirmDialog(this, form,
                "Edit Classroom", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    system.updateClassroom(id,
                        nameF.getText().trim(),
                        Integer.parseInt(capF.getText().trim()));
                    refreshClassroomModel(model);
                    msg("Classroom updated!");
                } catch (BookingException | NumberFormatException ex) {
                    err(ex.getMessage());
                }
            }
        });

        JButton delBtn = new JButton("🗑️ Delete Selected");
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { msg("Select a classroom first."); return; }
            int id = (int) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Delete classroom #" + id + "? Its bookings will also be removed.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    system.deleteClassroom(id);
                    refreshClassroomModel(model);
                    msg("Classroom deleted.");
                } catch (BookingException ex) { err(ex.getMessage()); }
            }
        });

        btnPanel.add(editBtn);
        btnPanel.add(delBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    /** Refreshes classroom table data */
    private void refreshClassroomModel(DefaultTableModel model) {
        model.setRowCount(0);
        for (Classroom c : system.getClassrooms())
            model.addRow(new Object[]{c.getId(), c.getName(), c.getCapacity()});
    }

    // ── Tab 2: Add new classroom ──────────────────────────────────────────

    private JPanel buildAddClassroomTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill   = GridBagConstraints.HORIZONTAL;

        JTextField idF   = new JTextField(10);
        JTextField nameF = new JTextField(18);
        JTextField capF  = new JTextField(10);

        g.gridx=0; g.gridy=0; panel.add(new JLabel("Room ID:"), g);
        g.gridx=1;             panel.add(idF, g);

        g.gridx=0; g.gridy=1; panel.add(new JLabel("Room Name:"), g);
        g.gridx=1;             panel.add(nameF, g);

        g.gridx=0; g.gridy=2; panel.add(new JLabel("Capacity:"), g);
        g.gridx=1;             panel.add(capF, g);

        JButton addBtn = new JButton("➕ Add Classroom");
        g.gridx=0; g.gridy=3; g.gridwidth=2;
        panel.add(addBtn, g);

        addBtn.addActionListener(e -> {
            try {
                int id  = Integer.parseInt(idF.getText().trim());
                int cap = Integer.parseInt(capF.getText().trim());
                system.addClassroom(id, nameF.getText().trim(), cap);
                idF.setText(""); nameF.setText(""); capF.setText("");
                msg("Classroom added successfully!");
            } catch (BookingException ex) {
                err(ex.getMessage());
            } catch (NumberFormatException ex) {
                err("ID and Capacity must be numbers.");
            }
        });

        return panel;
    }

    // ── Tab 3: All bookings ───────────────────────────────────────────────

    private JPanel buildAllBookingsTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("All bookings in the system:"), BorderLayout.NORTH);

        String[] cols = {"Booking #", "User", "Room", "Date", "Time Slot"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Booking b : system.getBookings())
            model.addRow(new Object[]{
                b.getBookingId(), b.getUser().getName(),
                b.getClassroom().getName(), b.getDate(), b.getTimeSlot()
            });

        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton cancelBtn = new JButton("❌ Cancel Selected Booking");
        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { msg("Select a booking first."); return; }
            int bookingId = (int) model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel booking #" + bookingId + "?",
                "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    system.cancelBooking(admin, bookingId);
                    model.removeRow(row);
                    msg("Booking cancelled.");
                } catch (BookingException ex) { err(ex.getMessage()); }
            }
        });

        panel.add(cancelBtn, BorderLayout.SOUTH);
        return panel;
    }

    // ── Utility helpers ───────────────────────────────────────────────────

    private void msg(String text) {
        JOptionPane.showMessageDialog(this, text, "Info",
            JOptionPane.INFORMATION_MESSAGE);
    }
    private void err(String text) {
        JOptionPane.showMessageDialog(this, text, "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

import javax.swing.*;
import java.awt.*;

/**
 * Login and Registration window.
 * First screen the user sees when starting the application.
 *
 * OOP Concept: Encapsulation, Exception Handling
 *
 * @author Student 03306
 * @version 1.0
 */
public class LoginFrame extends JFrame {

    private BookingSystem system;

    // Input fields
    private JTextField  emailField    = new JTextField(20);
    private JPasswordField passField  = new JPasswordField(20);

    /**
     * Creates the login window.
     * @param system the shared BookingSystem instance
     */
    public LoginFrame(BookingSystem system) {
        this.system = system;

        setTitle("Classroom Booking System - Login");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        buildUI();
        setVisible(true);
    }

    /** Builds all UI components */
    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ── Title ──
        JLabel title = new JLabel("🏫 Classroom Booking System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        main.add(title, BorderLayout.NORTH);

        // ── Form ──
        JPanel form = new JPanel(new GridLayout(4, 2, 8, 10));

        form.add(new JLabel("Email:"));
        form.add(emailField);

        form.add(new JLabel("Password:"));
        form.add(passField);

        form.add(new JLabel("")); // spacer

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> doLogin());
        form.add(loginBtn);

        form.add(new JLabel("No account?"));
        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(e -> openRegisterDialog());
        form.add(registerBtn);

        main.add(form, BorderLayout.CENTER);

        // ── Hint ──
        JLabel hint = new JLabel("Default Admin: admin@school.com / admin123",
                                  SwingConstants.CENTER);
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        main.add(hint, BorderLayout.SOUTH);

        add(main);
    }

    /** Handles the login button click */
    private void doLogin() {
        String email = emailField.getText().trim();
        String pass  = new String(passField.getPassword()).trim();

        try {
            Person p = system.login(email, pass);
            dispose(); // close login window
            if (p instanceof Admin) new AdminFrame(system, (Admin) p);
            else                    new UserFrame(system, (User) p);

        } catch (BookingException ex) {
            // Exception Handling: show error to user
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Opens a dialog for new user registration */
    private void openRegisterDialog() {
        JTextField nameF  = new JTextField(18);
        JTextField emailF = new JTextField(18);
        JPasswordField passF = new JPasswordField(18);

        JPanel panel = new JPanel(new GridLayout(6, 1, 5, 5));
        panel.add(new JLabel("Full Name:"));  panel.add(nameF);
        panel.add(new JLabel("Email:"));      panel.add(emailF);
        panel.add(new JLabel("Password:"));   panel.add(passF);

        int result = JOptionPane.showConfirmDialog(this, panel,
            "Register New Account", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                system.register(
                    nameF.getText().trim(),
                    emailF.getText().trim(),
                    new String(passF.getPassword()).trim()
                );
                JOptionPane.showMessageDialog(this,
                    "Account created! You can now log in.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (BookingException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

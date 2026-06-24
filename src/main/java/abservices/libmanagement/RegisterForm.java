
package abservices.libmanagement;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterForm extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "lms_user";
    private static final String DB_PASS = "lms_pass";

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmPasswordField;

    public RegisterForm() {
        setTitle("Library Management - Register");
        setSize(520, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        // Set window icon
        try {
            File iconFile = new File("images/UltiBooksManagementSystemNoBack.png");
            if (iconFile.exists()) {
                setIconImage(new ImageIcon(iconFile.getAbsolutePath()).getImage());
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }

        // Background panel with semi-transparent image
        JPanel backgroundPanel = new JPanel() {
            private Image backgroundImage;

            {
                try {
                    File bgFile = new File("Background_image.jfif");
                    if (bgFile.exists()) {
                        backgroundImage = ImageIO.read(bgFile);
                    }
                } catch (IOException e) {
                    System.err.println("Error loading background image: " + e.getMessage());
                }
                setOpaque(false);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    g2d.dispose();
                }
            }
        };

        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        backgroundPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Title Label
        JLabel titleLabel = new JLabel("Create a New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        backgroundPanel.add(titleLabel, gbc);

        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameLabel.setForeground(Color.BLACK);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.gridx = 0;
        backgroundPanel.add(usernameLabel, gbc);

        // Username Field with icon
        usernameField = new JTextField();
        styleTextField(usernameField);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        backgroundPanel.add(wrapFieldWithIcon(usernameField, "user_icon.png"), gbc);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 0, 5, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        backgroundPanel.add(passwordLabel, gbc);

        // Password Field with icon and toggle
        passwordField = new JPasswordField();
        styleTextField(passwordField);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        backgroundPanel.add(wrapPasswordFieldWithIcon(passwordField, "lock_icon.png"), gbc);

        // Confirm Password Label
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        confirmPasswordLabel.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 0, 5, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        backgroundPanel.add(confirmPasswordLabel, gbc);

        // Confirm Password Field with icon and toggle
        confirmPasswordField = new JPasswordField();
        styleTextField(confirmPasswordField);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        backgroundPanel.add(wrapPasswordFieldWithIcon(confirmPasswordField, "lock_icon.png"), gbc);

        // Register Button
        JButton registerBtn = new JButton("Register");
        styleButton(registerBtn);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 0, 10, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;
        backgroundPanel.add(registerBtn, gbc);

        registerBtn.addActionListener(e -> registerUser());

        // Back to Login Button
        JButton backBtn = new JButton("Back to Login");
        styleButton(backBtn);
        backBtn.setBackground(new Color(100, 100, 100));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        backgroundPanel.add(backBtn, gbc);

        backBtn.addActionListener(e -> {
            dispose();
        });

        add(backgroundPanel, BorderLayout.CENTER);
    }

    // Style for text and password fields
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
    }

    // Style for buttons
    private void styleButton(JButton btn) {
       btn.setBackground(new Color(0, 120, 215));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Wrap JTextField with icon label, aligned nicely
    private JPanel wrapFieldWithIcon(JTextField field, String iconPath) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);

        JLabel iconLabel = new JLabel();
        setIcon(iconLabel, iconPath);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);

        return panel;
    }

    // Wrap JPasswordField with icon and show/hide toggle
    private JPanel wrapPasswordFieldWithIcon(JPasswordField field, String iconPath) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);

        JLabel iconLabel = new JLabel();
        setIcon(iconLabel, iconPath);

        // Show/hide toggle button
        JToggleButton showPasswordButton = new JToggleButton("👁");
        showPasswordButton.setFocusable(false);
        showPasswordButton.setPreferredSize(new Dimension(40, 25));
        showPasswordButton.addActionListener(e -> {
            field.setEchoChar(showPasswordButton.isSelected() ? (char) 0 : '•');
        });

        showPasswordButton.addActionListener(e -> {
            if (showPasswordButton.isSelected()) {
                field.setEchoChar((char) 0);
            } else {
                field.setEchoChar('•');
            }
        });

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.add(showPasswordButton, BorderLayout.EAST);

        return panel;
    }

    private void setIcon(JLabel label, String iconPath) {
        try {
            File iconFile = new File(iconPath);
            if (iconFile.exists()) {
                Image img = ImageIO.read(iconFile);
                Image scaledImg = img.getScaledInstance(22, 22, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaledImg));
            }
        } catch (IOException e) {
            System.err.println("Failed to load icon: " + e.getMessage());
        }
    }

    private void registerUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            showMessageDialog("Error", "Please fill in all fields.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPass)) {
            showMessageDialog("Error", "Passwords do not match.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Ensure driver is available. Use reflection-first approach to avoid runtime missing-class errors.
            boolean driverReady = false;
            try {
                Class<?> dl = Class.forName("bonifacio_bscs1a.libmanagement.DriverLoader");
                dl.getMethod("ensureDriver").invoke(null);
                driverReady = true;
            } catch (ClassNotFoundException cnf) {
                // continue
            } catch (Exception ex) {
                // continue
            }

            if (!driverReady) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    driverReady = true;
                } catch (ClassNotFoundException e) {
                    try {
                        java.io.File libs = new java.io.File("libs");
                        if (libs.exists() && libs.isDirectory()) {
                            java.io.File[] jars = libs.listFiles((d, name) -> name.toLowerCase().startsWith("mysql") && name.toLowerCase().endsWith(".jar"));
                            if (jars != null) {
                                for (java.io.File jar : jars) {
                                    try {
                                        java.net.URL jarUrl = jar.toURI().toURL();
                                        java.net.URLClassLoader loader = new java.net.URLClassLoader(new java.net.URL[] { jarUrl }, Thread.currentThread().getContextClassLoader());
                                        String[] driverClasses = new String[] {"com.mysql.cj.jdbc.Driver", "com.mysql.jdbc.Driver"};
                                        for (String cls : driverClasses) {
                                            try {
                                                Class<?> c = Class.forName(cls, true, loader);
                                                Object o = c.getDeclaredConstructor().newInstance();
                                                if (o instanceof java.sql.Driver) {
                                                    java.sql.DriverManager.registerDriver((java.sql.Driver) o);
                                                    driverReady = true;
                                                    break;
                                                }
                                            } catch (Throwable t) {
                                                // ignore
                                            }
                                        }
                                        if (driverReady) break;
                                    } catch (Throwable t) {
                                        // ignore
                                    }
                                }
                            }
                        }
                    } catch (Throwable t) {
                        // ignore
                    }
                }
            }

            if (!driverReady) throw new ClassNotFoundException("MySQL JDBC driver not found or could not be loaded.");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, hashPassword(password));
                    stmt.executeUpdate();
                }
            }

            showMessageDialog("Success", "Registration successful for user: " + username, JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("duplicate") || e.getMessage().toLowerCase().contains("unique")) {
                showMessageDialog("Error", "Username already exists.", JOptionPane.ERROR_MESSAGE);
            } else {
                showMessageDialog("Error", "Database error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        } catch (ClassNotFoundException e) {
            showMessageDialog("Error", "MySQL driver not found: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void showMessageDialog(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegisterForm registerForm = new RegisterForm();
            registerForm.setVisible(true);
        });
    }
}

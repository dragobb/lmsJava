/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bonifacio_bscs1a.libmanagement;

/**
 *
 * @author ariel
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class LogIn extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JCheckBox rememberMeCheckbox;

    public LogIn() {
        setTitle("Library Management - Login");
        setSize(520, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        try {
            File iconFile = new File("images/UltiBooksManagementSystemNoBack.png");
            if (iconFile.exists()) {
                setIconImage(new ImageIcon(iconFile.getAbsolutePath()).getImage());
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }

        JPanel panel = new JPanel(new GridBagLayout()) {
            private Image backgroundImage;
            {
                try {
                    File imageFile = new File("Background_image.jfif");
                    if (imageFile.exists()) {
                        backgroundImage = ImageIO.read(imageFile);
                    }
                } catch (IOException e) {
                    System.err.println("Error loading background: " + e.getMessage());
                }
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
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        styleField(usernameField);
        panel.add(addIconToField(usernameField, "user_icon.png"), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        styleField(passwordField);
        panel.add(addIconToPasswordField(passwordField, "lock_icon.png"), gbc);

        // Remember Me checkbox
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        rememberMeCheckbox = new JCheckBox("Remember Me");
        rememberMeCheckbox.setOpaque(false);
        rememberMeCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(rememberMeCheckbox, gbc);

        // Login Button
        gbc.gridy++;
        JButton loginButton = new JButton("Login");
        styleButton(loginButton);
        loginButton.addActionListener(e -> {
            try {
                verifyLogin();
            } catch (SQLException ex) {
                showErrorDialog("Database Error", ex.getMessage());
            }
        });
        panel.add(loginButton, gbc);

        // Register Button
        gbc.gridy++;
        JButton registerButton = new JButton("Register");
        styleButton(registerButton);
        registerButton.addActionListener(e -> showRegisterForm());
        panel.add(registerButton, gbc);

        // Enter key listener
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        verifyLogin();
                    } catch (SQLException ex) {
                        showErrorDialog("Database Error", ex.getMessage());
                    }
                }
            }
        });

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 120, 215));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel addIconToField(JTextField field, String iconPath) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel iconLabel = new JLabel();
        try {
            File iconFile = new File(iconPath);
            if (iconFile.exists()) {
                ImageIcon icon = new ImageIcon(
                        new ImageIcon(iconFile.getAbsolutePath()).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)
                );
                iconLabel.setIcon(icon);
            }
        } catch (Exception e) {
            System.err.println("Icon error: " + e.getMessage());
        }
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel addIconToPasswordField(JPasswordField field, String iconPath) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel iconLabel = new JLabel();
        try {
            File iconFile = new File(iconPath);
            if (iconFile.exists()) {
                ImageIcon icon = new ImageIcon(
                        new ImageIcon(iconFile.getAbsolutePath()).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)
                );
                iconLabel.setIcon(icon);
            }
        } catch (Exception e) {
            System.err.println("Icon error: " + e.getMessage());
        }

        JToggleButton showPasswordButton = new JToggleButton("👁");
        showPasswordButton.setFocusable(false);
        showPasswordButton.setPreferredSize(new Dimension(40, 25));
        showPasswordButton.addActionListener(e -> {
            field.setEchoChar(showPasswordButton.isSelected() ? (char) 0 : '•');
        });

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.add(showPasswordButton, BorderLayout.EAST);
        return panel;
    }

    private void showRegisterForm() {
        new RegisterForm().setVisible(true);
    }

    private void verifyLogin() throws SQLException {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showErrorDialog("Input Error", "Please fill in both username and password.");
            return;
        }

        String hashedPassword = hashPassword(password);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_db", "root", "root")) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        showSuccessDialog("Login Successful", "Welcome, " + username + "!");
                        this.dispose();
                        new LibManagement(username).setVisible(true);
                    } else {
                        showErrorDialog("Login Failed", "Invalid username or password.");
                    }
                }
            }
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void showErrorDialog(String title, String message) {
        showCustomDialog(title, message, Color.RED);
    }

    private void showSuccessDialog(String title, String message) {
        showCustomDialog(title, message, new Color(0, 150, 0));
    }

    private void showCustomDialog(String title, String message, Color color) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(color);
        dialog.add(messageLabel, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dialog.dispose());
        styleButton(okButton);
        JPanel btnPanel = new JPanel();
        btnPanel.add(okButton);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LogIn::new);
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

/**
 *
 * @author ariel
 */
package abservices.libmanagement;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableRowSorter;

public class LibManagement extends JFrame {
    private final DefaultTableModel bookTableModel;
    private final String username; 
    private final String DB_URL = "jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private final String DB_USER = "lms_user";
    private final String DB_PASS = "lms_pass";

    public LibManagement(String username1) {
    this.username = username1;
    try {
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
                // try loading jars from libs
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
                                            // ignore and try next
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

        if (!driverReady) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC driver not found. Put the connector jar in libs/ or add it to the classpath.", "Driver Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (Throwable t) {
        // ignore - continue startup
    }
    bookTableModel = new DefaultTableModel(new Object[]{"Title", "Author", "ISBN", "Available"}, 0);
    setTitle("Library Management System");
    
    setExtendedState(JFrame.MAXIMIZED_BOTH);  // maximize window
    setResizable(false);                      // disable maximize button
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // Create main panel with custom background image
    JPanel mainPanel = new JPanel(new BorderLayout()) {
        private Image background;

        {
            try {
                ImageIcon icon = new ImageIcon("back1.jfif");
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    System.out.println("Background image loaded successfully!");
                    background = icon.getImage();
                } else {
                    System.out.println("Error loading background image.");
                }
            } catch (Exception e) {
                System.err.println("Error loading background image: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (background != null) {
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            } else {
                System.out.println("Background image is null");
            }
        }
    };

    mainPanel.setOpaque(true);
    mainPanel.setLayout(new BorderLayout());

    mainPanel.add(createSidebarPanel(username), BorderLayout.WEST);
    mainPanel.add(createModernBookListPanel(), BorderLayout.CENTER);
    add(mainPanel);

    try {
        File iconFile = new File("images/UltiBooksManagementSystemNoBack.png");
        if (iconFile.exists()) {
            setIconImage(new ImageIcon(iconFile.getAbsolutePath()).getImage());
            System.out.println("Icon loaded successfully from: " + iconFile.getAbsolutePath());
        } else {
            System.err.println("Icon file not found at: " + iconFile.getAbsolutePath());
        }
    } catch (Exception e) {
        System.err.println("Error loading icon: " + e.getMessage());
    }

    setVisible(true);
}



    private JPanel createSidebarPanel(String username) {  
    JPanel sidebar = new JPanel() {
        private Image background;

        {
            try {
                File bgFile = new File("sidebar_bg.gif");
                if (bgFile.exists()) {
                    background = new ImageIcon(bgFile.getAbsolutePath()).getImage();
                } else {
                    System.err.println("Sidebar background file not found.");
                }
            } catch (Exception e) {
                System.err.println("Failed to load sidebar background: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            
            // Enable antialiasing for smooth rendering
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // Create modern gradient background
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(45, 52, 67), // Dark blue-gray
                0, getHeight(), new Color(25, 30, 40) // Darker shade
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Add subtle background pattern overlay if image exists
            if (background != null) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                g2d.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }
            
            // Add subtle border accent
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2d.setColor(new Color(100, 150, 200));
            g2d.fillRect(getWidth() - 2, 0, 2, getHeight());
            
            g2d.dispose();
        }
    };

    sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
    sidebar.setPreferredSize(new Dimension(220, 0)); // Slightly wider for modern look
    sidebar.setOpaque(false);

    // Create modern welcome section
    JPanel welcomeSection = new JPanel();
    welcomeSection.setLayout(new BoxLayout(welcomeSection, BoxLayout.Y_AXIS));
    welcomeSection.setOpaque(false);
    welcomeSection.setMaximumSize(new Dimension(220, 80));
    
    JLabel welcomeTitle = new JLabel("Welcome");
    welcomeTitle.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
    welcomeTitle.setForeground(new Color(160, 170, 185));
    welcomeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    JLabel usernameLabel = new JLabel(username);
    usernameLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
    usernameLabel.setForeground(Color.WHITE);
    usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    welcomeSection.add(welcomeTitle);
    welcomeSection.add(Box.createVerticalStrut(4));
    welcomeSection.add(usernameLabel);

    // Create and style modern buttons
    JButton allBooksButton = createModernSidebarButton("Refresh Books", "refresh.png", "Reload all available books", new Color(64, 156, 255));
    JButton addBookButton = createModernSidebarButton("Add Book", "add_icon.png", "Add a new book to the system", new Color(52, 199, 89));
    JButton borrowersButton = createModernSidebarButton("Borrowers", "user.png", "View borrowers and borrowed books", new Color(255, 159, 10));
    JButton removeBookButton = createModernSidebarButton("Remove Book", "remove_icon.png", "Delete a book from the system", new Color(255, 69, 58));
    JButton logoutButton = createModernSidebarButton("Logout", "user-logout.png", "Log out of the system", new Color(142, 142, 147));

    // Add action listeners (keeping original functionality)
    addBookButton.addActionListener(e -> showAddBookDialog());
    removeBookButton.addActionListener(e -> showRemoveBookDialog());
    allBooksButton.addActionListener(e -> {
        JPanel container = findBookCardsContainer();
        if (container != null) {
            loadModernBookCards(container);
        }
    });
    borrowersButton.addActionListener(e -> {
        JPanel container = findBookCardsContainer();
        if (container != null) {
            loadBorrowedBooksByBorrower(container);
        }
    });
    logoutButton.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(sidebar, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SwingUtilities.getWindowAncestor(sidebar).dispose();
            LogIn();
        }
    });

    // Assemble sidebar with modern spacing
    sidebar.add(Box.createVerticalStrut(30));
    sidebar.add(welcomeSection);
    sidebar.add(Box.createVerticalStrut(40));

    // Add navigation section label
    JLabel navLabel = new JLabel("NAVIGATION");
    navLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 11));
    navLabel.setForeground(new Color(120, 130, 145));
    navLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    sidebar.add(navLabel);
    sidebar.add(Box.createVerticalStrut(20));

    // Add buttons with modern spacing
    JButton[] buttons = {allBooksButton, addBookButton, borrowersButton, removeBookButton};
    for (JButton btn : buttons) {
        sidebar.add(btn);
        sidebar.add(Box.createVerticalStrut(8));
    }
    
    // Add separator before logout
    sidebar.add(Box.createVerticalStrut(20));
    JSeparator separator = new JSeparator();
    separator.setForeground(new Color(60, 70, 85));
    separator.setMaximumSize(new Dimension(180, 1));
    sidebar.add(separator);
    sidebar.add(Box.createVerticalStrut(20));
    
    sidebar.add(logoutButton);
    sidebar.add(Box.createVerticalGlue());
    
    return sidebar;
}

private JButton createModernSidebarButton(String text, String iconPath, String tooltip, Color accentColor) {
    JButton button = new JButton(text) {
        private boolean isHovered = false;
        private boolean isPressed = false;
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Button background with modern styling
            Color bgColor;
            if (isPressed) {
                bgColor = new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 40);
            } else if (isHovered) {
                bgColor = new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 25);
            } else {
                bgColor = new Color(255, 255, 255, 10);
            }
            
            g2d.setColor(bgColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            
            // Modern border with accent color
            if (isHovered || isPressed) {
                g2d.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 60));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
            
            g2d.dispose();
            super.paintComponent(g);
        }
        
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            
            // Add subtle glow effect on hover
            if (isHovered) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 15));
                g2d.fillRoundRect(-2, -2, getWidth() + 4, getHeight() + 4, 16, 16);
                g2d.dispose();
            }
        }
    };
    
    // Modern button styling
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setOpaque(false);
    button.setForeground(Color.WHITE);
    button.setFont(new Font("SF Pro Display", Font.PLAIN, 14));
    button.setToolTipText(tooltip);
    button.setAlignmentX(Component.CENTER_ALIGNMENT);
    button.setPreferredSize(new Dimension(190, 44));
    button.setMaximumSize(new Dimension(190, 44));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setHorizontalAlignment(SwingConstants.LEFT);
    button.setIconTextGap(12);

    // Add modern hover effects
    button.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            button.getClass().getDeclaredFields()[0].setAccessible(true);
            try {
                button.getClass().getDeclaredFields()[0].setBoolean(button, true);
            } catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {}
            button.repaint();
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            button.getClass().getDeclaredFields()[0].setAccessible(true);
            try {
                button.getClass().getDeclaredFields()[0].setBoolean(button, false);
            } catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {}
            button.repaint();
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            try {
                button.getClass().getDeclaredFields()[1].setAccessible(true);
                button.getClass().getDeclaredFields()[1].setBoolean(button, true);
            } catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {}
            button.repaint();
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            try {
                button.getClass().getDeclaredFields()[1].setAccessible(true);
                button.getClass().getDeclaredFields()[1].setBoolean(button, false);
            } catch (IllegalAccessException | IllegalArgumentException | SecurityException ex) {}
            button.repaint();
        }
    });

    // Icon support like original code
    File iconFile = new File(iconPath);
    if (iconFile.exists()) {
        button.setIcon(new ImageIcon(new ImageIcon(iconFile.getAbsolutePath()).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
    }

    // Add subtle padding
    button.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
    
    return button;
}

    private JPanel findBookCardsContainer() {
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component innerComp : panel.getComponents()) {
                    if (innerComp instanceof JPanel) {
                        JPanel innerPanel = (JPanel) innerComp;
                        if (innerPanel.getLayout() instanceof BorderLayout) {
                            for (Component centerComp : innerPanel.getComponents()) {
                                if (centerComp instanceof JScrollPane) {
                                    JScrollPane scrollPane = (JScrollPane) centerComp;
                                    Component viewComp = scrollPane.getViewport().getView();
                                    if (viewComp instanceof JPanel) {
                                        JPanel jPanel = (JPanel) viewComp;
                                        return jPanel;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private JPanel createModernBookListPanel() {
    Image backgroundImage = loadImage("back2.jfif");

    JPanel mainPanel;
        mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (backgroundImage != null) {
                    g2d.drawImage(backgroundImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
                    g2d.setColor(new Color(255, 255, 255, 70)); // More transparent
                } else {
                    g2d.setPaint(new GradientPaint(0, 0, new Color(240, 248, 255), 0, getHeight(), new Color(230, 240, 250)));
                }
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
    mainPanel.setOpaque(false);

    JTextField searchField = createTextField("Search books by title, author or ISBN...");
    JComboBox<String> filterBox = createFilterBox();
    JPanel searchPanel = createSearchPanel(searchField, filterBox);

    JPanel cardContainer = new JPanel(new GridLayout(0, 3, 15, 15));
    cardContainer.setOpaque(false);
    cardContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JScrollPane scrollPane = new JScrollPane(cardContainer);
    scrollPane.setOpaque(false);
    scrollPane.getViewport().setOpaque(false);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);

    mainPanel.add(searchPanel, BorderLayout.NORTH);
    mainPanel.add(scrollPane, BorderLayout.CENTER);

    loadModernBookCards(cardContainer);

    searchField.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) { applyFilter(); }
        @Override
        public void removeUpdate(DocumentEvent e) { applyFilter(); }
        @Override
        public void changedUpdate(DocumentEvent e) { applyFilter(); }
        void applyFilter() {
            filterModernBookCards(cardContainer, searchField.getText(), filterBox.getSelectedItem().toString());
        }
    });

    filterBox.addActionListener(e ->
        filterModernBookCards(cardContainer, searchField.getText(), filterBox.getSelectedItem().toString())
    );

    return mainPanel;
}

// === Helper Methods ===

private Image loadImage(String path) {
    try {
        return new ImageIcon(path).getImage();
    } catch (Exception e) {
        System.err.println("Image not found: " + path);
        return null;
    }
}

private JTextField createTextField(String placeholder) {
    JTextField tf = new JTextField();
    tf.putClientProperty("JTextField.placeholderText", placeholder);
    tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    tf.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    tf.setPreferredSize(new Dimension(300, 34));
    tf.setOpaque(true);
    tf.setBackground(Color.WHITE);
    return tf;
}

private JComboBox<String> createFilterBox() {
    JComboBox<String> comboBox = new JComboBox<>(new String[]{"All Books", "Available Only", "Borrowed Only"});
    comboBox.setPreferredSize(new Dimension(150, 34));
    comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    comboBox.setOpaque(true);
    comboBox.setBackground(Color.WHITE);
    return comboBox;
}

private JPanel createSearchPanel(JTextField searchField, JComboBox<String> filterBox) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);
    panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

    JPanel left = new JPanel(new BorderLayout());
    left.setOpaque(false);
    left.add(searchField, BorderLayout.CENTER);
    left.setPreferredSize(new Dimension(400, 34));

    JLabel filterLabel = new JLabel("Filter:");
    filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
    filterLabel.setForeground(new Color(66, 66, 66));

    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    right.setOpaque(false);
    right.add(filterLabel);
    right.add(filterBox);

    panel.add(left, BorderLayout.CENTER);
    panel.add(right, BorderLayout.EAST);

    return panel;
}



private void loadModernBookCards(JPanel container) {
    container.removeAll(); 
    container.setLayout(new GridLayout(0, 5, 15, 15)); 
    container.setOpaque(false);
    container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM books");

        while (rs.next()) {
            String title = rs.getString("title");
            String author = rs.getString("author");
            String isbn = rs.getString("isbn");
            String imagePath = rs.getString("image_path");
            boolean available = rs.getBoolean("available");

            String borrower = null;
            String borrowDate = null;

            if (!available) {
                try (PreparedStatement borrowerStmt = conn.prepareStatement(
                        "SELECT member_name, borrow_date FROM borrowers WHERE book_title = ?")) {
                    borrowerStmt.setString(1, title);
                    ResultSet borrowerRs = borrowerStmt.executeQuery();

                    if (borrowerRs.next()) {
                        borrower = borrowerRs.getString("member_name");
                        borrowDate = borrowerRs.getString("borrow_date");
                    }
                }
            }

            JPanel card = createModernBookCard(title, author, isbn, available, borrower, borrowDate, imagePath);
            container.add(card);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading books: " + e.getMessage());
    }

    container.revalidate();
    container.repaint();
}



  private JPanel createModernBookCard(String title, String author, String isbn, 
                                  boolean available, String borrower, String borrowDate, String imagePath) {
    
    JPanel card = new JPanel(new BorderLayout(210, 300));
    card.setPreferredSize(new Dimension(210, 300)); 
    card.setMinimumSize(new Dimension(210, 280));   
    card.setMaximumSize(new Dimension(210, 300));   
    
    // Modern card styling with shadow effect
    card.setOpaque(true);
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 8, 5), // Outer shadow effect
            BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            )
    ));
    
    // Add subtle shadow effect
    card.setBackground(new Color(255, 255, 255));
    
    // Main content panel with background
    JPanel contentPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Gradient background
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(250, 250, 255),
                0, getHeight(), new Color(240, 245, 255)
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2d.dispose();
        }
    };
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setOpaque(false);
    
    // Top: Enhanced Book image/cover - MODIFIED TO SHOW ACTUAL IMAGE
    JPanel imagePanel = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Only draw background if no image is available
            if (imagePath == null || imagePath.trim().isEmpty()) {
                // Book cover background with gradient
                GradientPaint coverGradient = new GradientPaint(
                    0, 0, new Color(245, 245, 250),
                    0, getHeight(), new Color(235, 240, 250)
                );
                g2d.setPaint(coverGradient);
                g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 6, 6);
                
                // Border
                g2d.setColor(new Color(200, 210, 220));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 6, 6);
            }
            
            g2d.dispose();
        }
    };
    imagePanel.setPreferredSize(new Dimension(180, 100)); // Made taller for book covers
    imagePanel.setMinimumSize(new Dimension(180, 100));
    imagePanel.setMaximumSize(new Dimension(180, 100));
    imagePanel.setOpaque(false);
    
    // Book image or icon - MODIFIED TO LOAD ACTUAL IMAGES
    JLabel bookImageLabel = new JLabel();
    bookImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    bookImageLabel.setVerticalAlignment(SwingConstants.CENTER);
    
    // Try to load the actual book cover image
    boolean imageLoaded = false;
    if (imagePath != null && !imagePath.trim().isEmpty()) {
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(imagePath);
                Image originalImage = originalIcon.getImage();
                
                // Scale image to fit the panel while maintaining aspect ratio
                int panelWidth = 180;
                int panelHeight = 100;
                int imageWidth = originalImage.getWidth(null);
                int imageHeight = originalImage.getHeight(null);
                
                if (imageWidth > 0 && imageHeight > 0) {
                    // Calculate scaling to fit within panel
                    double scaleX = (double) panelWidth / imageWidth;
                    double scaleY = (double) panelHeight / imageHeight;
                    double scale = Math.min(scaleX, scaleY);
                    
                    int scaledWidth = (int) (imageWidth * scale);
                    int scaledHeight = (int) (imageHeight * scale);
                    
                    Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    bookImageLabel.setIcon(new ImageIcon(scaledImage));
                    imageLoaded = true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading book image: " + imagePath + " - " + e.getMessage());
        }
    }
    
    // If no image was loaded, show the default book icon
    if (!imageLoaded) {
        try {
            // Try to load default book icon image
            ImageIcon originalIcon = new ImageIcon("book.png");
            Image scaledImage = originalIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            bookImageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            // Fallback to Unicode book icon
            bookImageLabel.setText("📖");
            bookImageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        }
    }
    
    imagePanel.add(bookImageLabel, BorderLayout.CENTER);
    
    // Enhanced status badge with modern styling
    JPanel statusBadge = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color badgeColor = available ? new Color(76, 175, 80) : new Color(244, 67, 54);
            g2d.setColor(badgeColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            
            g2d.dispose();
        }
    };
    statusBadge.setOpaque(false);
    statusBadge.setPreferredSize(new Dimension(85, 24));
    statusBadge.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 2));
    
    JLabel statusLabel = new JLabel(available ? "Available" : "Borrowed");
    statusLabel.setForeground(Color.WHITE);
    statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
    statusBadge.add(statusLabel);
    
    // Position status badge
    JPanel badgeContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
    badgeContainer.setOpaque(false);
    badgeContainer.add(statusBadge);
    
    imagePanel.add(badgeContainer, BorderLayout.NORTH);
    contentPanel.add(imagePanel);
    contentPanel.add(Box.createVerticalStrut(8));
    
    // Middle: Enhanced Book details with better typography
    JPanel detailsPanel = new JPanel();
    detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
    detailsPanel.setOpaque(false);
    detailsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Title with modern typography
    String displayTitle = title;
    if (title.length() > 22) {
        displayTitle = title.substring(0, 19) + "...";
    }
    
    JLabel titleLabel = new JLabel("<html><div style='text-align: center;'>" + displayTitle + "</div></html>");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
    titleLabel.setForeground(new Color(33, 33, 33));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    titleLabel.setToolTipText(title);
    
    // Author with refined styling
    String displayAuthor = author;
    if (author.length() > 22) {
        displayAuthor = author.substring(0, 19) + "...";
    }
    
    JLabel authorLabel = new JLabel("<html><div style='text-align: center;'>by " + displayAuthor + "</div></html>");
    authorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
    authorLabel.setForeground(new Color(117, 117, 117));
    authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    authorLabel.setToolTipText(author);
    
    // ISBN with subtle styling
    JLabel isbnLabel = new JLabel("<html><div style='text-align: center;'>ISBN: " + isbn + "</div></html>");
    isbnLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    isbnLabel.setForeground(new Color(158, 158, 158));
    isbnLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    detailsPanel.add(titleLabel);
    detailsPanel.add(Box.createVerticalStrut(4));
    detailsPanel.add(authorLabel);
    detailsPanel.add(Box.createVerticalStrut(4));
    detailsPanel.add(isbnLabel);
    contentPanel.add(detailsPanel);
    
    // Enhanced Borrower info section
    if (!available && borrower != null) {
        JPanel borrowInfoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(255, 245, 245));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                
                g2d.setColor(new Color(255, 235, 235));
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 6, 6);
                
                g2d.dispose();
            }
        };
        borrowInfoPanel.setLayout(new BoxLayout(borrowInfoPanel, BoxLayout.Y_AXIS));
        borrowInfoPanel.setOpaque(false);
        borrowInfoPanel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        borrowInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        borrowInfoPanel.setMaximumSize(new Dimension(180, 45));
        
        String displayBorrower = borrower;
        if (borrower.length() > 18) {
            displayBorrower = borrower.substring(0, 15) + "...";
        }
        
        JLabel borrowerLabel = new JLabel("<html><div style='text-align: center;'><b>📋 " + displayBorrower + "</b></div></html>");
        borrowerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        borrowerLabel.setForeground(new Color(183, 28, 28));
        borrowerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        borrowerLabel.setToolTipText(borrower);
        
        JLabel dateLabel = new JLabel("<html><div style='text-align: center;'>📅 " + borrowDate + "</div></html>");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        dateLabel.setForeground(new Color(183, 28, 28));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        borrowInfoPanel.add(borrowerLabel);
        borrowInfoPanel.add(dateLabel);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(borrowInfoPanel);
    }
    
    // Bottom: Modern action button with enhanced styling
JButton actionButton;
if (available) {
    actionButton = new JButton("Borrow Book") {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isPressed()) {
                g2d.setColor(new Color(25, 118, 210));
            } else if (getModel().isRollover()) {
                g2d.setColor(new Color(30, 136, 229));
            } else {
                g2d.setColor(new Color(33, 150, 243));
            }
            
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            
            // Text
            g2d.setColor(Color.WHITE);
            g2d.setFont(getFont());
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 2;
            g2d.drawString(getText(), x, y);
            
            g2d.dispose();
        }
    };
    
   actionButton.addActionListener((ActionEvent e) -> {
    JDialog borrowDialog = new JDialog(this, "Borrow Book - Member Information", true);
    borrowDialog.setSize(600, 400);
    borrowDialog.setLocationRelativeTo(this);

    JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
    dialogPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 10, 10));

    JLabel nameLabel = new JLabel("Member Name:");
    JLabel idLabel = new JLabel("School ID:");
    JLabel imageLabel = new JLabel("Member Image:");

    JTextField nameField = new JTextField();
    JTextField idField = new JTextField();

    // Image selection components
    JPanel imageSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    JButton selectImageButton = new JButton("Choose Image");
    JLabel selectedImageLabel = new JLabel("No image selected");
    selectedImageLabel.setForeground(Color.GRAY);

    imageSelectPanel.add(selectImageButton);
    imageSelectPanel.add(Box.createHorizontalStrut(10));
    imageSelectPanel.add(selectedImageLabel);

    // Image preview panel
    JPanel imagePreviewPanel = new JPanel(new BorderLayout());
    imagePreviewPanel.setBorder(BorderFactory.createTitledBorder("Image Preview"));
    imagePreviewPanel.setPreferredSize(new Dimension(200, 250));

    JLabel imagePreviewLabel = new JLabel("No image selected", JLabel.CENTER);
    imagePreviewLabel.setPreferredSize(new Dimension(180, 220));
    imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    imagePreviewLabel.setOpaque(true);
    imagePreviewLabel.setBackground(Color.WHITE);

    imagePreviewPanel.add(imagePreviewLabel, BorderLayout.CENTER);

    final String[] selectedImagePath = {null};

    selectImageButton.addActionListener(ev -> {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Member Image");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png", "gif", "bmp");
        fileChooser.setFileFilter(imageFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showOpenDialog(borrowDialog);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath[0] = selectedFile.getAbsolutePath();
            selectedImageLabel.setText(selectedFile.getName());
            selectedImageLabel.setForeground(Color.BLACK);

            try {
                ImageIcon originalIcon = new ImageIcon(selectedImagePath[0]);
                Image originalImage = originalIcon.getImage();

                int maxWidth = 180;
                int maxHeight = 220;
                int originalWidth = originalImage.getWidth(null);
                int originalHeight = originalImage.getHeight(null);

                double scaleX = (double) maxWidth / originalWidth;
                double scaleY = (double) maxHeight / originalHeight;
                double scale = Math.min(scaleX, scaleY);

                int scaledWidth = (int) (originalWidth * scale);
                int scaledHeight = (int) (originalHeight * scale);

                Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);

                imagePreviewLabel.setIcon(scaledIcon);
                imagePreviewLabel.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(borrowDialog,
                        "Error loading image: " + ex.getMessage(),
                        "Image Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    });

    Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
    nameLabel.setFont(labelFont);
    idLabel.setFont(labelFont);
    imageLabel.setFont(labelFont);

    fieldsPanel.add(nameLabel);
    fieldsPanel.add(nameField);
    fieldsPanel.add(idLabel);
    fieldsPanel.add(idField);
    fieldsPanel.add(imageLabel);
    fieldsPanel.add(imageSelectPanel);

    mainPanel.add(fieldsPanel, BorderLayout.CENTER);
    mainPanel.add(imagePreviewPanel, BorderLayout.EAST);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelButton = new JButton("Cancel");
    JButton borrowButton = new JButton("Borrow");

    cancelButton.setFocusPainted(false);
    borrowButton.setFocusPainted(false);

    cancelButton.setBackground(new Color(220, 53, 69));
    cancelButton.setForeground(Color.WHITE);
    borrowButton.setBackground(new Color(0, 123, 255));
    borrowButton.setForeground(Color.WHITE);

    buttonPanel.add(cancelButton);
    buttonPanel.add(borrowButton);

    cancelButton.addActionListener(ev -> borrowDialog.dispose());

    borrowButton.addActionListener(ev -> {
    String memberName = nameField.getText().trim();
    String schoolId = idField.getText().trim();
    

    if (memberName.isEmpty() || schoolId.isEmpty()) {
        JOptionPane.showMessageDialog(borrowDialog, "⚠️ Please fill all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
        // 1. Update book availability (you must define 'title' or get selected book)
        PreparedStatement updateStmt = conn.prepareStatement(
            "UPDATE books SET available = 0 WHERE title = ?"
        );
        updateStmt.setString(1, title); // Ensure 'title' is defined from selected book
        updateStmt.executeUpdate();

        // 2. Insert borrower record
        PreparedStatement insertStmt = conn.prepareStatement(
            "INSERT INTO borrowers (member_name, school_id, member_image, book_title, borrow_date) VALUES (?, ?, ?, ?, ?)"
        );
        insertStmt.setString(1, memberName);
        insertStmt.setString(2, schoolId);
        insertStmt.setString(3, selectedImagePath[0]); // Can be null
        insertStmt.setString(4, title);
        insertStmt.setString(5, new java.sql.Date(System.currentTimeMillis()).toString());
        insertStmt.executeUpdate();

        JOptionPane.showMessageDialog(borrowDialog, String.format(
            "✅ Book borrowed successfully!\n\nMember: %s\nID: %s\nBook: %s", 
            memberName, schoolId, title
        ), "Success", JOptionPane.INFORMATION_MESSAGE);

        borrowDialog.dispose(); // Close the dialog after success

        // Refresh books if needed
        loadModernBookCards((JPanel)card.getParent()); // Make sure 'card' is accessible

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(borrowDialog, 
            "❌ Error borrowing book: " + ex.getMessage(),
            "Database Error", JOptionPane.ERROR_MESSAGE);
    }
});


    dialogPanel.add(mainPanel, BorderLayout.CENTER);
    dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

    borrowDialog.add(dialogPanel);
    borrowDialog.setVisible(true);
});

    } else {
        actionButton = new JButton("Return Book") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(194, 24, 91));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(216, 27, 96));
                } else {
                    g2d.setColor(new Color(233, 30, 99));
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), x, y);
                
                g2d.dispose();
            }
        };
        
        actionButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, 
                    "Return book: " + title + "\nBorrowed by: " + borrower,
                    "Return Book", JOptionPane.YES_NO_OPTION);
                    
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                    PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE books SET available=1 WHERE title=?");
                    updateStmt.setString(1, title);
                    updateStmt.executeUpdate();
                    
                    PreparedStatement deleteStmt = conn.prepareStatement(
                            "DELETE FROM borrowers WHERE book_title=?");
                    deleteStmt.setString(1, title);
                    deleteStmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(null, "✅ Book returned successfully!");
                    loadModernBookCards((JPanel)card.getParent());
                    
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "❌ Error returning book: " + ex.getMessage());
                }
            }
        });
    }
    
    // Enhanced button styling
    actionButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
    actionButton.setForeground(Color.WHITE);
    actionButton.setFocusPainted(false);
    actionButton.setBorderPainted(false);
    actionButton.setContentAreaFilled(false);
    actionButton.setOpaque(false);
    actionButton.setPreferredSize(new Dimension(200, 32));
    actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    contentPanel.add(Box.createVerticalStrut(8));
    contentPanel.add(actionButton);
    card.add(contentPanel, BorderLayout.CENTER);
    
    // Add hover effect to the entire card
    card.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 8, 5),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(100, 150, 255), 2, true),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    )
            ));
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(5, 5, 8, 5),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    )
            ));
        }
    });
    
    return card;
}
    private void filterModernBookCards(JPanel cardContainer, String searchText, String filterOption) {
    cardContainer.removeAll(); // clear old cards
    
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT b.title, b.author, b.isbn, b.available, b.image_path, ") // Added missing b.image_path
                    .append("br.member_name, br.borrow_date ")
                    .append("FROM books b ")
                    .append("LEFT JOIN borrowers br ON b.title = br.book_title ");
        
        // Handle empty search text
        if (searchText != null && !searchText.trim().isEmpty()) {
            queryBuilder.append("WHERE (b.title LIKE ? OR b.author LIKE ? OR b.isbn LIKE ?) ");
        } else {
            queryBuilder.append("WHERE 1=1 "); // Always true condition for filter-only queries
        }
        
        // Add filter conditions
        if ("Available Only".equals(filterOption)) {
            queryBuilder.append("AND b.available = 1 ");
        } else if ("Borrowed Only".equals(filterOption)) {
            queryBuilder.append("AND b.available = 0 ");
        }
        
        PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString());
        
        // Set parameters only if search text exists
        if (searchText != null && !searchText.trim().isEmpty()) {
            String searchPattern = "%" + searchText.trim() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
        }
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            String title = rs.getString("title");
            String author = rs.getString("author");
            String isbn = rs.getString("isbn");
            boolean available = rs.getInt("available") == 1;
            String imagePath = rs.getString("image_path"); // Now properly selected
            String borrower = rs.getString("member_name");
            String borrowDate = rs.getString("borrow_date");
            
            JPanel card = createModernBookCard(title, author, isbn, available, borrower, borrowDate, imagePath);
            cardContainer.add(card);
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error filtering books: " + e.getMessage());
    }
    
    cardContainer.revalidate();
    cardContainer.repaint();
}

    private void showAddBookDialog() {
    JDialog addBookDialog = new JDialog(this, "Add New Book", true);
    addBookDialog.setSize(600, 400);
    addBookDialog.setLocationRelativeTo(this);
    
    JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
    dialogPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
    // Main content panel
    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    
    // Left panel for form fields
    JPanel fieldsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
    
    JLabel titleLabel = new JLabel("Book Title:");
    JLabel authorLabel = new JLabel("Book Author:");
    JLabel isbnLabel = new JLabel("Book ISBN:");
    JLabel imageLabel = new JLabel("Book Cover:");
    
    JTextField bookTitleField = new JTextField();
    JTextField bookAuthorField = new JTextField();
    JTextField bookIsbnField = new JTextField();
    
    // Image selection components
    JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    JButton selectImageButton = new JButton("Choose Image");
    JLabel selectedImageLabel = new JLabel("No image selected");
    selectedImageLabel.setForeground(Color.GRAY);
    
    imagePanel.add(selectImageButton);
    imagePanel.add(Box.createHorizontalStrut(10));
    imagePanel.add(selectedImageLabel);
    
    // Image preview panel (right side)
    JPanel imagePreviewPanel = new JPanel(new BorderLayout());
    imagePreviewPanel.setBorder(BorderFactory.createTitledBorder("Image Preview"));
    imagePreviewPanel.setPreferredSize(new Dimension(200, 250));
    
    JLabel imagePreviewLabel = new JLabel("No image selected", JLabel.CENTER);
    imagePreviewLabel.setPreferredSize(new Dimension(180, 220));
    imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    imagePreviewLabel.setOpaque(true);
    imagePreviewLabel.setBackground(Color.WHITE);
    
    imagePreviewPanel.add(imagePreviewLabel, BorderLayout.CENTER);
    
    // Store the selected image path
    final String[] selectedImagePath = {null};
    
    // Image selection logic
    selectImageButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Book Cover Image");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            
            // Set file filters for images
            FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
                    "Image files", "jpg", "jpeg", "png", "gif", "bmp");
            fileChooser.setFileFilter(imageFilter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            
            int result = fileChooser.showOpenDialog(addBookDialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                selectedImagePath[0] = selectedFile.getAbsolutePath();
                selectedImageLabel.setText(selectedFile.getName());
                selectedImageLabel.setForeground(Color.BLACK);
                
                // Show image preview
                try {
                    ImageIcon originalIcon = new ImageIcon(selectedImagePath[0]);
                    Image originalImage = originalIcon.getImage();
                    
                    // Scale image to fit preview
                    int maxWidth = 180;
                    int maxHeight = 220;
                    int originalWidth = originalImage.getWidth(null);
                    int originalHeight = originalImage.getHeight(null);
                    
                    double scaleX = (double) maxWidth / originalWidth;
                    double scaleY = (double) maxHeight / originalHeight;
                    double scale = Math.min(scaleX, scaleY);
                    
                    int scaledWidth = (int) (originalWidth * scale);
                    int scaledHeight = (int) (originalHeight * scale);
                    
                    Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);
                    
                    imagePreviewLabel.setIcon(scaledIcon);
                    imagePreviewLabel.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(addBookDialog,
                            "Error loading image: " + ex.getMessage(),
                            "Image Error",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    });
    
    Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
    titleLabel.setFont(labelFont);
    authorLabel.setFont(labelFont);
    isbnLabel.setFont(labelFont);
    imageLabel.setFont(labelFont);
    
    fieldsPanel.add(titleLabel);
    fieldsPanel.add(bookTitleField);
    fieldsPanel.add(authorLabel);
    fieldsPanel.add(bookAuthorField);
    fieldsPanel.add(isbnLabel);
    fieldsPanel.add(bookIsbnField);
    fieldsPanel.add(imageLabel);
    fieldsPanel.add(imagePanel);
    
    mainPanel.add(fieldsPanel, BorderLayout.CENTER);
    mainPanel.add(imagePreviewPanel, BorderLayout.EAST);
    
    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelButton = new JButton("Cancel");
    JButton saveBookButton = new JButton("Save Book");
    
    cancelButton.setFocusPainted(false);
    saveBookButton.setFocusPainted(false);
    
    cancelButton.setBackground(new Color(220, 53, 69)); // Bootstrap Danger
    cancelButton.setForeground(Color.WHITE);
    saveBookButton.setBackground(new Color(40, 167, 69)); // Bootstrap Success
    saveBookButton.setForeground(Color.WHITE);
    
    buttonPanel.add(cancelButton);
    buttonPanel.add(saveBookButton);
    
    saveBookButton.addActionListener((ActionEvent e) -> {
        String title1 = bookTitleField.getText().trim();
        String author = bookAuthorField.getText().trim();
        String isbn = bookIsbnField.getText().trim();
        
        if (title1.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            JOptionPane.showMessageDialog(addBookDialog, "Please fill all required fields.");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            PreparedStatement stmt;
            
            if (selectedImagePath[0] != null) {
                // If image is selected, include it in the database
                stmt = conn.prepareStatement("INSERT INTO books (title, author, isbn, available, image_path) VALUES (?, ?, ?, ?, ?)");
                stmt.setString(1, title1);
                stmt.setString(2, author);
                stmt.setString(3, isbn);
                stmt.setInt(4, 1);
                stmt.setString(5, selectedImagePath[0]);
            } else {
                // No image selected
                stmt = conn.prepareStatement("INSERT INTO books (title, author, isbn, available) VALUES (?, ?, ?, ?)");
                stmt.setString(1, title1);
                stmt.setString(2, author);
                stmt.setString(3, isbn);
                stmt.setInt(4, 1);
            }
            
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(addBookDialog, "Book added successfully.");
            loadBooks();
            addBookDialog.dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(addBookDialog, "Database error: " + ex.getMessage());
        }
    });
    
    cancelButton.addActionListener(e -> addBookDialog.dispose());
    
    dialogPanel.add(mainPanel, BorderLayout.CENTER);
    dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
    
    addBookDialog.add(dialogPanel);
    addBookDialog.setVisible(true);
}
    

    private void loadBooks() {
        JPanel container = findBookCardsContainer();
        if (container != null) {
            loadModernBookCards(container);
        }
    }

    private void showRemoveBookDialog() {
    JDialog dialog = new JDialog(this, "🗑️ Remove Book", true);
    dialog.setSize(560, 460);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(10, 10));

    // Header Label
    JLabel header = new JLabel("📚 Select book(s) to remove");
    header.setFont(new Font("Segoe UI", Font.BOLD, 16));
    header.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

    // Search bar
    JTextField searchField = new JTextField();
    searchField.setPreferredSize(new Dimension(200, 50));
    searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    searchField.setBorder(BorderFactory.createTitledBorder("Search..."));

    JPanel searchPanel = new JPanel(new BorderLayout());
    searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 5, 15));
    searchPanel.add(searchField, BorderLayout.CENTER);

    // Table with book data
    DefaultTableModel removeModel = new DefaultTableModel(new Object[]{"Title", "Author", "ISBN"}, 0);
    JTable removeTable = new JTable(removeModel);
    removeTable.setRowHeight(24);
    removeTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    removeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    removeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    removeTable.setSelectionBackground(new Color(240, 240, 240));
    removeTable.setSelectionForeground(Color.BLACK);
    removeTable.setGridColor(new Color(220, 220, 220));

    JScrollPane scrollPane = new JScrollPane(removeTable);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
    scrollPane.getViewport().setBackground(Color.WHITE);

    // Row sorter for search functionality
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(removeModel);
    removeTable.setRowSorter(sorter);

    searchField.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) { filter(); }
        @Override
        public void removeUpdate(DocumentEvent e) { filter(); }
        @Override
        public void changedUpdate(DocumentEvent e) { filter(); }

        private void filter() {
            String text = searchField.getText().trim();
            if (text.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
            }
        }
    });

    // Delete button with styling
    JButton deleteButton = new JButton("🗑️ Delete Selected");
    deleteButton.setFocusPainted(false);
    deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
    deleteButton.setBackground(new Color(198, 40, 40));
    deleteButton.setForeground(Color.WHITE);
    deleteButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    deleteButton.addActionListener((ActionEvent e) -> {
        int[] selectedRows = removeTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(dialog, "Please select at least one book to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to delete selected book(s)?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                for (int row : selectedRows) {
                    int modelRow = removeTable.convertRowIndexToModel(row); // important for sorting
                    String title = (String) removeModel.getValueAt(modelRow, 0);
                    PreparedStatement stmt = conn.prepareStatement("DELETE FROM books WHERE title=?");
                    stmt.setString(1, title);
                    stmt.executeUpdate();
                }
                JOptionPane.showMessageDialog(dialog, "Book(s) deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadBooks();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error deleting book(s): " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    bottomPanel.add(deleteButton);

    // Load books
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT title, author, isbn FROM books");
        while (rs.next()) {
            removeModel.addRow(new Object[]{
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn")
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(dialog, "Failed to load books: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Compose dialog
    JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
    centerPanel.add(searchPanel, BorderLayout.NORTH);
    centerPanel.add(scrollPane, BorderLayout.CENTER);

    dialog.add(header, BorderLayout.NORTH);
    dialog.add(centerPanel, BorderLayout.CENTER);
    dialog.add(bottomPanel, BorderLayout.SOUTH);
    dialog.getContentPane().setBackground(Color.WHITE);

    dialog.setVisible(true);
}

    private void loadBorrowedBooksByBorrower(JPanel container) {
    container.removeAll();
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
    container.setOpaque(false);
    container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
        // Query to get unique borrowers with their info and book count
        String borrowerQuery = "SELECT br.member_name, br.school_id, br.member_image, COUNT(br.book_title) as book_count " +
                              "FROM borrowers br " +
                              "GROUP BY br.member_name, br.school_id, br.member_image " +
                              "ORDER BY br.member_name";
        
        PreparedStatement stmt = conn.prepareStatement(borrowerQuery);
        ResultSet rs = stmt.executeQuery();
        
        // Create a panel to hold borrower cards in a grid layout
        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        cardsPanel.setOpaque(false);
        
        while (rs.next()) {
            String memberName = rs.getString("member_name");
            String schoolId = rs.getString("school_id");
            String memberImage = rs.getString("member_image");
            int bookCount = rs.getInt("book_count");
            
            // Create borrower card
            JPanel borrowerCard = createBorrowerCard(memberName, schoolId, memberImage, bookCount, container);
            cardsPanel.add(borrowerCard);
        }
        
        container.add(cardsPanel);
        container.revalidate();
        container.repaint();
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading borrowers: " + e.getMessage());
    }
}

private JPanel createBorrowerCard(String memberName, String schoolId, String memberImage, int bookCount, JPanel parentContainer) {
    JPanel card = new JPanel();
    card.setLayout(new BorderLayout());
    card.setPreferredSize(new Dimension(200, 250));
    card.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    card.setBackground(Color.WHITE);
    card.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    // Image panel
    JPanel imagePanel = new JPanel();
    imagePanel.setLayout(new BorderLayout());
    imagePanel.setPreferredSize(new Dimension(150, 120));
    imagePanel.setBackground(new Color(245, 245, 245));
    
    JLabel imageLabel = new JLabel();
    imageLabel.setHorizontalAlignment(JLabel.CENTER);
    
    // Load member image or use default
    if (memberImage != null && !memberImage.isEmpty()) {
        try {
            ImageIcon originalIcon = new ImageIcon(memberImage);
            Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            imageLabel.setText("👤");
            imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        }
    } else {
        imageLabel.setText("👤");
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 40));
    }
    
    imagePanel.add(imageLabel, BorderLayout.CENTER);
    card.add(imagePanel, BorderLayout.NORTH);
    
    // Info panel
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setOpaque(false);
    infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
    
    // Member name
    JLabel nameLabel = new JLabel(memberName);
    nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    nameLabel.setForeground(new Color(40, 40, 40));
    nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // School ID
    JLabel idLabel = new JLabel("ID: " + schoolId);
    idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    idLabel.setForeground(new Color(100, 100, 100));
    idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Book count
    JLabel bookCountLabel = new JLabel(bookCount + " book(s) borrowed");
    bookCountLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
    bookCountLabel.setForeground(new Color(0, 120, 215));
    bookCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    infoPanel.add(nameLabel);
    infoPanel.add(Box.createVerticalStrut(5));
    infoPanel.add(idLabel);
    infoPanel.add(Box.createVerticalStrut(5));
    infoPanel.add(bookCountLabel);
    
    card.add(infoPanel, BorderLayout.CENTER);
    
    // Hover effects
    card.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 215), 2),
                BorderFactory.createEmptyBorder(9, 9, 9, 9)
            ));
            card.setBackground(new Color(248, 252, 255));
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            card.setBackground(Color.WHITE);
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
            // Show borrowed books for this member
            showBorrowedBooksForMember(memberName, parentContainer);
        }
    });
    
    return card;
}

private void showBorrowedBooksForMember(String memberName, JPanel container) {
    container.removeAll();
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
    container.setOpaque(false);
    container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
    // Combined header panel with back button and member name in one row
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setOpaque(false);
    headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Reduced bottom padding
    
    // Left side - Member info
    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    leftPanel.setOpaque(false);
    
    JLabel iconLabel = new JLabel("👤 ");
    iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    
    JLabel memberLabel = new JLabel("Books borrowed by: " + memberName);
    memberLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Slightly smaller font
    memberLabel.setForeground(new Color(40, 40, 40));
    
    leftPanel.add(iconLabel);
    leftPanel.add(memberLabel);
    
    // Right side - Back button
    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    rightPanel.setOpaque(false);
    
    JButton backButton = createModernBackButton();
    backButton.addActionListener(e -> loadBorrowedBooksByBorrower(container));
    rightPanel.add(backButton);
    
    // Add both sides to header
    headerPanel.add(leftPanel, BorderLayout.WEST);
    headerPanel.add(rightPanel, BorderLayout.EAST);
    
    container.add(headerPanel);
    
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
        String query = "SELECT b.title, b.author, b.isbn, b.image_path, br.borrow_date " +
                       "FROM borrowers br JOIN books b ON br.book_title = b.title " +
                       "WHERE br.member_name = ? " +
                       "ORDER BY br.borrow_date DESC";
        
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, memberName);
        ResultSet rs = stmt.executeQuery();
        
        // Books panel - with better layout control
JPanel booksPanel = new JPanel();
booksPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
booksPanel.setOpaque(false);

// Force the panel to stick to the top by setting alignment
booksPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
booksPanel.setAlignmentY(Component.TOP_ALIGNMENT);

boolean hasBooks = false;
while (rs.next()) {
    hasBooks = true;
    
    String title = rs.getString("title");
    String author = rs.getString("author");
    String isbn = rs.getString("isbn");
    String borrowDate = rs.getString("borrow_date");
    String imagePath = rs.getString("image_path");
    
    JPanel bookCard = createModernBookCard(title, author, isbn, false, memberName, borrowDate, imagePath);
    booksPanel.add(bookCard);
}

// Check if no books were found (all returned)
if (!hasBooks) {
    loadBorrowedBooksByBorrower(container);
    return;
}

// Wrap the books panel in another panel to control positioning
JPanel wrapperPanel = new JPanel(new BorderLayout());
wrapperPanel.setOpaque(false);
wrapperPanel.add(booksPanel, BorderLayout.NORTH); // Force to top

container.add(wrapperPanel);
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error loading books for member: " + e.getMessage());
    }
    
    container.revalidate();
    container.repaint();
}
// Helper method to create modern back button
private JButton createModernBackButton() {
    JButton backButton = new JButton();
    backButton.setText("← Back to Borrowers");
    backButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    backButton.setForeground(Color.WHITE);
    backButton.setBackground(new Color(108, 117, 125)); // Bootstrap secondary color
    backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    backButton.setFocusPainted(false);
    backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    backButton.setOpaque(true);
    
    // Rounded corners effect
    backButton.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(108, 117, 125), 1, true),
        BorderFactory.createEmptyBorder(8, 16, 8, 16)
    ));
    
    // Hover effect
    backButton.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            backButton.setBackground(new Color(90, 98, 104)); // Darker on hover
        }
        
        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            backButton.setBackground(new Color(108, 117, 125));
        }
    });
    
    return backButton;
}
    public static void main(String[] args) {
        try {
            Class<?> lafClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            LookAndFeel laf = (LookAndFeel) lafClass.getDeclaredConstructor().newInstance();
            UIManager.setLookAndFeel(laf);
        } catch (Exception e) {
            // FlatLaf not available; fall back to default look and feel
        }
        SwingUtilities.invokeLater(LogIn::new);
    }

    private void LogIn() {
        SwingUtilities.invokeLater(LogIn::new);
    }
}
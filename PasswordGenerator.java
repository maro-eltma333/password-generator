import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;
import java.util.Random;
import java.awt.geom.RoundRectangle2D;
import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.Timer;
import javax.swing.KeyStroke;

public class PasswordGenerator extends JFrame {
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:,.<>?";

    private JTextField lengthField;
    private JCheckBox lowercaseCheck;
    private JCheckBox uppercaseCheck;
    private JCheckBox numbersCheck;
    private JCheckBox specialCheck;
    private JTextField passwordField;
    private JButton generateButton;
    private JButton copyButton;
    private JButton closeButton;
    private JProgressBar strengthBar;
    private JLabel strengthLabel;
    private JLabel copyStatusLabel;

    public PasswordGenerator() {
        setTitle("Password Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        setAlwaysOnTop(true);
        setVisible(true);

        // Add window dragging functionality
        FrameDragListener frameDragListener = new FrameDragListener(this);
        addMouseListener(frameDragListener);
        addMouseMotionListener(frameDragListener);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color color1 = new Color(25, 25, 25);
                Color color2 = new Color(15, 15, 15);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Top buttons panel
        JPanel topButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topButtonsPanel.setOpaque(false);
        
        closeButton = new JButton();
        try {
            Image img = ImageIO.read(new File("close.png"));
            img = img.getScaledInstance(35, 32, Image.SCALE_SMOOTH);  // Increased size to 25x25
            closeButton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            closeButton.setText("✕");
            closeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        }
        closeButton.setPreferredSize(new Dimension(40, 40));  // Increased button size to 40x40
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> System.exit(0));
        topButtonsPanel.add(closeButton);
        mainPanel.add(topButtonsPanel);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Password Generator");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(255, 255, 255));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel);

        mainPanel.add(Box.createVerticalStrut(40));

        JPanel lengthPanel = createGlassPanel();
        lengthPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JLabel lengthLabel = new JLabel("Password Length (8-32):");
        lengthLabel.setForeground(new Color(255, 255, 255));
        lengthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lengthField = new JTextField("12", 3);
        lengthField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lengthField.setHorizontalAlignment(JTextField.CENTER);
        lengthField.setBackground(new Color(35, 35, 35));
        lengthField.setForeground(new Color(255, 255, 255));
        lengthField.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        lengthField.setCaretColor(new Color(52, 152, 219));
        lengthPanel.add(lengthLabel);
        lengthPanel.add(lengthField);
        mainPanel.add(lengthPanel);

        mainPanel.add(Box.createVerticalStrut(30));

        JPanel optionsPanel = createGlassPanel();
        optionsPanel.setLayout(new GridLayout(4, 1, 15, 15));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        lowercaseCheck = createStyledCheckbox("Lowercase Letters", true);
        uppercaseCheck = createStyledCheckbox("Uppercase Letters", true);
        numbersCheck = createStyledCheckbox("Numbers", true);
        specialCheck = createStyledCheckbox("Special Characters", true);
        
        optionsPanel.add(lowercaseCheck);
        optionsPanel.add(uppercaseCheck);
        optionsPanel.add(numbersCheck);
        optionsPanel.add(specialCheck);
        
        mainPanel.add(optionsPanel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Simple button panel with modern buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        
        generateButton = new JButton("Generate Password");
        generateButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        generateButton.setBackground(new Color(52, 152, 219));
        generateButton.setForeground(Color.WHITE);
        generateButton.setPreferredSize(new Dimension(180, 40));
        generateButton.setOpaque(true);
        generateButton.setBorderPainted(false);
        generateButton.setFocusPainted(false);
        generateButton.addActionListener(e -> generatePassword());
        
        copyButton = new JButton("Copy Password");
        copyButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        copyButton.setBackground(new Color(46, 204, 113));
        copyButton.setForeground(Color.WHITE);
        copyButton.setPreferredSize(new Dimension(180, 40));
        copyButton.setOpaque(true);
        copyButton.setBorderPainted(false);
        copyButton.setFocusPainted(false);
        copyButton.addActionListener(e -> copyToClipboard());
        
        buttonPanel.add(generateButton);
        buttonPanel.add(copyButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Password field panel
        JPanel passwordPanel = createGlassPanel();
        passwordPanel.setLayout(new BorderLayout(10, 10));
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        passwordField = new JTextField();
        passwordField.setEditable(false);
        passwordField.setFont(new Font("Consolas", Font.BOLD, 18));
        passwordField.setHorizontalAlignment(JTextField.CENTER);
        passwordField.setBackground(new Color(35, 35, 35));
        passwordField.setForeground(new Color(52, 152, 219));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        
        // Strength indicator
        JPanel strengthPanel = new JPanel(new BorderLayout(5, 0));
        strengthPanel.setOpaque(false);
        
        strengthLabel = new JLabel("Password Strength: ");
        strengthLabel.setForeground(Color.WHITE);
        strengthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        strengthPanel.add(strengthLabel, BorderLayout.WEST);
        
        strengthBar = new JProgressBar(0, 100);
        strengthBar.setStringPainted(true);
        strengthBar.setString("No Password");
        strengthBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        strengthBar.setBackground(new Color(60, 60, 60));
        strengthBar.setForeground(new Color(46, 204, 113));
        strengthBar.setBorderPainted(false);
        strengthPanel.add(strengthBar, BorderLayout.CENTER);
        
        passwordPanel.add(strengthPanel, BorderLayout.SOUTH);
        mainPanel.add(passwordPanel);

        // Copy status label
        copyStatusLabel = new JLabel("");
        copyStatusLabel.setForeground(new Color(46, 204, 113));
        copyStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(copyStatusLabel);
        
        add(mainPanel);
    }

    private JPanel createGlassPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                Color color = new Color(35, 35, 35, 180);
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JCheckBox createStyledCheckbox(String text, boolean selected) {
        JCheckBox checkbox = new JCheckBox(text, selected) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                if (isSelected()) {
                    g2d.setColor(new Color(52, 152, 219));
                    g2d.fillRoundRect(0, 0, 20, 20, 5, 5);
                    g2d.setColor(Color.WHITE);
                    g2d.drawString("✓", 5, 15);
                } else {
                    g2d.setColor(new Color(60, 60, 60));
                    g2d.fillRoundRect(0, 0, 20, 20, 5, 5);
                }
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g2d.drawString(text, 30, 15);
            }
        };
        checkbox.setOpaque(false);
        checkbox.setFocusPainted(false);
        checkbox.setIcon(new ImageIcon());
        checkbox.setSelectedIcon(new ImageIcon());
        return checkbox;
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                if (getModel().isPressed()) {
                    g2d.setColor(getBackground().darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(getBackground().brighter());
                } else {
                    g2d.setColor(getBackground());
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                g2d.drawString(getText(), x, y);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 40));
        button.setMinimumSize(new Dimension(180, 40));
        return button;
    }

    private void showCustomDialog(String message, String title, int messageType) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);
        dialog.setShape(new RoundRectangle2D.Double(0, 0, 400, 200, 20, 20));
        
        JPanel dialogPanel = new JPanel(new BorderLayout(20, 20));
        dialogPanel.setBackground(new Color(35, 35, 35));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Message panel
        JPanel messagePanel = new JPanel(new BorderLayout(10, 10));
        messagePanel.setOpaque(false);
        
        // Icon based on message type
        JLabel iconLabel = new JLabel();
        if (messageType == JOptionPane.ERROR_MESSAGE) {
            iconLabel.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
        } else if (messageType == JOptionPane.INFORMATION_MESSAGE) {
            iconLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
        }
        messagePanel.add(iconLabel, BorderLayout.WEST);

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageLabel.setForeground(new Color(255, 255, 255));
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        dialogPanel.add(messagePanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        
        JButton okButton = new JButton("OK") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(41, 128, 185));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(52, 152, 219));
                } else {
                    g2d.setColor(new Color(52, 152, 219));
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                g2d.drawString(getText(), x, y);
            }
        };
        
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setForeground(Color.WHITE);
        okButton.setPreferredSize(new Dimension(100, 35));
        okButton.setBorderPainted(false);
        okButton.setContentAreaFilled(false);
        okButton.setFocusPainted(false);
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(dialogPanel);
        dialog.setVisible(true);
    }

    private void generatePassword() {
        try {
            int length = Integer.parseInt(lengthField.getText());
            if (length < 8 || length > 32) {
                showCustomDialog("Password length must be between 8 and 32 characters", "Invalid Length", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean useLowercase = lowercaseCheck.isSelected();
            boolean useUppercase = uppercaseCheck.isSelected();
            boolean useNumbers = numbersCheck.isSelected();
            boolean useSpecial = specialCheck.isSelected();

            if (!(useLowercase || useUppercase || useNumbers || useSpecial)) {
                showCustomDialog("Please select at least one character type", "No Options Selected", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String password = generatePassword(length, useLowercase, useUppercase, useNumbers, useSpecial);
            passwordField.setText(password);
            updatePasswordStrength(password);

        } catch (NumberFormatException e) {
            showCustomDialog("Please enter a valid number for password length", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void copyToClipboard() {
        String password = passwordField.getText();
        if (!password.isEmpty()) {
            StringSelection selection = new StringSelection(password);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            
            // Show copy success message
            copyStatusLabel.setText("Password copied to clipboard!");
            Timer timer = new Timer(2000, e -> copyStatusLabel.setText(""));
            timer.setRepeats(false);
            timer.start();
        }
    }

    private String generatePassword(int length, boolean useLowercase, boolean useUppercase, 
                                  boolean useNumbers, boolean useSpecial) {
        StringBuilder charSet = new StringBuilder();
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        
        if (useLowercase) charSet.append(LOWERCASE);
        if (useUppercase) charSet.append(UPPERCASE);
        if (useNumbers) charSet.append(NUMBERS);
        if (useSpecial) charSet.append(SPECIAL_CHARS);
        
        if (useLowercase) password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        if (useUppercase) password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        if (useNumbers) password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        if (useSpecial) password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));
        
        while (password.length() < length) {
            password.append(charSet.charAt(random.nextInt(charSet.length())));
        }
        
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = passwordArray[index];
            passwordArray[index] = passwordArray[i];
            passwordArray[i] = temp;
        }
        
        return new String(passwordArray);
    }

    private void updatePasswordStrength(String password) {
        int score = 0;
        
        // Length check
        if (password.length() >= 8) score += 10;
        if (password.length() >= 12) score += 10;
        if (password.length() >= 16) score += 10;
        if (password.length() >= 20) score += 10;
        
        // Character variety check
        if (password.matches(".*[a-z].*")) score += 10;
        if (password.matches(".*[A-Z].*")) score += 10;
        if (password.matches(".*[0-9].*")) score += 10;
        if (password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{}|;:,.<>?].*")) score += 10;
        
        // Additional complexity checks
        if (password.matches(".*[a-z].*[A-Z].*[0-9].*")) score += 10;
        if (password.matches(".*[a-zA-Z].*[0-9].*[!@#$%^&*()\\-_=+\\[\\]{}|;:,.<>?].*")) score += 10;
        
        strengthBar.setValue(score);
        
        if (score < 30) {
            strengthBar.setForeground(new Color(231, 76, 60));  // Red
            strengthBar.setString("Weak");
        } else if (score < 60) {
            strengthBar.setForeground(new Color(230, 126, 34));  // Orange
            strengthBar.setString("Moderate");
        } else if (score < 80) {
            strengthBar.setForeground(new Color(241, 196, 15));  // Yellow
            strengthBar.setString("Strong");
        } else {
            strengthBar.setForeground(new Color(46, 204, 113));  // Green
            strengthBar.setString("Very Strong");
        }
    }

    // Add this new inner class for window dragging
    private class FrameDragListener extends MouseAdapter {
        private final JFrame frame;
        private Point mouseDownCompCoords = null;

        public FrameDragListener(JFrame frame) {
            this.frame = frame;
        }

        public void mouseReleased(MouseEvent e) {
            mouseDownCompCoords = null;
        }

        public void mousePressed(MouseEvent e) {
            mouseDownCompCoords = e.getPoint();
        }

        public void mouseDragged(MouseEvent e) {
            if (mouseDownCompCoords != null) {
                Point currCoords = e.getLocationOnScreen();
                frame.setLocation(currCoords.x - mouseDownCompCoords.x, 
                                currCoords.y - mouseDownCompCoords.y);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting Password Generator...");
        SwingUtilities.invokeLater(() -> {
            System.out.println("Creating PasswordGenerator instance...");
            new PasswordGenerator();
            System.out.println("PasswordGenerator instance created.");
        });
    }
} 
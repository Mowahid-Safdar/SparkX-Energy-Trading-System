
package sparkX.gui;

import javax.swing.*;
import java.awt.*;

import sparkX.data.MarketplaceData;
import sparkX.model.User;
import sparkX.model.Seller;
import sparkX.model.Buyer;

public class RegistrationScreen extends JFrame {

    private MarketplaceData marketplaceData;

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;

    private JComboBox<String> roleComboBox;

    private Color backgroundColor = new Color(16, 20, 24);
    private Color panelColor = new Color(27, 35, 43);
    private Color greenColor = new Color(0, 199, 129);
    private Color blueColor = new Color(49, 139, 255);
    private Color textColor = new Color(241, 245, 249);
    private Color mutedColor = new Color(154, 168, 181);

    private final int fieldWidth = 260;

    public RegistrationScreen(MarketplaceData marketplaceData) {

        this.marketplaceData = marketplaceData;

        setTitle("SparkX - Create Account");
        setSize(550, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(backgroundColor);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(backgroundColor);
        mainPanel.setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel();
        formPanel.setBackground(panelColor);
        formPanel.setPreferredSize(new Dimension(400, 510));
        formPanel.setBorder(
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        );

        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Create Your Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(
            "Join the SparkX energy marketplace"
        );
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(mutedColor);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(subtitleLabel);
        formPanel.add(Box.createVerticalStrut(30));

        // Full name
        formPanel.add(createLabel("Full Name"));
        nameField = createTextField();
        formPanel.add(nameField);

        formPanel.add(Box.createVerticalStrut(18));

        // Email
        formPanel.add(createLabel("Email Address"));
        emailField = createTextField();
        formPanel.add(emailField);

        formPanel.add(Box.createVerticalStrut(18));

        // Password
        formPanel.add(createLabel("Password"));
        passwordField = new JPasswordField();
        styleTextField(passwordField);
        formPanel.add(passwordField);

        formPanel.add(Box.createVerticalStrut(18));

        // Account role
        formPanel.add(createLabel("Account Type"));

        String[] roles = {
            "Select Account Type",
            "Seller",
            "Buyer"
        };

        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        roleComboBox.setBackground(panelColor);
        roleComboBox.setForeground(textColor);

        roleComboBox.setPreferredSize(
            new Dimension(fieldWidth, 42)
        );
        roleComboBox.setMinimumSize(
            new Dimension(fieldWidth, 42)
        );
        roleComboBox.setMaximumSize(
            new Dimension(fieldWidth, 42)
        );
        roleComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.add(roleComboBox);

        formPanel.add(Box.createVerticalStrut(30));

        // Register button
        JButton registerButton = new JButton("CREATE ACCOUNT");
        registerButton.setFont(
            new Font("Arial", Font.BOLD, 14)
        );
        registerButton.setBackground(greenColor);
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 45)
        );

        registerButton.addActionListener(e -> registerAccount());

        formPanel.add(registerButton);

        formPanel.add(Box.createVerticalStrut(15));

        // Back button
        JButton backButton = new JButton("Back to Welcome");
        backButton.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );
        backButton.setForeground(blueColor);
        backButton.setBackground(panelColor);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        backButton.addActionListener(e -> {
            new WelcomeScreen(marketplaceData);
            dispose();
        });

        formPanel.add(backButton);

        mainPanel.add(formPanel);

        add(mainPanel);

        setVisible(true);
    }

    // Creates form labels
    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(textColor);

        label.setPreferredSize(
            new Dimension(fieldWidth, 20)
        );
        label.setMinimumSize(
            new Dimension(fieldWidth, 20)
        );
        label.setMaximumSize(
            new Dimension(fieldWidth, 20)
        );

        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        return label;
    }

    // Creates standard text fields
    private JTextField createTextField() {

        JTextField field = new JTextField();
        styleTextField(field);

        return field;
    }

    // Applies consistent text field styling
    private void styleTextField(JTextField field) {

        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(backgroundColor);
        field.setForeground(textColor);
        field.setCaretColor(textColor);

        field.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mutedColor),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            )
        );

        field.setPreferredSize(
            new Dimension(fieldWidth, 42)
        );
        field.setMinimumSize(
            new Dimension(fieldWidth, 42)
        );
        field.setMaximumSize(
            new Dimension(fieldWidth, 42)
        );

        field.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    // Handles account registration
    private void registerAccount() {

        String fullName = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        String selectedRole =
            (String) roleComboBox.getSelectedItem();

        // Validate name
        if (fullName.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter your full name."
            );

            return;
        }

        // Validate email
        if (email.isEmpty()
                || !email.contains("@")
                || !email.contains(".")) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter a valid email address."
            );

            return;
        }

        // Validate password
        if (password.length() < 6) {

            JOptionPane.showMessageDialog(
                this,
                "Password must contain at least 6 characters."
            );

            return;
        }

        // Validate role
        if (selectedRole == null
                || selectedRole.equals("Select Account Type")) {

            JOptionPane.showMessageDialog(
                this,
                "Please select an account type."
            );

            return;
        }

        try {

            String userId =
                "USR-" + (marketplaceData.getUserCount() + 1);

            User newUser;

            if (selectedRole.equals("Seller")) {

                newUser = new Seller(
                    userId,
                    fullName,
                    email,
                    password
                );

            } else {

                newUser = new Buyer(
                    userId,
                    fullName,
                    email,
                    password
                );
            }

            marketplaceData.registerUser(newUser);

            JOptionPane.showMessageDialog(
                this,
                "Account created successfully!"
            );

            new WelcomeScreen(marketplaceData);
            dispose();

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Registration Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

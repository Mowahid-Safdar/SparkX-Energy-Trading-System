
package sparkX.gui;

import javax.swing.*;
import java.awt.*;

import sparkX.data.MarketplaceData;
import sparkX.model.User;
import sparkX.model.Seller;
import sparkX.model.Buyer;

public class LoginScreen extends JFrame {

    private MarketplaceData marketplaceData;

    private JTextField emailField;
    private JPasswordField passwordField;

    private Color backgroundColor = new Color(16, 20, 24);
    private Color panelColor = new Color(27, 35, 43);
    private Color greenColor = new Color(0, 199, 129);
    private Color blueColor = new Color(49, 139, 255);
    private Color textColor = new Color(241, 245, 249);
    private Color mutedColor = new Color(154, 168, 181);

    public LoginScreen(MarketplaceData marketplaceData) {

        this.marketplaceData = marketplaceData;

        setTitle("SparkX - Login");
        setSize(550, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(backgroundColor);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(backgroundColor);

        JPanel formPanel = new JPanel();

        formPanel.setBackground(panelColor);
        formPanel.setPreferredSize(new Dimension(400, 440));

        formPanel.setBorder(
            BorderFactory.createEmptyBorder(35, 35, 35, 35)
        );

        formPanel.setLayout(
            new BoxLayout(formPanel, BoxLayout.Y_AXIS)
        );

        JLabel titleLabel = new JLabel("Welcome Back");

        titleLabel.setFont(
            new Font("Arial", Font.BOLD, 27)
        );

        titleLabel.setForeground(textColor);

        titleLabel.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        JLabel subtitleLabel = new JLabel(
            "Login to your SparkX account"
        );

        subtitleLabel.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        subtitleLabel.setForeground(mutedColor);

        subtitleLabel.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(subtitleLabel);
        formPanel.add(Box.createVerticalStrut(35));

        // EMAIL
        formPanel.add(createLabel("Email Address"));

        emailField = createTextField();

        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(20));

        // PASSWORD
        formPanel.add(createLabel("Password"));

        passwordField = new JPasswordField();

        styleTextField(passwordField);

        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(35));

        // LOGIN BUTTON
        JButton loginButton = new JButton("LOGIN");

        loginButton.setFont(
            new Font("Arial", Font.BOLD, 14)
        );

        loginButton.setBackground(blueColor);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        loginButton.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        loginButton.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 45)
        );

        loginButton.addActionListener(
            e -> loginUser()
        );

        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(15));

        // BACK BUTTON
        JButton backButton = new JButton("Back to Welcome");

        backButton.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        backButton.setForeground(greenColor);
        backButton.setBackground(panelColor);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);

        backButton.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        backButton.addActionListener(e -> {
            new WelcomeScreen(marketplaceData);
            dispose();
        });

        formPanel.add(backButton);

        mainPanel.add(formPanel);

        add(mainPanel);

        setVisible(true);
    }

    // Creates labels
    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        label.setForeground(textColor);

        label.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        return label;
    }

    // Creates text fields
    private JTextField createTextField() {

        JTextField field = new JTextField();

        styleTextField(field);

        return field;
    }

    // Applies text field styling
    private void styleTextField(JTextField field) {

        field.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        field.setBackground(backgroundColor);
        field.setForeground(textColor);
        field.setCaretColor(textColor);

        field.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(mutedColor),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            )
        );

        field.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 42)
        );

        field.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );
    }

    // Handles login
    private void loginUser() {

        String email = emailField.getText().trim();

        String password =
            new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter your email and password."
            );

            return;
        }

        User user =
            marketplaceData.findUserByEmail(email);

        if (user == null) {

            JOptionPane.showMessageDialog(
                this,
                "No account found with this email address."
            );

            return;
        }

        if (!user.verifyPassword(password)) {

            JOptionPane.showMessageDialog(
                this,
                "Incorrect password."
            );

            return;
        }

        if (user instanceof Seller) {

            Seller seller = (Seller) user;

            new SellerDashboard(
                marketplaceData,
                seller
            );

            dispose();

        } else if (user instanceof Buyer) {

            Buyer buyer = (Buyer) user;

            new BuyerDashboard(
                marketplaceData,
                buyer
            );

            dispose();
        }
    }
}

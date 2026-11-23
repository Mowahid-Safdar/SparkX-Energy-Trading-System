
package sparkX.gui;

import javax.swing.*;
import java.awt.*;

import sparkX.data.MarketplaceData;

public class WelcomeScreen extends JFrame {

    private final MarketplaceData marketplaceData;

    private final Color background = new Color(16, 20, 24);
    private final Color panel = new Color(27, 35, 43);
    private final Color green = new Color(0, 199, 129);
    private final Color blue = new Color(49, 139, 255);
    private final Color white = new Color(241, 245, 249);
    private final Color muted = new Color(160, 170, 180);

    public WelcomeScreen(MarketplaceData marketplaceData) {

        this.marketplaceData = marketplaceData;

        setTitle("SparkX");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(background);
        setLayout(new BorderLayout());

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(background);
        header.setBorder(
            BorderFactory.createEmptyBorder(25, 45, 15, 45)
        );

        JLabel logo = new JLabel("SparkX");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 27));
        logo.setForeground(green);

        JLabel status = new JLabel("ENERGY MARKETPLACE");
        status.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        status.setForeground(muted);

        header.add(logo, BorderLayout.WEST);
        header.add(status, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // MAIN CONTENT
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(background);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(panel);
        content.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(48, 60, 70), 1
                ),
                BorderFactory.createEmptyBorder(55, 75, 55, 75)
            )
        );

        // ENERGY ICON
        JLabel icon = new JLabel("⚡");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 55));
        icon.setForeground(green);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // BRAND NAME
        JLabel title = new JLabel("SparkX");
        title.setFont(new Font("Segoe UI", Font.BOLD, 46));
        title.setForeground(white);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // PRODUCT DESCRIPTION
        JLabel subtitle = new JLabel(
            "Decentralized Energy Trading Platform"
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 19));
        subtitle.setForeground(green);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel description = new JLabel(
            "Buy and sell energy through a unified digital marketplace."
        );
        description.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        description.setForeground(muted);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        // BUTTONS
        JButton loginButton = createButton(
            "Login", green, background
        );

        JButton registerButton = createButton(
            "Create Account", blue, white
        );

        loginButton.addActionListener(e -> {
            new LoginScreen(marketplaceData);
            dispose();
        });

        registerButton.addActionListener(e -> {
            new RegistrationScreen(marketplaceData);
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(
            FlowLayout.CENTER, 15, 0
        ));
        buttonPanel.setBackground(panel);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ADD CONTENT
        content.add(icon);
        content.add(Box.createVerticalStrut(15));
        content.add(title);
        content.add(Box.createVerticalStrut(8));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(12));
        content.add(description);
        content.add(Box.createVerticalStrut(40));
        content.add(buttonPanel);

        mainPanel.add(content);

        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createButton(
            String text, Color buttonColor, Color textColor) {

        JButton button = new JButton(text);

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(textColor);
        button.setBackground(buttonColor);

        button.setPreferredSize(new Dimension(175, 48));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }
}

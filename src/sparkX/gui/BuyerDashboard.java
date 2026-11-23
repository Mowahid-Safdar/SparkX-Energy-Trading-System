
package sparkX.gui;

import javax.swing.*;
import java.awt.*;

import sparkX.data.MarketplaceData;
import sparkX.model.Buyer;

public class BuyerDashboard extends JFrame {

    private MarketplaceData marketplaceData;
    private Buyer buyer;

    private Color backgroundColor = new Color(16, 20, 24);
    private Color panelColor = new Color(27, 35, 43);
    private Color blueColor = new Color(49, 139, 255);
    private Color textColor = new Color(241, 245, 249);
    private Color mutedColor = new Color(154, 168, 181);

    private JPanel mainPanel;

    public BuyerDashboard(
            MarketplaceData marketplaceData,
            Buyer buyer) {

        this.marketplaceData = marketplaceData;
        this.buyer = buyer;

        setTitle("SparkX - Buyer Dashboard");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(backgroundColor);
        setLayout(new BorderLayout(20, 20));

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());

        headerPanel.setBackground(panelColor);
        headerPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        );

        JLabel titleLabel = new JLabel(
            "SparkX | Buyer Dashboard"
        );

        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(blueColor);

        JLabel welcomeLabel = new JLabel(
            "Welcome, " + buyer.getFullName()
        );

        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        welcomeLabel.setForeground(textColor);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // MAIN CONTENT
        mainPanel = new JPanel();
        mainPanel.setBackground(backgroundColor);
        mainPanel.setLayout(new BorderLayout(15, 20));

        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 30, 20, 30)
        );

        add(mainPanel, BorderLayout.CENTER);

        refreshDashboard();

        // FOOTER
        JPanel footerPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 15, 15)
        );

        footerPanel.setBackground(backgroundColor);

        JButton logoutButton = new JButton("Logout");

        logoutButton.setBackground(panelColor);
        logoutButton.setForeground(textColor);
        logoutButton.setFocusPainted(false);

        logoutButton.addActionListener(e -> {
            new WelcomeScreen(marketplaceData);
            dispose();
        });

        footerPanel.add(logoutButton);

        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Refreshes buyer dashboard information
    private void refreshDashboard() {

        mainPanel.removeAll();

        JLabel overviewLabel = new JLabel("Your Overview");

        overviewLabel.setFont(
            new Font("Arial", Font.BOLD, 21)
        );

        overviewLabel.setForeground(textColor);

        // INFORMATION CARDS
        JPanel cardsPanel = new JPanel(
            new GridLayout(1, 3, 15, 15)
        );

        cardsPanel.setBackground(backgroundColor);

        cardsPanel.add(createInfoCard(
            "Required Units",
            String.format("%.2f", buyer.getRequiredUnits())
        ));

        cardsPanel.add(createInfoCard(
            "Maximum Price / Unit",
            "Rs. " + String.format(
                "%.2f", buyer.getMaxPricePerUnit()
            )
        ));

        cardsPanel.add(createInfoCard(
            "Total Spent",
            "Rs. " + String.format(
                "%.2f", buyer.getTotalSpent()
            )
        ));

        // BUYER INFORMATION
        JPanel profilePanel = new JPanel(
            new GridLayout(4, 1, 5, 10)
        );

        profilePanel.setBackground(panelColor);

        profilePanel.setBorder(
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel profileTitle = new JLabel("Buyer Information");

        profileTitle.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        profileTitle.setForeground(blueColor);

        JLabel consumerTypeLabel = new JLabel(
            "Consumer Type: " + buyer.getConsumerType()
        );

        JLabel emailLabel = new JLabel(
            "Email: " + buyer.getEmail()
        );

        JLabel demandStatusLabel = new JLabel(
            buyer.isDemandSatisfied()
                ? "Demand Status: Satisfied"
                : "Demand Status: Pending"
        );

        styleProfileLabel(consumerTypeLabel);
        styleProfileLabel(emailLabel);
        styleProfileLabel(demandStatusLabel);

        profilePanel.add(profileTitle);
        profilePanel.add(consumerTypeLabel);
        profilePanel.add(emailLabel);
        profilePanel.add(demandStatusLabel);

        mainPanel.add(overviewLabel, BorderLayout.NORTH);
        mainPanel.add(cardsPanel, BorderLayout.CENTER);
        mainPanel.add(profilePanel, BorderLayout.SOUTH);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Creates dashboard information cards
    private JPanel createInfoCard(
            String title,
            String value) {

        JPanel card = new JPanel();

        card.setBackground(panelColor);

        card.setLayout(
            new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        card.setBorder(
            BorderFactory.createEmptyBorder(25, 15, 25, 15)
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        titleLabel.setForeground(mutedColor);

        titleLabel.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        JLabel valueLabel = new JLabel(value);

        valueLabel.setFont(
            new Font("Arial", Font.BOLD, 23)
        );

        valueLabel.setForeground(blueColor);

        valueLabel.setAlignmentX(
            Component.CENTER_ALIGNMENT
        );

        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    // Styles buyer information labels
    private void styleProfileLabel(JLabel label) {

        label.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        label.setForeground(textColor);
    }
}

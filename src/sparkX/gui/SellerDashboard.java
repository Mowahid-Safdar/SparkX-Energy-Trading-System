
package sparkX.gui;

import javax.swing.*;
import java.awt.*;

import sparkX.data.MarketplaceData;
import sparkX.model.Seller;
import sparkX.model.EnergyOffer;

public class SellerDashboard extends JFrame {

    private MarketplaceData marketplaceData;
    private Seller seller;

    private Color backgroundColor = new Color(16, 20, 24);
    private Color panelColor = new Color(27, 35, 43);
    private Color greenColor = new Color(0, 199, 129);
    private Color blueColor = new Color(45, 140, 230);
    private Color textColor = new Color(241, 245, 249);
    private Color mutedColor = new Color(154, 168, 181);

    private JPanel mainPanel;

    public SellerDashboard(
            MarketplaceData marketplaceData,
            Seller seller) {

        this.marketplaceData = marketplaceData;
        this.seller = seller;

        setTitle("SparkX - Seller Dashboard");
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

        JLabel titleLabel = new JLabel("SparkX | Seller Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(greenColor);

        JLabel welcomeLabel = new JLabel(
            "Welcome, " + seller.getFullName()
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

        // FOOTER BUTTONS
        JPanel footerPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 12, 15)
        );

        footerPanel.setBackground(backgroundColor);

        JButton generateButton =
            new JButton("Generate Daily Energy");

        generateButton.setBackground(greenColor);
        generateButton.setForeground(Color.BLACK);
        generateButton.setFocusPainted(false);

        generateButton.addActionListener(
            e -> generateEnergy()
        );

        JButton publishButton =
            new JButton("Publish Offer");

        publishButton.setBackground(blueColor);
        publishButton.setForeground(Color.WHITE);
        publishButton.setFocusPainted(false);

        publishButton.addActionListener(
            e -> publishOffer()
        );

        JButton configureButton =
            new JButton("Configure Profile");

        configureButton.setBackground(panelColor);
        configureButton.setForeground(textColor);
        configureButton.setFocusPainted(false);

        configureButton.addActionListener(
            e -> openProfileScreen()
        );

        JButton logoutButton = new JButton("Logout");

        logoutButton.setBackground(panelColor);
        logoutButton.setForeground(textColor);
        logoutButton.setFocusPainted(false);

        logoutButton.addActionListener(e -> {
            new WelcomeScreen(marketplaceData);
            dispose();
        });

        footerPanel.add(generateButton);
        footerPanel.add(publishButton);
        footerPanel.add(configureButton);
        footerPanel.add(logoutButton);

        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Opens seller profile configuration
    private void openProfileScreen() {

        new SellerProfileScreen(seller);

        setEnabled(false);

        Timer timer = new Timer(300, event -> {

            if (!isDisplayable()) {
                ((Timer) event.getSource()).stop();
                return;
            }

            boolean profileOpen = false;

            for (Window window : Window.getWindows()) {

                if (window instanceof SellerProfileScreen
                        && window.isDisplayable()) {

                    profileOpen = true;
                    break;
                }
            }

            if (!profileOpen) {

                ((Timer) event.getSource()).stop();

                refreshDashboard();
                setEnabled(true);
                toFront();
            }
        });

        timer.start();
    }

    // Generates daily energy
    private void generateEnergy() {

        if (!seller.isProfileConfigured()) {

            JOptionPane.showMessageDialog(
                this,
                "Please configure your seller profile first.",
                "Profile Required",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (seller.getAvailableUnits() > 0
                || seller.getReservedUnits() > 0) {

            JOptionPane.showMessageDialog(
                this,
                "You still have energy available or reserved "
                + "in the marketplace.\n\n"
                + "Trade or release those units before generating again.",
                "Existing Energy",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Generate daily energy using your saved profile?",
            "Confirm Energy Generation",
            JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            double generated = seller.generateDailyEnergy();
            double available = seller.getAvailableUnits();

            refreshDashboard();

            JOptionPane.showMessageDialog(
                this,
                "Energy generation completed!\n\n"
                + "Generated Units: "
                + String.format("%.2f", generated)
                + "\nAvailable Units: "
                + String.format("%.2f", available),
                "Generation Successful",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalStateException ex) {

            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Generation Not Available",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    // Publishes available energy to the marketplace
    private void publishOffer() {

        if (!seller.isProfileConfigured()) {

            JOptionPane.showMessageDialog(
                this,
                "Please configure your seller profile first.",
                "Profile Required",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        double available = seller.getAvailableUnits();

        if (available <= 0) {

            JOptionPane.showMessageDialog(
                this,
                "No available energy to publish.\n"
                + "Generate daily energy first.",
                "No Energy Available",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String input = JOptionPane.showInputDialog(
            this,
            "Available Units: "
            + String.format("%.2f", available)
            + "\nSelling Price: Rs. "
            + String.format("%.2f", seller.getPricePerUnit())
            + " per unit\n\n"
            + "How many units would you like to publish?",
            "Publish Energy Offer",
            JOptionPane.QUESTION_MESSAGE
        );

        if (input == null) {
            return;
        }

        try {

            double units = Double.parseDouble(input.trim());

            if (Double.isNaN(units)
                    || Double.isInfinite(units)
                    || units <= 0
                    || units > available) {

                JOptionPane.showMessageDialog(
                    this,
                    "Enter a valid quantity between 0 and "
                    + String.format("%.2f", available) + ".",
                    "Invalid Quantity",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            EnergyOffer offer =
                marketplaceData.getTradingService().publishOffer(
                    seller, units
                );

            refreshDashboard();

            JOptionPane.showMessageDialog(
                this,
                "Your energy offer has been published!\n\n"
                + "Offer ID: " + offer.getOfferId()
                + "\nPublished Units: "
                + String.format("%.2f", offer.getOfferedUnits())
                + "\nPrice Per Unit: Rs. "
                + String.format("%.2f", offer.getPricePerUnit())
                + "\nTotal Offer Value: Rs. "
                + String.format("%.2f", offer.getOfferValue()),
                "Offer Published",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter a valid numeric quantity.",
                "Invalid Input",
                JOptionPane.WARNING_MESSAGE
            );

        } catch (IllegalArgumentException
                | IllegalStateException ex) {

            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Unable to Publish Offer",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Refreshes dashboard information
    private void refreshDashboard() {

        mainPanel.removeAll();

        JLabel overviewLabel = new JLabel("Energy Overview");
        overviewLabel.setFont(new Font("Arial", Font.BOLD, 21));
        overviewLabel.setForeground(textColor);

        JPanel cardsPanel = new JPanel(
            new GridLayout(1, 3, 15, 15)
        );

        cardsPanel.setBackground(backgroundColor);

        cardsPanel.add(createInfoCard(
            "Generated Units",
            String.format("%.2f", seller.getGeneratedUnits())
        ));

        cardsPanel.add(createInfoCard(
            "Available Units",
            String.format("%.2f", seller.getAvailableUnits())
        ));

        cardsPanel.add(createInfoCard(
            "Total Earnings",
            "Rs. " + String.format(
                "%.2f", seller.getTotalEarnings()
            )
        ));

        JPanel profilePanel = new JPanel(
            new GridLayout(4, 1, 5, 10)
        );

        profilePanel.setBackground(panelColor);

        profilePanel.setBorder(
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel profileTitle = new JLabel("Seller Information");
        profileTitle.setFont(new Font("Arial", Font.BOLD, 18));
        profileTitle.setForeground(greenColor);

        JLabel sellerTypeLabel = new JLabel(
            "Seller Type: " + seller.getSellerType()
        );

        JLabel energySourceLabel = new JLabel(
            "Energy Source: " + seller.getEnergySource()
        );

        JLabel emailLabel = new JLabel(
            "Email: " + seller.getEmail()
        );

        styleProfileLabel(sellerTypeLabel);
        styleProfileLabel(energySourceLabel);
        styleProfileLabel(emailLabel);

        profilePanel.add(profileTitle);
        profilePanel.add(sellerTypeLabel);
        profilePanel.add(energySourceLabel);
        profilePanel.add(emailLabel);

        mainPanel.add(overviewLabel, BorderLayout.NORTH);
        mainPanel.add(cardsPanel, BorderLayout.CENTER);
        mainPanel.add(profilePanel, BorderLayout.SOUTH);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createInfoCard(String title, String value) {

        JPanel card = new JPanel();

        card.setBackground(panelColor);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.setBorder(
            BorderFactory.createEmptyBorder(25, 15, 25, 15)
        );

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(mutedColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 25));
        valueLabel.setForeground(greenColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private void styleProfileLabel(JLabel label) {

        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(textColor);
    }
}

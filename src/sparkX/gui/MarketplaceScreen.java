
package sparkX.gui;

import javax.swing.*;
import java.awt.*;

import sparkX.data.MarketplaceData;
import sparkX.model.Buyer;
import sparkX.model.EnergyOffer;
import sparkX.model.Transaction;

public class MarketplaceScreen extends JFrame {

    private MarketplaceData marketplaceData;
    private Buyer buyer;

    private Color backgroundColor = new Color(16, 20, 24);
    private Color panelColor = new Color(27, 35, 43);
    private Color blueColor = new Color(49, 139, 255);
    private Color greenColor = new Color(0, 199, 129);
    private Color textColor = new Color(241, 245, 249);
    private Color mutedColor = new Color(154, 168, 181);

    private JPanel offersPanel;

    public MarketplaceScreen(
            MarketplaceData marketplaceData,
            Buyer buyer) {

        this.marketplaceData = marketplaceData;
        this.buyer = buyer;

        setTitle("SparkX - Energy Marketplace");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(backgroundColor);
        setLayout(new BorderLayout(15, 15));

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());

        headerPanel.setBackground(panelColor);
        headerPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        );

        JLabel titleLabel = new JLabel("Energy Marketplace");

        titleLabel.setFont(
            new Font("Arial", Font.BOLD, 25)
        );

        titleLabel.setForeground(blueColor);

        JLabel subtitleLabel = new JLabel(
            "Browse available energy offers"
        );

        subtitleLabel.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        subtitleLabel.setForeground(mutedColor);

        JPanel headingPanel = new JPanel();
        headingPanel.setBackground(panelColor);
        headingPanel.setLayout(
            new BoxLayout(headingPanel, BoxLayout.Y_AXIS)
        );

        headingPanel.add(titleLabel);
        headingPanel.add(Box.createVerticalStrut(5));
        headingPanel.add(subtitleLabel);

        headerPanel.add(headingPanel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // OFFERS AREA
        offersPanel = new JPanel();

        offersPanel.setBackground(backgroundColor);

        offersPanel.setLayout(
            new BoxLayout(offersPanel, BoxLayout.Y_AXIS)
        );

        JScrollPane scrollPane = new JScrollPane(offersPanel);

        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(backgroundColor);

        add(scrollPane, BorderLayout.CENTER);

        refreshOffers();

        setVisible(true);
    }

    // Loads all active marketplace offers
    private void refreshOffers() {

        offersPanel.removeAll();

        int offerCount =
            marketplaceData.getTradingService().getOfferCount();

        int displayedOffers = 0;

        for (int i = 0; i < offerCount; i++) {

            EnergyOffer offer =
                marketplaceData.getTradingService().getOffer(i);

            if (offer != null
                    && offer.isActive()
                    && offer.getRemainingUnits() > 0) {

                offersPanel.add(createOfferCard(offer));

                offersPanel.add(
                    Box.createVerticalStrut(15)
                );

                displayedOffers++;
            }
        }

        if (displayedOffers == 0) {

            JLabel emptyLabel = new JLabel(
                "No active energy offers are available right now."
            );

            emptyLabel.setFont(
                new Font("Arial", Font.PLAIN, 16)
            );

            emptyLabel.setForeground(mutedColor);

            emptyLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
            );

            offersPanel.add(Box.createVerticalStrut(40));
            offersPanel.add(emptyLabel);
        }

        offersPanel.revalidate();
        offersPanel.repaint();
    }

    // Creates an individual energy offer card
    private JPanel createOfferCard(EnergyOffer offer) {

        JPanel card = new JPanel(new BorderLayout(20, 10));

        card.setBackground(panelColor);

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    new Color(55, 68, 80)
                ),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
            )
        );

        card.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 150)
        );

        // OFFER INFORMATION
        JPanel informationPanel = new JPanel();

        informationPanel.setBackground(panelColor);

        informationPanel.setLayout(
            new BoxLayout(informationPanel, BoxLayout.Y_AXIS)
        );

        JLabel offerIdLabel = new JLabel(
            offer.getOfferId()
        );

        offerIdLabel.setFont(
            new Font("Arial", Font.BOLD, 17)
        );

        offerIdLabel.setForeground(blueColor);

        JLabel sellerLabel = new JLabel(
            "Seller: " + offer.getSeller().getFullName()
        );

        JLabel unitsLabel = new JLabel(
            "Available Units: "
            + String.format("%.2f", offer.getRemainingUnits())
        );

        JLabel priceLabel = new JLabel(
            "Price Per Unit: Rs. "
            + String.format("%.2f", offer.getPricePerUnit())
        );

        styleOfferLabel(sellerLabel);
        styleOfferLabel(unitsLabel);
        styleOfferLabel(priceLabel);

        informationPanel.add(offerIdLabel);
        informationPanel.add(Box.createVerticalStrut(8));
        informationPanel.add(sellerLabel);
        informationPanel.add(Box.createVerticalStrut(5));
        informationPanel.add(unitsLabel);
        informationPanel.add(Box.createVerticalStrut(5));
        informationPanel.add(priceLabel);

        // BUY BUTTON
        JButton buyButton = new JButton("BUY UNITS");

        buyButton.setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        buyButton.setBackground(greenColor);
        buyButton.setForeground(Color.WHITE);
        buyButton.setFocusPainted(false);

        buyButton.addActionListener(
            e -> purchaseUnits(offer)
        );

        card.add(informationPanel, BorderLayout.CENTER);
        card.add(buyButton, BorderLayout.EAST);

        return card;
    }

    // Styles offer information
    private void styleOfferLabel(JLabel label) {

        label.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        label.setForeground(textColor);
    }

    // Handles buyer purchase
    private void purchaseUnits(EnergyOffer offer) {

        if (!buyer.isProfileConfigured()) {

            JOptionPane.showMessageDialog(
                this,
                "Please configure your buyer profile first."
            );

            return;
        }

        if (buyer.isDemandSatisfied()) {

            JOptionPane.showMessageDialog(
                this,
                "Your energy demand has already been satisfied."
            );

            return;
        }

        if (offer.getPricePerUnit() > buyer.getMaxPricePerUnit()) {

            JOptionPane.showMessageDialog(
                this,
                "This offer exceeds your maximum price per unit."
            );

            return;
        }

        String input = JOptionPane.showInputDialog(
            this,
            "Available units: " + offer.getRemainingUnits()
            + "\nYour remaining demand: "
            + buyer.getRequiredUnits()
            + "\nPrice per unit: Rs. "
            + offer.getPricePerUnit()
            + "\n\nEnter the number of units to purchase:"
        );

        if (input == null) {
            return;
        }

        try {

            double units = Double.parseDouble(input.trim());

            Transaction transaction =
                marketplaceData.getTradingService().buyUnits(
                    buyer,
                    offer,
                    units
                );

            if (transaction != null) {

                JOptionPane.showMessageDialog(
                    this,
                    "Purchase completed successfully!"
                    + "\nTransaction ID: "
                    + transaction.getTransactionId()
                    + "\nUnits Purchased: " + units
                    + "\nTotal Amount: Rs. "
                    + String.format(
                        "%.2f",
                        units * offer.getPricePerUnit()
                    )
                );

                refreshOffers();

            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter a valid numeric quantity."
            );

        } catch (IllegalArgumentException
                | IllegalStateException e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );
        }
    }
}

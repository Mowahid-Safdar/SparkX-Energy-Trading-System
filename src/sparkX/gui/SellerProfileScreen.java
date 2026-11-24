
package sparkX.gui;

import javax.swing.*;
import java.awt.*;

import sparkX.model.Seller;
import sparkX.data.MarketplaceData;

public class SellerProfileScreen extends JFrame {

    private Seller seller;
    private MarketplaceData marketplaceData;

    private JComboBox<String> sellerTypeBox;
    private JComboBox<String> energySourceBox;

    private JTextField capacityField;
    private JTextField hoursField;
    private JTextField performanceField;
    private JTextField consumptionField;
    private JTextField priceField;

    private Color backgroundColor = new Color(16, 20, 24);
    private Color panelColor = new Color(27, 35, 43);
    private Color greenColor = new Color(0, 199, 129);
    private Color textColor = new Color(241, 245, 249);
    private Color mutedColor = new Color(154, 168, 181);

    public SellerProfileScreen(
            Seller seller,
            MarketplaceData marketplaceData) {

        this.seller = seller;
        this.marketplaceData = marketplaceData;

        setTitle("SparkX - Configure Seller Profile");
        setSize(650, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(backgroundColor);
        setLayout(new BorderLayout(15, 15));

        // HEADER
        JPanel headerPanel = new JPanel();

        headerPanel.setBackground(backgroundColor);
        headerPanel.setLayout(new BoxLayout(
            headerPanel, BoxLayout.Y_AXIS
        ));

        headerPanel.setBorder(
            BorderFactory.createEmptyBorder(25, 30, 10, 30)
        );

        JLabel titleLabel = new JLabel("Seller Profile");

        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(greenColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(
            "Configure your energy generation details"
        );

        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(mutedColor);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // FORM
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 15, 18));

        formPanel.setBackground(panelColor);
        formPanel.setBorder(
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        );

        sellerTypeBox = new JComboBox<>(new String[] {
            "Household",
            "Business",
            "Solar Farm",
            "Power Generation Company",
            "Other"
        });

        energySourceBox = new JComboBox<>(new String[] {
            "Solar",
            "Wind",
            "Hydro",
            "Biomass",
            "Conventional",
            "Other"
        });

        capacityField = new JTextField();
        hoursField = new JTextField();
        performanceField = new JTextField();
        consumptionField = new JTextField();
        priceField = new JTextField();

        addFormRow(formPanel, "Seller Type", sellerTypeBox);
        addFormRow(formPanel, "Energy Source", energySourceBox);
        addFormRow(formPanel, "Generation Capacity (kW)", capacityField);
        addFormRow(formPanel, "Operating Hours (per day)", hoursField);
        addFormRow(formPanel, "Performance Ratio (0–1)", performanceField);
        addFormRow(formPanel, "Own Consumption (units)", consumptionField);
        addFormRow(formPanel, "Selling Price (Rs./unit)", priceField);

        add(formPanel, BorderLayout.CENTER);

        // FOOTER
        JPanel footerPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 15, 15)
        );

        footerPanel.setBackground(backgroundColor);

        JButton saveButton = new JButton("Save Profile");

        saveButton.setBackground(greenColor);
        saveButton.setForeground(Color.BLACK);
        saveButton.setFocusPainted(false);

        JButton cancelButton = new JButton("Cancel");

        cancelButton.setBackground(panelColor);
        cancelButton.setForeground(textColor);
        cancelButton.setFocusPainted(false);

        saveButton.addActionListener(e -> saveProfile());
        cancelButton.addActionListener(e -> dispose());

        footerPanel.add(saveButton);
        footerPanel.add(cancelButton);

        add(footerPanel, BorderLayout.SOUTH);

        loadExistingProfile();

        setVisible(true);
    }

    private void addFormRow(
            JPanel panel, String labelText, JComponent field) {

        JLabel label = new JLabel(labelText);

        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(textColor);

        field.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(label);
        panel.add(field);
    }

    private void loadExistingProfile() {

        if (!seller.isProfileConfigured()) {
            return;
        }

        sellerTypeBox.setSelectedItem(seller.getSellerType());
        energySourceBox.setSelectedItem(seller.getEnergySource());

        capacityField.setText(
            String.valueOf(seller.getGenerationCapacityKw())
        );

        hoursField.setText(
            String.valueOf(seller.getOperatingHours())
        );

        performanceField.setText(
            String.valueOf(seller.getPerformanceRatio())
        );

        consumptionField.setText(
            String.valueOf(seller.getOwnConsumptionUnits())
        );

        priceField.setText(
            String.valueOf(seller.getPricePerUnit())
        );
    }

    private void saveProfile() {

        try {

            String sellerType =
                (String) sellerTypeBox.getSelectedItem();

            String energySource =
                (String) energySourceBox.getSelectedItem();

            double capacity =
                Double.parseDouble(capacityField.getText().trim());

            double hours =
                Double.parseDouble(hoursField.getText().trim());

            double performance =
                Double.parseDouble(performanceField.getText().trim());

            double consumption =
                Double.parseDouble(consumptionField.getText().trim());

            double price =
                Double.parseDouble(priceField.getText().trim());

            seller.configureProfile(
                sellerType,
                energySource,
                capacity,
                hours,
                performance,
                consumption,
                price
            );

            // Save updated seller information to the local file
            marketplaceData.saveUsers();

            JOptionPane.showMessageDialog(
                this,
                "Seller profile saved successfully!"
            );

            dispose();

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter valid numeric values in all fields.",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Invalid Profile",
                JOptionPane.ERROR_MESSAGE
            );

        } catch (IllegalStateException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Profile was updated, but could not be saved to file.\n"
                + ex.getMessage(),
                "Storage Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}


package sparkX.gui;

import javax.swing.*;
import java.awt.*;

import sparkX.model.Buyer;
import sparkX.data.MarketplaceData;

public class BuyerProfileScreen extends JFrame {

    private Buyer buyer;
    private MarketplaceData marketplaceData;

    private JComboBox<String> consumerTypeComboBox;
    private JTextField requiredUnitsField;
    private JTextField maxPriceField;

    private Color backgroundColor = new Color(16, 20, 24);
    private Color panelColor = new Color(27, 35, 43);
    private Color blueColor = new Color(49, 139, 255);
    private Color textColor = new Color(241, 245, 249);
    private Color mutedColor = new Color(154, 168, 181);

    public BuyerProfileScreen(Buyer buyer, MarketplaceData marketplaceData) {

        this.buyer = buyer;
        this.marketplaceData = marketplaceData;

        setTitle("SparkX - Buyer Profile Setup");
        setSize(550, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(backgroundColor);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(backgroundColor);

        JPanel formPanel = new JPanel();
        formPanel.setBackground(panelColor);
        formPanel.setPreferredSize(new Dimension(420, 500));

        formPanel.setBorder(
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        );

        formPanel.setLayout(
            new BoxLayout(formPanel, BoxLayout.Y_AXIS)
        );

        // TITLE

        JLabel titleLabel = new JLabel("Buyer Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(
            "Configure your energy requirements"
        );

        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(mutedColor);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(subtitleLabel);
        formPanel.add(Box.createVerticalStrut(30));

        // CONSUMER TYPE

        formPanel.add(createLabel("Consumer Type"));

        String[] consumerTypes = {
            "Household",
            "Business",
            "Factory",
            "School / University",
            "Hospital",
            "Commercial Building",
            "Industrial Facility",
            "Other"
        };

        consumerTypeComboBox = new JComboBox<String>(consumerTypes);
        styleComboBox(consumerTypeComboBox);

        formPanel.add(consumerTypeComboBox);
        formPanel.add(Box.createVerticalStrut(20));

        // REQUIRED UNITS

        formPanel.add(createLabel("Required Units"));

        requiredUnitsField = createTextField();
        formPanel.add(requiredUnitsField);
        formPanel.add(Box.createVerticalStrut(20));

        // MAXIMUM PRICE

        formPanel.add(createLabel("Maximum Price Per Unit (Rs.)"));

        maxPriceField = createTextField();
        formPanel.add(maxPriceField);
        formPanel.add(Box.createVerticalStrut(35));

        // SAVE BUTTON

        JButton saveButton = new JButton("SAVE PROFILE");

        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setBackground(blueColor);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        saveButton.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 45)
        );

        saveButton.addActionListener(e -> saveProfile());

        formPanel.add(saveButton);
        formPanel.add(Box.createVerticalStrut(12));

        // CANCEL BUTTON

        JButton cancelButton = new JButton("Cancel");

        cancelButton.setFont(new Font("Arial", Font.PLAIN, 13));
        cancelButton.setForeground(mutedColor);
        cancelButton.setBackground(panelColor);
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        cancelButton.addActionListener(e -> dispose());

        formPanel.add(cancelButton);

        mainPanel.add(formPanel);
        add(mainPanel);

        loadExistingProfile();

        setVisible(true);
    }

    // Creates form labels
    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(textColor);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    // Creates text fields
    private JTextField createTextField() {

        JTextField field = new JTextField();

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

        field.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 42)
        );

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        return field;
    }

    // Styles consumer type selection
    private void styleComboBox(JComboBox<String> comboBox) {

        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setBackground(backgroundColor);
        comboBox.setForeground(textColor);

        comboBox.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 42)
        );

        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // Loads existing buyer profile information
    private void loadExistingProfile() {

        if (!buyer.isProfileConfigured()) {
            return;
        }

        consumerTypeComboBox.setSelectedItem(
            buyer.getConsumerType()
        );

        requiredUnitsField.setText(
            String.valueOf(buyer.getRequiredUnits())
        );

        maxPriceField.setText(
            String.valueOf(buyer.getMaxPricePerUnit())
        );
    }

    // Saves buyer profile
    private void saveProfile() {

        try {

            String consumerType =
                (String) consumerTypeComboBox.getSelectedItem();

            double requiredUnits =
                Double.parseDouble(
                    requiredUnitsField.getText().trim()
                );

            double maxPrice =
                Double.parseDouble(
                    maxPriceField.getText().trim()
                );

            buyer.configureProfile(
                consumerType,
                requiredUnits,
                maxPrice
            );

            marketplaceData.saveUsers();

            JOptionPane.showMessageDialog(
                this,
                "Buyer profile configured successfully!"
            );

            dispose();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                this,
                "Please enter valid numeric values."
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage()
            );

        } catch (IllegalStateException e) {

            JOptionPane.showMessageDialog(
                this,
                "Profile was updated, but could not be saved to the file.\n"
                + e.getMessage()
            );
        }
    }
}

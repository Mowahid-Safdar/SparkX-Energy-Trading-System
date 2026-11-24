
package sparkX.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

import sparkX.data.MarketplaceData;
import sparkX.model.Seller;
import sparkX.model.EnergyOffer;
import sparkX.model.Transaction;
import sparkX.ai.GeminiService;

public class SellerDashboard extends JFrame {

    private MarketplaceData marketplaceData;
    private Seller seller;

    private final Color backgroundColor = new Color(16, 20, 24);
    private final Color panelColor = new Color(27, 35, 43);
    private final Color greenColor = new Color(0, 199, 129);
    private final Color blueColor = new Color(45, 140, 230);
    private final Color textColor = new Color(241, 245, 249);
    private final Color mutedColor = new Color(154, 168, 181);

    private JPanel mainPanel;

    public SellerDashboard(
            MarketplaceData marketplaceData,
            Seller seller) {

        this.marketplaceData = marketplaceData;
        this.seller = seller;

        setTitle("SparkX - Seller Dashboard");
        setSize(1200, 800);
        setMinimumSize(new Dimension(1050, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        getContentPane().setBackground(backgroundColor);
        setLayout(new BorderLayout(15, 15));

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());

        headerPanel.setBackground(panelColor);
        headerPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        );

        JLabel titleLabel = new JLabel(
            "SparkX | Seller Dashboard"
        );

        titleLabel.setFont(
            new Font("Arial", Font.BOLD, 24)
        );

        titleLabel.setForeground(greenColor);

        JLabel welcomeLabel = new JLabel(
            "Welcome, " + seller.getFullName()
        );

        welcomeLabel.setFont(
            new Font("Arial", Font.PLAIN, 15)
        );

        welcomeLabel.setForeground(textColor);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // MAIN CONTENT
        mainPanel = new JPanel(new BorderLayout(10, 15));
        mainPanel.setBackground(backgroundColor);

        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 30, 10, 30)
        );

        add(mainPanel, BorderLayout.CENTER);

        // FOOTER BUTTONS
        JPanel footerPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 12, 12)
        );

        footerPanel.setBackground(backgroundColor);

        JButton generateButton =
            new JButton("Generate Daily Energy");

        styleButton(generateButton, greenColor, Color.BLACK);

        generateButton.addActionListener(
            e -> generateEnergy()
        );

        JButton publishButton =
            new JButton("Publish Offer");

        styleButton(publishButton, blueColor, Color.WHITE);

        publishButton.addActionListener(
            e -> publishOffer()
        );

        JButton configureButton =
            new JButton("Configure Profile");

        styleButton(configureButton, panelColor, textColor);

        configureButton.addActionListener(
            e -> openProfileScreen()
        );

        JButton refreshButton =
            new JButton("Refresh Dashboard");

        styleButton(refreshButton, panelColor, textColor);

        refreshButton.addActionListener(
            e -> refreshDashboard()
        );

        JButton logoutButton = new JButton("Logout");

        styleButton(logoutButton, panelColor, textColor);

        logoutButton.addActionListener(e -> {
            new WelcomeScreen(marketplaceData);
            dispose();
        });

        footerPanel.add(generateButton);
        footerPanel.add(publishButton);
        footerPanel.add(configureButton);
        footerPanel.add(refreshButton);
        footerPanel.add(logoutButton);

        add(footerPanel, BorderLayout.SOUTH);

        refreshDashboard();

        setVisible(true);
    }

    // Common button styling
    private void styleButton(
            JButton button,
            Color background,
            Color foreground) {

        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    // Opens seller profile configuration
    private void openProfileScreen() {

        new SellerProfileScreen(seller, marketplaceData);

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

            marketplaceData.saveUsers();

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

            marketplaceData.saveUsers();

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

    // Refreshes the complete dashboard
    private void refreshDashboard() {

        mainPanel.removeAll();

        JLabel overviewLabel = new JLabel("Energy Overview");

        overviewLabel.setFont(
            new Font("Arial", Font.BOLD, 21)
        );

        overviewLabel.setForeground(textColor);

        mainPanel.add(overviewLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();

        contentPanel.setBackground(backgroundColor);

        contentPanel.setLayout(
            new BoxLayout(contentPanel, BoxLayout.Y_AXIS)
        );

        // ENERGY CARDS
        JPanel cardsPanel = new JPanel(
            new GridLayout(1, 3, 15, 0)
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

        cardsPanel.setPreferredSize(
            new Dimension(1100, 115)
        );

        cardsPanel.setMinimumSize(
            new Dimension(900, 115)
        );

        cardsPanel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 115)
        );

        cardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // TABS
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.setFont(
            new Font("Arial", Font.BOLD, 14)
        );

        tabbedPane.setBackground(panelColor);
        tabbedPane.setForeground(textColor);

        tabbedPane.addTab(
            "My Profile & Offers",
            createOverviewPanel()
        );

        tabbedPane.addTab(
            "Sales History",
            createSalesHistoryPanel()
        );

        tabbedPane.addTab(
            "Seller AI Advisor",
            createSellerAdvisorPanel()
        );

        tabbedPane.setPreferredSize(
            new Dimension(1100, 450)
        );

        tabbedPane.setMinimumSize(
            new Dimension(900, 350)
        );

        tabbedPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(cardsPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(tabbedPane);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Creates the seller AI advisor panel
    private JPanel createSellerAdvisorPanel() {

        JPanel advisorPanel = new JPanel(new BorderLayout(12, 12));

        advisorPanel.setBackground(panelColor);

        advisorPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel titleLabel = new JLabel(
            "SparkX Seller AI Advisor"
        );

        titleLabel.setFont(
            new Font("Arial", Font.BOLD, 21)
        );

        titleLabel.setForeground(greenColor);

        JLabel descriptionLabel = new JLabel(
            "<html>Get AI-powered insights about your energy production, "
            + "pricing, marketplace offers and selling performance.</html>"
        );

        descriptionLabel.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        descriptionLabel.setForeground(mutedColor);

        JPanel topPanel = new JPanel();

        topPanel.setBackground(panelColor);

        topPanel.setLayout(
            new BoxLayout(topPanel, BoxLayout.Y_AXIS)
        );

        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(descriptionLabel);

        advisorPanel.add(topPanel, BorderLayout.NORTH);

        JTextArea outputArea = new JTextArea();

        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        outputArea.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        outputArea.setForeground(textColor);
        outputArea.setBackground(backgroundColor);

        outputArea.setText(
            "Your personalized seller analysis will appear here.\n\n"
            + "Click the button below to analyze your seller "
            + "profile and marketplace performance."
        );

        outputArea.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JScrollPane scrollPane = new JScrollPane(outputArea);

        scrollPane.setBorder(
            BorderFactory.createLineBorder(
                new Color(55, 65, 75)
            )
        );

        scrollPane.getViewport().setBackground(backgroundColor);

        advisorPanel.add(scrollPane, BorderLayout.CENTER);

        JButton generateAdviceButton =
            new JButton("Generate Seller Advice");

        styleButton(
            generateAdviceButton,
            greenColor,
            Color.BLACK
        );

        generateAdviceButton.setFont(
            new Font("Arial", Font.BOLD, 14)
        );

        generateAdviceButton.setPreferredSize(
            new Dimension(220, 42)
        );

        generateAdviceButton.addActionListener(e -> {

            String prompt = buildSellerAdvisorPrompt();

            outputArea.setText(
                "Analyzing your seller profile and marketplace data...\n\n"
                + "Please wait while the AI advisor prepares "
                + "your recommendations."
            );

            generateAdviceButton.setEnabled(false);
            generateAdviceButton.setText("Analyzing...");

            SwingWorker<String, Void> worker =
                new SwingWorker<String, Void>() {

                @Override
                protected String doInBackground() throws Exception {

                    GeminiService advisor = new GeminiService();

                    return advisor.generateResponse(prompt);
                }

                @Override
                protected void done() {

                    generateAdviceButton.setEnabled(true);
                    generateAdviceButton.setText(
                        "Generate Seller Advice"
                    );

                    try {

                        outputArea.setText(get());
                        outputArea.setCaretPosition(0);

                    } catch (Exception ex) {

                        Throwable cause = ex.getCause();

                        String error = cause != null
                            ? cause.getMessage()
                            : ex.getMessage();

                        outputArea.setText(
                            "Unable to generate seller recommendations.\n\n"
                            + "Please check your Gemini API connection "
                            + "and try again.\n\n"
                            + "Technical details: " + error
                        );
                    }
                }
            };

            worker.execute();
        });

        JPanel buttonPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER)
        );

        buttonPanel.setBackground(panelColor);
        buttonPanel.add(generateAdviceButton);

        advisorPanel.add(buttonPanel, BorderLayout.SOUTH);

        return advisorPanel;
    }

    // Builds a prompt using the seller's actual application data
    private String buildSellerAdvisorPrompt() {

        StringBuilder prompt = new StringBuilder();

        prompt.append(
            "You are the SparkX AI Energy Trading Advisor. "
            + "Analyze the following seller data and provide "
            + "practical, clear and personalized recommendations.\n\n"
        );

        prompt.append("SELLER INFORMATION\n");
        prompt.append("-------------------------\n");

        prompt.append("Seller type: ")
            .append(seller.getSellerType()).append("\n");

        prompt.append("Energy source: ")
            .append(seller.getEnergySource()).append("\n");

        prompt.append("Profile configured: ")
            .append(seller.isProfileConfigured()).append("\n");

        prompt.append("\nENERGY PERFORMANCE\n");
        prompt.append("-------------------------\n");

        prompt.append("Total generated units: ")
            .append(seller.getGeneratedUnits()).append("\n");

        prompt.append("Available units: ")
            .append(seller.getAvailableUnits()).append("\n");

        prompt.append("Reserved units: ")
            .append(seller.getReservedUnits()).append("\n");

        prompt.append("Current price per unit: Rs. ")
            .append(seller.getPricePerUnit()).append("\n");

        prompt.append("Total earnings: Rs. ")
            .append(seller.getTotalEarnings()).append("\n");

        prompt.append("\nSELLER'S PUBLISHED OFFERS\n");
        prompt.append("-------------------------\n");

        int offerCount =
            marketplaceData.getTradingService().getOfferCount();

        boolean hasOwnOffers = false;

        for (int i = 0; i < offerCount; i++) {

            EnergyOffer offer =
                marketplaceData.getTradingService().getOffer(i);

            if (offer.getSeller().getUserId()
                    .equalsIgnoreCase(seller.getUserId())) {

                hasOwnOffers = true;

                prompt.append("Offer ID: ")
                    .append(offer.getOfferId())
                    .append(", Offered units: ")
                    .append(offer.getOfferedUnits())
                    .append(", Remaining units: ")
                    .append(offer.getRemainingUnits())
                    .append(", Price per unit: Rs. ")
                    .append(offer.getPricePerUnit())
                    .append(", Active: ")
                    .append(offer.isActive())
                    .append("\n");
            }
        }

        if (!hasOwnOffers) {
            prompt.append("The seller has not published any offers.\n");
        }

        prompt.append("\nCURRENT MARKETPLACE OFFERS\n");
        prompt.append("-------------------------\n");

        boolean hasMarketOffers = false;

        for (int i = 0; i < offerCount; i++) {

            EnergyOffer offer =
                marketplaceData.getTradingService().getOffer(i);

            if (offer.isActive()
                    && !offer.getSeller().getUserId()
                        .equalsIgnoreCase(seller.getUserId())) {

                hasMarketOffers = true;

                prompt.append("Energy source: ")
                    .append(offer.getSeller().getEnergySource())
                    .append(", Remaining units: ")
                    .append(offer.getRemainingUnits())
                    .append(", Price per unit: Rs. ")
                    .append(offer.getPricePerUnit())
                    .append("\n");
            }
        }

        if (!hasMarketOffers) {
            prompt.append("No other active marketplace offers are available.\n");
        }

        prompt.append("\nSALES PERFORMANCE\n");
        prompt.append("-------------------------\n");

        int transactionCount =
            marketplaceData.getTradingService()
                .getTransactionCount();

        int sellerSales = 0;
        double totalSalesRevenue = 0;

        for (int i = 0; i < transactionCount; i++) {

            Transaction transaction =
                marketplaceData.getTradingService()
                    .getTransaction(i);

            if (transaction.getSeller().getUserId()
                    .equalsIgnoreCase(seller.getUserId())) {

                sellerSales++;

                totalSalesRevenue += transaction.getTotalAmount();
            }
        }

        prompt.append("Completed sales: ")
            .append(sellerSales).append("\n");

        prompt.append("Revenue from recorded sales: Rs. ")
            .append(totalSalesRevenue).append("\n");

        prompt.append("\nYOUR TASK\n");
        prompt.append("-------------------------\n");

        prompt.append(
            "Provide your response using these sections:\n"
            + "1. Overall seller performance\n"
            + "2. Energy production analysis\n"
            + "3. Pricing recommendations\n"
            + "4. Comparison with available marketplace offers\n"
            + "5. Selling strategy and practical suggestions\n"
            + "6. Three specific actions the seller should consider\n\n"
            + "Use the supplied numbers in your analysis. "
            + "Do not invent missing information, market prices "
            + "or financial guarantees. Clearly identify suggestions "
            + "as recommendations rather than guaranteed outcomes. "
            + "Keep the response professional and easy to understand."
        );

        return prompt.toString();
    }

    // Creates the profile and offers overview
    private JPanel createOverviewPanel() {

        JPanel overviewPanel = new JPanel(
            new GridLayout(1, 2, 15, 0)
        );

        overviewPanel.setBackground(backgroundColor);

        overviewPanel.setBorder(
            BorderFactory.createEmptyBorder(15, 5, 15, 5)
        );

        overviewPanel.add(createProfilePanel());
        overviewPanel.add(createOffersPanel());

        return overviewPanel;
    }

    // Creates seller sales history
    private JPanel createSalesHistoryPanel() {

        JPanel historyPanel = new JPanel(
            new BorderLayout(10, 10)
        );

        historyPanel.setBackground(panelColor);

        historyPanel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titleLabel = new JLabel(
            "Your Completed Sales"
        );

        titleLabel.setFont(
            new Font("Arial", Font.BOLD, 19)
        );

        titleLabel.setForeground(greenColor);

        historyPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {
            "Transaction ID",
            "Offer ID",
            "Buyer",
            "Units Sold",
            "Price / Unit",
            "Total Earnings",
            "Transaction Date"
        };

        DefaultTableModel model = new DefaultTableModel(
            columns, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int transactionCount =
            marketplaceData.getTradingService()
                .getTransactionCount();

        int sellerTransactionCount = 0;

        double totalSales = 0;

        DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

        for (int i = 0; i < transactionCount; i++) {

            Transaction transaction =
                marketplaceData.getTradingService()
                    .getTransaction(i);

            if (transaction.getSeller().getUserId()
                    .equalsIgnoreCase(seller.getUserId())) {

                sellerTransactionCount++;

                totalSales += transaction.getTotalAmount();

                model.addRow(new Object[] {
                    transaction.getTransactionId(),
                    transaction.getOfferId(),
                    transaction.getBuyer().getFullName(),
                    String.format(
                        "%.2f", transaction.getPurchasedUnits()
                    ),
                    "Rs. " + String.format(
                        "%.2f", transaction.getPricePerUnit()
                    ),
                    "Rs. " + String.format(
                        "%.2f", transaction.getTotalAmount()
                    ),
                    transaction.getTransactionDate()
                        .format(dateFormatter)
                });
            }
        }

        JTable historyTable = new JTable(model);

        styleTable(historyTable);

        historyTable.setAutoResizeMode(
            JTable.AUTO_RESIZE_OFF
        );

        int[] columnWidths = {
            125, 100, 160, 115, 125, 145, 210
        };

        for (int i = 0; i < columnWidths.length; i++) {

            historyTable.getColumnModel()
                .getColumn(i)
                .setPreferredWidth(columnWidths[i]);
        }

        JLabel summaryLabel = new JLabel(
            "Total Sales: " + sellerTransactionCount
            + "     |     Earnings from Recorded Transactions: Rs. "
            + String.format("%.2f", totalSales)
        );

        summaryLabel.setForeground(mutedColor);

        summaryLabel.setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        historyPanel.add(summaryLabel, BorderLayout.SOUTH);

        if (sellerTransactionCount == 0) {

            JLabel emptyLabel = new JLabel(
                "You haven't completed any sales yet."
            );

            emptyLabel.setForeground(mutedColor);

            emptyLabel.setHorizontalAlignment(
                SwingConstants.CENTER
            );

            historyPanel.add(
                emptyLabel, BorderLayout.CENTER
            );

        } else {

            JScrollPane scrollPane = new JScrollPane(
                historyTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
            );

            scrollPane.getViewport().setBackground(panelColor);

            historyPanel.add(
                scrollPane, BorderLayout.CENTER
            );
        }

        return historyPanel;
    }

    // Creates seller information panel
    private JPanel createProfilePanel() {

        JPanel profilePanel = new JPanel(
            new GridLayout(4, 1, 5, 5)
        );

        profilePanel.setBackground(panelColor);

        profilePanel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel profileTitle = new JLabel(
            "Seller Information"
        );

        profileTitle.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

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

        return profilePanel;
    }

    // Creates the seller's published offers panel
    private JPanel createOffersPanel() {

        JPanel offersPanel = new JPanel(
            new BorderLayout(5, 8)
        );

        offersPanel.setBackground(panelColor);

        offersPanel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titleLabel = new JLabel(
            "My Published Offers"
        );

        titleLabel.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        titleLabel.setForeground(greenColor);

        offersPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {
            "Offer ID",
            "Units",
            "Remaining",
            "Price / Unit",
            "Status"
        };

        DefaultTableModel model = new DefaultTableModel(
            columns, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int offerCount =
            marketplaceData.getTradingService().getOfferCount();

        for (int i = 0; i < offerCount; i++) {

            EnergyOffer offer =
                marketplaceData.getTradingService().getOffer(i);

            if (offer.getSeller().getUserId().equals(
                    seller.getUserId())) {

                String status = offer.isActive()
                    ? "Active" : "Closed";

                model.addRow(new Object[] {
                    offer.getOfferId(),
                    String.format("%.2f", offer.getOfferedUnits()),
                    String.format("%.2f", offer.getRemainingUnits()),
                    "Rs. " + String.format(
                        "%.2f", offer.getPricePerUnit()
                    ),
                    status
                });
            }
        }

        if (model.getRowCount() == 0) {

            JLabel emptyLabel = new JLabel(
                "No offers published yet."
            );

            emptyLabel.setForeground(mutedColor);

            emptyLabel.setFont(
                new Font("Arial", Font.PLAIN, 14)
            );

            offersPanel.add(emptyLabel, BorderLayout.CENTER);

        } else {

            JTable offersTable = new JTable(model);

            styleTable(offersTable);

            JScrollPane scrollPane =
                new JScrollPane(offersTable);

            scrollPane.getViewport().setBackground(panelColor);

            offersPanel.add(
                scrollPane, BorderLayout.CENTER
            );
        }

        return offersPanel;
    }

    // Common table styling
    private void styleTable(JTable table) {

        table.setBackground(panelColor);
        table.setForeground(textColor);
        table.setGridColor(new Color(55, 65, 75));

        table.setRowHeight(29);

        table.setFont(
            new Font("Arial", Font.PLAIN, 12)
        );

        table.setSelectionBackground(
            new Color(42, 91, 140)
        );

        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);

        table.getTableHeader().setBackground(
            new Color(35, 48, 57)
        );

        table.getTableHeader().setForeground(textColor);

        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 12)
        );
    }

    // Creates dashboard information cards
    private JPanel createInfoCard(String title, String value) {

        JPanel card = new JPanel();

        card.setBackground(panelColor);

        card.setLayout(
            new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        card.setBorder(
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        titleLabel.setForeground(mutedColor);

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);

        valueLabel.setFont(
            new Font("Arial", Font.BOLD, 25)
        );

        valueLabel.setForeground(greenColor);

        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    // Styles seller information labels
    private void styleProfileLabel(JLabel label) {

        label.setFont(
            new Font("Arial", Font.PLAIN, 14)
        );

        label.setForeground(textColor);
    }
}

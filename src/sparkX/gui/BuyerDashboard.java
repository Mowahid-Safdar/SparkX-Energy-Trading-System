package sparkX.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;

import sparkX.ai.GroqService;
import sparkX.data.MarketplaceData;
import sparkX.model.Buyer;
import sparkX.model.EnergyOffer;
import sparkX.model.Transaction;

public class BuyerDashboard extends JFrame {

    private MarketplaceData marketplaceData;
    private Buyer buyer;

    private final Color backgroundColor = new Color(16, 20, 24);
    private final Color panelColor = new Color(27, 35, 43);
    private final Color blueColor = new Color(49, 139, 255);
    private final Color textColor = new Color(241, 245, 249);
    private final Color mutedColor = new Color(154, 168, 181);

    private JPanel mainPanel;

    private JTable offersTable;
    private EnergyOffer[] displayedOffers;
    private int displayedOfferCount;

    private JTextArea advisorOutput;
    private JButton advisorButton;

    private GroqService groqService;

    public BuyerDashboard(
            MarketplaceData marketplaceData,
            Buyer buyer) {

        this.marketplaceData = marketplaceData;
        this.buyer = buyer;

        groqService = new GroqService();

        setTitle("SparkX - Buyer Dashboard");
        setSize(1250, 850);
        setMinimumSize(new Dimension(1100, 750));
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
            "SparkX | Buyer Dashboard"
        );

        titleLabel.setFont(
            new Font("Arial", Font.BOLD, 24)
        );

        titleLabel.setForeground(blueColor);

        JLabel welcomeLabel = new JLabel(
            "Welcome, " + buyer.getFullName()
        );

        welcomeLabel.setFont(
            new Font("Arial", Font.PLAIN, 15)
        );

        welcomeLabel.setForeground(textColor);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // MAIN CONTENT
        mainPanel = new JPanel(new BorderLayout());

        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(
            BorderFactory.createEmptyBorder(15, 30, 15, 30)
        );

        add(mainPanel, BorderLayout.CENTER);

        // FOOTER
        JPanel footerPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 12, 12)
        );

        footerPanel.setBackground(backgroundColor);

        JButton purchaseButton =
            new JButton("Purchase Selected Offer");

        styleButton(purchaseButton, blueColor, Color.WHITE);

        purchaseButton.addActionListener(
            e -> purchaseSelectedOffer()
        );

        JButton refreshButton =
            new JButton("Refresh Marketplace");

        styleButton(refreshButton, panelColor, textColor);

        refreshButton.addActionListener(
            e -> refreshDashboard()
        );

        JButton configureButton =
            new JButton("Configure Profile");

        styleButton(configureButton, panelColor, textColor);

        configureButton.addActionListener(
            e -> openProfileScreen()
        );

        JButton logoutButton = new JButton("Logout");

        styleButton(logoutButton, panelColor, textColor);

        logoutButton.addActionListener(e -> {
            new WelcomeScreen(marketplaceData);
            dispose();
        });

        footerPanel.add(purchaseButton);
        footerPanel.add(refreshButton);
        footerPanel.add(configureButton);
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

    // Opens buyer profile configuration
    private void openProfileScreen() {

        setEnabled(false);

        BuyerProfileScreen profileScreen =
            new BuyerProfileScreen(buyer, marketplaceData);

        profileScreen.addWindowListener(
            new WindowAdapter() {

                @Override
                public void windowClosed(WindowEvent e) {

                    refreshDashboard();

                    setEnabled(true);
                    toFront();
                }
            }
        );
    }

    // Refreshes dashboard information
    private void refreshDashboard() {

        mainPanel.removeAll();

        JPanel contentPanel = new JPanel();

        contentPanel.setBackground(backgroundColor);
        contentPanel.setLayout(
            new BoxLayout(contentPanel, BoxLayout.Y_AXIS)
        );

        JLabel overviewLabel =
            new JLabel("Your Overview");

        overviewLabel.setFont(
            new Font("Arial", Font.BOLD, 21)
        );

        overviewLabel.setForeground(textColor);
        overviewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // INFORMATION CARDS
        JPanel cardsPanel = new JPanel(
            new GridLayout(1, 3, 15, 0)
        );

        cardsPanel.setBackground(backgroundColor);

        cardsPanel.add(createInfoCard(
            "Remaining Demand",
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
            "Marketplace",
            createMarketplacePanel()
        );

        tabbedPane.addTab(
            "Transaction History",
            createTransactionHistoryPanel()
        );

        tabbedPane.addTab(
            "AI Energy Advisor",
            createAdvisorPanel()
        );

        tabbedPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        tabbedPane.setPreferredSize(
            new Dimension(1150, 440)
        );

        tabbedPane.setMinimumSize(
            new Dimension(900, 350)
        );

        // BUYER INFORMATION
        JPanel profilePanel = createProfilePanel();

        profilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        profilePanel.setPreferredSize(
            new Dimension(1150, 85)
        );

        profilePanel.setMinimumSize(
            new Dimension(900, 85)
        );

        profilePanel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 85)
        );

        contentPanel.add(overviewLabel);
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(cardsPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(tabbedPane);
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(profilePanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Creates marketplace panel
    private JPanel createMarketplacePanel() {

        JPanel marketplacePanel = new JPanel(
            new BorderLayout(10, 10)
        );

        marketplacePanel.setBackground(panelColor);

        marketplacePanel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titleLabel = new JLabel(
            "Available Energy Offers"
        );

        titleLabel.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        titleLabel.setForeground(blueColor);

        marketplacePanel.add(
            titleLabel, BorderLayout.NORTH
        );

        String[] columns = {
            "Offer ID",
            "Seller",
            "Energy Source",
            "Available Units",
            "Price / Unit"
        };

        DefaultTableModel model = new DefaultTableModel(
            columns, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        int totalOffers =
            marketplaceData.getTradingService().getOfferCount();

        displayedOffers = new EnergyOffer[totalOffers];
        displayedOfferCount = 0;

        for (int i = 0; i < totalOffers; i++) {

            EnergyOffer offer =
                marketplaceData.getTradingService().getOffer(i);

            if (offer.isActive()
                    && offer.getRemainingUnits() > 0) {

                displayedOffers[displayedOfferCount] = offer;
                displayedOfferCount++;

                model.addRow(new Object[] {
                    offer.getOfferId(),
                    offer.getSeller().getFullName(),
                    offer.getSeller().getEnergySource(),
                    String.format(
                        "%.2f", offer.getRemainingUnits()
                    ),
                    "Rs. " + String.format(
                        "%.2f", offer.getPricePerUnit()
                    )
                });
            }
        }

        offersTable = new JTable(model);

        styleTable(offersTable);

        offersTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        if (displayedOfferCount == 0) {

            JLabel emptyLabel = new JLabel(
                "No active energy offers are currently available."
            );

            emptyLabel.setForeground(mutedColor);
            emptyLabel.setHorizontalAlignment(
                SwingConstants.CENTER
            );

            marketplacePanel.add(
                emptyLabel, BorderLayout.CENTER
            );

        } else {

            JScrollPane scrollPane =
                new JScrollPane(offersTable);

            scrollPane.getViewport().setBackground(panelColor);

            marketplacePanel.add(
                scrollPane, BorderLayout.CENTER
            );
        }

        return marketplacePanel;
    }

    // Creates transaction history panel
    private JPanel createTransactionHistoryPanel() {

        JPanel historyPanel = new JPanel(
            new BorderLayout(10, 10)
        );

        historyPanel.setBackground(panelColor);

        historyPanel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titleLabel = new JLabel(
            "Your Purchase History"
        );

        titleLabel.setFont(
            new Font("Arial", Font.BOLD, 18)
        );

        titleLabel.setForeground(blueColor);

        historyPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {
            "Transaction ID",
            "Offer ID",
            "Seller",
            "Units Purchased",
            "Price / Unit",
            "Total Paid",
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

        int buyerTransactionCount = 0;

        DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

        for (int i = 0; i < transactionCount; i++) {

            Transaction transaction =
                marketplaceData.getTradingService()
                    .getTransaction(i);

            if (transaction.getBuyer().getUserId()
                    .equalsIgnoreCase(buyer.getUserId())) {

                buyerTransactionCount++;

                model.addRow(new Object[] {
                    transaction.getTransactionId(),
                    transaction.getOfferId(),
                    transaction.getSeller().getFullName(),
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
            125, 100, 160, 135, 125, 125, 210
        };

        for (int i = 0; i < columnWidths.length; i++) {

            historyTable.getColumnModel()
                .getColumn(i)
                .setPreferredWidth(columnWidths[i]);
        }

        JLabel countLabel = new JLabel(
            "Total Transactions: " + buyerTransactionCount
        );

        countLabel.setForeground(mutedColor);
        countLabel.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        historyPanel.add(countLabel, BorderLayout.SOUTH);

        if (buyerTransactionCount == 0) {

            JLabel emptyLabel = new JLabel(
                "You haven't made any purchases yet."
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

    // Creates AI Energy Advisor panel
    private JPanel createAdvisorPanel() {

        JPanel advisorPanel = new JPanel(
            new BorderLayout(12, 12)
        );

        advisorPanel.setBackground(panelColor);

        advisorPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel heading = new JLabel(
            "Your Personal Energy Advisor"
        );

        heading.setFont(
            new Font("Arial", Font.BOLD, 20)
        );

        heading.setForeground(blueColor);

        JLabel description = new JLabel(
            "<html>Get a practical offer recommendation based on "
            + "your remaining demand, price limit and marketplace.</html>"
        );

        description.setFont(
            new Font("Arial", Font.PLAIN, 13)
        );

        description.setForeground(mutedColor);

        JPanel topPanel = new JPanel(
            new BorderLayout(5, 8)
        );

        topPanel.setBackground(panelColor);

        topPanel.add(heading, BorderLayout.NORTH);
        topPanel.add(description, BorderLayout.CENTER);

        advisorOutput = new JTextArea();

        advisorOutput.setEditable(false);
        advisorOutput.setLineWrap(true);
        advisorOutput.setWrapStyleWord(true);

        advisorOutput.setFont(
            new Font("Arial", Font.PLAIN, 15)
        );

        advisorOutput.setBackground(backgroundColor);
        advisorOutput.setForeground(textColor);

        advisorOutput.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        advisorOutput.setText(
            "Click the button below to find the most suitable "
            + "energy offer for your remaining demand."
        );

        JScrollPane outputScrollPane =
            new JScrollPane(advisorOutput);

        outputScrollPane.getViewport().setBackground(backgroundColor);

        advisorButton = new JButton("Get Offer Recommendation");

        styleButton(advisorButton, blueColor, Color.WHITE);

        advisorButton.setFont(
            new Font("Arial", Font.BOLD, 14)
        );

        advisorButton.setPreferredSize(
            new Dimension(230, 42)
        );

        advisorButton.addActionListener(
            e -> generateBuyerAdvice()
        );

        JPanel bottomPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT)
        );

        bottomPanel.setBackground(panelColor);
        bottomPanel.add(advisorButton);

        advisorPanel.add(topPanel, BorderLayout.NORTH);
        advisorPanel.add(outputScrollPane, BorderLayout.CENTER);
        advisorPanel.add(bottomPanel, BorderLayout.SOUTH);

        return advisorPanel;
    }

    // Generates buyer recommendations using Groq
    private void generateBuyerAdvice() {

        if (!buyer.isProfileConfigured()) {

            JOptionPane.showMessageDialog(
                this,
                "Please configure your buyer profile first.",
                "Profile Required",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (buyer.getRequiredUnits() <= 0) {

            advisorOutput.setText(
                "Your energy demand has already been satisfied."
            );

            return;
        }

        boolean suitableOfferFound = false;

        int offerCount =
            marketplaceData.getTradingService().getOfferCount();

        for (int i = 0; i < offerCount; i++) {

            EnergyOffer offer =
                marketplaceData.getTradingService().getOffer(i);

            if (offer.isActive()
                    && offer.getRemainingUnits() > 0
                    && offer.getPricePerUnit()
                        <= buyer.getMaxPricePerUnit()) {

                suitableOfferFound = true;
                break;
            }
        }

        if (!suitableOfferFound) {

            advisorOutput.setText(
                "No active energy offer currently meets your "
                + "maximum price of Rs. "
                + String.format(
                    "%.2f", buyer.getMaxPricePerUnit()
                )
                + " per unit."
            );

            return;
        }

        String prompt = buildBuyerPrompt();

        advisorButton.setEnabled(false);
        advisorButton.setText("Analysing Offers...");

        advisorOutput.setText(
            "Analysing your demand and available offers..."
        );

        SwingWorker<String, Void> worker =
            new SwingWorker<String, Void>() {

                @Override
                protected String doInBackground() throws Exception {

                    return groqService.generateResponse(prompt);
                }

                @Override
                protected void done() {

                    advisorButton.setEnabled(true);
                    advisorButton.setText(
                        "Get Offer Recommendation"
                    );

                    try {

                        String response = get();

                        String recommendedOfferId =
                            extractAIField(response, "OFFER_ID");

                        double recommendedUnits =
                            Double.parseDouble(
                                extractAIField(response, "QUANTITY")
                            );

                        EnergyOffer recommendedOffer = null;

                        int count =
                            marketplaceData.getTradingService()
                                .getOfferCount();

                        for (int i = 0; i < count; i++) {

                            EnergyOffer offer =
                                marketplaceData.getTradingService()
                                    .getOffer(i);

                            if (String.valueOf(offer.getOfferId())
                                    .equalsIgnoreCase(
                                        recommendedOfferId
                                    )) {

                                recommendedOffer = offer;
                                break;
                            }
                        }

                        if (recommendedOffer == null
                                || !recommendedOffer.isActive()
                                || recommendedOffer.getRemainingUnits() <= 0
                                || recommendedOffer.getPricePerUnit()
                                    > buyer.getMaxPricePerUnit()) {

                            throw new IllegalStateException(
                                "AI selected an offer that is no longer "
                                + "available or does not meet your price limit."
                            );
                        }

                        double maximumQuantity = Math.min(
                            buyer.getRequiredUnits(),
                            recommendedOffer.getRemainingUnits()
                        );

                        if (Double.isNaN(recommendedUnits)
                                || Double.isInfinite(recommendedUnits)
                                || recommendedUnits <= 0
                                || recommendedUnits > maximumQuantity) {

                            throw new IllegalStateException(
                                "AI returned an invalid purchase quantity."
                            );
                        }

                        double price =
                            recommendedOffer.getPricePerUnit();

                        double totalPayment =
                            recommendedUnits * price;

                        String advice =
                            "Purchase "
                            + String.format("%.2f", recommendedUnits)
                            + " units of "
                            + recommendedOffer.getSeller().getEnergySource()
                            + " energy from Offer #"
                            + recommendedOffer.getOfferId()
                            + " at Rs. "
                            + String.format("%.2f", price)
                            + " per unit, for a total payment of Rs. "
                            + String.format("%.2f", totalPayment)
                            + ".";

                        advisorOutput.setText(advice);
                        advisorOutput.setCaretPosition(0);

                    } catch (Exception ex) {

                        Throwable cause = ex.getCause();

                        String error = cause != null
                            ? cause.getMessage()
                            : ex.getMessage();

                        advisorOutput.setText(
                            "Unable to generate a valid offer "
                            + "recommendation. Please try again.\n\n"
                            + "Details: " + error
                        );

                        advisorOutput.setCaretPosition(0);
                    }
                }
            };

        worker.execute();
    }

    // Builds a prompt using current buyer and marketplace data
    private String buildBuyerPrompt() {

        StringBuilder prompt = new StringBuilder();

        prompt.append(
            "You are the AI Energy Advisor inside SparkX, "
            + "a decentralized peer-to-peer energy trading system.\n\n"
        );

        prompt.append("BUYER INFORMATION\n");

        prompt.append("Buyer: ")
            .append(buyer.getFullName()).append("\n");

        prompt.append("Remaining demand: ")
            .append(buyer.getRequiredUnits())
            .append(" units\n");

        prompt.append("Maximum price per unit: Rs. ")
            .append(buyer.getMaxPricePerUnit()).append("\n");

        prompt.append("Amount already spent: Rs. ")
            .append(buyer.getTotalSpent()).append("\n");

        prompt.append("\nELIGIBLE MARKETPLACE OFFERS\n");

        prompt.append(
            "Only offers at or below the buyer's maximum price "
            + "are listed below.\n"
        );

        int offerCount =
            marketplaceData.getTradingService().getOfferCount();

        for (int i = 0; i < offerCount; i++) {

            EnergyOffer offer =
                marketplaceData.getTradingService().getOffer(i);

            if (offer.isActive()
                    && offer.getRemainingUnits() > 0
                    && offer.getPricePerUnit()
                        <= buyer.getMaxPricePerUnit()) {

                prompt.append("\nOffer ID: ")
                    .append(offer.getOfferId());

                prompt.append("\nSeller: ")
                    .append(offer.getSeller().getFullName());

                prompt.append("\nEnergy source: ")
                    .append(offer.getSeller().getEnergySource());

                prompt.append("\nAvailable quantity: ")
                    .append(offer.getRemainingUnits());

                prompt.append("\nPrice per unit: Rs. ")
                    .append(offer.getPricePerUnit());

                prompt.append("\n");
            }
        }

        prompt.append(
            "\nYOUR TASK\n"
            + "Choose the most economical suitable offer for the buyer.\n"
            + "Prefer the lowest price per unit, while ensuring that "
            + "the offer has enough available energy.\n"
            + "Recommend a quantity that does not exceed the buyer's "
            + "remaining demand or the offer's available quantity.\n\n"
        );

        prompt.append("STRICT RESPONSE FORMAT:\n");

        prompt.append("OFFER_ID: [exact offer ID]\n");
        prompt.append("QUANTITY: [numeric quantity]\n\n");

        prompt.append(
            "Return only these two lines. Do not include explanations, "
            + "headings, Markdown, or additional text. Do not invent "
            + "offer IDs, prices, or quantities. Do not claim that "
            + "a purchase has been completed."
        );

        return prompt.toString();
    }

    // Extracts a value from the AI response
    private String extractAIField(
            String response,
            String key) {

        String[] lines = response.split("\\r?\\n");

        for (String line : lines) {

            String cleanedLine = line.trim();

            if (cleanedLine.startsWith(key + ":")) {

                return cleanedLine.substring(
                    key.length() + 1
                ).trim();
            }
        }

        throw new IllegalStateException(
            "AI response is missing " + key
        );
    }

    // Applies common table styling
    private void styleTable(JTable table) {

        table.setBackground(panelColor);
        table.setForeground(textColor);
        table.setGridColor(new Color(55, 65, 75));

        table.setRowHeight(30);

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

    // Creates buyer information panel
    private JPanel createProfilePanel() {

        JPanel profilePanel = new JPanel(
            new GridLayout(1, 4, 15, 0)
        );

        profilePanel.setBackground(panelColor);

        profilePanel.setBorder(
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        );

        JLabel titleLabel =
            new JLabel("Buyer Information");

        titleLabel.setFont(
            new Font("Arial", Font.BOLD, 16)
        );

        titleLabel.setForeground(blueColor);

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

        profilePanel.add(titleLabel);
        profilePanel.add(consumerTypeLabel);
        profilePanel.add(emailLabel);
        profilePanel.add(demandStatusLabel);

        return profilePanel;
    }

    // Purchases selected marketplace offer
    private void purchaseSelectedOffer() {

        if (!buyer.isProfileConfigured()) {

            JOptionPane.showMessageDialog(
                this,
                "Please configure your buyer profile first.",
                "Profile Required",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (offersTable == null
                || offersTable.getSelectedRow() == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select an energy offer first.",
                "No Offer Selected",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int selectedRow = offersTable.getSelectedRow();

        EnergyOffer selectedOffer =
            displayedOffers[selectedRow];

        if (selectedOffer == null
                || !selectedOffer.isActive()
                || selectedOffer.getRemainingUnits() <= 0) {

            JOptionPane.showMessageDialog(
                this,
                "This offer is no longer available. "
                + "Please refresh the marketplace.",
                "Offer Unavailable",
                JOptionPane.WARNING_MESSAGE
            );

            refreshDashboard();
            return;
        }

        double demand = buyer.getRequiredUnits();

        if (demand <= 0) {

            JOptionPane.showMessageDialog(
                this,
                "Your energy demand has already been satisfied.",
                "Demand Satisfied",
                JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        double maximumPurchase = Math.min(
            demand,
            selectedOffer.getRemainingUnits()
        );

        String input = JOptionPane.showInputDialog(
            this,
            "Selected Offer: " + selectedOffer.getOfferId()
            + "\nSeller: "
            + selectedOffer.getSeller().getFullName()
            + "\nAvailable Units: "
            + String.format(
                "%.2f", selectedOffer.getRemainingUnits()
            )
            + "\nYour Remaining Demand: "
            + String.format("%.2f", demand)
            + "\nPrice Per Unit: Rs. "
            + String.format(
                "%.2f", selectedOffer.getPricePerUnit()
            )
            + "\n\nMaximum units you can purchase: "
            + String.format("%.2f", maximumPurchase)
            + "\n\nEnter purchase quantity:",
            "Purchase Energy",
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
                    || units > maximumPurchase) {

                JOptionPane.showMessageDialog(
                    this,
                    "Enter a quantity greater than 0 and no more than "
                    + String.format("%.2f", maximumPurchase)
                    + " units.",
                    "Invalid Quantity",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            double totalCost =
                units * selectedOffer.getPricePerUnit();

            int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Confirm your purchase?\n\n"
                + "Offer ID: " + selectedOffer.getOfferId()
                + "\nUnits: " + String.format("%.2f", units)
                + "\nTotal Cost: Rs. "
                + String.format("%.2f", totalCost),
                "Confirm Purchase",
                JOptionPane.YES_NO_OPTION
            );

            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }

            marketplaceData.getTradingService().buyUnits(
                buyer, selectedOffer, units
            );

            marketplaceData.saveUsers();

            refreshDashboard();

            JOptionPane.showMessageDialog(
                this,
                "Purchase completed successfully!\n\n"
                + "Energy Purchased: "
                + String.format("%.2f", units)
                + " units\nTotal Paid: Rs. "
                + String.format("%.2f", totalCost)
                + "\nRemaining Demand: "
                + String.format(
                    "%.2f", buyer.getRequiredUnits()
                ),
                "Purchase Successful",
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
                "Purchase Failed",
                JOptionPane.ERROR_MESSAGE
            );

            refreshDashboard();
        }
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
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
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
        card.add(Box.createVerticalStrut(12));
        card.add(valueLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    // Styles buyer information labels
    private void styleProfileLabel(JLabel label) {

        label.setFont(
            new Font("Arial", Font.PLAIN, 12)
        );

        label.setForeground(textColor);
    }
}
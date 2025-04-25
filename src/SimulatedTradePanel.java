import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * A Swing-based UI panel that allows users to simulate buying and selling stocks
 * in a visual trading environment. It mimics trading behavior without affecting
 * the actual market or other buyers. Tracks virtual shares, cash, and profit/loss.
 */
public class SimulatedTradePanel extends JPanel {

    /**
     * Reference to the stock market from which current prices will be retrieved
     * during buy/sell simulations.
     */
    private final StockMarket stockMarket;

    /**
     * Tracks the number of shares currently "owned" in the simulation.
     */
    private int simulatedShares = 0;

    /**
     * Total amount of money spent on buying shares throughout the simulation.
     */
    private double totalSpent = 0;

    /**
     * Total amount of money earned from selling shares during the simulation.
     */
    private double totalRevenue = 0;

    /**
     * Virtual cash balance available to simulate purchases.
     * Starts with an initial value of $1000.
     */
    private double cash = 1000;

    /**
     * Label that displays the current stock price from the market.
     */
    private final JLabel priceLabel;

    /**
     * Label that shows the number of shares owned by the user in the simulation.
     */
    private final JLabel sharesLabel;

    /**
     * Label showing the net profit or loss from simulated trading.
     */
    private final JLabel profitLabel;

    /**
     * Label displaying the current amount of virtual cash available to the user.
     */
    private final JLabel cashLabel;

    /**
     * Input field where the user specifies the number of shares to buy or sell in a simulated trade.
     */
    private final JTextField amountField;

    /**
     * Button that triggers the simulation of a buy action based on the amount entered.
     */
    private final JButton buyButton;

    /**
     * Button that triggers the simulation of a sell action based on the amount entered.
     */
    private final JButton sellButton;


    /**
     * Initializes the trading panel and constructs the UI.
     * Sets layout, styles, event listeners, and connects to the provided stock market.
     *
     * @param stockMarket the market object providing price data
     */
    public SimulatedTradePanel(StockMarket stockMarket) {
        this.stockMarket = stockMarket;

        // Grid layout: 8 rows, 1 column, with padding between rows
        setLayout(new GridLayout(8, 1, 5, 5));
        setBackground(Color.BLACK);

        // Titled border around the panel with white styling
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                "Simulated Trading");
        border.setTitleColor(Color.WHITE);
        setBorder(border);

        // Create all labels for stats display
        priceLabel = new JLabel();
        sharesLabel = new JLabel();
        profitLabel = new JLabel();
        cashLabel = new JLabel();

        // Create input field with a default value of "10"
        amountField = new JTextField("10");

        // Initialize buttons for buy and sell operations
        buyButton = new JButton("Simulate Buy");
        sellButton = new JButton("Simulate Sell");

        // Populate the labels with initial values
        updateLabels();

        // Set button behavior to execute simulation logic
        buyButton.addActionListener(this::simulateBuy);
        sellButton.addActionListener(this::simulateSell);

        // Style buttons (dark theme)
        buyButton.setBackground(Color.DARK_GRAY);
        sellButton.setBackground(Color.DARK_GRAY);
        buyButton.setForeground(Color.WHITE);
        sellButton.setForeground(Color.WHITE);
        buyButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        sellButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        // Style and add each label to the panel
        for (JLabel label : new JLabel[]{priceLabel, sharesLabel, profitLabel, cashLabel}) {
            label.setForeground(Color.WHITE);
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }

        // Add each stat label to the layout
        add(priceLabel);
        add(sharesLabel);
        add(cashLabel);
        add(profitLabel);

        // Create a horizontal row for the amount input field
        JPanel amountRow = new JPanel();
        amountRow.setLayout(new BoxLayout(amountRow, BoxLayout.X_AXIS));
        amountRow.setOpaque(false); // transparent background

        // Label for input field
        JLabel amountLabel = new JLabel("Amount to simulate:");
        amountLabel.setForeground(Color.WHITE);

        // Style the text field
        amountField.setPreferredSize(new Dimension(60, 24));
        amountField.setMaximumSize(new Dimension(60, 24));
        amountField.setBackground(Color.BLACK);
        amountField.setForeground(Color.WHITE);
        amountField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        amountField.setHorizontalAlignment(JTextField.CENTER); // center text

        // Add components to the row with spacing and alignment
        amountRow.add(Box.createHorizontalGlue());
        amountRow.add(amountLabel);
        amountRow.add(Box.createHorizontalStrut(10));
        amountRow.add(amountField);
        amountRow.add(Box.createHorizontalGlue());

        // Add the row and buttons to the panel
        add(amountRow);
        add(buyButton);
        add(sellButton);
    }

    /**
     * Updates the text on all labels to reflect the current simulated state.
     * Calculates net profit/loss including unrealized gain from held shares.
     */
    public void updateLabels() {
        double marketPrice = stockMarket.getCurrentPrice();

        priceLabel.setText(String.format("Current Price: $%.2f", marketPrice));
        sharesLabel.setText("Simulated Shares Owned: " + simulatedShares);
        cashLabel.setText(String.format("Cash: $%.2f", cash));

        // Net P/L = profit from sells - cost of buys + unrealized gain on remaining shares
        double netProfit = totalRevenue - totalSpent + (simulatedShares * marketPrice);
        profitLabel.setText(String.format("Net P/L: $%.2f", netProfit));
    }

    /**
     * Simulates the purchase of shares based on the current price and input amount.
     * Deducts from cash and increases the number of simulated shares owned.
     *
     * @param e the action event triggered by the buy button
     */
    private void simulateBuy(ActionEvent e) {
        try {
            int amount = Integer.parseInt(amountField.getText());

            if (amount > 0) {
                double price = stockMarket.getCurrentPrice();
                double totalCost = amount * price;

                // Check if user has enough virtual cash to buy
                if (cash >= totalCost) {
                    simulatedShares += amount;
                    totalSpent += totalCost;
                    cash -= totalCost;
                    updateLabels();
                } else {
                    JOptionPane.showMessageDialog(this, "Not enough cash to buy " + amount + " shares.");
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
        }
    }

    /**
     * Simulates the sale of shares at the current price.
     * Adds to cash and records revenue. Prevents selling more than owned.
     *
     * @param e the action event triggered by the sell button
     */
    private void simulateSell(ActionEvent e) {
        try {
            int amount = Integer.parseInt(amountField.getText());

            if (amount > 0 && amount <= simulatedShares) {
                double price = stockMarket.getCurrentPrice();
                double totalGain = amount * price;

                simulatedShares -= amount;
                totalRevenue += totalGain;
                cash += totalGain;
                updateLabels();
            } else if (amount > simulatedShares) {
                JOptionPane.showMessageDialog(this, "You don't have that many shares.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input.");
        }
    }
}

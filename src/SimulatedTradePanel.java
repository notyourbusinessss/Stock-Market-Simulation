import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SimulatedTradePanel extends JPanel {
    private final StockMarket stockMarket;

    private int simulatedShares = 0;
    private double totalSpent = 0;
    private double totalRevenue = 0;
    private double cash = 1000;

    private final JLabel priceLabel;
    private final JLabel sharesLabel;
    private final JLabel profitLabel;
    private final JLabel cashLabel;

    private final JTextField amountField;
    private final JButton buyButton;
    private final JButton sellButton;

    public SimulatedTradePanel(StockMarket stockMarket) {
        this.stockMarket = stockMarket;

        setLayout(new GridLayout(8, 1, 5, 5));
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Simulated Trading"));

        priceLabel = new JLabel();
        sharesLabel = new JLabel();
        profitLabel = new JLabel();
        cashLabel = new JLabel();

        amountField = new JTextField("10");
        buyButton = new JButton("Simulate Buy");
        sellButton = new JButton("Simulate Sell");

        updateLabels();

        buyButton.addActionListener(this::simulateBuy);
        sellButton.addActionListener(this::simulateSell);

        for (JLabel label : new JLabel[]{priceLabel, sharesLabel, profitLabel, cashLabel}) {
            label.setForeground(Color.WHITE);
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }

        add(priceLabel);
        add(sharesLabel);
        add(cashLabel);
        add(profitLabel);
        add(new JLabel("Amount to simulate:", SwingConstants.CENTER));
        add(amountField);
        add(buyButton);
        add(sellButton);
    }

    public void updateLabels() {
        double marketPrice = stockMarket.getCurrentPrice();
        priceLabel.setText(String.format("Current Price: $%.2f", marketPrice));
        sharesLabel.setText("Simulated Shares Owned: " + simulatedShares);
        cashLabel.setText(String.format("Cash: $%.2f", cash));
        double netProfit = totalRevenue - totalSpent + (simulatedShares * marketPrice);
        profitLabel.setText(String.format("Net P/L: $%.2f", netProfit));
    }

    private void simulateBuy(ActionEvent e) {
        try {
            int amount = Integer.parseInt(amountField.getText());
            if (amount > 0) {
                double price = stockMarket.getCurrentPrice();
                double totalCost = amount * price;

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
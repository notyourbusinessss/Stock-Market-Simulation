import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * BuyerStatsPanel is a Swing-based UI panel that displays detailed statistics
 * for individual buyers in a stock market simulation.
 *
 * It includes a dropdown to select a buyer and displays real-time stats such as:
 * - Starting Net Worth
 * - Capital
 * - Net Profit
 * - Estimated Net Worth
 * - Net Worth Change
 * - Shares Held
 *
 * Stats update every second using a Swing Timer.
 */
public class BuyerStatsPanel extends JPanel {

    private final List<Buyer> buyers;
    private final StockMarket stockMarket;
    private final Timer refreshTimer;
    private final JComboBox<String> buyerSelector;
    private final JLabel statLabel;

    /**
     * Constructs a BuyerStatsPanel for monitoring the performance of buyers in the market.
     *
     * @param buyers       a list of Buyer objects to track
     * @param stockMarket  reference to the StockMarket used for current price lookups
     */
    public BuyerStatsPanel(List<Buyer> buyers, StockMarket stockMarket) {
        this.buyers = buyers;
        this.stockMarket = stockMarket;

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.BLACK);

        // Set titled border with white color
        TitledBorder border = BorderFactory.createTitledBorder("Buyer Statistics");
        border.setTitleColor(Color.WHITE);
        border.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        setBorder(border);

        // Dropdown to select buyer
        buyerSelector = new JComboBox<>();
        buyerSelector.setBackground(Color.DARK_GRAY);
        buyerSelector.setForeground(Color.WHITE);
        for (Buyer buyer : buyers) {
            buyerSelector.addItem(buyer.getName());
        }
        buyerSelector.addActionListener(this::onBuyerSelected);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.BLACK);
        topPanel.add(new JLabel("Track Buyer: ") {{
            setForeground(Color.WHITE);
        }});
        topPanel.add(buyerSelector);

        // Stats area
        statLabel = new JLabel();
        statLabel.setForeground(Color.WHITE);
        statLabel.setVerticalAlignment(SwingConstants.TOP);
        statLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(statLabel);
        scrollPane.setBackground(Color.BLACK);
        scrollPane.getViewport().setBackground(Color.BLACK);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        refreshData(); // initial display

        // Timer to auto-refresh stats every second
        refreshTimer = new Timer(1000, e -> refreshData());
        refreshTimer.start();
    }

    /**
     * Called when a new buyer is selected from the dropdown. Triggers a stats refresh.
     *
     * @param e the action event (not used)
     */
    private void onBuyerSelected(ActionEvent e) {
        refreshData();
    }

    /**
     * Gathers statistics from the selected buyer and updates the display.
     * Colors values red if they are negative for clear visual feedback.
     */
    private void refreshData() {
        String selectedBuyerName = (String) buyerSelector.getSelectedItem();
        Buyer selectedBuyer = buyers.stream()
                .filter(b -> b.getName().equals(selectedBuyerName))
                .findFirst().orElse(null);

        if (selectedBuyer != null) {
            double startNetWorth = selectedBuyer.StartNetWorth;
            double netWorth = selectedBuyer.getCapital() + selectedBuyer.getHolding() * stockMarket.getCurrentPrice();
            double netProfit = selectedBuyer.getNetProfit();
            double capital = selectedBuyer.getCapital();
            int shares = selectedBuyer.getHolding();
            double NetworthChange = netWorth - startNetWorth;

            // Set color based on value
            String capitalColor = capital < 0 ? "red" : "white";
            String profitColor = netProfit < 0 ? "red" : "white";
            String worthColor = netWorth < 0 ? "red" : "white";
            String sharesColor = shares < 0 ? "red" : "white";
            String NetWorthChangeColor = NetworthChange < 0 ? "red" : "white";

            statLabel.setText(String.format(
                    "<html><div style='color:white;'>"
                            + "<b>%s</b><br/>"
                            + "Starting NetWorth: %.2f<br/>"
                            + "Capital: <span style='color:%s;'>$%.2f</span><br/>"
                            + "Net Profit: <span style='color:%s;'>$%.2f</span><br/>"
                            + "Estimated Net Worth: <span style='color:%s;'>$%.2f</span><br/>"
                            + "Net Worth Change: <span style='color:%s;'>$%.2f</span><br/>"
                            + "Shares Held: <span style='color:%s;'>%d</span>"
                            + "</div></html>",
                    selectedBuyer.getName(),
                    startNetWorth,
                    capitalColor, capital,
                    profitColor, netProfit,
                    worthColor, netWorth,
                    NetWorthChangeColor, NetworthChange,
                    sharesColor, shares
            ));
        }
    }

    /**
     * Stops the periodic refresh of buyer statistics.
     * Useful when the panel is being closed or replaced.
     */
    public void stopRefreshing() {
        refreshTimer.stop();
    }
}

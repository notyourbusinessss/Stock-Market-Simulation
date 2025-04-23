import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class BuyerStatsPanel extends JPanel {

    private final List<Buyer> buyers;
    private final StockMarket stockMarket;
    private final Timer refreshTimer;
    private final JComboBox<String> buyerSelector;
    private final JLabel statLabel;

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

        refreshTimer = new Timer(1000, e -> refreshData());
        refreshTimer.start();
    }

    private void onBuyerSelected(ActionEvent e) {
        refreshData();
    }

    private void refreshData() {
        String selectedBuyerName = (String) buyerSelector.getSelectedItem();
        Buyer selectedBuyer = buyers.stream()
                .filter(b -> b.getName().equals(selectedBuyerName))
                .findFirst().orElse(null);

        if (selectedBuyer != null) {
            double netWorth = selectedBuyer.getCapital() + selectedBuyer.getHolding() * stockMarket.getCurrentPrice();
            double netProfit = selectedBuyer.getNetProfit();
            double capital = selectedBuyer.getCapital();
            int shares = selectedBuyer.getHolding();

            // Set color based on value
            String capitalColor = capital < 0 ? "red" : "white";
            String profitColor = netProfit < 0 ? "red" : "white";
            String worthColor = netWorth < 0 ? "red" : "white";
            String sharesColor = shares < 0 ? "red" : "white"; // Just in case

            statLabel.setText(String.format(
                    "<html><div style='color:white;'>"
                            + "<b>%s</b><br/>"
                            + "Capital: <span style='color:%s;'>$%.2f</span><br/>"
                            + "Net Profit: <span style='color:%s;'>$%.2f</span><br/>"
                            + "Estimated Net Worth: <span style='color:%s;'>$%.2f</span><br/>"
                            + "Shares Held: <span style='color:%s;'>%d</span>"
                            + "</div></html>",
                    selectedBuyer.getName(),
                    capitalColor, capital,
                    profitColor, netProfit,
                    worthColor, netWorth,
                    sharesColor, shares
            ));
        }
    }



    public void stopRefreshing() {
        refreshTimer.stop();
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

public class ArrowPanel extends JPanel {

    private JButton upButton;
    private JButton downButton;
    private JLabel valueLabel;
    private StockMarket stock;
    private List<Double> priceHistory;

    public ArrowPanel(StockMarket stockMarket) {
        this.stock = stockMarket;
        this.priceHistory = new LinkedList<>();

        setLayout(new BorderLayout());

        // --- VALUE + BUTTON PANEL ON RIGHT ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false); // Transparent for black background

        valueLabel = new JLabel(String.format("%.2f", stock.MarketPrice), SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);

        upButton = new JButton("↑");
        downButton = new JButton("↓");

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        buttonPanel.add(upButton);
        buttonPanel.add(downButton);

        rightPanel.add(valueLabel, BorderLayout.NORTH);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.EAST);

        // BUTTON LOGIC
        upButton.addActionListener(e -> {
            stock.MarketPrice++;
            updateLabel();
        });

        downButton.addActionListener(e -> {
            stock.MarketPrice--;
            updateLabel();
        });

        // AUTO UPDATE + GRAPH
        new Timer(StockMarket.waiting, (ActionEvent e) -> {
            updateLabel();
            addPrice(stock.MarketPrice);
            repaint();
        }).start();

        // GLOBAL BACKGROUND COLOR
        setBackground(Color.BLACK);
    }

    private void addPrice(double price) {
        if (priceHistory.size() >= 100)
            priceHistory.remove(0);
        priceHistory.add(price);
    }

    public void updateLabel() {
        valueLabel.setText(String.format("%.2f", stock.MarketPrice));
    }

    public void updateLabel(StockMarket stockMarket) {
        stock.MarketPrice = stockMarket.MarketPrice;
        updateLabel();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (priceHistory.size() < 2)
            return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int paddingRight = 100; // reserve space for buttons and label
        int paddingLeft = 40;   // space for reference value labels
        int paddingBottom = 20;
        int paddingTop = 20;
        int graphWidth = w - paddingLeft - paddingRight;
        int graphHeight = h - paddingTop - paddingBottom;

        double max = priceHistory.stream().max(Double::compare).orElse(1.0);
        double min = priceHistory.stream().min(Double::compare).orElse(0.0);
        double range = Math.max(max - min, 1);

        // Draw horizontal reference lines
        int numLines = 5;
        g2.setColor(new Color(255, 255, 255, 50)); // semi-transparent white
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int i = 0; i <= numLines; i++) {
            int y = paddingTop + (graphHeight * i) / numLines;
            double value = max - (range * i / numLines);
            g2.drawLine(paddingLeft, y, paddingLeft + graphWidth, y);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(String.format("%.2f", value), 2, y + 4);
            g2.setColor(new Color(255, 255, 255, 50));
        }

        // Draw the stock price graph
        int spacing = graphWidth / (priceHistory.size() - 1);

        for (int i = 0; i < priceHistory.size() - 1; i++) {
            int x1 = paddingLeft + i * spacing;
            int x2 = paddingLeft + (i + 1) * spacing;

            int y1 = paddingTop + (int) ((max - priceHistory.get(i)) / range * graphHeight);
            int y2 = paddingTop + (int) ((max - priceHistory.get(i + 1)) / range * graphHeight);

            if (priceHistory.get(i + 1) > priceHistory.get(i)) {
                g2.setColor(Color.GREEN);
            } else {
                g2.setColor(Color.RED);
            }

            g2.drawLine(x1, y1, x2, y2);
        }
    }
}

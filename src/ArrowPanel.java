import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

public class ArrowPanel extends JPanel {
    private JButton pauseButton;
    private JButton upButton;
    private JButton downButton;
    private JLabel valueLabel;
    private JLabel marketStateLabel;
    private JLabel tickLabel;

    private StockMarket stock;
    private List<Double> priceHistory;
    private int tick = 0;

    public ArrowPanel(StockMarket stockMarket) {
        this.stock = stockMarket;
        this.priceHistory = new LinkedList<>();

        setLayout(new BorderLayout());

        // --- VALUE + BUTTON PANEL ON RIGHT ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false); // Transparent for black background
        int fixedWidth = 120;
        rightPanel.setPreferredSize(new Dimension(fixedWidth, 0));

        // Labels
        valueLabel = new JLabel(String.format("%.2f", stock.MarketPrice), SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);

        marketStateLabel = new JLabel("Market: Stable", SwingConstants.CENTER);
        marketStateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        marketStateLabel.setForeground(Color.LIGHT_GRAY);

        tickLabel = new JLabel(formatTick(tick), SwingConstants.CENTER);
        tickLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        tickLabel.setForeground(Color.LIGHT_GRAY);

        // Stack all labels vertically
        JPanel labelPanel = new JPanel(new GridLayout(3, 1));
        labelPanel.setOpaque(false);
        labelPanel.add(valueLabel);
        labelPanel.add(marketStateLabel);
        labelPanel.add(tickLabel);

        // Buttons
        upButton = new JButton("↑");
        downButton = new JButton("↓");
        pauseButton = new JButton("Pause");

        for (JButton button : new JButton[]{upButton, downButton, pauseButton}) {
            button.setBackground(Color.BLACK);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
        }

        pauseButton.addActionListener(e -> {
            stock.togglePause();
            pauseButton.setText(stock.isPaused() ? "Resume" : "Pause");
        });

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(upButton);
        buttonPanel.add(downButton);
        buttonPanel.add(pauseButton);

        rightPanel.add(labelPanel, BorderLayout.NORTH);
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
        new Timer(StockMarket.waiting * 10, (ActionEvent e) -> {
            updateLabel();
            marketStateLabel.setText("Market: " + getMarketState());

            if (!stock.isPaused()) {
                addPrice(stock.MarketPrice);
                tick++;
                tickLabel.setText(formatTick(tick));
            }

            repaint();
        }).start();

        // BACKGROUND COLOR
        setBackground(Color.BLACK);
    }

    private void addPrice(double price) {
        if (priceHistory.size() >= 200)
            for (int i = 0; i < 5; i++) {
                priceHistory.remove(0);
            }
        priceHistory.add(price);
    }

    public void updateLabel() {
        valueLabel.setText(String.format("%.2f", stock.MarketPrice));
    }

    public void updateLabel(StockMarket stockMarket) {
        stock.MarketPrice = stockMarket.MarketPrice;
        updateLabel();
    }

    public void resetTick() {
        tick = 0;
        tickLabel.setText(formatTick(tick));
    }

    private String formatTick(int tick) {
        int hours = tick % 24;
        int totalDays = tick / 24;
        int days = totalDays % 365;
        int years = totalDays / 365;
        return String.format("%d:%03d:%02d", years, days, hours);
    }

    private String getMarketState() {
        if (priceHistory.size() < 10) return "N/A";

        double recent = priceHistory.get(priceHistory.size() - 1);
        double past = priceHistory.get(priceHistory.size() - 10);
        double diff = recent - past;
        double percent = (diff / past) * 100;

        if (percent < -10)
            return "CRASHING";
        else if (percent < -2)
            return "Declining";
        else if (percent > 10)
            return "BOOMING";
        else if (percent > 2)
            return "Rising";
        else
            return "Stable";
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
        int paddingRight = 100;
        int paddingLeft = 40;
        int paddingBottom = 20;
        int paddingTop = 20;
        int graphWidth = w - paddingLeft - paddingRight;
        int graphHeight = h - paddingTop - paddingBottom;

        double max = priceHistory.stream().max(Double::compare).orElse(1.0);
        double min = priceHistory.stream().min(Double::compare).orElse(0.0);
        double range = Math.max(max - min, 1);

        // Horizontal lines
        int numLines = 5;
        g2.setColor(new Color(255, 255, 255, 50));
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int i = 0; i <= numLines; i++) {
            int y = paddingTop + (graphHeight * i) / numLines;
            double value = max - (range * i / numLines);
            g2.drawLine(paddingLeft, y, paddingLeft + graphWidth, y);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(String.format("%.2f", value), 2, y + 4);
            g2.setColor(new Color(255, 255, 255, 50));
        }

        // Line graph
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

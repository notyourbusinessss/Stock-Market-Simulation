import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

public class ArrowPanel extends JPanel {

    private int MAXVALS = 500;
    private JButton pauseButton, upButton, downButton;
    private JLabel valueLabel, marketStateLabel, timeLabel;
    private StockMarket stock;
    Color Background = new Color(15, 15, 15);
    private boolean showLine = true;
    private boolean showCandles = true;
    private boolean trimHistory = true;

    private List<Double> priceHistory = new LinkedList<>();
    private List<Candle> candleHistory = new LinkedList<>();

    private double tempOpen = -1;
    private double tempHigh = Double.MIN_VALUE;
    private double tempLow = Double.MAX_VALUE;
    private int tickCounter = 0;
    private static final int TICKS_PER_CANDLE = 12;

    private int totalTicks = 0;

    public ArrowPanel(StockMarket stockMarket) {
        this.stock = stockMarket;
        setLayout(new BorderLayout());

        // === RIGHT PANEL ===
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.BLACK);
        rightPanel.setPreferredSize(new Dimension(120, 0));

        // Top panel with value + market state + time
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.setBackground(Color.BLACK);

        valueLabel = new JLabel(String.format("%.2f", stock.MarketPrice), SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);

        marketStateLabel = new JLabel("Market: Stable", SwingConstants.CENTER);
        marketStateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        marketStateLabel.setForeground(Color.LIGHT_GRAY);

        timeLabel = new JLabel("Time: Year:0 Day:0 Hour:0", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        timeLabel.setForeground(Color.LIGHT_GRAY);

        topPanel.add(valueLabel);
        topPanel.add(marketStateLabel);
        topPanel.add(timeLabel);

        // Buttons
        upButton = new JButton("↑");
        downButton = new JButton("↓");
        pauseButton = new JButton("Pause");
        JButton toggleLineButton = new JButton("Hide Line");
        JButton toggleCandleButton = new JButton("Hide Candles");
        JButton toggleTrimButton = new JButton("Keep History");

        for (JButton button : new JButton[]{upButton, downButton, pauseButton, toggleLineButton, toggleCandleButton, toggleTrimButton}) {
            button.setBackground(Color.BLACK);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
        }

        upButton.addActionListener(e -> stock.MarketPrice++);
        downButton.addActionListener(e -> stock.MarketPrice--);

        pauseButton.addActionListener(e -> {
            stock.togglePause();
            pauseButton.setText(stock.isPaused() ? "Resume" : "Pause");
        });

        toggleLineButton.addActionListener(e -> {
            showLine = !showLine;
            toggleLineButton.setText(showLine ? "Hide Line" : "Show Line");
        });

        toggleCandleButton.addActionListener(e -> {
            showCandles = !showCandles;
            toggleCandleButton.setText(showCandles ? "Hide Candles" : "Show Candles");
        });

        toggleTrimButton.addActionListener(e -> {
            trimHistory = !trimHistory;
            toggleTrimButton.setText(trimHistory ? "Keep All History" : "Trim History");
        });


        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        buttonPanel.setBackground(Color.BLACK);

        buttonPanel.add(upButton);
        buttonPanel.add(downButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(toggleLineButton);
        buttonPanel.add(toggleCandleButton);
        buttonPanel.add(toggleTrimButton);

        rightPanel.add(topPanel, BorderLayout.NORTH);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.EAST);
        setBackground(new Color(15, 15, 15));

        new Timer(StockMarket.waiting * 10, (ActionEvent e) -> {
            updateLabel();
            marketStateLabel.setText("Market: " + getMarketState());

            if (!stock.isPaused()) {
                trackCandle(stock.MarketPrice);
                totalTicks += 10;
                int years = totalTicks / (24 * 365);
                int days = (totalTicks / 24) % 365;
                int hours = totalTicks % 24;
                timeLabel.setText(
                        "<html>Year: " + years + "<br>Day: " + days + "<br>" + hours + ":00" + "</html>"
                );

            }

            repaint();
        }).start();
    }

    private void trackCandle(double price) {
        if (trimHistory && priceHistory.size() > MAXVALS) {
            for (int i = 0; i < TICKS_PER_CANDLE; ++i) {
                if (trimHistory && candleHistory.size() > MAXVALS / TICKS_PER_CANDLE) {
                    candleHistory.remove(0);
                }
                priceHistory.remove(0);
            }
        }
        priceHistory.add(price);

        if (tempOpen == -1) tempOpen = price;
        tempHigh = Math.max(tempHigh, price);
        tempLow = Math.min(tempLow, price);
        tickCounter++;

        if (tickCounter >= TICKS_PER_CANDLE) {
            candleHistory.add(new Candle(tempOpen, price, tempHigh, tempLow));
            if (trimHistory && candleHistory.size() > MAXVALS / TICKS_PER_CANDLE) {
                candleHistory.remove(0);
            }
            tickCounter = 0;
            tempOpen = -1;
            tempHigh = Double.MIN_VALUE;
            tempLow = Double.MAX_VALUE;
        }
    }

    public void updateLabel() {
        valueLabel.setText(String.format("%.2f $", stock.MarketPrice));
    }

    private String getMarketState() {
        if (candleHistory.size() < 2) return "N/A";
        double recent = candleHistory.getLast().close;
        double past = candleHistory.get(candleHistory.size() - 2).close;
        double diff = recent - past;
        double percent = (diff / past) * 100;
        if (percent < -10) return "CRASHING";
        if (percent < -2) return "Declining";
        if (percent > 10) return "BOOMING";
        if (percent > 2) return "Rising";
        return "Stable";
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (priceHistory.size() < 1) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int paddingLeft = 40, paddingRight = 140, paddingTop = 20, paddingBottom = 20;
        int graphWidth = w - paddingLeft - paddingRight;
        int graphHeight = h - paddingTop - paddingBottom;

        List<Double> renderPrices = new LinkedList<>(priceHistory);
        renderPrices.add(stock.MarketPrice);

        double max = Math.max(
                renderPrices.stream().mapToDouble(p -> p).max().orElse(1),
                candleHistory.stream().mapToDouble(c -> c.high).max().orElse(1)
        );
        double min = Math.min(
                renderPrices.stream().mapToDouble(p -> p).min().orElse(0),
                candleHistory.stream().mapToDouble(c -> c.low).min().orElse(0)
        );
        double range = Math.max(max - min, 1);

        double spacing = (double) graphWidth / (renderPrices.size() - 1);
        int totalTicks = candleHistory.size() * TICKS_PER_CANDLE + tickCounter;
        double candleSpacing = (double) graphWidth / Math.max(1, totalTicks);

        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        g2.setColor(new Color(255, 255, 255, 50));
        for (int i = 0; i <= 5; i++) {
            int y = paddingTop + (graphHeight * i) / 5;
            double val = max - range * i / 5;
            g2.drawLine(paddingLeft, y, paddingLeft + graphWidth, y);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawString(String.format("%.2f", val), 2, y + 4);
            g2.setColor(new Color(255, 255, 255, 50));
        }

        if (showLine) {
            g2.setColor(Color.CYAN);
            for (int i = 0; i < renderPrices.size() - 1; i++) {
                int x1 = paddingLeft + (int) (i * spacing);
                int x2 = paddingLeft + (int) ((i + 1) * spacing);
                int y1 = paddingTop + (int) ((max - renderPrices.get(i)) / range * graphHeight);
                int y2 = paddingTop + (int) ((max - renderPrices.get(i + 1)) / range * graphHeight);
                g2.drawLine(x1, y1, x2, y2);
            }
        }

        if (showCandles) {
            for (int i = 0; i < candleHistory.size(); i++) {
                Candle c = candleHistory.get(i);
                int x = paddingLeft + (int) (i * TICKS_PER_CANDLE * candleSpacing + TICKS_PER_CANDLE / 2.0 * candleSpacing);

                int yHigh = paddingTop + (int) ((max - c.high) / range * graphHeight);
                int yLow = paddingTop + (int) ((max - c.low) / range * graphHeight);
                int yOpen = paddingTop + (int) ((max - c.open) / range * graphHeight);
                int yClose = paddingTop + (int) ((max - c.close) / range * graphHeight);

                g2.setColor(Color.WHITE);
                g2.drawLine(x, yHigh, x, yLow);

                int bodyTop = Math.min(yOpen, yClose);
                int bodyHeight = Math.max(1, Math.abs(yClose - yOpen));
                g2.setColor(c.close >= c.open ? Color.GREEN : Color.RED);
                g2.fillRect(x - 2, bodyTop, 4, bodyHeight);
            }

            if (tickCounter > 0) {
                int x = paddingLeft + (int) (candleHistory.size() * TICKS_PER_CANDLE * candleSpacing + tickCounter / 2.0 * candleSpacing);

                double currentPrice = stock.MarketPrice;

                int yHigh = paddingTop + (int) ((max - tempHigh) / range * graphHeight);
                int yLow = paddingTop + (int) ((max - tempLow) / range * graphHeight);
                int yOpen = paddingTop + (int) ((max - tempOpen) / range * graphHeight);
                int yClose = paddingTop + (int) ((max - currentPrice) / range * graphHeight);

                g2.setColor(Color.WHITE);
                g2.drawLine(x, yHigh, x, yLow);

                int bodyTop = Math.min(yOpen, yClose);
                int bodyHeight = Math.max(1, Math.abs(yClose - yOpen));
                g2.setColor(currentPrice >= tempOpen ? Color.GREEN : Color.RED);
                g2.fillRect(x - 2, bodyTop, 4, bodyHeight);
            }
        }
    }

}

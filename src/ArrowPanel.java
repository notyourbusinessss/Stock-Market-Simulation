import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

public class ArrowPanel extends JPanel {
    private final List<ScrollingNews> activeNews = new LinkedList<>();
    private CustomWindowPanel customTradeWindow;
    private CustomWindowPanel buyersStatsWindow;
    private final int timediff = 10;
    private int MAXVALS = 500;
    private JButton pauseButton, upButton, downButton;
    private JLabel valueLabel, marketStateLabel, timeLabel;
    private JLabel marketCapLabel;


    private StockMarket stock;
    Color Background = new Color(15, 15, 15);
    private boolean showLine = true;
    private boolean showCandles = true;
    private boolean trimHistory = false;

    private List<Double> priceHistory = new LinkedList<>();
    private List<Candle> candleHistory = new LinkedList<>();

    private double tempOpen = -1;
    private double tempHigh = Double.MIN_VALUE;
    private double tempLow = Double.MAX_VALUE;
    private int tickCounter = 0;
    private static final int TICKS_PER_CANDLE = 24;

    private int totalTicks = 0;

    private String lastDisplayedNews = "";
    private int newsX = getWidth();
    private Timer newsScrollTimer;

    public ArrowPanel(StockMarket stockMarket) {
        this.stock = stockMarket;
        setLayout(new BorderLayout());

        SimulatedTradePanel simPanel = new SimulatedTradePanel(stockMarket);
        customTradeWindow = new CustomWindowPanel(simPanel, false, "Simulated Trading Window");

        BuyerStatsPanel StatsPanel = new BuyerStatsPanel(stockMarket.getBuyers(),stockMarket);
        buyersStatsWindow = new CustomWindowPanel(StatsPanel,false,"Buyers current Statistics");

        JButton tradeToggleButton = new JButton("Toggle Trading UI");
        tradeToggleButton.addActionListener(e -> {
            if (!customTradeWindow.isWindowVisible()) {
                customTradeWindow.showWindow();
                customTradeWindow.setWindowSize(300, 500); // or any size you want

            } else {
                customTradeWindow.hideWindow();
            }
        });

        JButton statstogglebutton = new JButton("Toggle Stats UI");
        statstogglebutton.addActionListener(e -> {
            if (!buyersStatsWindow.isWindowVisible()) {
                buyersStatsWindow.showWindow();
                buyersStatsWindow.setWindowSize(300, 300); // or any size you want

            } else {
                buyersStatsWindow.hideWindow();
            }
        });

        JPanel newsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                synchronized (activeNews) {
                    for (ScrollingNews sn : activeNews) {
                        g.drawString("News: " + sn.message, sn.x, getHeight() - 5);
                    }
                }
            }
        };
        newsPanel.setOpaque(true);
        newsPanel.setBackground(new Color(20, 20, 20));
        newsPanel.setPreferredSize(new Dimension(0, 25));
        add(newsPanel, BorderLayout.NORTH);


        newsScrollTimer = new Timer(30, e -> {
            synchronized (activeNews) {
                activeNews.removeIf(sn -> sn.x + getFontMetrics(new Font("Arial", Font.BOLD, 12)).stringWidth("News: " + sn.message) < 0);
                for (ScrollingNews sn : activeNews) {
                    sn.x -= 2;
                }
            }
            newsPanel.repaint();
        });
        newsScrollTimer.start();


        // === RIGHT PANEL ===
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.BLACK);
        rightPanel.setPreferredSize(new Dimension(180, 0)); // or try 200 for more space


        JPanel topPanel = new JPanel(new GridLayout(4, 1));
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
        marketStateLabel = new JLabel("Market: Stable", SwingConstants.CENTER);
        marketCapLabel = new JLabel(String.format("Market Cap: $%.2f",stock.getCurrentPrice() * stock.getTotalShares()), SwingConstants.CENTER);
        marketCapLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        marketCapLabel.setForeground(Color.LIGHT_GRAY);

        topPanel.add(valueLabel);
        topPanel.add(marketCapLabel);
        topPanel.add(marketStateLabel);
        topPanel.add(timeLabel);

        upButton = new JButton("↑");
        downButton = new JButton("↓");
        pauseButton = new JButton("Pause");
        JButton toggleLineButton = new JButton("Hide Line");
        JButton toggleCandleButton = new JButton("Hide Candles");
        JButton toggleTrimButton = new JButton("Keep History");


        for (JButton button : new JButton[]{upButton, downButton, pauseButton, toggleLineButton, toggleCandleButton, toggleTrimButton,tradeToggleButton,statstogglebutton}) {
            button.setBackground(Color.BLACK);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setPreferredSize(new Dimension(160, 30));
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

        JPanel buttonPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(upButton);
        buttonPanel.add(downButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(toggleLineButton);
        buttonPanel.add(toggleCandleButton);
        buttonPanel.add(toggleTrimButton);
        buttonPanel.add(tradeToggleButton);
        buttonPanel.add(statstogglebutton);


        rightPanel.add(topPanel, BorderLayout.NORTH);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.EAST);
        setBackground(Background);

        new Timer(StockMarket.waiting * timediff, (ActionEvent e) -> {
            updateLabel();
            marketStateLabel.setText("Market: " + getMarketState());

            if (!stock.isPaused()) {

                trackCandle(stock.MarketPrice);
                totalTicks += timediff;

                simPanel.updateLabels();


                int years = totalTicks / (24 * 365);
                int days = (totalTicks / 24) % 365;
                int hours = totalTicks % 24;
                timeLabel.setText("<html>Year: " + years + "<br>Day: " + days + "<br>" + hours + ":00</html>");
            }

            if (!StockMarket.lastNews.equals(lastDisplayedNews)) {
                lastDisplayedNews = StockMarket.lastNews;

                int initialX = getWidth();
                FontMetrics fm = getFontMetrics(new Font("Arial", Font.BOLD, 12));

                synchronized (activeNews) {
                    for (ScrollingNews sn : activeNews) {
                        int messageWidth = fm.stringWidth("News: " + sn.message);
                        // If still visible, offset the new one further to the right
                        if (sn.x + messageWidth > initialX - 20) {
                            initialX = sn.x + messageWidth + 20;
                        }
                    }

                    activeNews.add(new ScrollingNews(lastDisplayedNews, initialX));
                }
            }





            repaint();
        }).start();
    }

    private void trackCandle(double price) {
        while(trimHistory && priceHistory.size() > MAXVALS) {
            for (int i = 0; i < TICKS_PER_CANDLE; ++i) {
                if (!priceHistory.isEmpty()) {
                    priceHistory.removeFirst();
                }
            }
        }
        priceHistory.add(price);

        if (tempOpen == -1) tempOpen = price;
        tempHigh = Math.max(tempHigh, price);
        tempLow = Math.min(tempLow, price);
        tickCounter++;

        if (tickCounter >= TICKS_PER_CANDLE) {
            candleHistory.add(new Candle(tempOpen, price, tempHigh, tempLow));
            while(trimHistory && candleHistory.size() > MAXVALS / TICKS_PER_CANDLE) {
                candleHistory.removeFirst();
            }
            tickCounter = 0;
            tempOpen = -1;
            tempHigh = Double.MIN_VALUE;
            tempLow = Double.MAX_VALUE;
        }
    }

    public void updateLabel() {
        valueLabel.setText(String.format("%.2f $", stock.MarketPrice));

        // Market cap = price × total shares
        double marketCap = stock.getCurrentPrice() * stock.getTotalShares();
        marketCapLabel.setText(String.format("Market Cap: $%,.2f", marketCap));
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
        int paddingLeft = 40, paddingRight = 180, paddingTop = 20, paddingBottom = 40;
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

        // === Fixed Horizontal Time Axis (Labels change with simulation time) ===
        int divisions = 8; // Number of tick marks
        int totalGraphHours = totalTicks; // You can scale this if using different resolutions

        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));

        for (int i = 0; i <= divisions; i++) {
            int x = paddingLeft + (graphWidth * i) / divisions;

            // Calculate time label from the right edge
            int hoursAgo = ((divisions - i) * totalGraphHours) / divisions;

            g2.drawLine(x, h - paddingBottom, x, h - paddingBottom + 4);
            g2.drawString("-" + hoursAgo + "H", x - 15, h - paddingBottom + 15);
        }



        // Line graph
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

        // Candlesticks
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

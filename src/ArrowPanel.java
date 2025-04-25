import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
/**
 * ArrowPanel is a custom Swing panel used in the stock market simulation GUI.
 * It displays the real-time stock graph (line and candlestick charts), handles user controls
 * (such as pausing, toggling visibility of data), and optionally visualizes news or events.
 *
 * It is directly tied to a {@link StockMarket} instance and renders its state to the screen.
 */
public class ArrowPanel extends JPanel {

    /** List of currently active scrolling news messages. */
    private final List<ScrollingNews> activeNews = new LinkedList<>();

    /** Trade panel window to display and interact with trading simulation. */
    private CustomWindowPanel customTradeWindow;

    /** Stats panel window showing real-time statistics of buyers. */
    private CustomWindowPanel buyersStatsWindow;

    /** Time interval multiplier for simulation updates. */
    private final int timediff = 10;

    /** Maximum number of historical values stored for rendering. */
    private int MAXVALS = 500;

    /** Button to pause/resume the simulation. */
    private JButton pauseButton;

    /** Button to increase the market price manually. */
    private JButton upButton;

    /** Button to decrease the market price manually. */
    private JButton downButton;

    /** Displays current stock value. */
    private JLabel valueLabel;

    /** Displays current state of the market (e.g., Stable, Crashing). */
    private JLabel marketStateLabel;

    /** Displays simulated time (e.g., Year, Day, Hour). */
    private JLabel timeLabel;

    /** Displays calculated market capitalization. */
    private JLabel marketCapLabel;

    /** Reference to the main stock market simulation object. */
    private StockMarket stock;

    /** Background color for the UI panel. */
    Color Background = new Color(15, 15, 15);

    /** Whether to show the line graph of price history. */
    private boolean showLine = true;

    /** Whether to show candlestick chart. */
    private boolean showCandles = true;

    /** Whether to trim historical data to improve performance. */
    private boolean trimHistory = false;

    /** Historical raw price values for graph rendering. */
    private List<Double> priceHistory = new LinkedList<>();

    /** Historical candlestick data derived from priceHistory. */
    private List<Candle> candleHistory = new LinkedList<>();

    /** Temporary opening value used when creating a new candlestick. */
    private double tempOpen = -1;

    /** Tracks the highest price since the last candlestick was created. */
    private double tempHigh = Double.MIN_VALUE;

    /** Tracks the lowest price since the last candlestick was created. */
    private double tempLow = Double.MAX_VALUE;

    /** Ticks counted since the last candlestick was added. */
    private int tickCounter = 0;

    /** Number of simulation ticks that form one candlestick. */
    private static final int TICKS_PER_CANDLE = 24;

    /** Total number of simulation ticks passed (for time tracking). */
    private int totalTicks = 0;

    /** Last news message displayed (to avoid repetition). */
    private String lastDisplayedNews = "";

    /** X-position used for horizontally scrolling news items. */
    private int newsX = getWidth();

    /** Timer that drives the scrolling animation for news items. */
    private Timer newsScrollTimer;


    /**
     * Constructs the ArrowPanel with all visualization and control components initialized.
     *
     * @param stockMarket the stock market object to observe and interact with
     */
    public ArrowPanel(StockMarket stockMarket) {
        // Store the reference to the stock market
        this.stock = stockMarket;
        setLayout(new BorderLayout());

        // Create and attach the simulated trade panel window
        SimulatedTradePanel simPanel = new SimulatedTradePanel(stockMarket);
        customTradeWindow = new CustomWindowPanel(simPanel, false, "Simulated Trading Window");

        // Create and attach the buyer statistics panel window
        BuyerStatsPanel StatsPanel = new BuyerStatsPanel(stockMarket.getBuyers(),stockMarket);
        buyersStatsWindow = new CustomWindowPanel(StatsPanel,false,"Buyers current Statistics");

        // Button to toggle the trade window
        JButton tradeToggleButton = new JButton("Toggle Trading UI");
        tradeToggleButton.addActionListener(e -> {
            if (!customTradeWindow.isWindowVisible()) {
                customTradeWindow.showWindow();
                customTradeWindow.setWindowSize(300, 500);
            } else {
                customTradeWindow.hideWindow();
            }
        });

        // Button to toggle the statistics window
        JButton statstogglebutton = new JButton("Toggle Stats UI");
        statstogglebutton.addActionListener(e -> {
            if (!buyersStatsWindow.isWindowVisible()) {
                buyersStatsWindow.showWindow();
                buyersStatsWindow.setWindowSize(300, 300);
            } else {
                buyersStatsWindow.hideWindow();
            }
        });

        // Top panel for scrolling news ticker
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

        // Timer to scroll the news messages
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

        // RIGHT PANEL
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.BLACK);
        rightPanel.setPreferredSize(new Dimension(180, 0));

        // Top panel with stock labels
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

        marketCapLabel = new JLabel(String.format("Market Cap: $%.2f",stock.getCurrentPrice() * stock.getTotalShares()), SwingConstants.CENTER);
        marketCapLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        marketCapLabel.setForeground(Color.LIGHT_GRAY);

        topPanel.add(valueLabel);
        topPanel.add(marketCapLabel);
        topPanel.add(marketStateLabel);
        topPanel.add(timeLabel);

        // Button Controls
        upButton = new JButton("↑");
        downButton = new JButton("↓");
        pauseButton = new JButton("Pause");
        JButton toggleLineButton = new JButton("Hide Line");
        JButton toggleCandleButton = new JButton("Hide Candles");
        JButton toggleTrimButton = new JButton("Keep History");

        // Style the buttons
        for (JButton button : new JButton[]{upButton, downButton, pauseButton, toggleLineButton, toggleCandleButton, toggleTrimButton, tradeToggleButton, statstogglebutton}) {
            button.setBackground(Color.BLACK);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setPreferredSize(new Dimension(160, 30));
        }

        // Attach button functionality
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

        // Panel for holding buttons vertically
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

        // Add panels to the layout
        rightPanel.add(topPanel, BorderLayout.NORTH);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);
        setBackground(Background);

        // Main simulation update timer
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

    /**
     * Adds a new price point to the chart and builds candlestick data if enough ticks have passed.
     *
     * @param price the current stock price
     */
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
    /**
     * Updates display labels such as market value and market cap.
     */
    public void updateLabel() {
        valueLabel.setText(String.format("%.2f $", stock.MarketPrice));

        // Market cap = price × total shares
        double marketCap = stock.getCurrentPrice() * stock.getTotalShares();
        marketCapLabel.setText(String.format("Market Cap: $%,.2f", marketCap));
    }

    /**
     * Computes the market state based on recent price changes.
     *
     * @return a string such as "Stable", "Rising", or "Crashing"
     */
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

    /**
     * Paints the graph, candlesticks, grid, and time labels.
     * Handles both the line graph and candle view depending on user toggles.
     *
     * @param g the Graphics context used for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        // base Swing painting
        super.paintComponent(g);

        // do nothing if no data to draw
        if (priceHistory.size() < 1) return;

        // cast to Graphics2D and enable anti-aliasing
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // get dimensions and padding
        int w = getWidth(), h = getHeight();
        int paddingLeft = 40, paddingRight = 180, paddingTop = 20, paddingBottom = 40;
        int graphWidth = w - paddingLeft - paddingRight;
        int graphHeight = h - paddingTop - paddingBottom;

        // combine price history and current price into one list
        List<Double> renderPrices = new LinkedList<>(priceHistory);
        renderPrices.add(stock.MarketPrice);

        // find min and max for scaling
        double max = Math.max(
                renderPrices.stream().mapToDouble(p -> p).max().orElse(1),
                candleHistory.stream().mapToDouble(c -> c.high).max().orElse(1)
        );
        double min = Math.min(
                renderPrices.stream().mapToDouble(p -> p).min().orElse(0),
                candleHistory.stream().mapToDouble(c -> c.low).min().orElse(0)
        );
        double range = Math.max(max - min, 1);

        // spacing between points on the graph
        double spacing = (double) graphWidth / (renderPrices.size() - 1);
        int totalTicks = candleHistory.size() * TICKS_PER_CANDLE + tickCounter;
        double candleSpacing = (double) graphWidth / Math.max(1, totalTicks);

        // draw horizontal grid lines and y-axis labels
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

        // draw time axis labels
        int divisions = 8;
        int totalGraphHours = totalTicks;
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        // Draw X-axis labels and tick marks for time divisions
        for (int i = 0; i <= divisions; i++) {
            // Calculate the x position of this tick mark
            int x = paddingLeft + (graphWidth * i) / divisions;

            // Calculate how many hours ago this tick represents
            int hoursAgo = ((divisions - i) * totalGraphHours) / divisions;

            // Draw a small vertical tick mark on the x-axis
            g2.drawLine(x, h - paddingBottom, x, h - paddingBottom + 4);

            // Draw the label (e.g., "-10H") under the tick mark
            g2.drawString("-" + hoursAgo + "H", x - 15, h - paddingBottom + 15);
        }

        // Draw line graph connecting price history if line view is enabled
        if (showLine) {
            g2.setColor(Color.CYAN); // Line graph color
            for (int i = 0; i < renderPrices.size() - 1; i++) {
                // Calculate x positions for two consecutive price points
                int x1 = paddingLeft + (int) (i * spacing);
                int x2 = paddingLeft + (int) ((i + 1) * spacing);

                // Calculate y positions (inverted, since y=0 is top of screen)
                int y1 = paddingTop + (int) ((max - renderPrices.get(i)) / range * graphHeight);
                int y2 = paddingTop + (int) ((max - renderPrices.get(i + 1)) / range * graphHeight);

                // Draw line between the two points
                g2.drawLine(x1, y1, x2, y2);
            }
        }

        // Draw historical candlesticks if enabled
        if (showCandles) {
            for (int i = 0; i < candleHistory.size(); i++) {
                Candle c = candleHistory.get(i);

                // X position for this candlestick
                int x = paddingLeft + (int) (i * TICKS_PER_CANDLE * candleSpacing + TICKS_PER_CANDLE / 2.0 * candleSpacing);

                // Convert price data into screen Y coordinates
                int yHigh = paddingTop + (int) ((max - c.high) / range * graphHeight);
                int yLow = paddingTop + (int) ((max - c.low) / range * graphHeight);
                int yOpen = paddingTop + (int) ((max - c.open) / range * graphHeight);
                int yClose = paddingTop + (int) ((max - c.close) / range * graphHeight);

                g2.setColor(Color.WHITE); // Wick color
                g2.drawLine(x, yHigh, x, yLow); // Draw the wick (high to low)

                // Draw the candle body (colored box between open and close)
                int bodyTop = Math.min(yOpen, yClose);
                int bodyHeight = Math.max(1, Math.abs(yClose - yOpen)); // Avoid 0-height

                // Green if price went up, red if it went down
                g2.setColor(c.close >= c.open ? Color.GREEN : Color.RED);
                g2.fillRect(x - 2, bodyTop, 4, bodyHeight);
            }

            // Draw current forming candlestick (incomplete one)
            if (tickCounter > 0) {
                // X position for the current candle being formed
                int x = paddingLeft + (int) (candleHistory.size() * TICKS_PER_CANDLE * candleSpacing + tickCounter / 2.0 * candleSpacing);

                // Get current stock price
                double currentPrice = stock.MarketPrice;

                // Convert temp candlestick values to Y coordinates
                int yHigh = paddingTop + (int) ((max - tempHigh) / range * graphHeight);
                int yLow = paddingTop + (int) ((max - tempLow) / range * graphHeight);
                int yOpen = paddingTop + (int) ((max - tempOpen) / range * graphHeight);
                int yClose = paddingTop + (int) ((max - currentPrice) / range * graphHeight);

                g2.setColor(Color.WHITE); // Wick color
                g2.drawLine(x, yHigh, x, yLow); // Wick from high to low

                // Body of current forming candle
                int bodyTop = Math.min(yOpen, yClose);
                int bodyHeight = Math.max(1, Math.abs(yClose - yOpen)); // Prevent 0-height box

                // Green if currently above open price, red otherwise
                g2.setColor(currentPrice >= tempOpen ? Color.GREEN : Color.RED);
                g2.fillRect(x - 2, bodyTop, 4, bodyHeight);
            }
        }

    }
}

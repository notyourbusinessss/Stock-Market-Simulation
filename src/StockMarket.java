import Skeleton.SimulationInput;
import Skeleton.Unit;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * The stock market is the middle ground of the interactions between the buyers and the Stock, here is where the buyers will buy or sell their stocks.
 */
public class StockMarket extends Unit{
    static int waiting = 10;
    Stock TrackedStock;
    private int avalibleShares;
    private List<MarketObserver> Stocks = new ArrayList<>();
    double MarketPrice;

    public static final Semaphore pauseLock = new Semaphore(1); // starts "unpaused"
    private volatile boolean paused = false;

    public void togglePause() {
        if (paused) {
            pauseLock.release();
        } else {
            try {
                pauseLock.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        paused = !paused;
    }

    public boolean isPaused() {
        return paused;
    }

    /**
     * Time in which the simulation will run
     */
    int Time;
    static int Now;

    static boolean open = true;

    public StockMarket(SimulationInput input) {
        super(input);
    }
    public StockMarket(SimulationInput input, int totalShares, double InitialPrice, int time, int now) {
        super(input);
        this.avalibleShares = totalShares;
        this.MarketPrice = InitialPrice;
        this.Time = time;
        this.Now = now;
        TrackedStock = Stock.getInstance(this.MarketPrice, this.avalibleShares);
        Stocks.add(TrackedStock);
    }

    double getMarketTrend(int GoBack){
        return TrackedStock.getTrend(GoBack);
    }
    double getCurrentPrice(){
        return MarketPrice;
    }

    void ForcedMarketPrice(ArrayList<Double> ForcedMarketPrices){
        TrackedStock.ForcedStock(ForcedMarketPrices);
    }

    synchronized void sell(int amount,Buyer buyer){
        buyer.removeholding(amount);
        avalibleShares += amount;
        buyer.Capital += amount*this.MarketPrice;
    }

    synchronized void updateStockPrice() {
        System.out.println("\t\t updating");
        if (avalibleShares == 0) return;

        double avg = TrackedStock.AVGAvalibleShares();
        double delta = avalibleShares - avg;
        double ratio = delta / avg;

        // Cap the max change to ±10%
        double maxChange = 0.1;
        ratio = Math.max(-maxChange, Math.min(maxChange, -ratio)); // Negate: less supply → increase price

        // Apply dampened change (only 5% of the allowed ratio)
        double changeFactor = 0.05;
        MarketPrice += MarketPrice * ratio * changeFactor * (avg > avalibleShares ? 1 : 3.5 );

        // Apply slight decay if no change
        if (Math.abs(delta) < avg * 0.05) {
            double decay = Math.min(0.05, 1.0 / (avg == 0 ? 100 : avg)) * MarketPrice;
            MarketPrice -= decay;
        }

        if (MarketPrice < 0.01) {
            MarketPrice = 0.01;
        }

        System.out.printf("\t\t Updated MarketPrice: %.2f\n", MarketPrice);
    }

    synchronized int getAvalibleShares(){
        return avalibleShares;
    }

    public void buy(int amount, Buyer buyer) {
        if (avalibleShares >= amount) {
            avalibleShares -= amount;
            buyer.addholding(amount);
            buyer.Capital -= amount*this.MarketPrice;
        } else {
            amount = 0;
        }
    }

    void updateStock(){
        for (MarketObserver observer : Stocks){
            observer.updateMarketState(avalibleShares, MarketPrice);
        }
    }

    static boolean isOpen(){
        return open;
    }

    @Override
    public void performAction() {

    }

    @Override
    public void submitStatistics() {

    }

    @Override
    public void run() {
        System.out.println("setting");
        ArrowPanel panel = new ArrowPanel(this);
        SwingUtilities.invokeLater(() -> {
            // Custom undecorated window with draggable title bar and close/minimize buttons
            JFrame frame = new JFrame();
            frame.setUndecorated(true);

            // Enable resizing on undecorated frame
            ResizeListener resizeListener = new ResizeListener(frame);
            frame.addMouseListener(resizeListener);
            frame.addMouseMotionListener(resizeListener);

            JPanel content = new JPanel(new BorderLayout());

            JPanel titleBar = new JPanel(new BorderLayout());
            titleBar.setBackground(Color.BLACK);
            titleBar.setPreferredSize(new Dimension(800, 30));

            JLabel title = new JLabel("  Stock Market Simulator");
            title.setForeground(Color.WHITE);
            title.setFont(new Font("Arial", Font.BOLD, 12));

            JButton close = new JButton("✕");
            close.setForeground(Color.WHITE);
            close.setBackground(Color.BLACK);
            close.setBorder(null);
            close.setFocusPainted(false);
            close.addActionListener(e -> System.exit(0));

            JButton minimize = new JButton("—");
            minimize.setForeground(Color.WHITE);
            minimize.setBackground(Color.BLACK);
            minimize.setBorder(null);
            minimize.setFocusPainted(false);
            minimize.addActionListener(e -> frame.setState(Frame.ICONIFIED));

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
            buttons.setOpaque(false);
            buttons.add(minimize);
            buttons.add(close);

            titleBar.add(title, BorderLayout.WEST);
            titleBar.add(buttons, BorderLayout.EAST);

            final Point[] clickPoint = {null};
            titleBar.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent e) {
                    clickPoint[0] = e.getPoint();
                }
            });
            titleBar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                public void mouseDragged(java.awt.event.MouseEvent e) {
                    Point p = frame.getLocation();
                    frame.setLocation(p.x + e.getX() - clickPoint[0].x, p.y + e.getY() - clickPoint[0].y);
                }
            });

            content.add(titleBar, BorderLayout.NORTH);
            content.add(panel, BorderLayout.CENTER);

            // Create a border wrapper panel to simulate a black border
            JPanel bordered = new JPanel(new BorderLayout());
            bordered.setBackground(Color.BLACK);
            bordered.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
            bordered.add(content, BorderLayout.CENTER);

            frame.setContentPane(bordered);
            frame.setSize(800, 400);
            frame.setLocationRelativeTo(null);
            frame.setResizable(true);
            frame.setVisible(true);
        });

        Buyer buyer1 = new Buyer(new SimulationInput(), "George -1-", (int) (this.avalibleShares * 0.1), this, 50, 100);
        this.avalibleShares -= buyer1.holding;
        Buyer buyer2 = new Buyer(new SimulationInput(), "Mark -2-", (int) (this.avalibleShares * 0.1), this, 100, 50);
        this.avalibleShares -= buyer2.holding;
        RandomBuyer buyer3 = new RandomBuyer(new SimulationInput(), "Random -1-", (int) (this.avalibleShares * 0.1), this, 50, 100);
        RandomBuyer buyer4 = new RandomBuyer(new SimulationInput(), "Random -2-", (int) (this.avalibleShares * 0.1), this, 50, 100);
        Thread A = new Thread(buyer1);
        Thread B = new Thread(buyer2);
        Thread C = new Thread(buyer3);
        Thread D = new Thread(buyer4);
        A.start();
        B.start();
        C.start();
        D.start();

        while (true || StockMarket.isOpen()) {
            try {
                pauseLock.acquire();
                pauseLock.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            TrackedStock.printAsciiPriceGraph();
            System.out.println("------------------------\n" + TrackedStock.getCurrentPrice() + " : " + TrackedStock.getTrend(10) + ", " + TrackedStock.AVGAvalibleShares() + "\n------------------------\n");

            updateStockPrice();
            updateStock();

            SwingUtilities.invokeLater(panel::updateLabel);

            try {
                Thread.sleep(waiting);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        StockMarket stockMarket = new StockMarket(new SimulationInput(), 1000, 50.00, 1000, 0);
        Thread A = new Thread(stockMarket);
        A.start();
    }
}

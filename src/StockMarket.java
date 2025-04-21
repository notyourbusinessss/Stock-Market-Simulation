import Skeleton.SimulationInput;
import Skeleton.Unit;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;


public class StockMarket extends Unit {
    int AmountSold = 0;
    int AmountBought = 0;

    static int waiting = 10;
    Stock TrackedStock;
    private int avalibleShares;
    private List<MarketObserver> Stocks = new ArrayList<>();
    double MarketPrice;

    public static final Semaphore pauseLock = new Semaphore(1);
    private volatile boolean paused = false;

    private boolean wasBuy = false;
    private boolean wasSell = false;
    private final Random rng = new Random();

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

    double getMarketTrend(int GoBack) {
        return TrackedStock.getTrend(GoBack);
    }

    double getCurrentPrice() {
        return MarketPrice;
    }

    void ForcedMarketPrice(ArrayList<Double> ForcedMarketPrices) {
        TrackedStock.ForcedStock(ForcedMarketPrices);
    }

    public synchronized void buy(int amount, Buyer buyer) {
        if (avalibleShares >= amount && amount > 0) {
            avalibleShares -= amount;
            buyer.addholding(amount);
            buyer.Capital -= amount * this.MarketPrice;
            wasBuy = true;
            AmountBought += amount;
        }
    }


    public synchronized void sell(int amount, Buyer buyer) {
        if (amount > 0) {
            buyer.removeholding(amount);
            avalibleShares += amount;
            buyer.Capital += amount * this.MarketPrice;
            wasSell = true;
            AmountSold += amount;
        }
    }

    synchronized void updateStockPrice() {
        System.out.println("\t\t[Updating Stock Price]");

        double avg = TrackedStock.AVGAvalibleShares();
        double delta = avg - avalibleShares;
        double ratio = delta / avg;

        double maxRatio = 0.1;
        double baseChangeFactor = 0.03;
        double priceChange = 0;

        int totalShares = TrackedStock.getTotalShares(); // Make sure this method exists in your Stock class

        if (wasBuy && !wasSell) {
            // Volume-adjusted buying impact
            double volumeFactor = Math.min(1.0, (double) AmountBought / (totalShares * 0.05));
            double directionalMultiplier = 0.75 * volumeFactor;
            double cappedRatio = Math.max(-maxRatio, Math.min(maxRatio, ratio));
            priceChange = cappedRatio * baseChangeFactor * directionalMultiplier;
        } else if (!wasBuy && wasSell) {
            // Volume-adjusted selling impact
            double volumeFactor = Math.min(3.0, (double) AmountSold / (totalShares * 0.05));
            double directionalMultiplier = 4.0 * volumeFactor;
            double cappedRatio = Math.max(-maxRatio, Math.min(maxRatio, ratio));
            priceChange = cappedRatio * baseChangeFactor * directionalMultiplier;
        } else if (!wasBuy && !wasSell) {
            // Idle → random drift
            double noise = (rng.nextDouble() * 0.01) - 0.005; // [-0.005, 0.005]
            System.out.printf("\t\t[Random Drift] %.4f\n", noise);
            priceChange = noise;
        }

        // === Price Resistance and Dampening ===
        double resistance = Math.max(0.1, 1.0 - (MarketPrice / 500.0));
        priceChange *= resistance;

        if (priceChange > 0) {
            priceChange *= Math.pow(0.97, MarketPrice / 10.0);  // Reduce gain as price grows
        }

        // === Clamp maximum price change per tick ===
        double maxTickChange = 0.02; // ±2%
        priceChange = Math.max(-maxTickChange, Math.min(maxTickChange, priceChange));

        // === Apply price change ===
        MarketPrice += MarketPrice * priceChange;

        // === Idle decay if price is too high ===
        if (!wasBuy && !wasSell && MarketPrice > 20) {
            double decay = MarketPrice * 0.002;
            MarketPrice -= decay;
            System.out.printf("\t\t[Idle Decay] -%.4f\n", decay);
        }

        // === Floor & Cap ===
        if (MarketPrice < 0.01) {
            MarketPrice = 0.01;
        } else if (MarketPrice > 1000.00) {
            MarketPrice = 500.00;
        }

        // === Debug ===
        System.out.printf("\t\t Final Price Change: %.4f\n", priceChange);
        System.out.printf("\t\t Updated Market Price: %.2f\n", MarketPrice);

        // === Reset trade state ===
        wasBuy = false;
        wasSell = false;
        AmountSold = 0;
        AmountBought = 0;
    }



    synchronized int getAvalibleShares() {
        return avalibleShares;
    }

    void updateStock() {
        for (MarketObserver observer : Stocks) {
            observer.updateMarketState(avalibleShares, MarketPrice);
        }
    }

    static boolean isOpen() {
        return open;
    }

    @Override
    public void performAction() {}

    @Override
    public void submitStatistics() {}

    @Override
    public void run() {
        System.out.println("setting");
        ArrowPanel arrowPanel = new ArrowPanel(this);
        SimulatedTradePanel simPanel = new SimulatedTradePanel(this);

        SwingUtilities.invokeLater(() -> {
            // === Window 1: Market Graph ===
            CustomWindowPanel marketWindow = new CustomWindowPanel(arrowPanel,true);
            marketWindow.showWindow(); // This shows your main stock window

            // === Window 2: Simulated Trading ===
            CustomWindowPanel simTradeWindow = new CustomWindowPanel(simPanel,false);
            simTradeWindow.showWindow(); // This shows your simulated trading panel
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

            SwingUtilities.invokeLater(arrowPanel::updateLabel);
            SwingUtilities.invokeLater(simPanel::updateLabels);

            try {
                Thread.sleep(waiting);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        StockMarket stockMarket = new StockMarket(new SimulationInput(), 10000, 50.00, 1000, 0);
        Thread A = new Thread(stockMarket);
        A.start();
    }

}

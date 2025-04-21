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

    SimulationInput input;

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

    private void updateStockPrice() {
        System.out.println("\t\t[Updating Stock Price]");

        // 1.  Net order flow during the last tick
        int netShares = AmountBought - AmountSold;      // +ve = buying pressure
        int issued    = TrackedStock.getIssuedShares(); // immutable float size

        // 2.  Convert order flow to a –1 … +1 proportion
        double proportion = (double) netShares / issued;

        // 3.  Smooth, bounded price‑impact curve
        double elasticity = 5.0;                        // volatility dial
        double delta      = MarketPrice * Math.tanh(elasticity * proportion);

        // 4.  Apply and clamp
        MarketPrice = Math.max(0.01,
                Math.min(1_000.0, MarketPrice + delta));

        // 5.  Logging & reset
        System.out.printf(
                "\t\tNet %+d (%.4f)  Δ%.4f  →  %.2f%n",
                netShares, proportion, delta, MarketPrice);

        AmountBought = 0;
        AmountSold   = 0;
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
        ArrowPanel panel = new ArrowPanel(this);

        SwingUtilities.invokeLater(() -> {
            CustomWindowPanel window = new CustomWindowPanel(panel);
            window.showWindow();
        });


        List<Thread> buyerThreads = new ArrayList<>();
        Random rand = new Random();
        for (int i = 1; i <= 6; i++) {
            int holding = 100 + rand.nextInt(1000);
            int activity = rand.nextInt(101);
            int trust = rand.nextInt(101);
            Buyer b = new Buyer(input, "Buyer #" + i, holding, this, activity, trust);
            this.avalibleShares -= b.holding;
            Thread t = new Thread(b);
            t.start();
            buyerThreads.add(t);
        }
        for (int i = 1; i <= 2; i++) {
            int holding = 100 + rand.nextInt(10000);
            int activity = rand.nextInt(101);
            int trust = rand.nextInt(101);
            RandomBuyer b = new RandomBuyer(input, "Big-Buyer #" + i, holding, this, activity, trust);
            this.avalibleShares -= b.holding;
            Thread t = new Thread(b);
            t.start();
            buyerThreads.add(t);
        }



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
        StockMarket stockMarket = new StockMarket(new SimulationInput(), 1000000, 60, 1000, 0);
        Thread A = new Thread(stockMarket);
        A.start();
    }
}

import Skeleton.SimulationInput;
import Skeleton.Unit;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;


public class StockMarket extends Unit {
    private double lastEventBias = 0.0; // [-1.0 (strongly negative) to +1.0 (strongly positive)]
    private double marketBias = 0.0; // Range: [-1.0, 1.0]


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
        // This method is synchronized to prevent concurrent access issues since it's likely called by multiple threads.
        System.out.println("\t\t[Updating Stock Price]");

        // Calculate the average number of available shares historically or across the system
        double avg = TrackedStock.AVGAvalibleShares();
        // Calculate the deviation from the average to assess supply imbalance
        double delta = avg - avalibleShares;
        double ratio = delta / avg; // Ratio used to determine pressure for price change

        // Constants to control price volatility
        double maxRatio = 0.1;             // Cap for how much influence the ratio can exert
        double baseChangeFactor = 0.03;    // Scaling factor for how much prices change
        double priceChange = 0;

        // Retrieve total number of issued shares (assumed fixed over the simulation)
        int totalShares = TrackedStock.getTotalShares(); // Ensure this method exists in Stock class

        // === If there was buying activity and no selling ===
        if (wasBuy && !wasSell) {
            // Volume factor increases impact if more stock was bought relative to market size
            double volumeFactor = Math.min(1.0, (double) AmountBought / (totalShares * 0.05));
            double directionalMultiplier = 1 * volumeFactor;  // Positive influence
            double cappedRatio = Math.max(-maxRatio, Math.min(maxRatio, ratio));  // Clamp the influence
            priceChange = cappedRatio * baseChangeFactor * directionalMultiplier;

            // === If there was only selling activity ===
        } else if (!wasBuy && wasSell) {
            // Stronger cap for selling since price drops are usually sharper
            double volumeFactor = Math.min(3.0, (double) AmountSold / (totalShares * 0.05));
            double directionalMultiplier = 1 * volumeFactor;  // Negative influence
            double cappedRatio = Math.max(-maxRatio, Math.min(maxRatio, ratio));  // Clamp the ratio
            priceChange = cappedRatio * baseChangeFactor * directionalMultiplier;

            // === If there was no activity (idle) ===
        } else if (!wasBuy && !wasSell) {
            // Introduce small random noise to simulate market drift
            double noise = (rng.nextDouble() * 0.01) - 0.005; // Random float in [-0.005, 0.005]
            System.out.printf("\t\t[Random Drift] %.4f\n", noise);
            priceChange = noise;
        }

        // Market sentiment bias
        if (!wasBuy && !wasSell) {
            priceChange += 0.005 * marketBias;
        } else {
            priceChange += 0.01 * marketBias;
        }


        // === Apply price resistance based on current price ===
        // Simulates diminishing return on price movement as price gets high
        double resistance = Math.max(0.1, 1.0 - (MarketPrice / 500.0));
        priceChange *= resistance;

        // === Further dampen price increase based on price tier ===
        // As price increases exponentially, limit how fast it can grow
        if (priceChange > 0) {
            priceChange *= Math.pow(0.97, MarketPrice / 10.0);  // Exponential dampening
        }

        // === Limit the max change per tick ===
        // Ensures price doesn't jump too much in one simulation tick
        double maxTickChange = 0.02; // ±2% max change
        priceChange = Math.max(-maxTickChange, Math.min(maxTickChange, priceChange));

        // === Apply the computed price change ===
        MarketPrice += MarketPrice * priceChange;

        // === Simulate natural decay if no one trades and price is too high ===
        if (!wasBuy && !wasSell && MarketPrice > 20) {
            double decay = MarketPrice * 0.002; // 0.2% decay
            MarketPrice -= decay;
            System.out.printf("\t\t[Idle Decay] -%.4f\n", decay);
        }

        // === Clamp MarketPrice to avoid nonsensical values ===
        if (MarketPrice < 0.01) {
            MarketPrice = 0.01;  // Set floor value
        } else if (MarketPrice > 1000.00) {
            MarketPrice = 500.00; // Set upper cap to prevent runaway prices
        }

        // === Logging the final values ===
        System.out.printf("\t\t Final Price Change: %.4f\n", priceChange);
        System.out.printf("\t\t Updated Market Price: %.2f\n", MarketPrice);

        // === Reset state flags for next tick ===
        wasBuy = false;
        wasSell = false;
        AmountSold = 0;
        AmountBought = 0;


        // === Slowly decay market bias toward neutrality ===
        marketBias *= 0.95; // 5% decay per tick
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

    void MajorEvent() {
        Random rand = new Random();

        // Base chance from -1.0 to 1.0, then subtract bias to skew opposite of last event
        double base = (rand.nextDouble() * 2.0) - 1.0; // [-1.0, 1.0]
        double eventImpact = base - (0.5 * lastEventBias); // skew slightly away from last outcome

        // Clamp to [-1, 1] to stay in valid range
        eventImpact = Math.max(-1.0, Math.min(1.0, eventImpact));

        // Scale severity
        double severity = 0.05 + rand.nextDouble() * 0.15; // [0.05, 0.20]
        double priceChange = MarketPrice * severity * eventImpact;

        // Apply change
        MarketPrice += priceChange;

        // Clamp to minimum
        if (MarketPrice < 0.01) {
            MarketPrice = 0.01;
        }

        // Update bias for next event
        lastEventBias = eventImpact;

        if (eventImpact > 0) {
            System.out.printf("Major Event (Positive): Stock increased by %.2f%% → New price: %.2f\n", severity * 100, MarketPrice);
            marketBias += 0.3; // boost positive bias
        } else {
            System.out.printf("Major Event (Negative): Stock decreased by %.2f%% → New price: %.2f\n", severity * 100, MarketPrice);
            marketBias -= 0.3; // increase negative bias
        }

        // Clamp bias
        marketBias = Math.max(-1.0, Math.min(1.0, marketBias));

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
            CustomWindowPanel marketWindow = new CustomWindowPanel(arrowPanel,true,"Stock Market");
            marketWindow.showWindow(); // This shows your main stock window

            // === Window 2: Simulated Trading ===
            CustomWindowPanel simTradeWindow = new CustomWindowPanel(simPanel,false,"Simulated Trading Window");
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

            if(Now%1000==0){
                MajorEvent();
            }
            Now++;
        }
    }

    public static void main(String[] args) {
        StockMarket stockMarket = new StockMarket(new SimulationInput(), 10000, 50.00, 1000, 0);
        Thread A = new Thread(stockMarket);
        A.start();
    }

}

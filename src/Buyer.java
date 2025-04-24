import Skeleton.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * The Buyer class represents a simulated market participant with decision-making logic based on market trends,
 * trust levels, and activity. Each buyer runs on its own thread and participates in a stock market simulation
 * by buying or selling stock and tracking statistics throughout the simulation.
 */
public class Buyer extends Robot implements StockObserver {

    /**
     * Timestamp of the last trade executed by the buyer, used to enforce cooldown periods.
     */
    private long lastTradeTime = 0;

    /**
     * Minimum cooldown time (in milliseconds) between trades for a highly active buyer.
     */
    private static final long MIN_COOLDOWN = StockMarket.waiting * 10;

    /**
     * Maximum cooldown time (in milliseconds) between trades for a very passive buyer.
     */
    private static final long MAX_COOLDOWN = StockMarket.waiting * 100;

    /**
     * Total amount of money the buyer has spent purchasing stocks.
     */
    private double totalSpent = 0;

    /**
     * Total amount of money the buyer has earned from selling stocks.
     */
    private double totalEarned = 0;

    /**
     * The net worth of the buyer at the start of the simulation (capital + value of initial holdings).
     */
    public final double StartNetWorth;

    /**
     * Flag used to indicate that the buyer should stop its thread execution.
     */
    private boolean stop = false;

    /**
     * If true, the buyer will print its trading activity to the console for logging/debugging.
     */
    boolean speak = true;

    /**
     * Trust level in the market. A value from 0 (no trust, likely to sell) to 100 (full trust, likely to buy).
     */
    double baseTrust;

    /**
     * Activity level of the buyer. A value from 0 (inactive, low-frequency trading) to 100 (very active).
     */
    double activity;

    /**
     * The current price used internally by the buyer for making decisions (not always the latest market price).
     */
    double price;

    /**
     * Flag that is set to true when the buyer receives a new price update and should consider making a trade.
     */
    volatile boolean newpricing = false;

    /**
     * Reference to the stock market the buyer is trading in. Used for accessing current price, submitting trades, etc.
     */
    StockMarket stockMarket;

    /**
     * Number of shares currently held by the buyer.
     */
    int holding;

    /**
     * The amount of capital the buyer has left for purchasing stocks.
     */
    double Capital = 1000;


    /**
     * Constructs a Buyer instance with specified parameters.
     *
     * @param input       the simulation input parameters
     * @param name        the name of the buyer
     * @param holding     initial number of shares held
     * @param stockMarket reference to the shared StockMarket instance
     * @param baseTrust   trust level (0-100)
     * @param activity    activity level (0-100)
     */
    public Buyer(SimulationInput input, String name, int holding, StockMarket stockMarket, double baseTrust, double activity) {
        super(name, input);
        System.out.println("Buyer initialized: " + getName());
        this.holding = holding;
        this.stockMarket = stockMarket;
        this.baseTrust = baseTrust;
        this.activity = activity;
        stockMarket.TrackedStock.addObserver(this);
        stockMarket.addBuyer(this);
        StartNetWorth = holding * stockMarket.getCurrentPrice() + Capital;

        // Register tracked statistics
        this.getStats().addStatistic("Total Buys", new BasicStatistic("Total Buys"));
        this.getStats().addStatistic("Total Sells", new BasicStatistic("Total Sells"));
        this.getStats().addStatistic("Total Buy Value", new BasicStatistic("Total Buy Value"));
        this.getStats().addStatistic("Total Sell Value", new BasicStatistic("Total Sell Value"));
    }

    /**
     * Calculates cooldown between trades based on activity level.
     */
    private long getTradeCooldownMillis() {
        double ratio = activity / 100.0;
        return (long)(MAX_COOLDOWN - (ratio * (MAX_COOLDOWN - MIN_COOLDOWN)));
    }

    /**
     * Checks if the buyer is allowed to make a trade.
     */
    private boolean canTrade() {
        return System.currentTimeMillis() - lastTradeTime >= getTradeCooldownMillis();
    }

    void removeholding(int amount) {
        holding -= amount;
    }

    void addholding(int amount) {
        holding += amount;
    }

    /**
     * Determines how far back in hours the buyer will analyze price trends.
     *
     * @return number of hours to look back
     */
    int getLookbackHours() {
        int maxLookback = 730; // one month
        int minLookback = 24;  // one day
        double ratio = activity / 100.0;
        return (int)(maxLookback - ratio * (maxLookback - minLookback));
    }

    /**
     * Calculates the percentage trend score based on past and recent prices.
     */
    public double getTrendScore() {
        int lookbackTicks = Math.max(3, getLookbackTicks());
        int size = stockMarket.TrackedStock.getTrackedsize();
        if (size < lookbackTicks + 1) return 0;

        double recent = stockMarket.TrackedStock.getPriceAt(size - 1);
        double past = stockMarket.TrackedStock.getPriceAt(size - 1 - lookbackTicks);

        if (past == 0) return 0;

        return ((recent - past) / past) * 100;
    }

    /**
     * Returns the number of ticks to look back based on activity.
     */
    private int getLookbackTicks() {
        return (int)(20 - (activity / 5));
    }

    /**
     * Makes a decision based on trend score, trust, and random variation.
     *
     * @return 1 = Sell, 2 = Buy, 3 = Hold
     */
    int makeDecision() {
        double trendScore = getTrendScore();
        int randomness = new Random().nextInt(21) - 10;
        double confidence = 0;

        if (trendScore <= -20) {
            double sellPressure = (100 - baseTrust) * 0.6;
            double buyRescue = baseTrust * 0.3;
            double activityBoost = activity * 0.2;
            confidence = buyRescue - sellPressure + activityBoost + randomness;
            System.out.println(getName() + " sees a CRASHING market");
        } else if (trendScore <= -5) {
            confidence = ((50 - baseTrust) * 0.5) + (activity * 0.2) + randomness;
            System.out.println(getName() + " sees a DECLINING market");
        } else if (trendScore <= 5) {
            double trustBias = (baseTrust - 50) * 0.05;
            double activityBias = (activity - 50) * 0.05;
            confidence = 2 + trustBias - randomness * activityBias;
        } else if (trendScore <= 20) {
            double trustBias = (baseTrust - 50) * 0.5;
            double activityBoost = activity * 0.2;
            confidence = trustBias + activityBoost + randomness;
            System.out.println(getName() + " sees a RISING market");
        } else {
            double trustBias = baseTrust * 0.8;
            double activityBoost = activity * -0.3;
            confidence = trustBias + activityBoost + randomness;
            System.out.println(getName() + " sees a BOOMING market");
        }

        if (confidence < -10 && holding > 0) return 1;
        if (confidence > 10 && Capital > 0) return 2;
        return 3;
    }

    /**
     * Calculates how many shares to buy or sell based on market sentiment and buyer attributes.
     *
     * @param selling whether the buyer is selling
     * @return amount to buy/sell
     */
    synchronized int getTransactionAmount(boolean selling) {
        double percent = (-(stockMarket.getMarketTrend(getLookbackHours()) - stockMarket.getCurrentPrice())) / stockMarket.getCurrentPrice();
        double trendScore = percent * 100;

        double scalingFactor;
        int baseAmount = (int)(activity / 10) + new Random().nextInt(5) - 2;
        baseAmount = Math.max(1, baseAmount);

        if (trendScore <= -75) {
            scalingFactor = selling ? (1.0 - baseTrust / 100.0) + (activity / 200.0) : baseTrust / 200.0;
        } else if (trendScore <= -30) {
            scalingFactor = selling ? (1.0 - baseTrust / 150.0) + (activity / 300.0) : baseTrust / 300.0;
        } else if (trendScore <= 10) {
            scalingFactor = 0.2 + (activity / 500.0);
        } else if (trendScore <= 50) {
            scalingFactor = selling ? (1.0 - baseTrust / 200.0) : baseTrust / 150.0 + (activity / 300.0);
        } else {
            scalingFactor = selling ? 0.1 : baseTrust / 100.0 + activity / 200.0;
        }

        scalingFactor += (new Random().nextDouble() * 0.2) - 0.1;
        scalingFactor = Math.max(0.1, Math.min(1.0, scalingFactor));

        if (selling) {
            if (holding <= 0) return 0;
            int amountToSell = (int)(holding * scalingFactor);
            return Math.max(1, Math.min(amountToSell, holding));
        } else {
            int available = stockMarket.getAvalibleShares();
            if (available <= 0) return 0;
            int maxPossible = (int)Math.floor(Capital / stockMarket.getCurrentPrice());
            int amountToBuy = (int)(Math.min(maxPossible, available) * scalingFactor);
            return Math.max(1, Math.min(amountToBuy, available));
        }
    }

    /**
     * Performs the action determined by the decision function (buy/sell/hold).
     */
    @Override
    public void performAction() {
        if (!canTrade()) return;

        switch (makeDecision()) {
            case 1:
                int soldAmount = getTransactionAmount(true);
                stockMarket.sell(soldAmount, this);
                if (speak) {
                    System.out.println(getName() + " Sold " + soldAmount + " shares at price " + stockMarket.getCurrentPrice());
                }
                totalEarned += soldAmount * stockMarket.getCurrentPrice();
                this.getStats().getStatistic("Total Sells").addValue(soldAmount);
                this.getStats().getStatistic("Total Sell Value").addValue(soldAmount * stockMarket.getCurrentPrice());
                break;

            case 2:
                int buyAmount = getTransactionAmount(false);
                double cost = buyAmount * stockMarket.getCurrentPrice();
                if (Capital >= cost && buyAmount > 0) {
                    stockMarket.buy(buyAmount, this);
                    if (speak) {
                        System.out.println(getName() + " bought " + buyAmount + " shares at price " + stockMarket.getCurrentPrice());
                    }
                    totalSpent += cost;
                    this.getStats().getStatistic("Total Buys").addValue(buyAmount);
                    this.getStats().getStatistic("Total Buy Value").addValue(cost);
                }
                break;

            case 3:
                // Hold: do nothing
                break;
        }
    }

    /**
     * Submits end-of-simulation metrics to the Statistics singleton.
     */
    @Override
    public void submitStatistics() {
        double netProfit = totalEarned - totalSpent;
        double currentNetWorth = Capital + (holding * stockMarket.getCurrentPrice());
        double netWorthChange = currentNetWorth - StartNetWorth;

        double avgBuyPrice = this.getStats().getStatistic("Total Buys").summarize() > 0
                ? this.getStats().getStatistic("Total Buy Value").summarize() / this.getStats().getStatistic("Total Buys").summarize()
                : 0;

        double avgSellPrice = this.getStats().getStatistic("Total Sells").summarize() > 0
                ? this.getStats().getStatistic("Total Sell Value").summarize() / this.getStats().getStatistic("Total Sells").summarize()
                : 0;

        this.getStats().addStatistic("Net Profit", new BasicStatistic("Net Profit")).addValue(netProfit);
        this.getStats().addStatistic("Net Worth", new BasicStatistic("Net Worth")).addValue(currentNetWorth);
        this.getStats().addStatistic("Net Worth Change", new BasicStatistic("Net Worth Change")).addValue(netWorthChange);
        this.getStats().addStatistic("Average Buy Price", new BasicStatistic("Average Buy Price")).addValue(avgBuyPrice);
        this.getStats().addStatistic("Average Sell Price", new BasicStatistic("Average Sell Price")).addValue(avgSellPrice);
        this.getStats().addStatistic("Holding", new BasicStatistic("Holding")).addValue(holding);
    }

    /**
     * Main thread loop for the Buyer. Waits for price updates and performs actions when notified.
     */
    @Override
    public void run() {
        System.out.println("\t\t Running Buyer");
        while (true) {
            synchronized (this) {
                while (!newpricing && !stop) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                if (stop) break;
                performAction();
                newpricing = false;
            }
        }
        System.out.println("\t\t Stopping Buyer");
    }

    /**
     * Receives a stock price update and wakes the Buyer thread.
     */
    @Override
    public void getnewpricing(double price) {
        synchronized (this) {
            newpricing = true;
            notify();
        }
    }

    public double getCapital() {
        return Capital;
    }

    public int getHolding() {
        return holding;
    }

    public double getNetProfit() {
        return totalEarned - totalSpent;
    }

    /**
     * Signals the buyer thread to stop gracefully.
     */
    public void Stop() {
        stop = true;
        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * Resets the buyer's capital to a default lower amount (e.g. for testing).
     */
    public void MakeNormal() {
        this.Capital = 100;
    }
}

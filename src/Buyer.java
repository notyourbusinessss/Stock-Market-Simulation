import Skeleton.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Buyer extends Unit implements StockObserver {
    boolean speak = true;
    /**
     * Base trust is the trust someone will have in a certain market
     * 0   --> no trust in the market at all : will sell more then he will buy.
     * 50  --> moderate trust : no action is more likely
     * 100 --> Extreme trust in the market : will buy more than he will sell
     */
    double baseTrust;
    /**
     * Represents on a scale of 0 - 100 how active and reactive a buyer is this determines the amount of stock he will buy/sell per transactions and how far back he will look back
     * 0   --> looks quite far in the past, sells in and buys in low amounts
     * 50  --> looks moderatly back into the pasts sells in moderate amounts
     * 100 --> looks quite close to the present ( about 24h ) and sells/ buys in high amounts.
     *
     */
    double activity;
    double price;
    volatile boolean newpricing = false;
    StockMarket stockMarket;
    String name;
    /**
     * current holding of the buyer.
     */
    int holding;

    double Capital = 1000;


    public Buyer(SimulationInput input) {
        super(input);
    }
    public Buyer(SimulationInput input, String name, int holding, StockMarket stockMarket, double baseTrust, double activity) {
        super(input);
        System.out.println("Creating Buyer");
        this.name = name;
        this.holding = holding;
        this.stockMarket = stockMarket;
        this.baseTrust = baseTrust;
        this.activity = activity;
        stockMarket.TrackedStock.addObserver(this);
    }

    void removeholding(int amount){
        holding -= amount;
    }
    void addholding(int amount){
        holding += amount;
    }

    /**
     * depending on how active someone is they would look at trends in different time frames
     * @return an integer representing hours
     */
    int getLookbackHours() {
        if(false) {
            return 5;
        }

        int maxLookback = 730; // one month in hours
        int minLookback = 24; // one day in hours, duh...

        double ratio = activity / 100.0;
        int lookback = (int)(maxLookback - ratio * (maxLookback - minLookback));

        return lookback;
    }

    public double getTrendScore() {
        int lookbackTicks = Math.max(3, getLookbackTicks()); // Based on activity

        int size = stockMarket.TrackedStock.getTrackedsize();
        if (size < lookbackTicks + 1) return 0;

        double recent = stockMarket.TrackedStock.getPriceAt(size - 1);
        double past = stockMarket.TrackedStock.getPriceAt(size - 1 - lookbackTicks);

        if (past == 0) return 0;

        double diff = recent - past;
        return (diff / past) * 100;
    }
    private int getLookbackTicks() {
        // Convert activity [0-100] into lookback range (e.g., 3 to 20 ticks)
        return (int)(20 - (activity / 5)); // activity = 0 ⇒ lookback 20, activity = 100 ⇒ lookback 0
    }


    /**
     * The buyer depending on certain variables will make a decision and return
     * 1 : sell
     * 2 : buy
     * 3 : hold
     * @return
     */
    int makeDecision() {
        double trendScore = getTrendScore(); // already in percentage, do NOT multiply by 100

        int randomness = new Random().nextInt(21) - 10;
        double confidence = 0;

        if (trendScore <= -20) {
            double sellPressure = (100 - baseTrust) * 0.6;
            double buyRescue = baseTrust * 0.3;
            double activityBoost = activity * 0.2;
            confidence = buyRescue - sellPressure + activityBoost + randomness;
            System.out.println(name + " sees a CRASHING market");
        } else if (trendScore <= -5) {
            confidence = ((50 - baseTrust) * 0.5) + (activity * 0.2) + randomness;
            System.out.println(name + " sees a DECLINING market");
        } else if (trendScore <= 5) {
            double trustBias = (baseTrust - 50) * 0.05;
            double activityBias = (activity - 50) * 0.05;
            confidence = 2 + trustBias - randomness * activityBias;
            //System.out.println(name + " sees a STABLE market");
        } else if (trendScore <= 20) {
            double trustBias = (baseTrust - 50) * 0.5;
            double activityBoost = activity * 0.2;
            confidence = trustBias + activityBoost + randomness;
            System.out.println(name + " sees a RISING market");
        } else {
            double trustBias = baseTrust * 0.8;
            double activityBoost = activity * -0.3;
            confidence = trustBias + activityBoost + randomness;
            System.out.println(name + " sees a BOOMING market");
        }

        if (confidence < -10 && holding > 0) return 1;
        if (confidence > 10 && Capital > 0) return 2;
        return 3;
    }


    synchronized int getTransactionAmount(boolean selling) {
        double percent = (-(stockMarket.getMarketTrend(getLookbackHours()) - stockMarket.getCurrentPrice())) / stockMarket.getCurrentPrice();
        double trendScore = percent * 100;

        double scalingFactor;
        int baseAmount = (int)(activity / 10) + new Random().nextInt(5) - 2;
        baseAmount = Math.max(1, baseAmount);

        if (trendScore <= -75) {
            // Market is crashing
            scalingFactor = selling
                    ? (1.0 - baseTrust / 100.0) + (activity / 200.0)
                    : baseTrust / 200.0;
        } else if (trendScore <= -30) {
            // Market is declining
            scalingFactor = selling
                    ? (1.0 - baseTrust / 150.0) + (activity / 300.0)
                    : baseTrust / 300.0;
        } else if (trendScore <= 10) {
            // Market is stable
            scalingFactor = 0.2 + (activity / 500.0);
        } else if (trendScore <= 50) {
            // Market is rising
            scalingFactor = selling
                    ? (1.0 - baseTrust / 200.0)
                    : baseTrust / 150.0 + (activity / 300.0);
        } else {
            // Market is booming
            scalingFactor = selling
                    ? 0.1
                    : baseTrust / 100.0 + activity / 200.0;
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
     *
     */
    @Override
    public void performAction() {
        switch (makeDecision()) {
            case 1:
                int soldAmount = getTransactionAmount(true);
                stockMarket.sell(soldAmount, this);
                if (speak) {
                    System.out.println(name + " Sold " + soldAmount + " shares at a price of " +
                            stockMarket.getCurrentPrice() + " totaling at: " +
                            (stockMarket.getCurrentPrice() * soldAmount) + " | Capital: " + Capital);
                }
                break;
            case 2:
                int buyAmount = getTransactionAmount(false);
                double cost = buyAmount * stockMarket.getCurrentPrice();

                if (Capital >= cost && buyAmount > 0) {
                    stockMarket.buy(buyAmount, this);
                    if (speak) {
                        System.out.println(name + " bought " + buyAmount + " shares at a price of " +
                                stockMarket.getCurrentPrice() + " | Cost: " + cost + " | Capital: " + Capital);
                    }
                } else if (speak) {
                    System.out.println(name + " wanted to buy but couldn't afford. Needed: " + cost + " | Has: " + Capital);
                }
                break;
            case 3:
                //if (speak) System.out.println(name + " is holding.");
                break;
        }
    }


    /**
     *
     */
    @Override
    public void submitStatistics() {

    }

    @Override
    public void run() {
        System.out.println("\t\t Running Buyer");
        while (StockMarket.isOpen()) {
            /// Fix this
            if(Capital < 0 ){
                Capital = 0;
            }
            synchronized (this) {
                while (!newpricing) {
                    try {
                        wait(); // wait until notify() is called
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                performAction();
                newpricing = false;

                }
            if(!stockMarket.isOpen()){
                return;
            }
        }
    }


    /**
     * Testing grounds
     * @param args
     */
    public static void main(String[] args) {
        Buyer buyer = new Buyer(new SimulationInput());
    }

    @Override
    public void getnewpricing(double price) {
        //System.out.println(name + "\t\t Got new pricing");
        synchronized (this) {
            newpricing = true;
            notify(); // wake up the thread
        }
    }

}

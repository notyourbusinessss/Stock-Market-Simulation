import Skeleton.*;
import java.util.Random;

public class Buyer extends Unit implements StockObserver {
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
    boolean newpricing;
    StockMarket stockMarket;
    String name;
    /**
     * current holding of the buyer.
     */
    int holding;


    public Buyer(SimulationInput input) {
        super(input);
    }
    public Buyer(SimulationInput input, String name, int holding, StockMarket stockMarket, double baseTrust, double activity) {
        super(input);
        this.name = name;
        this.holding = holding;
        this.stockMarket = stockMarket;
        this.baseTrust = baseTrust;
        this.activity = activity;

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
        int maxLookback = 730; // one month in hours
        int minLookback = 24; // one day in hours, duh...

        double ratio = activity / 100.0;
        int lookback = (int)(maxLookback - ratio * (maxLookback - minLookback));

        return lookback;
    }

    /**
     * The buyer depending on certain variables will make a decision and return
     * 1 : sell
     * 2 : buy
     * 3 : hold
     * @return
     */
    int makeDecision() {

        double percent = stockMarket.getMarketTrend(getLookbackHours())/stockMarket.getCurrentPrice();
        // Normalize market trend for weighting
        double trendScore = percent * 100; // e.g. +5 for up 5%, -10 for down 10%
        trendScore = Math.max(-100, Math.min(100, trendScore)); // clamp range

        // Add randomness to simulate unpredictable human behavior
        int randomness = new Random().nextInt(21) - 10; // [-10, +10]

        // Compute confidence score (tune weights as needed)
        double confidence = (trendScore * 0.6)
                + (baseTrust * 0.4)
                + (activity * 0.3)
                + randomness;

        // Decision thresholds
        if (confidence < -10 && holding > 0){ //cannot sell something they do not have

            return 1; // SELL
        }
        if (confidence > 10) {
            return 2;  // BUY
        }
        return 3;                       // HOLD

    }
    int getTransactionAmount(boolean selling) {
        switch (selling ? 1 : 2) {
            case 1:


                break;
            case 2:
                int avalibleStocks = stockMarket.getAvalibleShares();

                break;
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
                stockMarket.sell(soldAmount,this);
                System.out.println(name + " Sold " + soldAmount + " shares at a price of " + stockMarket.getCurrentPrice() + " totaling at a price of " + stockMarket.getCurrentPrice()*soldAmount);
                break;
            case 2:
                int buyAmount = getTransactionAmount(false);
                stockMarket.buy(buyAmount,this);
                break;
            case 3:
                //Do nothing, your holding...
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
        while (StockMarket.isOpen()) {
            if(newpricing){
                performAction();
                /// at the end of the action make the newpricing variable false
                newpricing = false;
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
        newpricing = true;

    }
}

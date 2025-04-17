import Skeleton.*;
import java.util.Random;

public class Buyer extends Unit {
    /// Base stats for behavioral tendencies
    double baseTrust;
    double activity;
    StockMarket stockMarket;


    public Buyer(SimulationInput input) {
        super(input);
    }

    /**
     * The buyer depending on certain variables will make a decision and return
     * 1 : sell
     * 2 : buy
     * 3 : hold
     * @return
     */
    int makeDecision() {
        double percent = stockMarket.getMarketTrend(50)/stockMarket.getCurrentPrice();
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
        if (confidence < -10) return 1; // SELL
        if (confidence > 10) return 2;  // BUY
        return 3;                       // HOLD
    }

    /**
     *
     */
    @Override
    public void performAction() {

    }

    /**
     *
     */
    @Override
    public void submitStatistics() {

    }

    /**
     * Testing grounds
     * @param args
     */
    public static void main(String[] args) {

    }
}

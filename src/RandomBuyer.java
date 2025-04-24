import Skeleton.SimulationInput;
import java.util.Random;

/**
 * RandomBuyer is a type of {@link Buyer} that makes decisions to buy, sell, or hold at random.
 * It does not evaluate trends or market behavior and simply chooses actions stochastically.
 * This class is useful for testing randomness and comparing against more strategic buyers.
 */
public class RandomBuyer extends Buyer {

    /**
     * Constructs a RandomBuyer with specified attributes and disables console output by default.
     *
     * @param input       simulation configuration input
     * @param name        the name of the buyer
     * @param holding     initial number of shares held
     * @param stockMarket reference to the shared stock market instance
     * @param baseTrust   trust in the market (not used in this subclass)
     * @param activity    activity level (not used in this subclass)
     */
    public RandomBuyer(SimulationInput input, String name, int holding, StockMarket stockMarket, double baseTrust, double activity) {
        super(input, name, holding, stockMarket, baseTrust, activity);
        this.speak = false; // disable logs for random buyers
    }

    /**
     * Makes a completely random decision to sell or buy (if allowed), otherwise holds.
     *
     * @return 1 = Sell, 2 = Buy, 3 = Hold
     */
    @Override
    int makeDecision() {
        Random rand = new Random();
        boolean decision = rand.nextBoolean(); // true = sell, false = buy

        if (decision && holding > 0) {
            return 1; // SELL
        } else if (!decision && stockMarket.getAvalibleShares() > 0 && Capital > 0) {
            return 2; // BUY
        } else {
            return 3; // HOLD
        }
    }

    /**
     * Randomly selects how many shares to buy or sell within valid constraints.
     *
     * @param selling true if selling, false if buying
     * @return number of shares to transact
     */
    @Override
    int getTransactionAmount(boolean selling) {
        Random rand = new Random();
        if (selling) {
            int amount = rand.nextInt(holding + 1); // sell up to all holdings
            return amount;
        } else {
            int maxAffordable = (int)Math.floor(Capital / stockMarket.getCurrentPrice());
            int maxAvailable = stockMarket.getAvalibleShares();
            int maxBuyable = Math.min(maxAffordable, maxAvailable);
            int amount = rand.nextInt(maxBuyable + 1); // buy up to what's affordable and available
            return amount;
        }
    }
}

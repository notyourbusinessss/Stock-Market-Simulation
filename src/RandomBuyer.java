import Skeleton.SimulationInput;
import java.util.Random;

public class RandomBuyer extends Buyer {

    public RandomBuyer(SimulationInput input, String name, int holding, StockMarket stockMarket, double baseTrust, double activity) {
        super(input, name, holding, stockMarket, baseTrust, activity);
        this.speak = false;
    }

    @Override
    int makeDecision() {
        Random rand = new Random();
        boolean decision = rand.nextBoolean(); // true = sell, false = buy

        //System.out.printf("%s -- Holding: %d, Capital: %.2f%n", name, holding,Capital);

        if (decision && holding > 0) {
            return 1; // SELL
        } else if (!decision && stockMarket.getAvalibleShares() > 0 && Capital > 0) {
            return 2; // BUY
        } else {
            return 3; // HOLD
        }
    }

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
            int amount = rand.nextInt(maxBuyable + 1); // buy up to affordable + available
            return amount;
        }
    }
}

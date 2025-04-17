import Skeleton.*;

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
        if(percent < 0){



        } else if (percent > 0) {



        }
        return 1;
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

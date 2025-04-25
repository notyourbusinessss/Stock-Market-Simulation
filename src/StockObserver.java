/**
 * The StockObserver interface should be implemented by any class that wants to be notified
 * when the stock price is updated. This is part of the Observer design pattern used in the simulation.
 */
public interface StockObserver {

    /**
     * Called when the stock price is updated. The implementing class can use this method
     * to respond to new price data (e.g., to make a buy/sell decision).
     *
     * @param price the newly updated stock price
     */
    void getnewpricing(double price);
}

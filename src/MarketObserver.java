/**
 * Interface for observers that want to be notified about changes in the market state.
 * Implementing classes will receive updates when the number of shares or the stock price changes.
 */
public interface MarketObserver {

    /**
     * Called when the market updates its state.
     *
     * @param shares the number of shares currently available in the market
     * @param price  the current price of the stock
     */
    void updateMarketState(int shares, double price);
}

/**
 * Factory class for creating instances of {@link Buyer} with customized attributes.
 * Encapsulates the logic for assigning shares, setting initial trust and activity levels,
 * and configuring behavioral traits like logging or capital resets.
 */
public class BuyerFactory {

    /**
     * Reference to the stock market simulation. Used to access shared state such as available shares.
     */
    private final StockMarket market;

    /**
     * Constructs a BuyerFactory bound to a specific StockMarket instance.
     *
     * @param market the stock market simulation in which buyers will be created
     */
    public BuyerFactory(StockMarket market) {
        this.market = market;
    }

    /**
     * Creates a new {@link Buyer} with the given parameters.
     *
     * @param name         the buyer's display name
     * @param sharePercent the percentage (0.0–1.0) of available shares to allocate to this buyer
     * @param trust        initial market trust level (0 to 100)
     * @param activity     how active the buyer is in the market (0 to 100)
     * @param normal       if true, sets buyer's capital to a baseline (via {@code MakeNormal()})
     * @param speak        if true, enables console output of buyer activity
     * @return a fully configured {@link Buyer} ready to participate in the simulation
     */
    public Buyer createBuyer(String name, double sharePercent, double trust, double activity, boolean normal, boolean speak) {
        int holding = (int) (market.getAvalibleShares() * sharePercent);
        market.decreaseAvalibleShares(holding);

        Buyer buyer = new Buyer(market.getSimInput(), name, holding, market, trust, activity);
        if (normal) {
            buyer.MakeNormal();
        }
        buyer.speak = speak;
        return buyer;
    }
}

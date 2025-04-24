public class BuyerFactory {
    private final StockMarket market;

    public BuyerFactory(StockMarket market) {
        this.market = market;
    }

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

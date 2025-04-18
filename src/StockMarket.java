import java.util.ArrayList;
import java.util.List;

public class StockMarket implements StockObserver {
    Stock TrackedStock;
    private int avalibleShares;
    private List<MarketObserver> Stocks = new ArrayList<>();
    /**
     * This will calculate the trend over a specified amount of time and give you the trend
     * @return
     */
    double getMarketTrend(int GoBack){
        return TrackedStock.getTrend(GoBack);

    }
    double getCurrentPrice(){
        return TrackedStock.getCurrentPrice();
    }

    void ForcedMarketPrice(ArrayList<Double> ForcedMarketPrices){
        TrackedStock.ForcedStock(ForcedMarketPrices);
    }

    void sell(int amount){

    }

    int getAvalibleShares(){
        return avalibleShares;
    }
    void buy(int amount){

    }
    void updateStock(){
        /// make sure the stock only updates once per tick
        for (MarketObserver observer : Stocks){
            observer.updateMarketState();
        }
    }
}

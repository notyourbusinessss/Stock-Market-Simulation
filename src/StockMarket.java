import java.util.ArrayList;

public class StockMarket implements StockObserver {
    Stock TrackedStock;
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
}

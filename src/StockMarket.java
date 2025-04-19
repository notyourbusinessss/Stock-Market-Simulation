import Skeleton.SimulationInput;
import Skeleton.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * The stock market is the middle ground of the interactions between the buyers and the Stock, here is where the buyers will buy or sell their stocks.
 */
public class StockMarket extends Unit{
    Stock TrackedStock;
    private int avalibleShares;
    private List<MarketObserver> Stocks = new ArrayList<>();
    double MarketPrice;
    /**
     * Time in which the simulation will run
     */
    int Time;
    int Now;

    static boolean open;

    public StockMarket(SimulationInput input) {
        super(input);
    }

    /**
     * This will calculate the trend over a specified amount of time and give you the trend
     * @return
     */
    double getMarketTrend(int GoBack){
        return TrackedStock.getTrend(GoBack);

    }
    double getCurrentPrice(){
        return MarketPrice;
    }

    void ForcedMarketPrice(ArrayList<Double> ForcedMarketPrices){
        TrackedStock.ForcedStock(ForcedMarketPrices);
    }

    synchronized void sell(int amount,Buyer buyer){
        buyer.removeholding(amount);
        avalibleShares += amount;
    }

    void updateStockPrice(){
        if(avalibleShares == 0){

            return;
        }
        if(avalibleShares > TrackedStock.AVGAvalibleShares()){
            double percentage = ((double)(avalibleShares - TrackedStock.AVGAvalibleShares()) /(double)TrackedStock.AVGAvalibleShares())*(-1) - 1;
            /// update Market Price
        }else if(avalibleShares < TrackedStock.AVGAvalibleShares()){
            double percentage = ((double)(avalibleShares - TrackedStock.AVGAvalibleShares()) /(double)TrackedStock.AVGAvalibleShares())*(-1) + 1;
            /// update Market Price
        }else{

        }
    }

    int getAvalibleShares(){
        return avalibleShares;
    }
    void buy(int amount,Buyer buyer){

    }
    void updateStock(){
        /// make sure the stock only updates once per tick
        for (MarketObserver observer : Stocks){
            observer.updateMarketState(avalibleShares, MarketPrice);
        }
    }

    static boolean isOpen(){
        return open;
    }

    @Override
    public void performAction() {

    }

    @Override
    public void submitStatistics() {

    }


    @Override
    public void run() {
        while (StockMarket.isOpen()) {
            if(Now >= Time){
                open = false;
                break;
            }
            if(Now == 0){
                /// initialize everything
                Buyer buyer1 = new Buyer();
            }else{
                updateStock();

            }

            Time++;
        }
    }



}

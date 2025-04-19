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

    static boolean open = true;

    public StockMarket(SimulationInput input) {
        super(input);
    }
    public StockMarket(SimulationInput input, int totalShares, double InitialPrice, int time, int now) {
        super(input);
        this.avalibleShares = totalShares;
        this.MarketPrice = InitialPrice;
        this.Time = time;
        this.Now = now;
        TrackedStock = Stock.getInstance(this.MarketPrice, this.avalibleShares);
        Stocks.add(TrackedStock);
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

    synchronized void updateStockPrice() {
        System.out.println("\t\t updating");
        if (avalibleShares == 0) {
            return;
        }

        double avg = TrackedStock.AVGAvalibleShares();
        double percentage;

        if (avalibleShares > avg) {
            percentage = ((double)(avalibleShares - TrackedStock.AVGAvalibleShares()) /(double)TrackedStock.AVGAvalibleShares())*(-1);
            System.out.println("\t\t avalible shares: " + avalibleShares + "; percentage: " + percentage);
            MarketPrice += MarketPrice * percentage;
        } else if (avalibleShares < avg) {
            percentage = ((double)(avalibleShares - TrackedStock.AVGAvalibleShares()) /(double)TrackedStock.AVGAvalibleShares())*(-1);
            System.out.println("\t\t avalible shares: " + avalibleShares + "; percentage: " + percentage);
            MarketPrice += MarketPrice * percentage;
        } else {
            // when supply == average supply, apply slight decay
            double decay = 1.0 / (avg == 0 ? MarketPrice : avg);
            System.out.println("\t\t decaying " + decay);
            MarketPrice -= decay;
        }
        System.out.println("\t\t MarketPrice : " + MarketPrice);
        // Clamp price to a minimum of 1 cent
        if (MarketPrice < 0.01) {
            MarketPrice = 0.01;
        }

    }


    int getAvalibleShares(){
        return avalibleShares;
    }
    void buy(int amount,Buyer buyer){
        buyer.addholding(amount);
        avalibleShares -= amount;
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
            if(Now > Time){
                open = false;
                break;
            }
            if(Now == 0){
                System.out.println("setting");
                /// initialize everything
                Buyer buyer1 = new Buyer(new SimulationInput(),"George -1-",(int)(this.avalibleShares*0.1),this,50,100);
                this.avalibleShares -= buyer1.holding;
                Buyer buyer2 = new Buyer(new SimulationInput(),"Mark -2-",(int)(this.avalibleShares*0.1),this,50,100);
                this.avalibleShares -= buyer2.holding;
                Thread A  = new Thread(buyer1);
                Thread B = new Thread(buyer2);
                A.start();
                B.start();
                /*try {
                    A.join();
                    B.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }*/
            }else{
                updateStockPrice();
                updateStock();
            }
            System.out.println(this.TrackedStock + " : " + this.Now);

            Now++;
        }
        System.out.println("stop");
        return;
    }

    /**
     * Testing grounds
     * @param args
     */
    public static void main(String[] args) {
       StockMarket stockMarket = new StockMarket(new SimulationInput(),100,50.00,1000,0);
        Thread A = new Thread(stockMarket);
        A.start();
    }

}

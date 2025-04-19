import java.util.ArrayList;
import java.util.List;

/**
 * Stock class will have the data of the stock and also the calculation of the trends
 * used singleton in order to [ADD DESCRIPTION HERE]
 * Stock is also an observer of the market
 */
public class Stock implements MarketObserver{
    private static Stock instance = null;
    private List<StockObserver> observers = new ArrayList<>();
    private double currentPrice;
    private ArrayList<Double> trackedPrices;
    private ArrayList<Integer> trackedShares;


    /// let's make it a singleton yaaayyyyy
    private Stock(double currentPrice,int shares){
        currentPrice = currentPrice;
        trackedPrices = new ArrayList<>();
        trackedPrices.add(currentPrice);
        trackedShares = new ArrayList<>();
        trackedShares.add(shares);

    }
    public static synchronized Stock getInstance(double startingPrice,int shares){
        if(instance == null){
            instance = new Stock(startingPrice,shares);
        }
        return instance;
    }

    void addObserver(StockObserver observer){
        observers.add(observer);
        System.out.println("\t\t Observer added : " + observer.getClass().getName());
    }

    double getPriceAt(int time){
        return trackedPrices.get(time);
    }

    double getTrend(int Given){
        int time = Math.max(0, Math.min(Given, this.trackedPrices.size() - 1));
        double AVG = 0;
        double trend = 0;
        for(int i = time; i > 0; i--){
            AVG += trackedPrices.get(i);
        }
        AVG = AVG/(time);
        for(int i = time; i > 0; i--){
            if(trackedPrices.get(i) > AVG){
                trend += trackedPrices.get(i);
            }else if(trackedPrices.get(i) < AVG){
                trend -= trackedPrices.get(i);
            }
        }
        return trend;
    }

    int AVGAvalibleShares(){
        int sum = 0;
        for(int i : trackedShares){
            sum += i;
        }
        return sum/trackedShares.size();
    }

    double getCurrentPrice(){
        return currentPrice;
    }

    void ForcedStock(ArrayList<Double> ForcedMarketPrices){
       trackedPrices = ForcedMarketPrices;
    }
    void NotifyBuyers(){
        for(StockObserver observer : observers){
            observer.getnewpricing(currentPrice);
        }
    }

    @Override
    public String toString() {
        String output = "";
        output += "Stock Price: " + this.trackedPrices.getLast();
        return output;
    }

    @Override
    public void updateMarketState(int shares, double price) {
        trackedPrices.add(price);
        trackedShares.add(shares);
        currentPrice = price;
        if(currentPrice != trackedPrices.getLast()){
            System.out.println("!!!!!!!!!!!!!!!!!!!!! ERROR !!!!!!!!!!!!!!!!!!!!!");
        }
        NotifyBuyers();
    }
}

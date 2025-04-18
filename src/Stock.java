import java.util.ArrayList;
import java.util.List;

/**
 * Stock class will have the data of the stock and also the calculation of the trends
 * used singleton in order to [ADD DESCRIPTION HERE]
 */
public class Stock {
    private static Stock instance = null;
    private List<StockObserver> observers = new ArrayList<>();
    private double currentPrice;
    private ArrayList<Double> trackedPrices;


    /// let's make it a singleton yaaayyyyy
    private Stock(double currentPrice){
        currentPrice = currentPrice;
        trackedPrices = new ArrayList<>();
        trackedPrices.add(currentPrice);

    }
    public static Stock getInstance(double startingPrice){
        if(instance == null){
            instance = new Stock(startingPrice);
        }
        return instance;
    }

    void addObserver(StockObserver observer){
        observers.add(observer);
    }

    double getPriceAt(int time){
        return trackedPrices.get(time);
    }

    double getTrend(int time){
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
    double getCurrentPrice(){
        return currentPrice;
    }

    void ForcedStock(ArrayList<Double> ForcedMarketPrices){
       trackedPrices = ForcedMarketPrices;
    }
}

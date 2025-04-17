import java.util.ArrayList;

public class Stock {
    private double currentPrice;
    private ArrayList<Double> trackedPrices;

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
}

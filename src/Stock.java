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
    int startShares = 0;

    public int getTrackedsize(){
        return trackedPrices.size();
    }


    /// let's make it a singleton yaaayyyyy
    private Stock(double currentPrice,int shares){
        this.currentPrice = currentPrice;
        trackedPrices = new ArrayList<>();
        trackedPrices.add(currentPrice);
        trackedShares = new ArrayList<>();
        trackedShares.add(shares);
        startShares = shares;

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

    double getTrend(int given) {
        int size = trackedPrices.size();
        int start = Math.max(0, size - given); // start index

        if (size == 0) return 1; // fallback

        double sum = 0;
        for (int i = start; i < size; i++) {
            sum += trackedPrices.get(i);
        }

        int count = size - start;
        return (count > 0) ? (sum / count) : trackedPrices.get(size - 1);
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
        output += "Stock Price: " + this.trackedPrices.getLast() + " with Shares: " + this.trackedShares.getLast();
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

    public void printAsciiPriceGraph() {
        if (trackedPrices.isEmpty()) {
            System.out.println("No prices to display.");
            return;
        }

        // Determine the price range
        double max = trackedPrices.stream().max(Double::compareTo).get();
        double min = trackedPrices.stream().min(Double::compareTo).get();

        int height = 10; // Number of rows for the graph
        int width = trackedPrices.size();

        // Map each price to a vertical level
        for (int level = height; level >= 0; level--) {
            double threshold = min + ((max - min) * level / height);
            StringBuilder row = new StringBuilder();
            for (double price : trackedPrices) {
                if (price >= threshold) {
                    row.append(" * ");
                } else {
                    row.append("   ");
                }
            }
            System.out.printf("%.2f |%s%n", threshold, row.toString());
        }

        // X-axis
        System.out.print("      ");
        for (int i = 0; i < width; i++) {
            System.out.print("---");
        }
        System.out.println();
        System.out.print("      ");
        for (int i = 0; i < width; i++) {
            System.out.printf("%2d ", i);
        }
        System.out.println();
    }

    public int getTotalShares() {
        return trackedShares.getLast();
    }
}

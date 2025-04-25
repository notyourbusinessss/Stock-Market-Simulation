import java.util.ArrayList;
import java.util.List;

/**
 * The Stock class models a stock in a simulation, tracking its price and share history.
 * It follows the Singleton pattern to ensure a single consistent stock instance exists during runtime.
 * This class also implements the {@link MarketObserver} interface to react to market updates
 * and acts as an observable for all registered {@link StockObserver} instances (e.g., Buyers).
 */
public class Stock implements MarketObserver {

    /** Singleton instance of the Stock. */
    private static Stock instance = null;

    /** List of observers that are notified when the stock price changes. */
    private final List<StockObserver> observers = new ArrayList<>();

    /** The current price of the stock. */
    private double currentPrice;

    /** History of stock prices over time. */
    private ArrayList<Double> trackedPrices;

    /** History of available shares corresponding to each price update. */
    private ArrayList<Integer> trackedShares;

    /** The number of shares at the beginning of the simulation. */
    int startShares = 0;

    /**
     * Returns the number of price entries stored (used for calculating trends).
     * @return the size of the tracked price history
     */
    public int getTrackedsize() {
        return trackedPrices.size();
    }

    /**
     * Private constructor for the Singleton pattern.
     * Initializes the stock with an initial price and share count.
     *
     * @param currentPrice initial price of the stock
     * @param shares       initial number of available shares
     */
    private Stock(double currentPrice, int shares) {
        this.currentPrice = currentPrice;
        trackedPrices = new ArrayList<>();
        trackedPrices.add(currentPrice);
        trackedShares = new ArrayList<>();
        trackedShares.add(shares);
        startShares = shares;
    }

    /**
     * Returns the singleton instance of the stock, creating it if necessary.
     *
     * @param startingPrice the initial price to use if the instance doesn't exist
     * @param shares        the initial number of shares
     * @return the singleton instance
     */
    public static synchronized Stock getInstance(double startingPrice, int shares) {
        if (instance == null) {
            instance = new Stock(startingPrice, shares);
        }
        return instance;
    }

    /**
     * Registers a new observer that will be notified on stock price updates.
     *
     * @param observer the observer to add
     */
    void addObserver(StockObserver observer) {
        observers.add(observer);
        System.out.println("\t\t Observer added : " + observer.getClass().getName());
    }

    /**
     * Retrieves the stock price at a specific point in time.
     *
     * @param time the index in the trackedPrices list
     * @return the stock price at the given time
     */
    double getPriceAt(int time) {
        return trackedPrices.get(time);
    }

    /**
     * Calculates the average stock price over the last 'given' entries.
     *
     * @param given the number of recent prices to average
     * @return the average price over that window
     */
    double getTrend(int given) {
        int size = trackedPrices.size();
        int start = Math.max(0, size - given);

        if (size == 0) return 1;

        double sum = 0;
        for (int i = start; i < size; i++) {
            sum += trackedPrices.get(i);
        }

        int count = size - start;
        return (count > 0) ? (sum / count) : trackedPrices.get(size - 1);
    }

    /**
     * Calculates the average number of available shares recorded in the history.
     *
     * @return the average of tracked available shares
     */
    int AVGAvalibleShares() {
        int sum = 0;
        for (int i : trackedShares) {
            sum += i;
        }
        return sum / trackedShares.size();
    }

    /**
     * Gets the current market price of the stock.
     *
     * @return the latest stock price
     */
    double getCurrentPrice() {
        return currentPrice;
    }

    /**
     * Forces the stock's price history to a custom list (used for testing or replays).
     *
     * @param ForcedMarketPrices the list of historical prices to inject
     */
    void ForcedStock(ArrayList<Double> ForcedMarketPrices) {
        trackedPrices = ForcedMarketPrices;
    }

    /**
     * Notifies all registered observers (e.g., Buyers) about the latest price update.
     */
    void NotifyBuyers() {
        for (StockObserver observer : observers) {
            observer.getnewpricing(currentPrice);
        }
    }

    /**
     * Provides a summary of the stock's latest price and share count.
     *
     * @return formatted string of current stock state
     */
    @Override
    public String toString() {
        return "Stock Price: " + this.trackedPrices.getLast() + " with Shares: " + this.trackedShares.getLast();
    }

    /**
     * Called when the market updates its state. Stores new price and share count,
     * updates current price, and notifies all observers.
     *
     * @param shares number of shares available
     * @param price  the new market price of the stock
     */
    @Override
    public void updateMarketState(int shares, double price) {
        trackedPrices.add(price);
        trackedShares.add(shares);
        currentPrice = price;

        if (currentPrice != trackedPrices.getLast()) {
            System.out.println("!!!!!!!!!!!!!!!!!!!!! ERROR !!!!!!!!!!!!!!!!!!!!!");
        }

        NotifyBuyers();
    }

    /**
     * Prints a simple ASCII graph of historical stock prices to the console.
     * Useful for debugging or non-GUI testing.
     */
    public void printAsciiPriceGraph() {
        if (trackedPrices.isEmpty()) {
            System.out.println("No prices to display.");
            return;
        }

        double max = trackedPrices.stream().max(Double::compareTo).get();
        double min = trackedPrices.stream().min(Double::compareTo).get();

        int height = 10;
        int width = trackedPrices.size();

        for (int level = height; level >= 0; level--) {
            double threshold = min + ((max - min) * level / height);
            StringBuilder row = new StringBuilder();
            for (double price : trackedPrices) {
                row.append(price >= threshold ? " * " : "   ");
            }
            System.out.printf("%.2f |%s%n", threshold, row.toString());
        }

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

    /**
     * Retrieves the most recently recorded number of available shares.
     *
     * @return last value in the trackedShares list
     */
    public int getTotalShares() {
        return trackedShares.getLast();
    }
}

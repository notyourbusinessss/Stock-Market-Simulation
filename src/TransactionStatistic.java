import Skeleton.Statistic;

/**
 * A specialized statistic class that tracks transaction values and calculates the average value per transaction.
 * Extends the base {@link Statistic} class and adds tracking for total value and transaction count.
 * Thread-safe via synchronization for multi-threaded environments.
 */
public class TransactionStatistic extends Statistic {

    /**
     * The total value of all recorded transactions.
     */
    private double totalValue = 0;

    /**
     * The total number of transactions recorded.
     */
    private int transactionCount = 0;

    /**
     * Constructs a TransactionStatistic with a given name for identification.
     *
     * @param name the name of this statistic (used for display or lookup)
     */
    public TransactionStatistic(String name) {
        super(name);
    }

    /**
     * Records a new transaction by adding its value to the total and incrementing the count.
     * Also stores the value in the base class for generic use.
     *
     * @param value the value of the transaction to record
     */
    public synchronized void recordTransaction(double value) {
        totalValue += value;
        transactionCount++;
        addValue(value);  // Store individual transaction in base class list
    }

    /**
     * Calculates and returns the average value per transaction.
     * If no transactions have occurred, returns 0.
     *
     * @return average transaction value
     */
    @Override
    public float summarize() {
        return (float) (transactionCount == 0 ? 0 : totalValue / transactionCount);
    }

    /**
     * Prints a detailed summary of the transaction statistic to the console.
     */
    @Override
    public void printStatistic() {
        System.out.printf("\t\tAverage Transaction Value: %.2f (Total: %.2f from %d transactions)\n",
                summarize(), totalValue, transactionCount);
    }
}

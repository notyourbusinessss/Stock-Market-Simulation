import Skeleton.Statistic;

public class TransactionStatistic extends Statistic {
    private double totalValue = 0;
    private int transactionCount = 0;

    public TransactionStatistic(String name) {
        super(name);
    }

    public synchronized void recordTransaction(double value) {
        totalValue += value;
        transactionCount++;
        addValue(value);  // Store individual transaction
    }

    @Override
    public float summarize() {
        return (float) (transactionCount == 0 ? 0 : totalValue / transactionCount);
    }

    @Override
    public void printStatistic() {
        System.out.printf("\t\tAverage Transaction Value: %.2f (Total: %.2f from %d transactions)\n",
                summarize(), totalValue, transactionCount);
    }
}

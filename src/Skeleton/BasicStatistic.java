package Skeleton;

public class BasicStatistic extends Statistic {
    public BasicStatistic(String name) {
        super(name);
    }

    @Override
    public float summarize() {
        float sum = 0;
        for (Object val : values) {
            if (val instanceof Number) {
                sum += ((Number) val).floatValue();
            }
        }
        return sum;
    }

    @Override
    public void printStatistic() {
        System.out.printf("\t%s: %.2f%n", getName(), summarize());
    }
}

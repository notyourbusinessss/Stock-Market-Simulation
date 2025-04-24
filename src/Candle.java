/**
 * Represents a single candlestick in a financial chart.
 * A candlestick contains the open, close, high, and low prices
 * for a given time period in the stock market simulation.
 */
public class Candle {

    /**
     * The opening price at the start of the time period.
     */
    double open;

    /**
     * The closing price at the end of the time period.
     */
    double close;

    /**
     * The highest price reached during the time period.
     */
    double high;

    /**
     * The lowest price reached during the time period.
     */
    double low;

    /**
     * Constructs a Candle instance with the specified OHLC values.
     *
     * @param open  the price at the beginning of the time period
     * @param close the price at the end of the time period
     * @param high  the highest price during the time period
     * @param low   the lowest price during the time period
     */
    Candle(double open, double close, double high, double low) {
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
    }
}

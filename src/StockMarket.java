import Skeleton.SimulationInput;
import Skeleton.Unit;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * The stock market is the middle ground of the interactions between the buyers and the Stock, here is where the buyers will buy or sell their stocks.
 */
public class StockMarket extends Unit{
    static int waiting = 10;
    Stock TrackedStock;
    private int avalibleShares;
    private List<MarketObserver> Stocks = new ArrayList<>();
    double MarketPrice;

    public static final Semaphore pauseLock = new Semaphore(1); // starts "unpaused"
    private volatile boolean paused = false;

    public void togglePause() {
        if (paused) {
            pauseLock.release();
        } else {
            try {
                pauseLock.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        paused = !paused;
    }

    public boolean isPaused() {
        return paused;
    }

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
        buyer.Capital += amount*this.MarketPrice;
    }

    synchronized void updateStockPrice() {
        System.out.println("\t\t updating");
        if (avalibleShares == 0) return;

        double avg = TrackedStock.AVGAvalibleShares();
        double delta = avalibleShares - avg;
        double ratio = delta / avg;

        // Cap the max change to ±10%
        double maxChange = 0.1;
        ratio = Math.max(-maxChange, Math.min(maxChange, -ratio)); // Negate: less supply → increase price

        // Apply dampened change (only 5% of the allowed ratio)
        double changeFactor = 0.05; // Adjust this to make it more/less sensitive
        MarketPrice += MarketPrice * ratio * changeFactor;

        // Apply slight decay if no change
        if (Math.abs(delta) < avg * 0.05) {
            double decay = Math.min(0.05, 1.0 / (avg == 0 ? 100 : avg)) * MarketPrice;
            MarketPrice -= decay;
        }

        if (MarketPrice < 0.01) {
            MarketPrice = 0.01;
        }

        System.out.printf("\t\t Updated MarketPrice: %.2f\n", MarketPrice);
    }



    synchronized int getAvalibleShares(){
        return avalibleShares;
    }

    //boolean canBuy(Buyer buyer){
    //    if()
    //}

    public void buy(int amount, Buyer buyer) {
        // Should include something like:
        if (avalibleShares >= amount) {
            avalibleShares -= amount;
            buyer.addholding(amount);
            buyer.Capital -= amount*this.MarketPrice;
            // update price, trend, etc.
        } else {
            // prevent invalid buys
            amount = 0;
        }
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

                System.out.println("setting");
                /// initialize everything
                ArrowPanel panel = new ArrowPanel(this);
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Arrow Panel Demo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(panel);
                frame.setSize(200, 150);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });


        Buyer buyer1 = new Buyer(new SimulationInput(), "George -1-", (int) (this.avalibleShares * 0.1), this, 50, 100);
                this.avalibleShares -= buyer1.holding;
                Buyer buyer2 = new Buyer(new SimulationInput(), "Mark -2-", (int) (this.avalibleShares * 0.1), this, 50, 100);
                this.avalibleShares -= buyer2.holding;
                RandomBuyer buyer3 = new RandomBuyer(new SimulationInput(), "Random -2-", (int) (this.avalibleShares * 0.1), this, 50, 100);
                Thread A = new Thread(buyer1);
                Thread B = new Thread(buyer2);
                Thread C = new Thread(buyer3);
                A.start();
                B.start();
                C.start();
                /*try {
                    A.join();
                    B.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }*/

        while ( true ||StockMarket.isOpen()) {

            try {
                pauseLock.acquire();
                pauseLock.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

                TrackedStock.printAsciiPriceGraph();
                System.out.println("------------------------\n"+TrackedStock.getCurrentPrice() + " : " + TrackedStock.getTrend(10) + ", " + TrackedStock.AVGAvalibleShares() + "\n------------------------\n");

                //if()

                updateStockPrice();
                updateStock();


                SwingUtilities.invokeLater(panel::updateLabel);


                try {
                    Thread.sleep(waiting);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
            System.out.println(this.TrackedStock + " : " + this.Now);

            Now++;

        System.out.println("stop");
        TrackedStock.printAsciiPriceGraph();
        return;
    }

    /**
     * Testing grounds
     * @param args
     */
    public static void main(String[] args) {
       StockMarket stockMarket = new StockMarket(new SimulationInput(),1000,50.00,1000,0);
        Thread A = new Thread(stockMarket);
        A.start();
    }

}

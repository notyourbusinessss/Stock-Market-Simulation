import Skeleton.SimulationInput;
import Skeleton.Unit;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;


public class StockMarket extends Unit {

    //just some fun lines
    String[] positiveEvents = {
            "Company releases groundbreaking new product.",
            "Earnings report exceeds all expectations.",
            "CEO announces major investment in R&D.",
            "Company wins major government contract.",
            "Stock receives strong buy recommendation from top analyst.",
            "Market sentiment surges after positive economic news.",
            "Company announces stock buyback program.",
            "Successful product launch generates strong sales.",
            "Tech innovation draws industry-wide attention.",
            "Positive customer feedback boosts investor confidence.",
            "Company expands into international markets.",
            "Strategic partnership formed with major player.",
            "Merger or acquisition announced with favorable terms.",
            "Competitor suffers setback, benefiting this company.",
            "Dividend payout increased by 20%.",
            "New leadership brings optimistic vision.",
            "Positive report from regulatory agency.",
            "Supply chain improvements reduce operating costs.",
            "Company recognized with industry award.",
            "Institutional investor increases stake.",
            "Unexpected patent approval boosts company valuation.",
            "Government introduces favorable regulation for industry.",
            "Industry-wide demand surge benefits company operations.",
            "Investor day presentation impresses Wall Street.",
            "New subscription model outperforms expectations.",
            "Exclusive media coverage highlights growth potential.",
            "Influencer campaign goes viral, boosts brand.",
            "Strong holiday season sales reported.",
            "Competitor acquisition drives market consolidation optimism.",
            "Debt refinancing secures lower interest rates.",
            "Insider purchases signal executive confidence.",
            "AI integration improves company efficiency.",
            "Expansion into emerging markets shows early success.",
            "Renewable energy transition earns ESG praise.",
            "Successful IPO of subsidiary company.",
            "Product gets featured in top consumer rankings.",
            "Cloud migration slashes IT overhead.",
            "Long-term contract secured with government agency.",
            "Shareholders vote overwhelmingly in favor of new direction.",
            "R&D breakthrough opens up new revenue stream.",
            "Stock added to major market index (e.g., S&P 500).",
            "Company signs multi-year deal with global retailer.",
            "AI-driven analytics boost forecast accuracy.",
            "Consumer loyalty program sees massive adoption.",
            "Record-breaking preorders reported for new product.",
            "Environmental initiative praised by watchdogs.",
            "Breakthrough in renewable tech positions company as leader.",
            "Rebound in sector lifts entire industry.",
            "Stock hits all-time high after bullish forecast.",
            "Strong institutional buying reported.",
            "Company announces zero-debt milestone.",
            "Top-rated app in app store rankings.",
            "Record number of new customer acquisitions.",
            "Positive earnings surprise beats all estimates.",
            "Free cash flow exceeds previous projections.",
            "Strong forward guidance issued by executives.",
            "Analysts revise target price upward.",
            "CEO featured in major business publication.",
            "Product receives major certification or approval.",
            "Major competitor exits market, increasing share potential.",
            "Unexpected shoutout from Elon Musk sends stock soaring.",
            "Company meme goes viral, boosts brand awareness.",
            "Stock trending on Reddit's r/wallstreetbets.",
            "Rumors swirl of a tech giant acquisition.",
            "Unexpected endorsement from celebrity investor.",
            "Company AI bot beats human competition in contest.",
            "CEO goes on popular podcast, shares bold vision.",
            "Government offers massive subsidy for green initiative.",
            "Stock hits circuit breaker due to rapid gains.",
            "Company product featured in blockbuster movie.",
            "Cryptocurrency integration excites investors.",
            "Viral TikTok boosts product sales overnight.",
            "Insiders increase holdings — markets react positively.",
            "Company launches NFT line, sells out in seconds.",
            "First in industry to adopt quantum encryption.",
            "New space partnership with national space agency.",
            "Environmental impact declared 'net positive' by watchdog.",
            "CEO surprises market by slashing their own salary.",
            "Revenue milestone reached 6 months early.",
            "Influencer giveaway campaign breaks social media records."



    };


    String[] negativeEvents = {
            "Unexpected quarterly loss reported.",
            "CEO involved in corporate scandal.",
            "Product recall causes investor panic.",
            "Major lawsuit filed against the company.",
            "Market rattled by economic uncertainty.",
            "Competitor launches superior alternative product.",
            "Leadership shakeup raises concerns.",
            "Regulatory investigation announced.",
            "High-profile customer ends contract.",
            "Company misses key production deadline.",
            "Supply chain disruption affects delivery.",
            "Negative press surrounding company practices.",
            "Social media backlash trends globally.",
            "Downgrade from major credit agency.",
            "Investor confidence shaken by accounting irregularities.",
            "Failed product launch leads to poor reviews.",
            "Security breach affects customer data.",
            "Labor strike halts operations.",
            "Analyst changes rating to 'Sell'.",
            "Key executive unexpectedly resigns.",
            "Patent dispute jeopardizes key product.",
            "Unexpected tax ruling increases liabilities.",
            "Major client announces shift to competitor.",
            "Cost overruns delay critical project.",
            "Company fined for environmental violations.",
            "Hacktivist group targets company website.",
            "New legislation threatens current business model.",
            "Internal email leak reveals dysfunction.",
            "Public boycott called over ethical concerns.",
            "Whistleblower reveals potential fraud.",
            "Earnings guidance revised downward.",
            "High turnover rates spark management worries.",
            "Employee satisfaction ratings plummet.",
            "Negative analyst note triggers sell-off.",
            "Class-action lawsuit initiated by consumers.",
            "Boardroom conflict surfaces in media.",
            "Product flunks independent safety test.",
            "Company delisted from sustainability index.",
            "Quarterly earnings fall short of projections.",
            "Investigative report links company to unethical supplier.",
            "Stock dropped from major index.",
            "Sudden downgrade from 'Buy' to 'Hold'.",
            "Shareholder lawsuit targets board decisions.",
            "Interest rate hikes hit financing plans.",
            "Important vendor files for bankruptcy.",
            "New tariffs impact supply chain costs.",
            "Failed merger results in investor backlash.",
            "Audit firm resigns unexpectedly.",
            "Quarterly revenue falls below street estimates.",
            "Reputation hit by poor working condition reports.",
            "Disappointing product review goes viral.",
            "Early investor pulls out of funding round.",
            "Cost-cutting leads to mass layoffs.",
            "Flagship store closes in key market.",
            "Data loss incident raises compliance concerns.",
            "Key partner ends long-term agreement.",
            "Company misses debt repayment deadline.",
            "Product pulled from shelves by retailer.",
            "Bankruptcy rumors surface in financial circles.",
            "Stock price plummets after insider sells large stake.",
            "CEO tweets something questionable — again.",
            "Meme stock status fades, investors flee.",
            "Internal AI mistakenly leaks confidential data.",
            "Livestreamed investor call goes off the rails.",
            "Product explodes during live demo.",
            "CEO caught playing mobile games during earnings call.",
            "Market confused by cryptic company tweet.",
            "Unintended 'reply all' email goes public.",
            "Investor sues over misleading horoscope-style guidance.",
            "Annual report accidentally includes lorem ipsum.",
            "Accounting spreadsheet leaked — shows '?? profit'.",
            "Zoom filter mishap during board meeting leaks online.",
            "Drone delivery fails spectacularly on live TV.",
            "Company's VR launch causes mass nausea.",
            "Marketing AI responds rudely to customers.",
            "Board member quits via Instagram story.",
            "Auto-reply email confirms insider trading.",
            "Stock temporarily delisted due to typo.",
            "CEO caught using ChatGPT to write mission statement.",
    };





    private double lastEventBias = -1.0; // [-1.0 (strongly negative) to +1.0 (strongly positive)]
    private double marketBias = 2; // Range: [-1.0, 1.0]
    private int nextMajorEventTick = 0;
    private final Random rand = new Random();

    private final List<Buyer> buyers = new ArrayList<>();

    int AmountSold = 0;
    int AmountBought = 0;

    static int waiting = 10;
    Stock TrackedStock;
    private int avalibleShares;
    private List<MarketObserver> Stocks = new ArrayList<>();
    double MarketPrice;

    public static final Semaphore pauseLock = new Semaphore(1);
    private volatile boolean paused = false;

    private boolean wasBuy = false;
    private boolean wasSell = false;
    private final Random rng = new Random();

    int Time;
    static int Now;
    static boolean open = true;
    static String lastNews;
    private int initalShares;

    public StockMarket(SimulationInput input) {
        super("Market",input);
    }

    public StockMarket(SimulationInput input, int totalShares, double InitialPrice, int time, int now) {
        super("Market",input);
        this.avalibleShares = totalShares;
        this.MarketPrice = InitialPrice;
        this.Time = time;
        this.Now = now;
        TrackedStock = Stock.getInstance(this.MarketPrice, this.avalibleShares);
        Stocks.add(TrackedStock);
        nextMajorEventTick = rand.nextInt(1001) + 500; // [500, 1500]
        lastNews =String.format("Company makes Debut on stock market with a Starting price of %.2f$",MarketPrice);
        initalShares = totalShares;
    }

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

    double getMarketTrend(int GoBack) {
        return TrackedStock.getTrend(GoBack);
    }

    double getCurrentPrice() {
        return MarketPrice;
    }

    void ForcedMarketPrice(ArrayList<Double> ForcedMarketPrices) {
        TrackedStock.ForcedStock(ForcedMarketPrices);
    }

    public void buy(int amount, Buyer buyer) {
        if (avalibleShares >= amount && amount > 0) {
            avalibleShares -= amount;
            buyer.addholding(amount);
            buyer.Capital -= amount * this.MarketPrice;
            wasBuy = true;
            AmountBought += amount;
        }
    }


    public void sell(int amount, Buyer buyer) {
        if (amount > 0) {
            buyer.removeholding(amount);
            avalibleShares += amount;
            buyer.Capital += amount * this.MarketPrice;
            wasSell = true;
            AmountSold += amount;
        }
    }

    synchronized void updateStockPrice() {
        // This method is synchronized to prevent concurrent access issues since it's likely called by multiple threads.
        //System.out.println("\t\t[Updating Stock Price]");

        // Calculate the average number of available shares historically or across the system
        double avg = TrackedStock.AVGAvalibleShares();
        // Calculate the deviation from the average to assess supply imbalance
        double delta = avg - avalibleShares;
        double ratio = delta / avg; // Ratio used to determine pressure for price change

        // Constants to control price volatility
        double maxRatio = 0.1;             // Cap for how much influence the ratio can exert
        double baseChangeFactor = 0.03;    // Scaling factor for how much prices change
        double priceChange = 0;

        // Retrieve total number of issued shares (assumed fixed over the simulation)
        int totalShares = TrackedStock.getTotalShares(); // Ensure this method exists in Stock class

        // === If there was buying activity and no selling ===
        if (wasBuy && !wasSell) {
            // Volume factor increases impact if more stock was bought relative to market size
            double volumeFactor = Math.min(1.0, (double) AmountBought / (totalShares * 0.05));
            double directionalMultiplier = 1 * volumeFactor;  // Positive influence
            double cappedRatio = Math.max(-maxRatio, Math.min(maxRatio, ratio));  // Clamp the influence
            priceChange = cappedRatio * baseChangeFactor * directionalMultiplier;

            // === If there was only selling activity ===
        } else if (!wasBuy && wasSell) {
            // Stronger cap for selling since price drops are usually sharper
            double volumeFactor = Math.min(3.0, (double) AmountSold / (totalShares * 0.05));
            double directionalMultiplier = 1 * volumeFactor;  // Negative influence
            double cappedRatio = Math.max(-maxRatio, Math.min(maxRatio, ratio));  // Clamp the ratio
            priceChange = cappedRatio * baseChangeFactor * directionalMultiplier;

            // === If there was no activity (idle) ===
        } else if (!wasBuy && !wasSell) {
            // Introduce small random noise to simulate market drift
            double noise = (rng.nextDouble() * 0.01) - 0.005; // Random float in [-0.005, 0.005]
            //System.out.printf("\t\t[Random Drift] %.4f\n", noise);
            priceChange = noise;
        }

        // Market sentiment bias
        if (!wasBuy && !wasSell) {
            priceChange += 0.003 * (marketBias + 0.1);
        } else {
            priceChange += 0.01 * marketBias;
        }


        // === Apply price resistance based on current price ===
        // Simulates diminishing return on price movement as price gets high
        double resistance = Math.max(0.1, 1.0 - (MarketPrice / 500.0));
        priceChange *= resistance;

        // === Further dampen price increase based on price tier ===
        // As price increases exponentially, limit how fast it can grow
        if (priceChange > 0) {
            priceChange *= Math.pow(0.97, MarketPrice / 10.0);  // Exponential dampening
        }

        // === Limit the max change per tick ===
        // Ensures price doesn't jump too much in one simulation tick
        double maxTickChange = 0.02; // ±2% max change
        priceChange = Math.max(-maxTickChange, Math.min(maxTickChange, priceChange));

        // === Apply the computed price change ===
        MarketPrice += MarketPrice * priceChange;

        // === Simulate natural decay if no one trades and price is too high ===
        if (!wasBuy && !wasSell && MarketPrice > 20) {
            double decay = MarketPrice * 0.002; // 0.2% decay
            MarketPrice -= decay;
            //System.out.printf("\t\t[Idle Decay] -%.4f\n", decay);
        }

        // === Clamp MarketPrice to avoid nonsensical values ===
        if (MarketPrice < 0.01) {
            MarketPrice = 0.01;  // Set floor value
        } else if (MarketPrice > 1000.00) {
            MarketPrice = 500.00; // Set upper cap to prevent runaway prices
        }

        // === Logging the final values ===
        //System.out.printf("\t\t Final Price Change: %.4f\n", priceChange);
        //System.out.printf("\t\t Updated Market Price: %.2f\n", MarketPrice);

        // === Reset state flags for next tick ===
        wasBuy = false;
        wasSell = false;
        AmountSold = 0;
        AmountBought = 0;


        if (!wasBuy && !wasSell) {
            marketBias *= 0.98;
        } else {
            marketBias *= 0.99;
        }
    }




    synchronized int getAvalibleShares() {
        return avalibleShares;
    }

    void updateStock() {
        for (MarketObserver observer : Stocks) {
            observer.updateMarketState(avalibleShares, MarketPrice);
        }
    }

    static boolean isOpen() {
        return open;
    }

    void MajorEvent() {
        Random rand = new Random();

        // Base chance from -1.0 to 1.0, then subtract bias to skew opposite of last event
        double base = (rand.nextDouble() * 2.0) - 1.0; // [-1.0, 1.0]
        double eventImpact = base - (0.5 * lastEventBias); // skew slightly away from last outcome

        // Clamp to [-1, 1] to stay in valid range
        eventImpact = Math.max(-1.0, Math.min(1.0, eventImpact));

        // Scale severity
        double severity = 0.05 + rand.nextDouble() * 0.15; // [0.05, 0.20]
        // Clamp negative severity
        if (eventImpact < 0) {
            severity *= 0.6; // Reduce crash impact
        }
        double priceChange = MarketPrice * severity * eventImpact;

        // Apply change
        MarketPrice += priceChange;

        // Clamp to minimum
        if (MarketPrice < 0.01) {
            MarketPrice = 0.01;
        }

        // Update bias for next event
        lastEventBias = eventImpact;

        if (eventImpact > 0) {
            String message = positiveEvents[rand.nextInt(positiveEvents.length)];
            lastNews = message;
            System.out.printf("Positive Market Event: %s\n", message);
            lastNews += String.format(" Stock increased by %.2f%%",severity*100);
            marketBias += severity*100;
            System.out.printf("Stock increased by %.2f%% → New price: %.2f\n Market biase : %f\n", severity * 100, MarketPrice,marketBias);
        } else {
            String message = negativeEvents[rand.nextInt(negativeEvents.length)];
            lastNews = message;
            System.out.printf("Negative Market Event: %s\n", message);
            lastNews += String.format(" Stock decreased by %.2f%%",severity*100);
            marketBias -= severity*100;
            System.out.printf("Stock decreased by %.2f%% → New price: %.2f\n Market biase : %f\n", severity * 100, MarketPrice,marketBias);
        }


        // Clamp bias
        marketBias = Math.max(-1.0, Math.min(1.0, marketBias));

    }

    public void addBuyer(Buyer buyer) {
        buyers.add(buyer);
    }

    @Override
    public void performAction() {}

    @Override
    public void submitStatistics() {}



    @Override
    public void run() {
        Buyer buyer1 = new Buyer(this.getSimInput(), "George -1-", (int) (this.avalibleShares * 0.1), this, 100, 80);
        this.avalibleShares -= buyer1.holding;
        buyer1.speak = false;
        Buyer buyer2 = new Buyer(this.getSimInput(), "Mark -2-", (int) (this.avalibleShares * 0.1), this, 70, 50);
        this.avalibleShares -= buyer2.holding;
        buyer2.speak = false;
        Buyer buyer3 = new Buyer(this.getSimInput(), "Adam -3-", (int) (this.avalibleShares * 0.1), this, 0, 80);// because of extremly high activity he will buy and sell A LOT, bassically day trader
        this.avalibleShares -= buyer3.holding;
        buyer3.speak = false;
        Buyer buyer4 = new Buyer(this.getSimInput(), "Eve -4-", (int) (this.avalibleShares * 0.1), this, 0, 0);
        this.avalibleShares -= buyer4.holding;
        buyer4.speak = false;
        Buyer buyer5 = new Buyer(this.getSimInput(), "Normal Dude -5-",  (10), this, 0, 60);
        buyer5.MakeNormal();
        this.avalibleShares -= buyer5.holding;
        buyer5.speak = false;
        //RandomBuyer buyer3 = new RandomBuyer(new SimulationInput(), "General Market -1-", (int) (this.avalibleShares * 0.1), this, 50, 100);
        //RandomBuyer buyer4 = new RandomBuyer(new SimulationInput(), "General Market -2-", (int) (this.avalibleShares * 0.1), this, 50, 100);
        Thread A = new Thread(buyer1);
        Thread B = new Thread(buyer2);
        Thread C = new Thread(buyer3);
        Thread D = new Thread(buyer4);
        Thread E = new Thread(buyer5);
        A.start();
        B.start();
        C.start();
        D.start();
        E.start();




        ArrowPanel arrowPanel = new ArrowPanel(this);


        SwingUtilities.invokeLater(() -> {

            CustomWindowPanel marketWindow = new CustomWindowPanel(arrowPanel,true,"Stock Market");
            marketWindow.showWindow(); // This shows your main stock window

        });




        while (StockMarket.isOpen()) {
            try {
                pauseLock.acquire();
                pauseLock.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //TrackedStock.printAsciiPriceGraph();
            //System.out.println("------------------------\n" + TrackedStock.getCurrentPrice() + " : " + TrackedStock.getTrend(10) + ", " + TrackedStock.AVGAvalibleShares() + "\n------------------------\n");

            updateStockPrice();
            updateStock();

            SwingUtilities.invokeLater(arrowPanel::updateLabel);
            //SwingUtilities.invokeLater(simPanel::updateLabels);

            try {
                Thread.sleep(waiting);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (Now == nextMajorEventTick) {
                MajorEvent();
                nextMajorEventTick = Now + rand.nextInt(1001) + 500; // schedule next event
            }
            Now++;
        }
        System.out.println("Stock Market is now closed");

        // Stop buyers
        for (Buyer buyer : buyers) {
            buyer.submitStatistics();
            buyer.Stop();
        }

        // Wait for threads to finish
        try {
            A.join();
            B.join();
            C.join();
            D.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        SwingUtilities.invokeLater(() -> {
            for (Window w : Window.getWindows()) {
                if (w instanceof JFrame) {
                    w.dispose();
                }
            }
        });

        System.out.println("Stock Market is now closed");
        return;
    }
    static public void CloseMarket(){
        open = false;
    }

    public List<Buyer> getBuyers() {
        return buyers;
    }


    public double getTotalShares() {
        return initalShares;
    }
    public static void main(String[] args) {

    }
}

import javax.swing.*;

/**
 * SimulatedTradeWindow is a standalone window that embeds a {@link SimulatedTradePanel}
 * inside a custom undecorated {@link CustomWindowPanel}. It allows users to simulate trades
 * independently from the core stock market logic.
 *
 * This window is meant for testing and demonstration purposes only.
 */
public class SimulatedTradeWindow {

    /**
     * The JFrame that holds the simulated trading panel and manages its display.
     */
    private final JFrame frame;

    /**
     * Constructs and displays a new simulated trading window.
     * Initializes the trading UI, wraps it in a custom-styled window panel, and shows it.
     *
     * @param stockMarket the stock market instance used for fetching live prices
     */
    public SimulatedTradeWindow(StockMarket stockMarket) {
        // Create the trading panel for user interaction
        SimulatedTradePanel tradePanel = new SimulatedTradePanel(stockMarket);

        // Wrap the trade panel inside a custom UI window container
        CustomWindowPanel windowPanel = new CustomWindowPanel(tradePanel, false, "Simulated Trading Window");

        // Create the actual JFrame window
        frame = new JFrame("Simulated Trading Window");
        frame.setContentPane(windowPanel);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null); // center on screen

        // Ensure closing this window does not terminate the main application
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Optional: log to console when the window is closed
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.out.println("[Simulated Trading Window] Closed.");
            }
        });

        // Show the window
        frame.setVisible(true);
    }
}

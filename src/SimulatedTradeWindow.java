import javax.swing.*;

public class SimulatedTradeWindow {

    private final JFrame frame;

    public SimulatedTradeWindow(StockMarket stockMarket) {
        SimulatedTradePanel tradePanel = new SimulatedTradePanel(stockMarket);

        CustomWindowPanel windowPanel = new CustomWindowPanel(tradePanel,false);

        frame = new JFrame("Simulated Trading Window");
        frame.setContentPane(windowPanel);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);

        // Make sure closing only disposes this one window
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // (Optional) Add a window listener for logging/debugging
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.out.println("[Simulated Trading Window] Closed.");
            }
        });

        frame.setVisible(true);
    }
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ArrowPanel extends JPanel {

    private JButton upButton;
    private JButton downButton;
    private JLabel valueLabel;
    private double currentValue;
    private StockMarket stock;

    @Override
    public void repaint(Rectangle r) {
        this.updateLabel();
        super.repaint(r);

    }

    public ArrowPanel(StockMarket stockMarket) {
        this.stock = stockMarket;

        setLayout(new BorderLayout(10, 10));

        valueLabel = new JLabel(String.format("%.2f", stock.MarketPrice), SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));

        upButton = new JButton("↑");
        downButton = new JButton("↓");

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        buttonPanel.add(upButton);
        buttonPanel.add(downButton);

        add(valueLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);

        upButton.addActionListener(e -> {
            stock.MarketPrice++;
            updateLabel();
        });

        downButton.addActionListener(e -> {
            stock.MarketPrice--;
            updateLabel();
        });

        // 🔁 Auto-refresh display every 100ms
        new Timer(100, e -> updateLabel()).start();
    }


    public void updateLabel() {
        valueLabel.setText(String.format("%.2f", stock.MarketPrice));
    }
    public void updateLabel(StockMarket stockMarket) {
        stock.MarketPrice = stockMarket.MarketPrice;
        valueLabel.setText(String.format("%.2f", stock.MarketPrice));
    }


    // For demonstration
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Arrow Panel Demo");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().add(new ArrowPanel());
//        frame.setSize(200, 150);
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//    }
}

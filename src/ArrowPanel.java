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
        currentValue = stockMarket.MarketPrice;

        // Layout for the buttons and label
        setLayout(new BorderLayout(10, 10));

        // Initialize label
        valueLabel = new JLabel(String.valueOf(currentValue), SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Initialize buttons
        upButton = new JButton("↑");
        downButton = new JButton("↓");

        // Panel to hold buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        buttonPanel.add(upButton);
        buttonPanel.add(downButton);

        // Add components
        add(valueLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);

        // Button listeners
        upButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentValue++;
                updateLabel();
            }
        });

        downButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentValue--;
                updateLabel();
            }
        });
    }

    public void updateLabel() {
        valueLabel.setText(String.valueOf(currentValue));
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

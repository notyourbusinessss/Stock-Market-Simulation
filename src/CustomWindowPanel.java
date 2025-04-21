import javax.swing.*;
import java.awt.*;

public class CustomWindowPanel extends JPanel {

    private final JFrame frame;
    private boolean isFullscreen = false;
    private Rectangle windowedBounds; // Store window size before going fullscreen

    public CustomWindowPanel(JPanel innerContent) {
        super(new BorderLayout());

        // === Title Bar ===
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Color.BLACK);
        titleBar.setPreferredSize(new Dimension(800, 30));

        JLabel title = new JLabel("  Stock Market Simulator");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 12));

        JButton close = new JButton("✕");
        close.setForeground(Color.WHITE);
        close.setBackground(Color.BLACK);
        close.setBorder(null);
        close.setFocusPainted(false);

        JButton fullscreenToggle = new JButton("⛶"); // Unicode full screen symbol
        fullscreenToggle.setForeground(Color.WHITE);
        fullscreenToggle.setBackground(Color.BLACK);
        fullscreenToggle.setBorder(null);
        fullscreenToggle.setFocusPainted(false);

        JButton minimize = new JButton("—");
        minimize.setForeground(Color.WHITE);
        minimize.setBackground(Color.BLACK);
        minimize.setBorder(null);
        minimize.setFocusPainted(false);


        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
        buttons.setOpaque(false);
        buttons.add(minimize);
        buttons.add(fullscreenToggle);
        buttons.add(close);

        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(buttons, BorderLayout.EAST);

        // === Drag Support ===
        final Point[] clickPoint = {null};
        titleBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                clickPoint[0] = e.getPoint();
            }
        });
        titleBar.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (frame != null && !isFullscreen) {
                    Point p = frame.getLocation();
                    frame.setLocation(p.x + e.getX() - clickPoint[0].x, p.y + e.getY() - clickPoint[0].y);
                }
            }
        });

        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        this.setBackground(Color.BLACK);
        this.add(titleBar, BorderLayout.NORTH);
        this.add(innerContent, BorderLayout.CENTER);

        // === Frame Setup ===
        this.frame = new JFrame();
        frame.setUndecorated(true);
        frame.setContentPane(this);
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        // === Resize Support ===
        ResizeListener resizeListener = new ResizeListener(frame);
        frame.addMouseListener(resizeListener);
        frame.addMouseMotionListener(resizeListener);

        // === Action Listeners ===
        close.addActionListener(e -> System.exit(0));
        minimize.addActionListener(e -> frame.setState(Frame.ICONIFIED));
        fullscreenToggle.addActionListener(e -> toggleFullscreen());
    }

    public void showWindow() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    private void toggleFullscreen() {
        GraphicsConfiguration config = frame.getGraphicsConfiguration();
        GraphicsDevice device = config.getDevice();

        if (!isFullscreen) {
            windowedBounds = frame.getBounds();
            frame.dispose();
            frame.setUndecorated(true);
            frame.setVisible(true);
            device.setFullScreenWindow(frame);
            isFullscreen = true;
        } else {
            device.setFullScreenWindow(null);
            frame.dispose();
            frame.setUndecorated(true);
            frame.setBounds(windowedBounds);
            frame.setVisible(true);
            isFullscreen = false;
        }
    }

}

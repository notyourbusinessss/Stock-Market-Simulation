import javax.swing.*;
import java.awt.*;

public class CustomWindowPanel extends JPanel {
    private final boolean exitOnClose;
    private final JFrame frame;
    private boolean isFullscreen = false;
    private Rectangle windowedBounds;

    public CustomWindowPanel(JPanel innerContent, boolean exitOnClose, String titleGiven) {
        super(new BorderLayout());
        this.exitOnClose = exitOnClose;

        // === Title Bar ===
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Color.BLACK);
        titleBar.setPreferredSize(new Dimension(800, 30));

        JLabel title = new JLabel(titleGiven);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 12));

        JButton close = new JButton("✕");
        JButton fullscreenToggle = new JButton("⛶");
        JButton minimize = new JButton("—");

        for (JButton button : new JButton[]{close, fullscreenToggle, minimize}) {
            button.setForeground(Color.WHITE);
            button.setBackground(Color.BLACK);
            button.setBorder(null);
            button.setFocusPainted(false);
        }

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
        close.addActionListener(e -> {
            if (exitOnClose) System.exit(0);
            else frame.dispose();
        });
        minimize.addActionListener(e -> frame.setState(Frame.ICONIFIED));
        fullscreenToggle.addActionListener(e -> toggleFullscreen());
    }

    public void showWindow() {
        if (!frame.isVisible()) {
            SwingUtilities.invokeLater(() -> frame.setVisible(true));
        }
    }
    public void hideWindow() {
        frame.setVisible(false);
    }

    private void toggleFullscreen() {
        GraphicsDevice device = getCurrentGraphicsDevice();

        if (!isFullscreen) {
            windowedBounds = frame.getBounds();
            Rectangle bounds = device.getDefaultConfiguration().getBounds();

            frame.dispose();
            frame.setUndecorated(true);
            frame.setBounds(bounds); // simulate fullscreen
            frame.setVisible(true);
            isFullscreen = true;
        } else {
            frame.dispose();
            frame.setUndecorated(true);
            frame.setBounds(windowedBounds);
            frame.setVisible(true);
            isFullscreen = false;
        }
    }

    private GraphicsDevice getCurrentGraphicsDevice() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = ge.getScreenDevices();

        for (GraphicsDevice device : devices) {
            GraphicsConfiguration config = device.getDefaultConfiguration();
            Rectangle bounds = config.getBounds();
            try {
                Point location = frame.getLocationOnScreen();
                if (bounds.contains(location)) {
                    return device;
                }
            } catch (IllegalComponentStateException ignored) {
            }
        }

        // Fallback
        return ge.getDefaultScreenDevice();
    }

    public boolean isWindowVisible() {
        return frame.isVisible();
    }
    public void setWindowSize(int width, int height) {
        frame.setSize(width, height);
    }

}

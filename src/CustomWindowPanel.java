import javax.swing.*;
import java.awt.*;

/**
 * CustomWindowPanel is a Swing component that provides a borderless window with
 * a custom title bar and support for minimize, fullscreen toggle, and close buttons.
 *
 * It can wrap any other JPanel (`innerContent`) and display it in a stylized JFrame.
 */
public class CustomWindowPanel extends JPanel {

    /**
     * Determines whether the application should exit the simulation or close the frame when the close button is clicked.
     */
    private final boolean exitOnClose;

    /**
     * The underlying JFrame that contains this panel and manages the window display.
     */
    private final JFrame frame;

    /**
     * Indicates whether the window is currently in fullscreen mode.
     */
    private boolean isFullscreen = false;

    /**
     * Stores the window's bounds before switching to fullscreen mode,
     * so it can be restored when exiting fullscreen.
     */
    private Rectangle windowedBounds;


    /**
     * Constructs a CustomWindowPanel with the given content and window behavior options.
     *
     * @param innerContent the JPanel to be embedded in the window
     * @param exitOnClose  whether closing the window should exit the market/simulation
     * @param titleGiven   the title text shown on the title bar
     */
    public CustomWindowPanel(JPanel innerContent, boolean exitOnClose, String titleGiven) {
        super(new BorderLayout());
        this.exitOnClose = exitOnClose;

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

        this.frame = new JFrame();
        frame.setUndecorated(true);
        frame.setContentPane(this);
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

        ResizeListener resizeListener = new ResizeListener(frame);
        frame.addMouseListener(resizeListener);
        frame.addMouseMotionListener(resizeListener);

        close.addActionListener(e -> {
            if (exitOnClose) {
                frame.dispose();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                StockMarket.CloseMarket();
            } else frame.dispose();
        });

        minimize.addActionListener(e -> frame.setState(Frame.ICONIFIED));
        fullscreenToggle.addActionListener(e -> toggleFullscreen());
    }

    /**
     * Displays the window if it is not already visible.
     */
    public void showWindow() {
        if (!frame.isVisible()) {
            SwingUtilities.invokeLater(() -> frame.setVisible(true));
        }
    }

    /**
     * Hides the window from view.
     */
    public void hideWindow() {
        frame.setVisible(false);
    }

    /**
     * Toggles between fullscreen and windowed mode for the frame.
     */
    private void toggleFullscreen() {
        GraphicsDevice device = getCurrentGraphicsDevice();

        if (!isFullscreen) {
            windowedBounds = frame.getBounds();
            Rectangle bounds = device.getDefaultConfiguration().getBounds();

            frame.dispose();
            frame.setUndecorated(true);
            frame.setBounds(bounds);
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

    /**
     * Returns the current graphics device (monitor) on which the frame is displayed.
     */
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

        return ge.getDefaultScreenDevice();
    }

    /**
     * Checks if the window is currently visible.
     *
     * @return true if the frame is visible, false otherwise
     */
    public boolean isWindowVisible() {
        return frame.isVisible();
    }

    /**
     * Sets the window size to a custom width and height.
     *
     * @param width  the new width of the frame
     * @param height the new height of the frame
     */
    public void setWindowSize(int width, int height) {
        frame.setSize(width, height);
    }
}

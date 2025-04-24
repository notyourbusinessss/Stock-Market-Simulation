import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ResizeListener provides manual resizing capability for an undecorated {@link JFrame}.
 * It allows resizing by dragging any edge of the frame, mimicking native window resizing.
 * This class is useful for custom window panels without the default OS window chrome.
 */
class ResizeListener extends MouseAdapter {

    private final JFrame frame;
    private static final int BORDER_DRAG_THICKNESS = 6;
    private Point clickPoint = null;
    private int dragSide = -1;

    /**
     * Constructs a ResizeListener for a given frame.
     *
     * @param frame the undecorated frame to attach resizing behavior to
     */
    ResizeListener(JFrame frame) {
        this.frame = frame;
    }

    /**
     * Records the mouse position and determines the edge being dragged.
     *
     * @param e the mouse press event
     */
    @Override
    public void mousePressed(MouseEvent e) {
        clickPoint = e.getPoint();
        dragSide = getResizeSide(e.getPoint());
    }

    /**
     * Resizes the frame based on the mouse drag movement and the edge that was grabbed.
     *
     * @param e the mouse drag event
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragSide == -1) return;

        Rectangle bounds = frame.getBounds();
        Point dragPoint = e.getLocationOnScreen();

        switch (dragSide) {
            case 0: // Left edge
                int dx = dragPoint.x - bounds.x;
                bounds.x += dx;
                bounds.width -= dx;
                break;
            case 1: // Right edge
                bounds.width = dragPoint.x - bounds.x;
                break;
            case 2: // Top edge
                int dy = dragPoint.y - bounds.y;
                bounds.y += dy;
                bounds.height -= dy;
                break;
            case 3: // Bottom edge
                bounds.height = dragPoint.y - bounds.y;
                break;
        }

        frame.setBounds(bounds);
    }

    /**
     * Determines which side of the window (if any) is being interacted with based on proximity.
     *
     * @param p the point of the mouse press relative to the frame
     * @return an integer indicating which edge is active: 0=left, 1=right, 2=top, 3=bottom, -1=none
     */
    private int getResizeSide(Point p) {
        if (p.x < BORDER_DRAG_THICKNESS) return 0; // Left
        if (p.x > frame.getWidth() - BORDER_DRAG_THICKNESS) return 1; // Right
        if (p.y < BORDER_DRAG_THICKNESS) return 2; // Top
        if (p.y > frame.getHeight() - BORDER_DRAG_THICKNESS) return 3; // Bottom
        return -1;
    }
}

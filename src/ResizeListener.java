import Skeleton.SimulationInput;
import Skeleton.Unit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

// Resize handler for undecorated window
class ResizeListener extends MouseAdapter {
    private final JFrame frame;
    private static final int BORDER_DRAG_THICKNESS = 6;
    private Point clickPoint = null;
    private int dragSide = -1;

    ResizeListener(JFrame frame) {
        this.frame = frame;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        clickPoint = e.getPoint();
        dragSide = getResizeSide(e.getPoint());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragSide == -1) return;

        Rectangle bounds = frame.getBounds();
        Point dragPoint = e.getLocationOnScreen();

        switch (dragSide) {
            case 0: // Left
                int dx = dragPoint.x - bounds.x;
                bounds.x += dx;
                bounds.width -= dx;
                break;
            case 1: // Right
                bounds.width = dragPoint.x - bounds.x;
                break;
            case 2: // Top
                int dy = dragPoint.y - bounds.y;
                bounds.y += dy;
                bounds.height -= dy;
                break;
            case 3: // Bottom
                bounds.height = dragPoint.y - bounds.y;
                break;
        }
        frame.setBounds(bounds);
    }

    private int getResizeSide(Point p) {
        if (p.x < BORDER_DRAG_THICKNESS) return 0; // Left
        if (p.x > frame.getWidth() - BORDER_DRAG_THICKNESS) return 1; // Right
        if (p.y < BORDER_DRAG_THICKNESS) return 2; // Top
        if (p.y > frame.getHeight() - BORDER_DRAG_THICKNESS) return 3; // Bottom
        return -1;
    }
}

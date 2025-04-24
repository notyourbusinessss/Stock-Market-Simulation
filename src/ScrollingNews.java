/**
 * Represents a single scrolling news message in the stock market UI.
 * Each news item contains a message and a horizontal position (`x`) to control its animation.
 */
public class ScrollingNews {

    /**
     * The text content of the news message.
     */
    String message;

    /**
     * The current x-position of the message on the screen. Used to animate scrolling.
     */
    int x;

    /**
     * Constructs a ScrollingNews instance with a message and a starting horizontal position.
     *
     * @param message the news message to display
     * @param startX  the initial x-coordinate for rendering the message
     */
    ScrollingNews(String message, int startX) {
        this.message = message;
        this.x = startX;
    }
}

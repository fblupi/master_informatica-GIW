package news;

/**
 * New with title and text
 */
public class New {
    /**
     * Title of the new
     */
    private String title;

    /**
     * Text of the new
     */
    private String text;

    /**
     * Create a New with empty title and text
     */
    public New() {
        title = "";
        text = "";
    }

    /**
     * Create a New with a title and a text
     * @param title Title of the new
     * @param text Text of the new
     */
    public New(String title, String text) {
        this.title = title;
        this.text = text;
    }

    /**
     * Get title of the new
     * @return Title of the new
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title of the new
     * @param title Title of the new
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get text of the new
     * @return Text of the new
     */
    public String getText() {
        return text;
    }

    /**
     * Set text of the new
     * @param text Text of the new
     */
    public void setText(String text) {
        this.text = text;
    }
}

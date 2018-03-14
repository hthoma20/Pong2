package harrison.pong;

/**
 * Created by Harrison on 3/13/2018.
 */

public class Paddle extends Wall {

    //size of paddle
    private int width;
    private int height;

    /**
     * paddle constructor
     *
     * @param left   coord of wall
     * @param top    coord of wall
     * @param right  coord of wall
     * @param bottom coord of wall
     * @param color  of wall
     */
    public Paddle(int left, int top, int right, int bottom, int color) {
        super(left, top, right, bottom, color);
        width = right-left;
        height = top-bottom;
    }

    /**
     * sets x-coord of center of paddle
     */
    public void setX (int x) {
        left = x-(width/2);
        right = x+(width/2);
    }

    /**
     * sets y-coord of center of paddle
     */
    public void setY (int y) {
        top = y-(height/2);
        bottom = y+(height/2);
    }


}

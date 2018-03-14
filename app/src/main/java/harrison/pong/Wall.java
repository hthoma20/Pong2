package harrison.pong;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Harrison on 3/13/2018.
 */

public class Wall {

    //the coordinates of rectangle that is wall
    protected int left;
    protected int top;
    protected int right;
    protected int bottom;
    private Paint paint = new Paint () ;

    /**
     * wall constructor
     *
     * @param left coord of wall
     * @param top coord of wall
     * @param right coord of wall
     * @param bottom coord of wall
     * @param color of wall
     */
    public Wall ( int left, int top, int right, int bottom, int color) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.paint.setColor(color);
    }

    /**
     * draws rectangle at coords
     * @param c canvas on which to draw
     */
    public void onDraw (Canvas c) {
        c.drawRect(left, top, right, bottom, paint);
    }

    /**
     * @param x coord of point to test
     * @param y coord of point to test
     * @return whether the point is within/on the wall
     */
    public boolean touches (int x, int y) {
        if (x < left) return false;
        if (y < top) return false;
        if (x > right) return false;
        if (y > bottom) return false;

        return true;
    }

}

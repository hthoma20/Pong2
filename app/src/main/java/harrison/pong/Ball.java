package harrison.pong;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Harrison on 3/13/2018.
 */

public class Ball {

    //center location of ball
    private double x;
    private double y;
    private int radius;
    private int speed; //in pixels/sec
    public double direction; //in radians
    private Paint paint = new Paint();

    /**
     * Ball constructor
     *
     * @param x center coord of ball
     * @param y center coord of ball
     * @param rad of ball
     * @param spd speed ball is traveling
     * @param dir direction ball is moving in
     * @param col color of ball
     */
    public Ball (double x, double y, int rad, int spd, double dir, int col){
        this.x = x;
        this.y = y;
        this.radius = rad;
        this.speed = spd;
        this.direction = dir;
        this.paint.setColor(col);
    }

    /**
     * Ball constructor that randomizes speed and direction
     *
     * @param x
     * @param y
     * @param rad
     * @param col
     */
    public Ball (double x, double y, int rad, int col) {
        this.x = x;
        this.y = y;
        this.radius = rad;
        this.paint.setColor(col);

        //set random speed
        int minSpeed = 50;
        int maxSpeed = 300;
        this.speed = (int) (Math.random()*(maxSpeed-minSpeed)+minSpeed);

        //set random direction
        this.direction = Math.random()*2*Math.PI;
    }

    /**
     * draws ball on canvas
     * @param c canvas on which to draw
     */
    public void onDraw (Canvas c){
        c.drawCircle((float)x,(float)y,radius,paint);
    }

    /**
     * moves ball according to how much time has passed and its speed
     *
     * @param deltaT the amount of time since last move in milliseconds
     */
    public void move (int deltaT){
        double xSpeed = Math.cos(direction)*speed;
        double ySpeed = Math.sin(direction)*speed;

        double deltaX = xSpeed*deltaT/1000;
        double deltaY = ySpeed*deltaT/1000;

        x += deltaX;
        y += deltaY;
    }
}
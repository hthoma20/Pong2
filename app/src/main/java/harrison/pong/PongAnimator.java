package harrison.pong;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Created by Harrison on 3/13/2018.
 */

public class PongAnimator implements Animator{

    //array of walls: left, top, right
    private Wall[] walls = new Wall[4];
    private static final int LEFT = 0;
    private static final int TOP = 1;
    private static final int RIGHT = 2;
    private static final int PADDLE = 3;

    private Ball ball;

    private int tickInterval = 7;

    /**
     * PongAnimator constructor
     */
    public PongAnimator (){
        int screenWidth=2550;
        int screenHeight = 1500;
        int wallWidth = 65;
        int wallColor = 0xff555555;

        walls[LEFT] =
                new Wall(0,wallWidth,
                        wallWidth,screenHeight,wallColor);
        walls[TOP] =
                new Wall(wallWidth,0,
                        screenWidth-wallWidth,wallWidth,wallColor);
        walls[RIGHT] =
                new Wall(screenWidth-wallWidth,wallWidth,
                        screenWidth,screenHeight,wallColor);

        walls[PADDLE] = new Paddle (screenWidth/2, screenHeight-wallWidth,
                        screenWidth/2+300,screenHeight, 0xffff0000);

        ball = new Ball (screenWidth/4,screenHeight/4,45,1000,Math.PI/3,0xff0000ff);
    }

    public void onDraw (Canvas c){
        //draw walls
        for (int i=0; i<walls.length; i++) {
            walls[i].onDraw(c);
        }

        ball.onDraw(c);
    }

    @Override
    public int interval() {
        return tickInterval; //how many millis between ticks
    }

    @Override
    public int backgroundColor() {
        return 0xff000000;
    }

    @Override
    public boolean doPause() {
        return false;
    }

    @Override
    public boolean doQuit() {
        return false;
    }

    @Override
    public void tick(Canvas canvas) {
        ball.move(tickInterval);

        //ball's location
        int ballX = (int) ball.getX();
        int ballY = (int) ball.getY();
        int ballRad = (int) ball.getRadius();
        double ballDir = ball.getDirection (); //direction as an angle
        int quadDir = ball.quadrantDirection(); //direction as cardinal direction

        int i;
        //checks if ball hits any wall
        for (i=0; i<walls.length; i++) {
            if (walls[i].isPointWithin(ballX, ballY, ballRad)) {
                ball.incrementDirection(Math.PI/2);
                break;
            }
        }

        if(i < walls.length) {
            //check which side of wall ball hits
            //and determine which angle ball will bounce off at
            switch (walls[i].sideClosestTo(ballX, ballY)) {
                case Wall.LEFTSIDE:
                    //if ball is traveling east
                    if (quadDir == ball.NE || quadDir == ball.SE) {
                        ball.setDirection(Math.PI - ballDir);
                    }
                    break;
                case Wall.TOPSIDE:
                    //if ball is traveling south
                    if (quadDir == ball.SE || quadDir == ball.SW) {
                        ball.setDirection(-ballDir);
                    }
                    break;
                case Wall.RIGHTSIDE:
                    //if ball is traveling west
                    if (quadDir == ball.NW || quadDir == ball.SW) {
                        ball.setDirection(Math.PI-ballDir);
                    }
                    break;
                case Wall.BOTTOMSIDE:
                    //if ball is traveling north
                    if (quadDir == ball.NE || quadDir == ball.NW) {
                        ball.setDirection(-ballDir);
                    }
                    break;
            }
        }

        onDraw(canvas);

    }

    @Override
    public void onTouch(MotionEvent event) {
        ((Paddle)walls[PADDLE]).setX((int)event.getX());
    }
}

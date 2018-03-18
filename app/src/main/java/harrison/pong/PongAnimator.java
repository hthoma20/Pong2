package harrison.pong;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
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

    private final Paddle paddle; //just a reference to the paddle in walls[PADDLE]
    private Ball ball;

    //whether ball is in play
    private boolean ballInPlay;

    private int screenWidth;
    private int screenHeight;

    private int score; //number of times ball hits paddle

    private int tickInterval = 7;

    /**
     * PongAnimator constructor
     */
    public PongAnimator (){
        screenWidth = 2550;
        screenHeight = 1500;
        int wallWidth = 65;

        int wallColor = 0xff555555;
        int ballRad = 45;

        paddle= new Paddle (screenWidth/2, screenHeight-wallWidth,
                screenWidth/2+300,screenHeight, 0xffff0000);

        walls[LEFT] =
                new Wall(0,wallWidth,
                        wallWidth,screenHeight,wallColor);
        walls[TOP] =
                new Wall(wallWidth,0,
                        screenWidth-wallWidth,wallWidth,wallColor);
        walls[RIGHT] =
                new Wall(screenWidth-wallWidth,wallWidth,
                        screenWidth,screenHeight,wallColor);

        walls[PADDLE] = paddle;

        ball = new Ball(paddle.getCenterX(),paddle.getTop()-ballRad,
                        ballRad,0,0,0xff0000ff);

        ballInPlay = false;
        score= 0;
    }

    public void onDraw (Canvas c){
        //draw walls
        for (Wall wall : walls) {
            wall.onDraw(c);
        }

        drawScore(c);
        ball.onDraw(c);
    }

    private void drawScore(Canvas c){
        Paint scorePaint= new Paint();
        scorePaint.setColor(0xff000000);
        scorePaint.setTextSize(paddle.getHeight());
        scorePaint.setTextAlign(Paint.Align.CENTER);

        c.drawText
                (""+score,paddle.getCenterX(),paddle.getBottom()-5,scorePaint);

        /**
         * External citation
         * Date: 3/16/18
         * Problem: Wanted to draw the score on the surface
         * Resource: Android Canvas API, Android Paint API
         * Solution: drawText(), setTextAlign()
         */
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
        //if ball not in play, we don't need to do anything
        if(ballInPlay) {

            ball.move(tickInterval);

            //check for collision with a wall
            Wall hitWall = checkCollision();
            //if we hit a wall, bounce the ball
            if (hitWall != null) bounceBall(hitWall);

            if (hitWall == paddle) score++;

            //if ball was out of bounds, restart ball in playing position
            restartBall();
        }

        onDraw(canvas);
    }

    /**
     * checks if ball has hit a wall
     *
     * @return the wall that was hit,
     *          null if no collision
     */
    private Wall checkCollision(){
        //ball's location
        int ballX = (int) ball.getX();
        int ballY = (int) ball.getY();
        int ballRad = ball.getRadius();


        //the wall we are checking for a collision with
        //checks if ball hits any wall
        for (Wall currWall : walls) {
            //if the ball is within the radius of the current wall
            if (currWall.isPointWithin(ballX, ballY, ballRad)) {
                //we have found the wall the ball is touching
                //so return which side of the wall the ball hit
                return currWall;
            }
        }

        //no wall was hit
        return null;
    }

    /**
     * bounces the ball off wall
     * @param wall the wall that was hit
     */
    private void bounceBall(Wall wall){
        //the side of the wall that was hit
        int wallSide= wall.sideClosestTo(
                (int)ball.getX(),(int)ball.getY());

        //the direction of the ball in radians
        double ballDir= ball.getDirection();

        //the direction of the ball as a compass quadrant:
        // NE, NW, SW, SE
        double quadDir= ball.quadrantDirection();

        //check which side of wall ball hits
        //and determine which angle ball will bounce off at
        switch (wallSide) {
            case Wall.LEFTSIDE:
                //if ball is traveling east
                if (quadDir == Ball.NE || quadDir == Ball.SE) {
                    ball.setDirection(Math.PI - ballDir);
                }
                break;
            case Wall.TOPSIDE:
                //if ball is traveling south
                if (quadDir == Ball.SE || quadDir == Ball.SW) {
                    ball.setDirection(-ballDir);
                }
                break;
            case Wall.RIGHTSIDE:
                //if ball is traveling west
                if (quadDir == Ball.NW || quadDir == Ball.SW) {
                    ball.setDirection(Math.PI-ballDir);
                }
                break;
            case Wall.BOTTOMSIDE:
                //if ball is traveling north
                if (quadDir == Ball.NE || quadDir == Ball.NW) {
                    ball.setDirection(-ballDir);
                }
                break;
        }
    }


    /**
     * checks if ball is out of bounds
     * if so, places ball back in play
     * @return whether ball was out of bounds
     */
    private boolean restartBall () {
        int ballRad = ball.getRadius();

        //ball still in valid area
        if(ball.getY() < screenHeight+ballRad) return false;

        ball = new Ball (paddle.getCenterX(),paddle.getTop()-ballRad,
                ballRad,0,0,0xff0000ff);
        ballInPlay = false;
        score= 0;

        return true;
    }


    @Override
    public void onTouch(MotionEvent event) {
        if (!ballInPlay) {
            //if screen was tapped
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //todo add start button
                startBall();
            }
            else { //makes dead ball follow paddle's location
                ball.setX(paddle.getCenterX());
            }
        }
        movePaddle((int) event.getX());
    }

    /**
     * sets random speed and direction of starting ball
     */
    public void startBall () {
        ballInPlay = true;

        //set random speed
        int minSpeed = 1000;
        int maxSpeed = 3000;
        int spd = (int) (Math.random()*(maxSpeed-minSpeed)+minSpeed);
        ball.setSpeed(spd);

        //set random direction
        double minDir = Math.PI/6;
        double maxDir = 5*Math.PI/6;
        double dir = Math.random()*(maxDir-minDir)+minDir;
        ball.setDirection(dir);
    }

    /**
     * moves paddle according to right and left boundaries
     * @param x coord of where we want to move paddle
     */
    public void movePaddle (int x) {
        paddle.setCenterX(x);

        //these are boundaries that paddle is allowed to be within
        int leftBoundary = walls[LEFT].getRight();
        int rightBoundary = walls[RIGHT].getLeft();
        //make sure paddle is in bounds

        if (paddle.getLeft() < leftBoundary) {
            paddle.setLeft(leftBoundary);
        }
        else if (paddle.getRight() > rightBoundary) {
            paddle.setRight(rightBoundary);
        }
    }
}

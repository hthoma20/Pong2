package harrison.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

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

    //the list of balls in play
    private ArrayList<Ball> balls= new ArrayList<>();

    //the radius of initial ball
    private int initialBallRadius= 45;

    //whether ball is in play
    private boolean ballInPlay;

    private int screenWidth;
    private int screenHeight;

    private int score; //number of times ball hits paddle

    private int tickInterval = 7;

    //reference to Controls object that owns this PongAnimator
    private Controls control = null;

    /**
     * PongAnimator constructor
     */
    public PongAnimator (){
        screenWidth = 2550;
        screenHeight = 1300;
        int wallWidth = 65;

        int wallColor = 0xff555555;

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

        //adds the initial ball to list of balls
        balls.add( new Ball(paddle.getCenterX(),paddle.getTop()-initialBallRadius,
                    initialBallRadius,0,0,0xff0000ff) );

        ballInPlay = false;
        score= 0;
    }

    public void onDraw (Canvas c){
        //draw walls
        for (Wall wall : walls) {
            wall.onDraw(c);
        }

        drawBoundary(c);
        drawScore(c);

        for(Ball ball : balls) {
            if(ball == null){
                int poop= 1;
            }
            ball.onDraw(c);
        }
    }

    private void drawBoundary (Canvas c) {
        Paint boundaryPaint = new Paint ();
        boundaryPaint.setColor(0xffffffff);

        //the length of each segment of broken line
        int interval = 25;

        for(int i=0; i <= screenWidth; i+= 2*interval){
            c.drawLine(i,screenHeight,i+interval,screenHeight,boundaryPaint);
        }
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

    /**
     * if we don't already have a control, sets control to given control
     * @param control the control to set
     * @return whether we set the control
     */
    public boolean setControls (Controls control) {
        if (this.control != null) return false;

        this.control = control;
        return true;
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

            //only if every ball is not in play do we restart
            //there is at least one ball in play
            boolean hasBallInPlay = false;

            //for(int i=0; i<balls.size(); i++) {
                //Ball ball = balls.get(i);

            for (Ball ball : balls) {
                ball.move(tickInterval);

                //check for collision with a wall
                Wall hitWall = checkCollision(ball);
                //if we hit a wall, bounce the ball
                if (hitWall != null) bounceBall(ball, hitWall);

                if (hitWall == paddle) score++;

                //if  out of bounds, restart ball in playing position
                if (ballInBounds(ball)) hasBallInPlay=true;
            }

            //if no balls are in play
            if (!hasBallInPlay) {
                restartBall();
            }
        }

        onDraw(canvas);
    }

    /**
     * checks if ball has hit a wall
     *
     * @return the wall that was hit,
     *          null if no collision
     */
    private Wall checkCollision(Ball ball){
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
     * checks every ball if it is in valid area
     * if not, removes it from balls array list
     *
     * @return whether any ball is in valid area
     *
     * if this returns false, balls array list will be empty
     */
    private boolean ballInBounds (Ball ball) {
        boolean hasInBounds= false;

        //if the current ball is in bounds
        if(ball.getY() < screenHeight+ball.getRadius()){
            //we found an in-bounds ball
            hasInBounds= true;
        }
        else{ //the current ball is out of bounds
            balls.remove(ball);
        }

        //if all balls are out og bounds, ball is not in play
        if(!hasInBounds) ballInPlay= false;

        return hasInBounds;
    }

    /**
     * bounces the ball off wall
     *
     * @param ball the ball that hit wall
     * @param wall the wall that was hit by ball
     */
    private void bounceBall(Ball ball, Wall wall){
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
     * places ball back in starting position
     */
    private void restartBall () {
        //empty balls array list
        balls.clear();

        balls.add( new Ball (paddle.getCenterX(),paddle.getTop()-initialBallRadius,
                initialBallRadius,0,0,0xff0000ff) );

        ballInPlay = false;
        score= 0;

        control.ballRestarted();
    }

    /**
     * if ball is dead, starts game
     * if ball is live, restarts game
     *
     * @param minSpeed that player wants
     * @param maxSpeed that player wants
     *
     * @return whether the game was started (F) or restarted (T)
     */
    public boolean changeBallState (int minSpeed, int maxSpeed) {
        if (ballInPlay) {
            restartBall();
            return true;
        }
        else {
            assert balls.size()==1;
            startBall(balls.get(0),minSpeed, maxSpeed);
            return false;
        }
    }

    /**
     * sets random speed and direction of starting ball
     *
     * @param
     * @param
     * @param
     */
    public void startBall (Ball ball, int minSpeed, int maxSpeed) {
        ballInPlay = true;

        //set random speed
        int spd = (int) (Math.random()*(maxSpeed-minSpeed)+minSpeed);
        ball.setSpeed(spd);

        //set random direction
        double minDir = Math.PI/6;
        double maxDir = 5*Math.PI/6;
        double dir = Math.random()*(maxDir-minDir)+minDir;
        ball.setDirection(dir);
    }

    /**
     *
     */
    public void addBall (int minSpeed, int maxSpeed) {
        //cannot add ball if there is stationary ball on paddle
         if (!ballInPlay) return;

        //able to add a ball if current ball(s) are in play
        //no ball on paddle
        Ball ballToAdd = new Ball (paddle.getCenterX(),paddle.getTop()-initialBallRadius,
                initialBallRadius,0,0,0xff0000ff);
        //todo random color
        balls.add(ballToAdd);

        //ball shoots out from paddle at random speed and direction
        startBall(ballToAdd,minSpeed,maxSpeed);
    }

    @Override
    public void onTouch(MotionEvent event) {
        if (!ballInPlay) {
            moveBallToPaddle();
        }

        movePaddle((int) event.getX());
    }

    /**
     * moves the presumably single ball with the paddle
     */
    private void moveBallToPaddle(){
        if (balls.size() == 1){ //if there is a ball
            //move the ball with the paddle
            balls.get(0).setX(paddle.getCenterX());
        }
    }

    /**
     * moves paddle according to right and left boundaries
     * @param x coord of where we want to move paddle
     */
    public void movePaddle (int x) {
        paddle.setCenterX(x);
        checkPaddleToWall();
    }

    /**
     * checks if paddle is touching or past any wall
     * if so, moves paddle within wall boundaries
     * @return whether paddle had to move
     */
    private boolean checkPaddleToWall () {
        //these are boundaries that paddle is allowed to be within
        int leftBoundary = walls[LEFT].getRight();
        int rightBoundary = walls[RIGHT].getLeft();
        //make sure paddle is in bounds

        if (paddle.getLeft() < leftBoundary) {
            paddle.setLeft(leftBoundary);
            return true;
        }
        if (paddle.getRight() > rightBoundary) {
            paddle.setRight(rightBoundary);
            return true;
        }

        return false;
    }

    /**
     * if ball is out of play, the paddle size will change
     *
     * @param paddleSize the new size of paddle
     * @return whether the size was changed
     */
    public boolean changePaddleSize(int paddleSize){
        if(ballInPlay) return false;

        paddle.setWidth(paddleSize);
        //makes sure paddle does not pass wall boundary
        if (checkPaddleToWall()) {
            //if paddle moved, ball should follow
            moveBallToPaddle();
        }
        return true;
    }
}

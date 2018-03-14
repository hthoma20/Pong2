package harrison.pong;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

/**
 * Created by Harrison on 3/13/2018.
 */

public class PongAnimator implements Animator {

    //array of walls: left, top, right
    private Wall[] walls = new Wall[3];
    private static final int LEFT = 0;
    private static final int TOP = 1;
    private static final int RIGHT = 2;

    private Paddle paddle;
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

        paddle = new Paddle (screenWidth/2, screenHeight-wallWidth,
                        screenWidth/2+300,screenHeight, 0xffff0000);

        ball = new Ball (screenWidth/2,screenHeight/2,45,1000,0,0xff0000ff);
    }

    public void onDraw (Canvas c){
        //draw walls
        for (int i=0; i<walls.length; i++) {
            walls[i].onDraw(c);
        }

        paddle.onDraw(c);
        ball.onDraw(c);
    }


    @Override
    public int interval() {
        return tickInterval; //how many millis between ticks
    }

    @Override
    public int backgroundColor() {
        return 0;
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
        onDraw(canvas);
    }

    @Override
    public void onTouch(MotionEvent event) {
        paddle.setX((int)event.getX());
        paddle.setY((int)event.getY());

    }
}

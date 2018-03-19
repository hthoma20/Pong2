package harrison.pong;


import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;

/**
 * Created by Harrison on 3/19/2018.
 */

public class Controls implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    PongAnimator pong;
    Button startButton;
    SeekBar paddleSeekBar;
    RadioGroup speedRadioGroup;

    int minPaddle= 50;
    int maxPaddle= 700;

    /**
     * controls constructor
     *
     * @param pong
     * @param start
     * @param paddleSize
     * @param speedRadioGroup
     */
    public Controls (PongAnimator pong, Button start, SeekBar paddleSize,
                     RadioGroup speedRadioGroup) {
        this.pong = pong;
        this.startButton = start;
        this.paddleSeekBar = paddleSize;
        this.speedRadioGroup = speedRadioGroup;

        startButton.setOnClickListener (this);
        paddleSeekBar.setOnSeekBarChangeListener(this);

        paddleSeekBar.setMax(maxPaddle-minPaddle);
        setSeekBarToSize((maxPaddle-minPaddle)/2);
    }

    @Override
    public void onClick(View view) {
        if (view == startButton) {
            pong.startGame(2000,3000);
        }
    }

    /**
     * @return the size the paddle should be based on the seekBar
     */
    private int paddleSize(){
        return paddleSeekBar.getProgress()+minPaddle;
    }

    /**
     * changes seekBar to reflect given paddle size
     * @param size the size of paddle
     */
    private void setSeekBarToSize(int size){
        int progress= size-minPaddle;
        paddleSeekBar.setProgress(progress);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        pong.changePaddleSize(paddleSize());
    }

    //unused methods
    @Override
    public void onStartTrackingTouch(SeekBar seekBar){}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}

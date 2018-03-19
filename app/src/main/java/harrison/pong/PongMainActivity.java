package harrison.pong;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;

/**
 * PongMainActivity
 *
 * This is the activity for the Pong game. It attaches a PongAnimator to
 * an AnimationSurface.
 *
 * @author Andrew Nuxoll
 * @author Steven R. Vegdahl
 * @version July 2013
 *
 */
public class PongMainActivity extends AppCompatActivity {



    /**
     * creates an AnimationSurface containing a TestAnimator.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pong_main);

        // Connect the animation surface with the animator
        AnimationSurface mySurface = (AnimationSurface) this
                .findViewById(R.id.animationSurface);
        PongAnimator pong = new PongAnimator();
        mySurface.setAnimator(pong);

        Button startButton= (Button)findViewById(R.id.buttonStart);
        SeekBar paddleSizeBar= (SeekBar)findViewById(R.id.seekBarPaddleSize);
        RadioGroup ballSpeedRadio= (RadioGroup)findViewById(R.id.radioGroupSpeed);

        Controls control = new Controls(pong,startButton,paddleSizeBar,ballSpeedRadio);
    }
}

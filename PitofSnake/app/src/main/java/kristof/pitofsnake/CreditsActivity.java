package kristof.pitofsnake;
//Work done by Tushar Mittal
import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class CreditsActivity extends Activity {

    private TextView textView;
    private Animation animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.credits);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("CREDITS \n" + "Graphics and Menus     Tushar\n" + "Game Engine        Ronan and Kristof\n" + "BackGround Music        www.soundbible.com\n"
                + "Sound effects       www.AndroidGamePrograming.com");

        textView.startAnimation(animation);
    }
}

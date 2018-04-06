package kristof.pitofsnake;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class HomeScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home_screen);
        MediaPlayer player = MediaPlayer.create(this, R.raw.backgroundsong);
        player.setLooping(true);
        player.start();


    }

    public void onClickSend(View view) {
        MediaPlayer buttonsound = MediaPlayer.create(this, R.raw.buttonsound);
        buttonsound.start();
        Intent intent = new Intent(this, MenuScreen.class);
        startActivity(intent);
    }

    public void onClickStart(View view) {
        MediaPlayer buttonsound = MediaPlayer.create(this, R.raw.buttonsound);
        buttonsound.start();
        Intent intent = new Intent(this, PitofSnake.class);
        startActivity(intent);
    }
}

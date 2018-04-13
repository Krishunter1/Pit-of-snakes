package kristof.pitofsnake;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class HomeScreen extends Activity {
    private static MediaPlayer player;
    private boolean musicPlay = true;
    private static final String MUSIC_PLAY = "music-play";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home_screen);
        player = MediaPlayer.create(this, R.raw.backgroundsong);
        player.setLooping(true);
        startBackgroundmusic(musicPlay);

    }

    public void startBackgroundmusic(boolean value) {
        musicPlay = value;
        if(value) {
            player.start();
        }
        else {
            player.pause();
        }
    }

    private void SavePreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor2 = sharedPreferences.edit();
        editor2.putBoolean(MUSIC_PLAY, musicPlay);
        editor2.commit();
    }

    private void LoadPreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        musicPlay = sharedPreferences.getBoolean(MUSIC_PLAY, musicPlay);
    }

    public void onClickSend(View view) {
        MediaPlayer buttonsound = MediaPlayer.create(this, R.raw.buttonsound);
        buttonsound.start();
        Intent intent = new Intent(this, MenuScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void onClickStart(View view) {
        MediaPlayer buttonsound = MediaPlayer.create(this, R.raw.buttonsound);
        buttonsound.start();
        Intent intent = new Intent(this, PitofSnake.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadPreferences();
    }

    @Override
    protected void onPause() {
        SavePreferences();
        super.onPause();
    }

    @Override
    public void onStop() {
        SavePreferences();
        super.onStop();
    }
}

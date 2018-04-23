package kristof.pitofsnake;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;

//work done by Tushar Mittal
public class HomeScreen extends Activity {

    private static MediaPlayer player;
    private static MediaPlayer buttonsound;
    private boolean musicPlay = true; //stores if the background music is to be played or not
    private boolean fxPlay = true; //stores if the sound effects are to be played or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home_screen);
        player = MediaPlayer.create(this, R.raw.backgroundsong); //creates a mediaPlayer with background music file
        player.setLooping(true);
        LoadPreferences();
        if(musicPlay) {
            startBackgroundmusic(musicPlay);
        }
    }

    //Method is called to play button sound effect
    public void playButtonSound() {
        buttonsound = MediaPlayer.create(this, R.raw.buttonsound);
        //checks if the soundeffects value is true
        if(fxPlay) {
            buttonsound.start();
        }
    }

    //Method is called to play background music
    public void startBackgroundmusic(boolean value) {
        //it is checked if the user wants to play the background music or not
        if(value) {
            player.start();
        }
        else {
            player.pause();
        }
    }

    /*This method loads the selections made by the user in the menu screen if it's the first time user has
     * started the application then the values are set as default to true
     */
    private void LoadPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        musicPlay = sharedPreferences.getBoolean("music_value", true);
        fxPlay = sharedPreferences.getBoolean("sound_fx", true);
    }

    //event listener method to open menu screen
    public void onClickSend(View view) {
        playButtonSound();
        Intent intent = new Intent(this, MenuScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out); //animation used to open menu screen
    }

    //event listener method to start the game
    public void onClickStart(View view) {
        playButtonSound();
        Intent intent = new Intent(this, PitofSnake.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadPreferences();
    }
}

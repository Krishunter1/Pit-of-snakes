package kristof.pitofsnake;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

//Work done by Tushar Mittal
public class MenuScreen extends Activity {
    private static final String IMAGE_NUMBER_ONE = "image-number-one";
    private static final String IMAGE_NUMBER_TWO = "image-number-two";
    private static final String MUSIC_VALUE = "music_value";
    private static final String SOUND_FX = "sound_fx";
    private int current_image1;
    private int current_image2;
    private boolean decision = true; //boolean value to store the selection of background music
    private boolean soundfx = true; //boolean value to store the selection of soundEffects
    int[] images = {R.drawable.speaker1, R.drawable.muteicon}; //stores the two images used for speaker symbol
    ImageView imgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu_screen);

        if (savedInstanceState != null) {
            current_image1 = savedInstanceState.getInt(IMAGE_NUMBER_ONE);
            current_image2 = savedInstanceState.getInt(IMAGE_NUMBER_TWO);
            decision = savedInstanceState.getBoolean(MUSIC_VALUE);
            soundfx = savedInstanceState.getBoolean(SOUND_FX);
            setImage();
        }

    }

    protected void onResume() {
        super.onResume();
        LoadPreferences();
    }

    protected void onPause() {
        SavePreferences();
        super.onPause();
    }

    //event listener for stopping and starting sound effects
    public void onClickStopEffect( View view ) {
        imgView = (ImageView)findViewById(R.id.volume);
        current_image1++;
        current_image1 = current_image1 % images.length; //used to switch between the images on button press
        imgView.setImageResource(images[current_image1]);

        //checks the current selection and updates it on button press
        if(soundfx)
            soundfx = false;
        else if(!soundfx)
            soundfx = true;
    }

    //event listener for stopping and starting background music
    public void onClickStopMusic( View view ) {
        imgView = (ImageView)findViewById(R.id.volume2);
        current_image2++;
        current_image2 = current_image2 % images.length; //used to switch between the images on button press
        imgView.setImageResource(images[current_image2]);

        //checks the current selection and updates it on button press
        if(decision)
            decision = false;
        else if(!decision)
            decision = true;

        //startBackgroundMusic method in the HomeScreen class is called to start or stop the background music
        HomeScreen home = new HomeScreen();
        home.startBackgroundmusic(decision);
    }

    //event listener to start creditsActivity
    public void onClickCredits( View view ) {
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(IMAGE_NUMBER_ONE, current_image1);
        outState.putInt(IMAGE_NUMBER_TWO, current_image2);
        outState.putBoolean(MUSIC_VALUE, decision);
        outState.putBoolean(SOUND_FX, soundfx);
        super.onSaveInstanceState(outState);

    }

    //saves the values of the images and the sounds so that they can be loaded again when the application is started again
    private void SavePreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(IMAGE_NUMBER_ONE, current_image1);
        editor.putInt(IMAGE_NUMBER_TWO, current_image2);
        editor.putBoolean(MUSIC_VALUE, decision);
        editor.putBoolean(SOUND_FX, soundfx);
        editor.commit();
    }

    //load the preferences of the user from the previous selections
    private void LoadPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        current_image1 = sharedPreferences.getInt(IMAGE_NUMBER_ONE, current_image1);
        current_image2 = sharedPreferences.getInt(IMAGE_NUMBER_TWO, current_image2);
        decision = sharedPreferences.getBoolean(MUSIC_VALUE, decision);
        soundfx = sharedPreferences.getBoolean(SOUND_FX, soundfx);

        setImage();

    }

    //this method is called to update the speaker images when the buttons are pressed
    private void setImage() {
        imgView = (ImageView)findViewById(R.id.volume);
        imgView.setImageResource(images[current_image1]);
        imgView = (ImageView)findViewById(R.id.volume2);
        imgView.setImageResource(images[current_image2]);
    }

    @Override
    public void onStop() {
        SavePreferences();
        super.onStop();
    }

}

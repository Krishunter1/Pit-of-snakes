package kristof.pitofsnake;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import static junit.runner.BaseTestRunner.savePreferences;

public class MenuScreen extends Activity {
    private static final String IMAGE_NUMBER_ONE = "image-number-one";
    private static final String IMAGE_NUMBER_TWO = "image-number-two";
    private int current_image1;
    private int current_image2;

    int[] images = {R.drawable.speaker1, R.drawable.muteicon};
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

    public void onClickStopEffect( View view ) {
        imgView = (ImageView)findViewById(R.id.volume);
        current_image1++;
        current_image1 = current_image1 % images.length;
        imgView.setImageResource(images[current_image1]);
    }

    public void onClickStopMusic( View view ) {
        imgView = (ImageView)findViewById(R.id.volume2);
        current_image2++;
        current_image2 = current_image2 % images.length;
        imgView.setImageResource(images[current_image2]);
    }

    public void onClickCredits( View view ) {
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(IMAGE_NUMBER_ONE, current_image1);
        outState.putInt(IMAGE_NUMBER_TWO, current_image2);
        super.onSaveInstanceState(outState);

    }

    private void SavePreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(IMAGE_NUMBER_ONE, current_image1);
        editor.putInt(IMAGE_NUMBER_TWO, current_image2);
        editor.commit();   // I missed to save the data to preference here,.
    }

    private void LoadPreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        current_image1 = sharedPreferences.getInt(IMAGE_NUMBER_ONE, current_image1);
        current_image2 = sharedPreferences.getInt(IMAGE_NUMBER_TWO, current_image2);

        setImage();

    }

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

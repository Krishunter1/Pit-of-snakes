package kristof.pitofsnake;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class MenuScreen extends Activity {
    private static final String IMAGE_NUMBER = "image-number";
    private int current_image1;
    int[] images = {R.drawable.speaker1, R.drawable.muteicon};
    ImageView imgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu_screen);

        if (savedInstanceState != null) {
            current_image1 = savedInstanceState.getInt(IMAGE_NUMBER);
            imgView = (ImageView)findViewById(R.id.volume);
            imgView.setImageResource(images[current_image1]);
        }

    }

    protected void onResume() {
        super.onResume();
        LoadPreferences();
    }

    public void onClickStopEffect( View view ) {
        imgView = (ImageView)findViewById(R.id.volume);
        current_image1++;
        current_image1 = current_image1 % images.length;
        imgView.setImageResource(images[current_image1]);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(IMAGE_NUMBER, current_image1);
        super.onSaveInstanceState(outState);

    }

    private void SavePreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(IMAGE_NUMBER, current_image1);
        editor.commit();   // I missed to save the data to preference here,.
    }

    private void LoadPreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        current_image1 = sharedPreferences.getInt(IMAGE_NUMBER, current_image1);
        imgView = (ImageView)findViewById(R.id.volume);
        imgView.setImageResource(images[current_image1]);

    }

    @Override
    public void onBackPressed() {
        SavePreferences();
        super.onBackPressed(); //Check if you still want to go back
    }

}

package kristof.pitofsnake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }
    public void onClickSend(View view) {
        Intent intent = new Intent(this, MenuScreen.class);
        startActivity(intent);
    }

    public void onClickStart(View view) {
        Intent intent = new Intent(this, PitofSnake.class);
        startActivity(intent);
    }
}

package kristof.pitofsnake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MenuScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);
    }
    public void startGame(View view) {
        Intent intent = new Intent(this, PitofSnake.class);
        startActivity(intent);
    }
}

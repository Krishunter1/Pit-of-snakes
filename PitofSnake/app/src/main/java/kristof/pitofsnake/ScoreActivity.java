package kristof.pitofsnake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ScoreActivity extends Activity {

    private static final String HIGH_SCORE = "high-score";
    private int scored;
    private int highScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        scored = intent.getIntExtra("score",0);

        TextView showScore = (TextView) findViewById(R.id.currentScore);
        showScore.setText("Your Score " + scored );

        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highScore = settings.getInt(HIGH_SCORE, 0);

        if(scored > highScore) {
            highScore = scored;
            setText1();
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(HIGH_SCORE, highScore);
            editor.commit();
        }
        else {
            setText2();
        }
    }

    private void setText1() {
        TextView highScoreDisplay = (TextView) findViewById(R.id.highScore);
        highScoreDisplay.setText("NEW HIGH SCORE:" + highScore);
    }

    private void setText2() {
        TextView highScoreDisplay = (TextView) findViewById(R.id.highScore);
        highScoreDisplay.setText("HIGH SCORE:" + highScore);
    }

    public void onClickStart(View view) {
        Intent intent = new Intent(this, PitofSnake.class);
        startActivity(intent);
    }


}

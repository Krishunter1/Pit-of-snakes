package kristof.pitofsnake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class ScoreActivity extends Activity {

    private static final String HIGH_SCORE = "high-score";
    private int scored;
    private int highScore = 0;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private String quote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext()); //using the facebook sdk
        setContentView(R.layout.activity_score);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //init FB
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);



        Intent intent = getIntent();
        scored = intent.getIntExtra("score",0);

        TextView showScore = (TextView) findViewById(R.id.currentScore);
        showScore.setText("Your Score " + scored );

        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highScore = settings.getInt(HIGH_SCORE, 0);

        if(scored > highScore) {
            highScore = scored;
            setText1();
            quote = "Hey guys I just made a new High Score of " + scored + " in the game Pit of Snakes. Try this game out!";
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(HIGH_SCORE, highScore);
            editor.commit();
        }
        else {
            setText2();
            quote = "Hey guys I just scored " + scored + " in the game Pit of Snakes. Try this game out!";
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

    public void onClickShare(View view)
    {
        ShareContent linkContent = new ShareLinkContent.Builder()
                .setQuote(quote)
                .setContentUrl(Uri.parse("https://drive.google.com/file/d/15eXjPUdGSxLfpr4b7ovlyMtOGzzyLd58/view?usp=sharing"))
                .build();
        if(ShareDialog.canShow(ShareLinkContent.class))
        {
            shareDialog.show(linkContent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
    }


}

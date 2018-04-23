package kristof.pitofsnake;
//Work done by Ronan Barry
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

        //get the score from the intent
        Intent intent = getIntent();
        scored = intent.getIntExtra("score",0);

        //Display the current score of the Player
        TextView showScore = (TextView) findViewById(R.id.currentScore);
        showScore.setText("Your Score " + scored );

        //Get the highest score of the player the default value is 0
        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highScore = settings.getInt(HIGH_SCORE, 0);

        //If the current score is higher than the high score then the high score gets updated
        if(scored > highScore) {
            highScore = scored;
            setText1();
            //set the quote that would be used to share on facebook
            quote = "Hey guys I just made a new High Score of " + scored + " in the game Pit of Snakes. Try this game out!";
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(HIGH_SCORE, highScore);
            editor.commit();
        }
        else {
            setText2();
            //set the quote that would be used for sharing on facebook
            quote = "Hey guys I just scored " + scored + " in the game Pit of Snakes. Try this game out!";
        }
    }

    //New high score is displayed
    private void setText1() {
        TextView highScoreDisplay = (TextView) findViewById(R.id.highScore);
        highScoreDisplay.setText("NEW HIGH SCORE:" + highScore);
    }

    //old high score us displayed
    private void setText2() {
        TextView highScoreDisplay = (TextView) findViewById(R.id.highScore);
        highScoreDisplay.setText("HIGH SCORE:" + highScore);
    }

    //Event listener to play again
    public void onClickStart(View view) {
        Intent intent = new Intent(this, PitofSnake.class);
        startActivity(intent);
    }

    //Event listener to share score on facebook
    //since facebook doesn't allow to share an image with a quote thus a link to the image of
    //the game is provided. This is basically a reference of how the share link would look.
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


    //on back button press bring back to home screen
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
    }


}

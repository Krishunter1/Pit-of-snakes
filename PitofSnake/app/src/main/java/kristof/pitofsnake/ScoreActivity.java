package kristof.pitofsnake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.annotation.Target;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ScoreActivity extends Activity {

    private static final String HIGH_SCORE = "high-score";
    private int scored;
    private int highScore = 0;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
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

    public void onClickShare(View view)
    {
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setQuote("this is useful")
                .setContentUrl(Uri.parse("https://youtube.com"))
                .build();
        if(ShareDialog.canShow(ShareLinkContent.class))
        {
            shareDialog.show(linkContent);
        }
    }


}

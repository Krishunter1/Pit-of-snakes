package kristof.pitofsnake;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class ModeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mode);
    }

    public void onClickMode1 (View view) {

    }

    public void onClickMode2(View view) {

    }

    public void onClickMode3(View view) {

    }
}

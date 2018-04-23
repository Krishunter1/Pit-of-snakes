package kristof.pitofsnake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.GestureDetectorCompat;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

//Coded by Kristof
//The activity calls a PitView Object that acts as the View for this activity
//Gestrue detector is also implemented to recognise onFlick commands, which is used for our swiping controls
//Vibration is also present here so the device can vibrate when food is eaten or player dies.
//Also a activity with choice of game mode wasin't made as we were having difficult implementing it properly
public class PitofSnake extends Activity implements GestureDetector.OnGestureListener {

    // Declare an instance of SnakeView
    PitView pitView;
    //Declared values for onFLick calculation
    private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_MAX_OFF_PATH = 3000;
    private static final int SWIPE_THRESHOLD_VELOCITY = 1500;
    public static Context mContext;
    //Sets up variable to act as the detector for input
    private GestureDetectorCompat detector;
    //Sets up variable to set up vibrations
    public static Vibrator vibrator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initilaise the variables as new GestureDetector
        detector = new GestureDetectorCompat(this,this);
        //Ask device to allow vibrations
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //find out the width and height of the screen
        Display display = getWindowManager().getDefaultDisplay();

        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);
        mContext = getBaseContext();
        // Create a new View based on the SnakeView class
        pitView = new PitView(this, size);

        // Make snakeView the default view of the Activity
        setContentView(pitView);
    }

    // Start the thread in snakeView when this Activity
    // is shown to the player

    //Set up a onTouchEvent listener
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.detector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void onResume() {
        super.onResume();
        pitView.resume();
    }

    // Make sure the thread in snakeView is stopped
    // If this Activity is about to be closed
    @Override
    protected void onPause() {
        super.onPause();
        pitView.pause();
    }
    //All of these are called when GestureDetector interface is implemented, most of these are empty except onFlick
    @Override
    public boolean onDown(MotionEvent e) {
        //filler methord
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        //filler method
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        //filler methord
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //filler methord
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        //filler method

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH || Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH)
                return false;

            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // right to left swipe
               PitView.direction = PitView.Direction.LEFT;
               PitView.snakeXs[0]--;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // left to right swipe
                PitView.direction = PitView.Direction.RIGHT;
                PitView.snakeXs[0]++;
            } else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                // bottom to top
                PitView.direction = PitView.Direction.UP;
                PitView.snakeYs[0]--;
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                // top to bottom
                PitView.direction = PitView.Direction.DOWN;
                PitView.snakeYs[0]++;
            }

        } catch (Exception e) {
            // nothing
        }

        return false;
    }

}

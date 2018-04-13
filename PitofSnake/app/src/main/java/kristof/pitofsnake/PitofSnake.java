package kristof.pitofsnake;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;

//THe game in its current stage is pretty simplistic, we had the menu screen to play the game and go into options
//however, prehaps becasue we were using Dropbox until now, the activites and Layouts havent fully passed on
//Hence the menu right now is just a barebone play buttom

public class PitofSnake extends Activity implements GestureDetector.OnGestureListener {

    // Declare an instance of SnakeView
    PitView pitView;
    // We will initialize it in onCreate
    // once we have more details about the Player's device
    private static final int SWIPE_MIN_DISTANCE = 5;
    private static final int SWIPE_MAX_OFF_PATH = 3000;
    private static final int SWIPE_THRESHOLD_VELOCITY = 1500;
    private GestureDetectorCompat detector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detector = new GestureDetectorCompat(this,this);

        //find out the width and height of the screen
        Display display = getWindowManager().getDefaultDisplay();

        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create a new View based on the SnakeView class
        pitView = new PitView(this, size);

        // Make snakeView the default view of the Activity
        setContentView(pitView);
    }

    // Start the thread in snakeView when this Activity
    // is shown to the player

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.detector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
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

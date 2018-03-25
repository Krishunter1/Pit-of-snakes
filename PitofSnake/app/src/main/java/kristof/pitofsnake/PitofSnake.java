package kristof.pitofsnake;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

//THe game in its current stage is pretty simplistic, we had the menu screen to play the game and go into options
//however, prehaps becasue we were using Dropbox until now, the activites and Layouts havent fully passed on
//Hence the menu right now is just a barebone play buttom

public class PitofSnake extends Activity {

    // Declare an instance of SnakeView
    PitView pitView;
    // We will initialize it in onCreate
    // once we have more details about the Player's device

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
}

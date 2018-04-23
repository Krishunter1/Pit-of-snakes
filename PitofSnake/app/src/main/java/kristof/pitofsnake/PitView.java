package kristof.pitofsnake;
//coded by Kristof
//The reason the game is not running on Matrixes but instead uses Draw class
//is because the Draw class was the easiest way to implement the old style Snake game feel
//while being able to implement more modern touches with power ups.
//Also a activity with choice of game mode wasin't made as we were having difficult implementing it properly
//However the power ups act as if the game modes were selected, so the player is able to pick them up
//and change there game experience if they choose to do so.
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Random;

class PitView extends SurfaceView implements Runnable {

    // Allow code to run seperate to UI
    private Thread thread = null;
    //Checks if the game is running, volatile so can be acessed outside and inside thread
    private volatile boolean playing;

    // set up canvas for drawing
    private Canvas canvas;
    // This is required by the Canvas class to do the drawing
    private SurfaceHolder holder;
    // Variables to set up different colours for the snake/food/powerup
    private Paint paint;
    private Paint paint2;
    private Paint paint3;


    // Reference to activity
    private Context m_context;

    // Set up sound effects
    private SoundPool SoundPool;
    private int m_get_mouse_sound = -1;
    private int m_dead_sound = -1;

    // Track movement in Direction
    public enum Direction {UP, RIGHT, DOWN, LEFT}
    // Start by heading to the right
    public static Direction direction = Direction.RIGHT;

    // Find out the screen resolution of devices
    private int screenWidth;
    private int screenHeight;

    // Control pausing between updates
    private long nextFrameTime;
    // Update game 2 times a second, starting slow and increasing with powerup.
    private  long FPS = 2;
    private final long MILLIS_IN_A_SECOND = 1000;

    // The location in the grid of all the segments
    public static int[] snakeXs;
    public static int[] snakeYs;

    // Lenght of the snake
    private int snakeLength;

    // X and Y loctation of the mouse
    private int mouseX;
    private int mouseY;

    //location of speed upgrade
    private int speedX;
    private int speedY;

    // location of mass upgrade
    private int massX;
    private int massY;

    //stores the user selection of sound effects
    private boolean soundfx;
    //Set up score variable
    private int score;
    public static Context context;

    // The size in pixels of a snake segment
    private int blockSize;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 25;
    private int numBlocksHigh; // determined dynamically

    //Setting up the constructor
    public PitView(Context context, Point size) {
        super(context);

        m_context = context;

        screenWidth = size.x;
        screenHeight = size.y;

        //Determine the size of each block/place on the game board
        blockSize = screenWidth / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        numBlocksHigh = screenHeight / blockSize;

        // Set the sound up
        loadSound();

        //load the value of sound effects
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(m_context);
        soundfx = sharedPreferences.getBoolean("sound_fx", true);

        // Initialize the drawing objects
        holder = getHolder();
        paint = new Paint();
        paint2 = new Paint();
        paint3 = new Paint();

        // If you score 200 you are riewarded with a crash achevement!
        snakeXs = new int[200];
        snakeYs = new int[200];

        // Start the game
        startGame();
    }

    public void startGame() {
        // Start with just a head, in the middle of the screen
        snakeLength = 1;
        snakeXs[0] = NUM_BLOCKS_WIDE / 2;
        snakeYs[0] = numBlocksHigh / 2;

        // And a to a mouse to screen
        spawnMouse();
        //Add a speed powerup to screen
        spawnSpeed();
        //Add a mass powerup to screen
        spawnMass();

        // Reset the m_Score
        score = 0;

        // Setup m_NextFrameTime so an update is triggered immediately
        nextFrameTime = System.currentTimeMillis();
    }

    public void loadSound() {
        SoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            // Create objects of the 2 required classes
            // Use m_Context because this is a reference to the Activity
            AssetManager assetManager = m_context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the two sounds in memory
            descriptor = assetManager.openFd("get_mouse_sound.ogg");
            m_get_mouse_sound = SoundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("death_sound.ogg");
            m_dead_sound = SoundPool.load(descriptor, 0);

        } catch (IOException e) {
            // Error
        }
    }
    //Class to spawn mouse
    public void spawnMouse() {
        Random random = new Random();
        mouseX = random.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
        mouseY = random.nextInt(numBlocksHigh - 1) + 1;
    }
    //When mouse is eaten, increasing snake lenght by 1, spawn another mouse, incremeent score by 1 and play the sound effect
    private void eatMouse() {
        snakeLength++;
        spawnMouse();
        score = score + 1;
        playSoundEffectsMouse();
    }
    //Class to spawn Speed
    public void spawnSpeed() {
        Random random2 = new Random();
        speedX = random2.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
        speedY = random2.nextInt(numBlocksHigh - 1) + 1;
    }
    //Class to spawn Mass
    public void spawnMass() {
        Random random3 = new Random();
        massX = random3.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
        massY = random3.nextInt(numBlocksHigh - 1) + 1;
    }
    //When speed is eating ,increase the FPS of the game(speed of it) and increment score by 1, play sound effect of eating
    private void eatSpeed() {
        if( FPS == 2){
            FPS = 4;
        }
        else if (FPS == 4) {
            FPS = 6;
        }
        else if (FPS == 6) {
            FPS = 8;
        }
        spawnSpeed();
        score = score + 1;

        playSoundEffectsMouse();
    }

    //method called to play sound after eating food
    private void playSoundEffectsMouse() {
        //checks if the user wants the sound effects to be played
        if(soundfx) {
            SoundPool.play(m_get_mouse_sound, 1, 1, 0, 0, 1);
            PitofSnake.vibrator.vibrate(200);
        }
    }

    //method called to play sound effect on death
    private void playSoundEffectDeath() {
        //checks if the user wants the sound effects to be played
        if(soundfx) {
            SoundPool.play(m_dead_sound, 1, 1, 0, 0, 1);
            PitofSnake.vibrator.vibrate(50);
        }
    }
    //When mass is eaten, increasing snake length by 3, spawn new mass and increment score by 2, play sound effect
    private void eatMass() {
        snakeLength = snakeLength + 3;
        spawnMass();
        score = score + 2;
        playSoundEffectsMouse();
    }

    private void moveSnake(){
        // Start moving the snake
        for (int i = snakeLength; i > 0; i--) {
            // starting from back and moving the pixel foward to the one infont
            //head is not included because head has nothing in front
            snakeXs[i] = snakeXs[i - 1];
            snakeYs[i] = snakeYs[i - 1];
        }

        // Move head to direction chosen
        switch (direction) {
            case UP:
                snakeYs[0]--;
                break;

            case RIGHT:
                snakeXs[0]++;
                break;

            case DOWN:
                snakeYs[0]++;
                break;

            case LEFT:
                snakeXs[0]--;
                break;
        }
    }

    private boolean detectDeath(){
        boolean dead = false;
        //check if a wall has been hit on the edge of the screen
        if (snakeXs[0] <= -1) dead = true;
        if (snakeXs[0] >= NUM_BLOCKS_WIDE) dead = true;
        if (snakeYs[0] <= -1) dead = true;
        if (snakeYs[0] >= numBlocksHigh) dead = true;
        //check if player ran head into the body
        for (int i = snakeLength - 1; i > 0; i--) {
            if ((i > 4) && (snakeXs[0] == snakeXs[i]) && (snakeYs[0] == snakeYs[i])) {
                dead = true;
            }
        }

        return dead;
    }
    //Keeps the game running
    public void updateGame() {
        //Checks if a mouse has been eaten
        if (snakeXs[0] == mouseX && snakeYs[0] == mouseY ) {
            eatMouse();
        }
        moveSnake();
        //checks if a Speed upgrade is eaten
        if (snakeXs[0] == speedX  && snakeYs[0] == speedY ) {
            eatSpeed();
        }
        moveSnake();
        //check if a Mass upgrade is eaten
        if (snakeXs[0] == massX && snakeYs[0] == massY) {
            eatMass();
        }
        moveSnake();
        //If the player died
        if (detectDeath()) {
            playSoundEffectDeath();

            //The current score is sent to the activity named ScoreActivity and the activity is called
            context = PitofSnake.getContext();
            Intent intent = new Intent(context, ScoreActivity.class);
            intent.putExtra("score",score);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        }
    }
    //class to drawn the game
    public void drawGame() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();

            //load a bitmap image and set it to the screen
            Bitmap b = BitmapFactory.decodeResource(getResources(),R.drawable.back);
            Bitmap background = Bitmap.createScaledBitmap
                    (b, screenWidth, screenHeight, true);
            canvas.drawBitmap(background,0,0,null);

            //Snake colour
            paint.setColor(Color.argb(255, 255, 255, 255));
            paint2.setColor(Color.argb( 255, 255, 0 ,0));
            paint2.setColor(Color.argb( 255, 0, 0, 255));
            //set up score on UI
            paint.setTextSize(30);
            canvas.drawText("Score:" + score, 15, 30, paint);

            //Draw the snake
            for (int i = 0; i < snakeLength; i++) {
                canvas.drawRect(snakeXs[i] * blockSize,
                        (snakeYs[i] * blockSize),
                        (snakeXs[i] * blockSize) + blockSize,
                        (snakeYs[i] * blockSize) + blockSize,
                        paint);
            }

            //draw the mouse
            canvas.drawRect(mouseX * blockSize,
                    (mouseY * blockSize),
                    (mouseX * blockSize) + blockSize,
                    (mouseY * blockSize) + blockSize,
                    paint);

            //draw speed boost
            canvas.drawRect(speedX * blockSize,
                    (speedY * blockSize),
                    (speedX * blockSize) + blockSize,
                    (speedY * blockSize) + blockSize,
                    paint2);

            //draw mass boost
            canvas.drawRect(massX * blockSize,
                    (massY *blockSize),
                    (massX * blockSize) + blockSize,
                    (massY * blockSize) + blockSize,
                    paint3);

            // Draw the whole frame
            holder.unlockCanvasAndPost(canvas);
        }
    }
    //Keep the game running
    public boolean checkForUpdate() {
        if(nextFrameTime <= System.currentTimeMillis()){
            nextFrameTime =System.currentTimeMillis() + MILLIS_IN_A_SECOND / FPS;
            return true;
        }

        return false;
    }


    @Override
    public void run() {
        // The check for m_Playing prevents a crash at the start
        while (playing) {

            // Update the amount of times the current FPS is
            if(checkForUpdate()) {
                updateGame();
                drawGame();
            }

        }
    }

    public void pause() {
        playing = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void resume() {
        playing = true;
        thread = new Thread(this);
        thread.start();
    }

}

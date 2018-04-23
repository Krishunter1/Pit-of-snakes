package kristof.pitofsnake;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Random;

class PitView extends SurfaceView implements Runnable {

    // All the code will run separately to the UI
    private Thread thread = null;
    // This variable determines when the game is playing
    // It is declared as volatile because
    // it can be accessed from inside and outside the thread
    private volatile boolean playing;

    // This is what we draw on
    private Canvas canvas;
    // This is required by the Canvas class to do the drawing
    private SurfaceHolder holder;
    // This lets us control colors etc
    private Paint paint;
    private Paint paint2;
    private Paint paint3;


    // This will be a reference to the Activity
    private Context m_context;

    // Sound
    private SoundPool SoundPool;
    private int m_get_mouse_sound = -1;
    private int m_dead_sound = -1;

    // For tracking movement m_Direction
    public enum Direction {UP, RIGHT, DOWN, LEFT}
    // Start by heading to the right
    public static Direction direction = Direction.RIGHT;

    // What is the screen resolution
    private int screenWidth;
    private int screenHeight;

    // Control pausing between updates
    private long nextFrameTime;
    // Update the game 10 times per second
    private  long FPS = 2;
    // There are 1000 milliseconds in a second
    private final long MILLIS_IN_A_SECOND = 1000;
    // We will draw the frame much more often

    // The location in the grid of all the segments
    public static int[] snakeXs;
    public static int[] snakeYs;

    // How long is the snake at the moment
    private int snakeLength;

    // Where is the mouse
    private int mouseX;
    private int mouseY;

    //location of speed upgrade
    private int speedX;
    private int speedY;

    private int massX;
    private int massY;

    private int score;
    public static Context context;


    // The size in pixels of a snake segment
    private int blockSize;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 25;
    private int numBlocksHigh; // determined dynamically

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

        // Initialize the drawing objects
        holder = getHolder();
        paint = new Paint();
        paint2 = new Paint();
        paint3 = new Paint();

        // If you score 200 you are rewarded with a crash achievement!
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

        // And a mouse to eat
        spawnMouse();
        spawnSpeed();
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

    public void spawnMouse() {
        Random random = new Random();
        mouseX = random.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
        mouseY = random.nextInt(numBlocksHigh - 1) + 1;
    }

    private void eatMouse() {
        snakeLength++;
        spawnMouse();
        score = score + 1;
        SoundPool.play(m_get_mouse_sound, 1, 1, 0, 0, 1);
        PitofSnake.vibrator.vibrate(200);
    }
    public void spawnSpeed() {
        Random random2 = new Random();
        speedX = random2.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
        speedY = random2.nextInt(numBlocksHigh - 1) + 1;
    }

    public void spawnMass() {
        Random random3 = new Random();
        massX = random3.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
        massY = random3.nextInt(numBlocksHigh - 1) + 1;
    }

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
        SoundPool.play(m_get_mouse_sound, 1, 1, 0, 0, 1);
        PitofSnake.vibrator.vibrate(200);
    }


    private void eatMass() {
        snakeLength = snakeLength + 3;
        spawnMass();
        score = score + 2;
        SoundPool.play(m_get_mouse_sound, 1, 1, 0, 0, 1);
        PitofSnake.vibrator.vibrate(200);
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

    public void updateGame() {
        if (snakeXs[0] == mouseX && snakeYs[0] == mouseY ) {
            eatMouse();
        }
        moveSnake();
        if (snakeXs[0] == speedX  && snakeYs[0] == speedY ) {
            eatSpeed();
        }
        moveSnake();

        if (snakeXs[0] == massX && snakeYs[0] == massY) {
            eatMass();
        }
        moveSnake();
        if (detectDeath()) {
            //start again
            SoundPool.play(m_dead_sound, 1, 1, 0, 0, 1);

            context = PitofSnake.getContext();
            Intent intent = new Intent(context, ScoreActivity.class);
            intent.putExtra("score",score);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            PitofSnake.vibrator.vibrate(50);
        }
    }

    public void drawGame() {
        if (holder.getSurface().isValid()) {
            canvas = holder.lockCanvas();
            //background colour
            canvas.drawColor(Color.argb(255, 120, 197, 87));
            //Snake colour
            paint.setColor(Color.argb(255, 255, 255, 255));
            paint2.setColor(Color.argb( 255, 255, 0 ,0));
            paint2.setColor(Color.argb( 255, 0, 0, 255));

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

            // Update 10 times a second
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

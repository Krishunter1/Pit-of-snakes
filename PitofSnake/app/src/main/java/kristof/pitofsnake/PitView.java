package kristof.pitofsnake;

import android.content.Context;
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

//the engine of the game, early prelimery stage//
class PitView extends SurfaceView implements Runnable {

    // All the code will run separately to the UI
    private Thread m_Thread = null;
    // This variable determines when the game is playing
    // It is declared as volatile because
    // it can be accessed from inside and outside the thread
    private volatile boolean m_Playing;

    // This is what we draw on
    private Canvas m_Canvas;
    // This is required by the Canvas class to do the drawing
    private SurfaceHolder m_Holder;
    // This lets us control colors etc
    private Paint m_Paint;

    // This will be a reference to the Activity
    private Context m_context;

    // Sound
    private SoundPool m_SoundPool;
    private int m_get_mouse_sound = -1;
    private int m_dead_sound = -1;

    // For tracking movement m_Direction
    public enum Direction {UP, RIGHT, DOWN, LEFT}
    // Start by heading to the right
    private Direction m_Direction = Direction.RIGHT;

    // What is the screen resolution
    private int m_ScreenWidth;
    private int m_ScreenHeight;

    // Control pausing between updates
    private long m_NextFrameTime;
    // Update the game 10 times per second
    private final long FPS = 10;
    // There are 1000 milliseconds in a second
    private final long MILLIS_IN_A_SECOND = 1000;
    // We will draw the frame much more often

    // The current m_Score
    private int m_Score;

    // The location in the grid of all the segments
    private int[] m_SnakeXs;
    private int[] m_SnakeYs;

    // How long is the snake at the moment
    private int m_SnakeLength;

    // Where is the mouse
    private int m_MouseX;
    private int m_MouseY;

    // The size in pixels of a snake segment
    private int m_BlockSize;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int m_NumBlocksHigh; // determined dynamically

    public PitView(Context context, Point size) {
        super(context);

        m_context = context;

        m_ScreenWidth = size.x;
        m_ScreenHeight = size.y;

        //Determine the size of each block/place on the game board
        m_BlockSize = m_ScreenWidth / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        m_NumBlocksHigh = m_ScreenHeight / m_BlockSize;

        // Set the sound up
        loadSound();

        // Initialize the drawing objects
        m_Holder = getHolder();
        m_Paint = new Paint();

        // If you score 200 you are rewarded with a crash achievement!
        m_SnakeXs = new int[200];
        m_SnakeYs = new int[200];

        // Start the game
        startGame();
    }

    public void startGame() {
        // Start with just a head, in the middle of the screen
        m_SnakeLength = 1;
        m_SnakeXs[0] = NUM_BLOCKS_WIDE / 2;
        m_SnakeYs[0] = m_NumBlocksHigh / 2;

        // And a mouse to eat
        spawnMouse();

        // Reset the m_Score
        m_Score = 0;

        // Setup m_NextFrameTime so an update is triggered immediately
        m_NextFrameTime = System.currentTimeMillis();
    }

    public void loadSound() {
        m_SoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            // Create objects of the 2 required classes
            // Use m_Context because this is a reference to the Activity
            AssetManager assetManager = m_context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the two sounds in memory
            descriptor = assetManager.openFd("get_mouse_sound.ogg");
            m_get_mouse_sound = m_SoundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("death_sound.ogg");
            m_dead_sound = m_SoundPool.load(descriptor, 0);

        } catch (IOException e) {
            // Error
        }
    }

    public void spawnMouse() {
        Random random = new Random();
        m_MouseX = random.nextInt(NUM_BLOCKS_WIDE - 1) + 1;
        m_MouseY = random.nextInt(m_NumBlocksHigh - 1) + 1;
    }

    private void eatMouse(){
        m_SnakeLength++;
        spawnMouse();
        m_Score = m_Score + 1;
        m_SoundPool.play(m_get_mouse_sound, 1, 1, 0, 0, 1);
    }

    private void moveSnake(){
        // Start moving the snake
        for (int i = m_SnakeLength; i > 0; i--) {
            // starting from back and moving the pixel foward to the one infont
            //head is not included because head has nothing in front
            m_SnakeXs[i] = m_SnakeXs[i - 1];
            m_SnakeYs[i] = m_SnakeYs[i - 1];
        }

        // Move head to direction chosen
        switch (m_Direction) {
            case UP:
                m_SnakeYs[0]--;
                break;

            case RIGHT:
                m_SnakeXs[0]++;
                break;

            case DOWN:
                m_SnakeYs[0]++;
                break;

            case LEFT:
                m_SnakeXs[0]--;
                break;
        }
    }

    private boolean detectDeath(){
        boolean dead = false;
        //check if a wall has been hit on the edge of the screen
        if (m_SnakeXs[0] == -1) dead = true;
        if (m_SnakeXs[0] >= NUM_BLOCKS_WIDE) dead = true;
        if (m_SnakeYs[0] == -1) dead = true;
        if (m_SnakeYs[0] == m_NumBlocksHigh) dead = true;
        //check if player ran head into the body
        for (int i = m_SnakeLength - 1; i > 0; i--) {
            if ((i > 4) && (m_SnakeXs[0] == m_SnakeXs[i]) && (m_SnakeYs[0] == m_SnakeYs[i])) {
                dead = true;
            }
        }

        return dead;
    }

    public void updateGame() {
        if (m_SnakeXs[0] == m_MouseX && m_SnakeYs[0] == m_MouseY) {
            eatMouse();
        }
        moveSnake();
        if (detectDeath()) {
            //start again
            m_SoundPool.play(m_dead_sound, 1, 1, 0, 0, 1);

            startGame();
        }
    }

    public void drawGame() {
        if (m_Holder.getSurface().isValid()) {
            m_Canvas = m_Holder.lockCanvas();
            //background colour
            m_Canvas.drawColor(Color.argb(255, 120, 197, 87));
            //Snake colour
            m_Paint.setColor(Color.argb(255, 255, 255, 255));

            m_Paint.setTextSize(30);
            m_Canvas.drawText("Score:" + m_Score, 10, 30, m_Paint);

            //Draw the snake
            for (int i = 0; i < m_SnakeLength; i++) {
                m_Canvas.drawRect(m_SnakeXs[i] * m_BlockSize,
                        (m_SnakeYs[i] * m_BlockSize),
                        (m_SnakeXs[i] * m_BlockSize) + m_BlockSize,
                        (m_SnakeYs[i] * m_BlockSize) + m_BlockSize,
                        m_Paint);
            }

            //draw the mouse
            m_Canvas.drawRect(m_MouseX * m_BlockSize,
                    (m_MouseY * m_BlockSize),
                    (m_MouseX * m_BlockSize) + m_BlockSize,
                    (m_MouseY * m_BlockSize) + m_BlockSize,
                    m_Paint);

            // Draw the whole frame
            m_Holder.unlockCanvasAndPost(m_Canvas);
        }
    }

    public boolean checkForUpdate() {
        if(m_NextFrameTime <= System.currentTimeMillis()){
            m_NextFrameTime =System.currentTimeMillis() + MILLIS_IN_A_SECOND / FPS;
            return true;
        }

        return false;
    }


    @Override
    public void run() {
        // The check for m_Playing prevents a crash at the start
        while (m_Playing) {

            // Update 10 times a second
            if(checkForUpdate()) {
                updateGame();
                drawGame();
            }

        }
    }

    public void pause() {
        m_Playing = false;
        try {
            m_Thread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void resume() {
        m_Playing = true;
        m_Thread = new Thread(this);
        m_Thread.start();
    }

}

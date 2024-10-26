package com.example.dreambound;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;



public class GameView extends SurfaceView implements Runnable {
    private Thread gameThread = new Thread(this);
    private boolean isPlaying;
    private Player player;
    private CreatureEntity creatureEntity;
    private SurfaceHolder surfaceHolder;
    private float targetX, targetY;
    private static final float enemiesDetectionRadius = 400.0f;

    private long startTime, loopTime;

    private GameDataManager gameDataManager;
    private boolean isMoving;

    GameEngine gameEngine;

    private CollisionHandler collisionHandler;
    private Obstacle bush1;
    private Tile walkOnMe1;
    private Tile walkOnMe2;

    private ArrayList<CreatureEntity> creatures = new ArrayList<>();
    private ArrayList<GameObject> collidables = new ArrayList<>();
    private ArrayList<GameObject> staticObjects = new ArrayList<>();
    private ArrayList<GameObject> allObjects = new ArrayList<>();

    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        gameDataManager = new GameDataManager();
        createObjects();
        startEngineAndPullData();
        gameDataManager.LoadGameState(context, player, creatures);
        targetX = player.getX();
        targetY = player.getY();
        collisionHandler = new CollisionHandler(context, collidables);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            control();
        }
    }

    private void startEngineAndPullData(){
        if (gameEngine == null) {
            gameEngine = new GameEngine();
            creatures = gameEngine.getCreaturesLoadedIn();
            staticObjects = gameEngine.getStaticObjects();
            allObjects = gameEngine.getAllObjects();
            collidables = gameEngine.getCollisionObjects();
            player = gameEngine.getPlayer();
        }
    }

    private void createObjects() {
        player = new Player(100, 500, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE);
        creatureEntity = new CreatureEntity(2200, 500, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE);
        bush1 = new Obstacle(1000, 500);
        walkOnMe1 = new Tile(1000, 400);
        walkOnMe2 = new Tile(1000, 600);
    }



    private void update() {
        startTime = SystemClock.uptimeMillis();

        player.playerMovement(targetX, targetY);

        for (CreatureEntity entity: creatures) {
            entity.followPlayer(player);
        }

        creatureEntity.followPlayer(player);
        collisionHandler.HandleCollision();
        checkBoundaries();
    }


    private void checkBoundaries() {
        if (player.getX() < 0) {
            player.setX(0);
        } else if (player.getX() + player.getWidth() > getWidth()) {
            player.setX(getWidth() - player.getWidth());
        }

        if (player.getY() < 0) {
            player.setY(0);
        } else if (player.getY() + player.getHeight() > getHeight()) {
            player.setY(getHeight() - player.getHeight());
        }
    }

    public void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.BLACK);


                for (GameObject object : allObjects){
                    object.draw(canvas);
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                targetX = event.getX();
                targetY = event.getY();
                player.setIsMoving(true);
                break;
        }
        return true;
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
        gameDataManager.LoadGameState(getContext(), player, creatures);
        player.setX(player.getX());
        player.setY(player.getY());
    }

    public void pause() {
        try {
            isPlaying = false;
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Interrupted", "Interrupted while pausing");      //cleaned up exception to get more receptive feedback
        }
        gameDataManager.SaveGameState(getContext(), player, creatures);
    }

    private void control() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            Log.e("Interrupted", "Interrupted while sleeping");    //cleaned up exception to get more receptive feedback
        }
    }
}

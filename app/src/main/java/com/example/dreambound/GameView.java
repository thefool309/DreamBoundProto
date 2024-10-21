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

    //Objects/Tiles - Initialize in CreateObjects Method    private Obstacle bush1;
    private Obstacle wall1;
    private Obstacle wall2;
    private Obstacle wall3;
    private Obstacle wall4;
    private Obstacle bush1;
    private Tile walkOnMe1;
    private Tile walkOnMe2;

    //end Object/Tiles
    private ArrayList<CreatureEntity> creatures = new ArrayList<>();
    private ArrayList<GameObject> collidables = new ArrayList<>();
    private ArrayList<GameObject> staticObjects = new ArrayList<>();
    private ArrayList<GameObject> allObjects = new ArrayList<>();
    private ArrayList<Obstacle> obstacles = new ArrayList<>();


    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        //start engine
        if (gameEngine == null) {
            startEngineAndPullData();
        }
        //load game if there is one
        gameDataManager = new GameDataManager();
        gameDataManager.LoadGameState(context, player, creatures);
        targetX = player.getX();
        targetY = player.getY();
        collisionHandler = new CollisionHandler(context, collidables, staticObjects);
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
            gameEngine = new GameEngine(getContext());
            creatures = gameEngine.getCreaturesLoadedIn();
            staticObjects = gameEngine.getStaticObjects();
            allObjects = gameEngine.getAllObjects();
            obstacles = gameEngine.getObstacles();
            collidables = gameEngine.getCollisionObjects();
            player = gameEngine.getPlayer();
        }
    }

    private void update() {
        if (isMoving) {
            startTime = SystemClock.uptimeMillis();
            float playerX = player.getX();
            float playerY = player.getY();
            float deltaX = targetX - playerX;
            float deltaY = targetY - playerY;
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            if (distance > player.getPlayerMovementSpeed()) {
                float stepX = player.getPlayerMovementSpeed() * (deltaX / distance);
                float stepY = player.getPlayerMovementSpeed() * (deltaY / distance);
                player.setX(playerX + stepX);
                player.setY(playerY + stepY);
            } else {
                player.setX(targetX);
                player.setY(targetY);
            }
        }else {
            player.setX(targetX);
            player.setY(targetY);
            isMoving = false;
        }

        for (CreatureEntity entity: creatures) {
            entity.followPlayer(player);
        }
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

                for(GameObject floor : allObjects){
                    floor.draw(canvas);
                }
                for (Obstacle object : obstacles){
                    object.draw(canvas);
                }
                player.draw(canvas);
                for (CreatureEntity creature : creatures){
                    creature.draw(canvas);
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
                isMoving = true;
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
        loopTime = SystemClock.uptimeMillis() - startTime;
        //pausing here to make sure we update the right number of times per second
        if (loopTime < Constants.DELAY) {
            try {
                Thread.sleep(Constants.DELAY - loopTime);
            } catch (InterruptedException e) {
                Log.e("Interrupted", "Interrupted while sleeping");
            }
        }
    }
}

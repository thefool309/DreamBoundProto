package com.example.dreambound;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private static final float playerMovementSpeed = 5.0f;
    private static final float enemiesDetectionRadius = 400.0f;
    private GameDataManager gameDataManager;
    private boolean isMoving;
    private CollisionHandler collisionHandler;
    private Obstacle bush1;
    private Tile walkOnMe1;
    private Tile walkOnMe2;
    private ArrayList<GameObject> objects = new ArrayList<>();
    private ArrayList<GameObject> collidables = new ArrayList<>();

    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        gameDataManager = new GameDataManager();
        player = new Player(100, 500, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE);
        creatureEntity = new CreatureEntity(2200, 500, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE);
        gameDataManager.LoadGameState(getContext(),player,creatureEntity);
        bush1 = new Obstacle(1000, 500);
        walkOnMe1 = new Tile(1000, 400);
        walkOnMe2 = new Tile(1000, 600);

        objects.add(player);
        objects.add(creatureEntity);
        objects.add(bush1);
        objects.add(walkOnMe1);
        objects.add(walkOnMe2);

        for (GameObject object : objects){
            if (!object.getNoCollision()){
                collidables.add(object);
            }
        }
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

    private void update() {
        if (isMoving) {
            float playerX = player.getX();
            float playerY = player.getY();
            float deltaX = targetX - playerX;
            float deltaY = targetY - playerY;
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            if (distance > playerMovementSpeed) {
                float stepX = playerMovementSpeed * (deltaX / distance);
                float stepY = playerMovementSpeed * (deltaY / distance);
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

        // Check if creatureEntity is not null before calling followPlayer()
        if (creatureEntity != null) {
            creatureEntity.followPlayer(player, enemiesDetectionRadius);
        }

        Log.i("Update Method", "Checking for collisions");
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
                for (GameObject object : objects) {
                    if (object != null) {
                        object.draw(canvas);
                    }
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
        gameDataManager.LoadGameState(getContext(), player, creatureEntity);
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
        gameDataManager.SaveGameState(getContext(), player, creatureEntity);
    }

    private void control() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            Log.e("Interrupted", "Interrupted while sleeping");    //cleaned up exception to get more receptive feedback
        }
    }

    public void removeDefeatedEnemy() {
        // Remove the creature entity from the list of game objects and collidables
        if (creatureEntity != null && objects.contains(creatureEntity)) {
            objects.remove(creatureEntity);
            collidables.remove(creatureEntity);
            creatureEntity = null; // Mark as null to prevent future references
        }
    }
}

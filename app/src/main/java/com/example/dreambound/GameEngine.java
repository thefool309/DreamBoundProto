package com.example.dreambound;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;

import android.graphics.Bitmap;


public class GameEngine {
    //context for the Bitmaps
    private Context context;
    private Player player;
    private ArrayList<CreatureEntity> creaturesLoadedIn = new ArrayList();
    private ArrayList<Obstacle> obstacles = new ArrayList();
    private ArrayList<GameObject> collideables = new ArrayList<>();
    private ArrayList<GameObject> allObjects = new ArrayList<>();
    private ArrayList<GameObject> staticObjects = new ArrayList<>();

    public GameEngine(Context context) {
        this.context = context;
        createObjects();
    }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public ArrayList<GameObject> getAllObjects() { return allObjects; }
    public void setAllObjects(ArrayList<GameObject> allObjects) { this.allObjects = allObjects; }

    public ArrayList<GameObject> getStaticObjects() { return staticObjects; }

    public ArrayList<GameObject> getCollisionObjects() { return collideables; }

    public ArrayList<Obstacle> getObstacles() { return obstacles; }


    public ArrayList<CreatureEntity> getCreaturesLoadedIn() { return creaturesLoadedIn; }


    private void createObjects() {
        // here we will parse json data to create objects needed elsewhere in the game

        CreatureEntity enemy1;
        CreatureEntity enemy2;
        Obstacle wall1;
        Obstacle wall2;
        Obstacle wall3;
        Obstacle wall4;
        Obstacle wall5;
        Obstacle wall6;
        Obstacle wall7;
        Obstacle wall8;
        Tile grassTile;


        player = new Player(150, 800, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE);

        //bitmap wall 1 & 3
        Bitmap topAndBottom = BitmapFactory.decodeResource(context.getResources(),R.drawable.wall1_3);
        //Bitmap wall 2 & 4
        Bitmap leftAndRight = BitmapFactory.decodeResource(context.getResources(),R.drawable.walls2_4);
        //iwallX = image wallX ie; Bitmap iWall5 is image for wall 5
        Bitmap iWall5 = BitmapFactory.decodeResource(context.getResources(),R.drawable.wall_5);
        Bitmap iWall6 = BitmapFactory.decodeResource(context.getResources(),R.drawable.wall_6);
        Bitmap iWall7 = BitmapFactory.decodeResource(context.getResources(),R.drawable.wall_7);
        Bitmap iWall8 = BitmapFactory.decodeResource(context.getResources(),R.drawable.wall_8);

        enemy1 = new CreatureEntity(550, 650, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE);
        enemy2 = new CreatureEntity(2200, 800, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE);
        wall1 = new Obstacle(0, 0, 2400, 40,topAndBottom);
        wall2 = new Obstacle (2368,0, 32 , 1080,leftAndRight);
        wall3 = new Obstacle(0,922, 2400, 32,topAndBottom);
        wall4 = new Obstacle(0,0, 40, 1080,leftAndRight);
        wall5 = new Obstacle(250,42,32 , 150,iWall5);
        wall6 = new Obstacle(250,400,32 , 525,iWall6);
        wall7 = new Obstacle(600,42,80 , 550,iWall7);
        wall8 = new Obstacle(1550,200,550 , 550,iWall8);

        //Bitmap for GRASS texture
        Bitmap grass = BitmapFactory.decodeResource(context.getResources(), R.drawable._grass);
        grassTile = new Tile(0, 0,2400,1000, grass);


        allObjects.add(enemy1);
        allObjects.add(enemy2);

        creaturesLoadedIn.add(enemy1);
        creaturesLoadedIn.add(enemy2);

        collideables.add(enemy1);
        collideables.add(enemy2);
        collideables.add(player);

        obstacles.add(wall1);
        allObjects.add(wall1);
        obstacles.add(wall2);
        allObjects.add(wall2);
        obstacles.add(wall3);
        allObjects.add(wall3);
        obstacles.add(wall4);
        allObjects.add(wall4);
        obstacles.add(wall5);
        allObjects.add(wall5);
        obstacles.add(wall6);
        allObjects.add(wall6);
        obstacles.add(wall7);
        allObjects.add(wall7);
        obstacles.add(wall8);
        allObjects.add(wall8);

        allObjects.add(grassTile);
        allObjects.add(player);

        upcastObjectsIntoArrays();


    }
    private void upcastObjectsIntoArrays() {
        for (GameObject object : allObjects){
            if (object.getHasCollision() && object.getCanMove()){
                collideables.add(object);
            }
            else if (object.getHasCollision() && !object.getCanMove()){
                staticObjects.add(object);
            }
        }
    }

}

package com.example.dreambound;

import java.util.ArrayList;
import davidiserovich.TMXLoader.*;
import android.graphics.Bitmap;

public class GameEngine {
    private Player player;
    private ArrayList<CreatureEntity> creaturesLoadedIn = new ArrayList();
    private ArrayList<Obstacle> obstacles = new ArrayList();
    private ArrayList<GameObject> collideables = new ArrayList<>();
    private ArrayList<GameObject> allObjects = new ArrayList<>();
    private ArrayList<GameObject> staticObjects = new ArrayList<>();

    public GameEngine() {
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
        Obstacle bush1;
        Tile walkOnMe1;
        Tile walkOnMe2;


        player = new Player(100, 500, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE);
        enemy1 = new CreatureEntity(2200, 500, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE);
        enemy2 = new CreatureEntity(2100, 400, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE);
        bush1 = new Obstacle(1000, 500);
        walkOnMe1 = new Tile(1000, 400);
        walkOnMe2 = new Tile(1000, 600);

        allObjects.add(player);
        allObjects.add(enemy1);
        allObjects.add(enemy2);

        creaturesLoadedIn.add(enemy1);
        creaturesLoadedIn.add(enemy2);

        allObjects.add(bush1);
        obstacles.add(bush1);

        allObjects.add(walkOnMe1);
        allObjects.add(walkOnMe2);

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
}

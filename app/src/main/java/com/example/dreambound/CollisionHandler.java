package com.example.dreambound;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;

public class CollisionHandler {

    Context context;
    float windowWidth;
    float windowHeight;
    float gridWidth;
    float gridHeight;
    private ArrayList<GameObject> objects;
    private CollisionListener listener;
    GameObject creatureToRemove = null;


    interface CollisionListener {
        void onCollisionWithCreature();
    }

    CollisionHandler(Context context, ArrayList<GameObject> objects, GameObject creatureToRemove) {
        this.context = context;
        this.objects = objects;
        this.creatureToRemove = creatureToRemove;
        if (context instanceof CollisionListener) {
            this.listener = (CollisionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement CollisionListener");
        }

        windowWidth = context.getResources().getDisplayMetrics().widthPixels;
        windowHeight = context.getResources().getDisplayMetrics().heightPixels;
        gridWidth = (int) (windowWidth / Constants.CHUNK_SIZE);
        gridHeight = (int) (windowHeight / Constants.CHUNK_SIZE);
    }
    //generic check between two objects for collision
    private boolean checkCollision(GameObject player, GameObject target) {
        return player.getX() < target.getX() + target.getWidth() &&
                player.getX() + player.getWidth() > target.getX() &&
                player.getY() < target.getY() + target.getHeight() &&
                player.getY() + player.getHeight() > target.getY();
    }

    void HandleCollision() {
        for (GameObject object : collideables) {
            for (GameObject target : objects) {
                if (!target.getHasCollision() || !object.getHasCollision()) {
                    continue;
                }
                else if (object.checkCollision(target)) {
                    if(object.getIsPlayer() || target.getIsPlayer()) {
                        collisionWithObjectEvent(object, target);

                    }
                    else {
                        collisionFromCreaturesToObjectsEvent();
                    }

                }
            }
        }
        for (GameObject object : collideables) {
            for (GameObject target : collideables) {
                if (target == object) {
                    continue;
                }
                if (!target.getHasCollision() || !object.getHasCollision()) {
                    continue;
                }
                else if (object.checkCollision(target)) {
                    if (object.getIsPlayer() || target.getIsPlayer()) {
                        collisionWithCreatureEntitiesEvent();
                    }
                    else {
                        collisionFromCreaturesToObjectsEvent();
                    }
                }
            }
        }
    }



    private void collisionWithObjectEvent() {
        Log.i("Collision Detected", "Collision with object event");
    }

    private void collisionWithCreatureEntitiesEvent() {
        Log.i("Collision Detected", "Collision with Creature event");
        if (listener != null) {
            listener.onCollisionWithCreature();
        }
    }

    private void collisionFromCreaturesToObjectsEvent() {
        Log.i("Collision Detected", "Collision From Creature To Objects event");
    }
}

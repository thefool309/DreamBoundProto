package com.example.dreambound;

import android.content.Context;
import android.util.Log;


import java.util.ArrayList;

public class CollisionHandler {

    Context context;
    int windowHeight;
    int windowWidth;
    int gridHeight;
    int gridWidth;

    //TODO: Data structure for collide-able entities
        ArrayList<GameObject> collideables;
    //TODO: Data structure for static objects
        ArrayList<GameObject> objects;
    //TODO: Constructor generate our tile maps
    CollisionHandler(Context context, ArrayList<GameObject> collideables, ArrayList<GameObject> staticObjects) {
        this.context = context;
        this.objects = staticObjects;
        this.collideables = collideables;

        gridHeight = (int) (windowHeight / Constants.CHUNK_SIZE);
        gridWidth = (int) (windowWidth / Constants.CHUNK_SIZE);
        windowHeight = context.getResources().getDisplayMetrics().heightPixels;
        windowWidth = context.getResources().getDisplayMetrics().widthPixels;
    }
    //generic check between two objects for collision
    private boolean checkCollision(GameObject object, GameObject target) {
        return object.getX() <= target.getX() + target.getWidth() &&
                object.getX() + object.getWidth() >= target.getX() &&
                object.getY() <= target.getY() + target.getHeight() &&
                object.getY() + object.getHeight() >= target.getY();
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
                        collisionFromCreaturesToNonPlayer();
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
                        collisionFromCreaturesToNonPlayer();
                    }
                }
            }
        }
    }



    private void collisionWithObjectEvent(GameObject object, GameObject target) {
        //Calculate the distance vector between centers

        final float BUFFER = 0.01f;

        // First, handle the horizontal (X-axis) movement
        float newX = object.getX() + object.deltaX; // Calculate the new X position
        object.setX(newX);  // Temporarily set the new X position

        // Check for X-axis collision and correct if necessary
        if (object.getIsColliding()) {
            if (object.deltaX > 0) {
                object.setX(target.getX() - object.getWidth() - BUFFER);  // Snap object to the left of target
            } else if (object.deltaX < 0) {
                object.setX(target.getX() + target.getWidth() + BUFFER);  // Snap object to the right of target
            }
        }

        //Handle the vertical (Y-axis) movement
        float newY = object.getY() + object.deltaY; // Calculate the new Y position
        object.setY(newY);

        // Check for Y-axis collision and correct if necessary
        if (object.getIsColliding()) {
            if (object.deltaY > 0) {
                object.setY(target.getY() - object.getHeight() - BUFFER);  // Snap object to the bottom of target
            } else if (object.deltaY < 0) {
                object.setY(target.getY() + target.getHeight() + BUFFER);  // Snap object to the top of target
            }
        }

        Log.i("Player Collision Detected", "Collision with object event");
        Log.i("Collision Info", "Colliding on X: " + object.deltaX + ", Y: " + object.deltaY);
        Log.i("Position Adjustment", "Player X: " + object.getX() + ", Player Y: " + object.getY());

    }

    private void collisionWithCreatureEntitiesEvent() {
        //TODO: Implement fragment

        //TODO:Implement logic to receive boolean from intent or fragment

        //TODO:Implement Game Over logic and screen.


        Log.i("Collision Detected", "Collision with Creature event");
    }

    private void collisionFromCreaturesToNonPlayer() {
        Log.i("CreatureEntity Collision Detected", "Collision From Creature To Non-Player event");
    }


}

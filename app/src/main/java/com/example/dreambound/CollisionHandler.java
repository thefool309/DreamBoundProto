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
    CollisionListener listener;


    GameObject creatureToRemove = null;
    //TODO: Data structure for collide-able entities
    ArrayList<GameObject> collidables = new ArrayList<>();
    //TODO: Data structure for static objects
    ArrayList<GameObject> objects;

    CollisionHandler(Context context, ArrayList<GameObject> collidables, ArrayList<GameObject> staticObjects) {
        this.context = context;
        this.objects = staticObjects;
        this.collidables = collidables;

        gridHeight = (int) (windowHeight / Constants.CHUNK_SIZE);
        gridWidth = (int) (windowWidth / Constants.CHUNK_SIZE);
        windowHeight = context.getResources().getDisplayMetrics().heightPixels;
        windowWidth = context.getResources().getDisplayMetrics().widthPixels;
    }


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
        for (GameObject object : collidables) {
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
        for (GameObject object : collidables) {
            for (GameObject target : collidables) {
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



    private void collisionWithObjectEvent(GameObject object, GameObject target) {
        // Define edges for object and target bounding boxes
        float objectLeft = object.getX();
        float objectRight = objectLeft + object.getWidth();
        float objectTop = object.getY();
        float objectBottom = objectTop + object.getHeight();

        float targetLeft = target.getX();
        float targetRight = targetLeft + target.getWidth();
        float targetTop = target.getY();
        float targetBottom = targetTop + target.getHeight();

        // Calculate overlap only if there's a collision along both axes
        if (objectRight > targetLeft && objectLeft < targetRight &&
                objectBottom > targetTop && objectTop < targetBottom) {

            // Determine overlap on X axis
            float xOverlap = Math.min(objectRight - targetLeft, targetRight - objectLeft);

            // Determine overlap on Y axis
            float yOverlap = Math.min(objectBottom - targetTop, targetBottom - objectTop);

            // Resolve collision by moving object the minimal distance out of overlap
            if (xOverlap < yOverlap) {
                // Adjust in X direction
                if (object.getX() < target.getX()) {
                    object.setX(targetLeft - object.getWidth() - 0.1f); // Add buffer to prevent re-collision
                } else {
                    object.setX(targetRight + 0.1f); // Add buffer
                }
            } else {
                // Adjust in Y direction
                if (object.getY() < target.getY()) {
                    object.setY(targetTop - object.getHeight() - 0.1f); // Add buffer
                } else {
                    object.setY(targetBottom + 0.1f); // Add buffer
                }
            }

            Log.i("Collision Detected", "Adjusted position to resolve collision");
        }
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

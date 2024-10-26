package com.example.dreambound;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.io.Serializable;
import java.util.ArrayList;



enum BoxTag {
    PaintBox(31),
    HitBox(32);

    private final int ID;

    BoxTag(int id) {
        ID = id;
    }

    public int getID() {
        return ID;
    }
}

public class GameObject implements Serializable {


    //nested classes
    public static class Point implements Serializable {
        public float x;
        public float y;
        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Line implements Serializable {
        public Point pointA;
        public Point pointB;

        public Line(Point pointA, Point pointB) {
            this.pointA = pointA;
            this.pointB = pointB;
        }
        public Line(float x1, float y1, float x2, float y2) {
            this.pointA = new Point(x1, y1);
            this.pointB = new Point(x2, y2);
        }

        public Point getStartPoint(){
            return pointA;
        }
        public Point getEndPoint() {
            return pointB;
        }
    }

    public static class RectangleBox implements Serializable {
        public Point position;
        public float width, height;

        ArrayList<Point> vertices = new ArrayList<>();

        public RectangleBox(float x, float y, float width, float height) {
            position = new Point(x, y);
            this.width = width;
            this.height = height;
            generateVertices();
        }

        private void generateVertices(){
            vertices.add(new Point(position.x, position.y));
            vertices.add(new Point(position.x + width, position.y));
            vertices.add(new Point(position.x + width, position.y + height));
            vertices.add(new Point(position.x, position.y + height));
        }
    }
    //member variables
    RectangleBox box;
    transient Paint paint;

    public void initPaint(int color) {
        paint = new Paint();
        paint.setColor(color);
    }

    public float velocity = 0.00f;
    public float currentVelocityX = 0.0f;
    public float currentVelocityY = 0.0f;
    public float deltaX = 0.0f;
    public float deltaY = 0.0f;

    public boolean isTile = false;
    public boolean hasCollision = false;
    public boolean isPlayer = false;
    public boolean isCharacter = false;
    public boolean isCreature = false;
    public boolean isNPC = false;
    public boolean canMove = false;
    public boolean isMoving = false;
    public boolean isColliding = false;

    GameObject(float x, float y, float width, float height) {
        box = new RectangleBox(x, y, width, height);
        initPaint(Color.WHITE); //will be switched with a default of transparent
        setVelocity(0.0f);

    }

    //accessors and mutators
    public float getX() { return box.position.x; }

    public float getY() { return box.position.y; }

    public void setX(float x) { box.position.x = x; }

    public void setY(float y) {
        box.position.y = y;
    }

    public float getDeltaX() { return deltaX; }

    public float getDeltaY() { return deltaY; }

    public void setDeltaX(float deltaX) { this.deltaX = deltaX; }

    public void setDeltaY(float deltaY) { this.deltaY = deltaY; }

    public boolean getIsCharacter() { return isCharacter; }

    public void setIsCharacter(boolean character) { isCharacter = character; }

    public boolean getIsNPC() { return isNPC; }

    public void setIsNPC(boolean npc) { isNPC = npc; }

    public boolean getIsTile() { return isTile; }

    public void setIsTile(boolean tile) { isTile = tile; }

    public void setCanMove() { canMove = true; }

    public void setCanMove(boolean canMove) { this.canMove = canMove; }

    public boolean getCanMove() { return canMove; }

    public boolean getHasCollision() { return hasCollision; }

    public void setHasCollision() { setHasCollision(true); }

    public void setHasCollision(boolean hasCollision) { this.hasCollision = hasCollision; }

    public boolean getIsPlayer() { return isPlayer; }

    public void setIsPlayer(boolean player) { isPlayer = player; }

    public boolean getIsCreature() { return isCreature; }

    public void setIsCreature(boolean creature) { isCreature = creature; }

    public void setIsMoving(boolean moving) { isMoving = moving; }

    public boolean getIsMoving() { return isMoving; }

    public RectangleBox getBox() {
        return box;
    }

    public float getWidth() {
        return box.width;
    }

    public float getHeight() {
        return box.height;
    }

    public float getVelocity() { return velocity; }

    public void setVelocity(float velocity) { this.velocity = velocity; }

    public boolean getIsColliding() { return isColliding; }

    public void setIsColliding() { isColliding = true; }

    public void setIsColliding(boolean colliding) { isColliding = colliding; }

    public boolean checkCollision(GameObject target) {
        isColliding = getX() < target.getX() + target.getWidth() &&
                getX() + getWidth() > target.getX() &&
                getY() < target.getY() + target.getHeight() &&
                getY() + getHeight() > target.getY();
        if (isColliding){
            target.setIsColliding();
        }

        return isColliding;
    }

    //change dimensions function
    public void changeDimensions(int width, int height) {
        box.width = width;
        box.height = height;
    }

    //set position functions
    public void setPosition(float _x, float _y) {
        box.position.x = _x - box.width / 2;
        box.position.y = _y - box.height / 2;
    }

    public void setPosition(Point point){
        box.position = new Point(point.x, point.y);
    }
    //draw functions
    public void draw(Canvas canvas) {
        canvas.drawRect(box.position.x, box.position.y, box.position.x + box.width, box.position.y + box.height, paint);
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRect(box.position.x, box.position.y, box.position.x + box.width, box.position.y + box.height, paint);
    }
}



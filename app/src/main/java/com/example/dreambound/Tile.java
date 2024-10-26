package com.example.dreambound;

import android.graphics.*;

public class Tile extends GameObject {
    public Bitmap image;

    public Tile(float x, float y) {
        super(x, y, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE);
        setIsTile(true);
        setHasCollision(false);
        paint = new Paint();
        paint.setColor(Color.GRAY);
    }

    // Constructor with variable size and Bitmap.
    public Tile(float x, float y, float width, float height, Bitmap image) {
        super(x, y, width, height);
        setIsTile(true);
        setHasCollision(false);
        paint = new Paint();
        paint.setColor(Color.GRAY);
        this.image = image;

    }

    @Override
    public void draw(Canvas canvas) {
        if (image != null) {
            canvas.drawBitmap(image, getX(), getY(), paint);
        } else {
            canvas.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), paint);
        }
    }
}

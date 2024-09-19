package com.example.dreambound;

import android.graphics.Canvas;
import android.graphics.Color;


public class Player extends Character{

    //constructor
    public Player(float _x, float _y, float _playerWidth, float _playerHeight) {
        super(_x, _y, _playerWidth, _playerHeight);
        setIsPlayer(true);
        setNoCollision(false);
        paint.setColor(Color.RED);
    }
    //draw
    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), paint);
    }
}
package com.example.dreambound;

import android.graphics.Canvas;
import android.graphics.Color;
import java.io.Serializable;

public class Player extends gameCharacter implements Serializable {



    //constructor
    public Player(float _x, float _y, float _playerWidth, float _playerHeight) {
        super(_x, _y, _playerWidth, _playerHeight);
        setIsPlayer(true);
        initPaint(Color.RED);
        setCanMove();
        setVelocity(5.00f);

    }
    //accessors and mutators


    public void playerMovement(float targetX, float targetY) {
        if (getIsMoving()) {
            float playerX = getX();
            float playerY = getY();
            deltaX = targetX - playerX;
            deltaY = targetY - playerY;
            float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            if (distance > getVelocity()) {
                currentVelocityX = getVelocity() * (deltaX / distance);
                currentVelocityY = getVelocity() * (deltaY / distance);

                setX(playerX + currentVelocityX);
                setY(playerY + currentVelocityY);
            } else {
                setX(targetX);
                setY(targetY);
            }
        }else {
            setX(targetX);
            setY(targetY);
            isMoving = false;
        }
    }

    //draw
    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(box.position.x, box.position.y, box.position.x + box.width, box.position.y + box.height, paint);
    }
}
package com.example.dreambound;

import android.graphics.Canvas;
import android.graphics.Color;
import java.io.Serializable;

public class CreatureEntity extends gameCharacter implements Serializable {
    private  float creatureEntitySpeed = 3.0f;
    private float creatureEntityDetectionRadius = 400.0f;
    //constructor
    public CreatureEntity(float x, float y, float _enemiesWidth, float _enemiesHeight)  {
        super(x, y, _enemiesWidth, _enemiesHeight);
        setIsCreature(true);
        setHasCollision(true);
        setCanMove(true);
        setVelocity(3.0f);
        initPaint(Color.BLUE);
    }

    //draw
    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(box.position.x, box.position.y, box.position.x + box.width, box.position.y + box.height, paint);
    }

    //accessors and mutators

    public float getCreatureEntityDetectionRadius() { return creatureEntityDetectionRadius; }

    public void setCreatureEntityDetectionRadius(float creatureEntityDetectionRadius) { this.creatureEntityDetectionRadius = creatureEntityDetectionRadius; }

    //enemy movement ai
    public void followPlayer(Player player) {
        float deltaX = player.getX() - this.getX();
        float deltaY = player.getY() - this.getY();
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance < creatureEntityDetectionRadius) {
            float stepX = creatureEntitySpeed * (deltaX / distance);
            float stepY = creatureEntitySpeed * (deltaY / distance);
            this.setX(this.getX() + stepX);
            this.setY(this.getY() + stepY);
        }
    }
}
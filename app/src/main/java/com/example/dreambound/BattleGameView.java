package com.example.dreambound;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class BattleGameView extends View {
    private CreatureEntity[] creatures;
    private Player[] players;
    private OnEnemySelectedListener enemySelectedListener;

    public BattleGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public void setCreatures(CreatureEntity[] creatures) {
        this.creatures = creatures;
    }

    public void setOnEnemySelectedListener(OnEnemySelectedListener listener) {
        this.enemySelectedListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (creatures != null) {
            for (CreatureEntity creature : creatures) {
                if (creature.isAlive()) {
                    creature.draw(canvas);
                }
            }
        }
        if (players != null) {
            for (Player player : players) {
                if (player.isAlive()) {
                    player.draw(canvas);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && creatures != null) {
            float x = event.getX();
            float y = event.getY();

            // Check if the touch is inside any enemy's area
            for (int i = 0; i < creatures.length; i++) {
                CreatureEntity creature = creatures[i];
                if (creature.isAlive() && isTouchInsideObject(x, y, creature)) {
                    if (enemySelectedListener != null) {
                        enemySelectedListener.onEnemySelected(i);
                    }
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean isTouchInsideObject(float x, float y, CreatureEntity creature) {
        return x >= creature.getX() && x <= creature.getX() + creature.getWidth() &&
                y >= creature.getY() && y <= creature.getY() + creature.getHeight();
    }

    public interface OnEnemySelectedListener {
        void onEnemySelected(int enemyIndex);
    }
}

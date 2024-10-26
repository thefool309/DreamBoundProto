package com.example.dreambound;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class BattleGameView extends View {
    private CreatureEntity[] creatures;
    private Player[] players;

    public BattleGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public void setCreatures(CreatureEntity[] creatures) {
        this.creatures = creatures;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (CreatureEntity creature : creatures) {
            creature.draw(canvas);
        }
        for (Player player : players) {
            player.draw(canvas);
        }
    }
}

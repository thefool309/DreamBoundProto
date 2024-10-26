package com.example.dreambound;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import java.io.*;
import java.util.ArrayList;

public class GameDataManager {
    private static final String FILE_NAME = "gameState.dat";


    public void SaveGameState(Context context, Player player, ArrayList<CreatureEntity> entities) {
        try {
            Log.d("GameDataManager", "Saving game state...");
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(player);
            oos.writeObject(entities);

            oos.close();
            fos.close();
            Log.d("GameDataManager", "Saved game state!");
        } catch (Exception e) {
            Log.d("GameDataManager", "Saving game state failed!" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void LoadGameState(Context context, Player player, ArrayList<CreatureEntity> entities) {
        File file = context.getFileStreamPath(FILE_NAME);
        if (file.exists()) {
            try {
                Log.d("GameDataManager", "Loading game state...");
                FileInputStream fis = context.openFileInput(FILE_NAME);
                ObjectInputStream ois = new ObjectInputStream(fis);

                Player savedPlayer = (Player) ois.readObject();
                ArrayList<CreatureEntity> savedEntities;
                savedEntities = (ArrayList<CreatureEntity>) ois.readObject();

                player.setPosition(savedPlayer.getX(), savedPlayer.getY());
                player.initPaint(Color.RED);
                for (int i = 0; i < savedEntities.size(); i++) {
                    entities.get(i).setPosition(savedEntities.get(i).getX(), savedEntities.get(i).getY());
                    entities.get(i).initPaint(Color.BLUE);
                }
                ois.close();
                fis.close();
                Log.d("GameDataManager", "Loaded game state!");
            } catch (Exception e) {
                Log.d("GameDataManager", "Loading game state failed!" + e.getMessage());
            }
        }else {
            Log.d("GameDataManager", "No game state found!");
        }
    }
}

package com.example.dreambound;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.util.LinkedList;
import java.util.List;

public class BattleFragment extends Fragment {
    private Player[] players;
    private CreatureEntity[] enemies;
    private int currentTurnIndex = 0;
    private BattleGameView battleGameView;
    private Button attackButton;
    private TextView battleLogTextView;
    private List<String> battleLog = new LinkedList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battle, container, false);
        battleGameView = view.findViewById(R.id.battleGameView);
        battleLogTextView = view.findViewById(R.id.battleLogTextView);

        attackButton = view.findViewById(R.id.buttonAttack);
        attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerAttack();
            }
        });
        players = new Player[] {new Player(100, 600, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE)
                , new Player(100, 700, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE)
                , new Player(100, 800, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE)};
        enemies = new CreatureEntity[] {new CreatureEntity(2200, 600, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE)
                , new CreatureEntity(2200, 700, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE)
                , new CreatureEntity(2200, 800, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE)};
        if (battleGameView != null) {
            battleGameView.setCreatures(enemies);
            battleGameView.setPlayers(players);
        }else {
            Log.d("BattleFragment", "BattleGameView is null");
        }

        startBattle();

        return view;
    }

    private void startBattle() {
        updateTurn();
    }

    private void updateTurn() {
        // Check if the battle is over
        if (allPlayersDefeated()) {
            logBattleMessage("All players are defeated. Game Over.");
            return; // Stop further turns
        }

        if (allEnemiesDefeated()) {
            logBattleMessage("All enemies are defeated. You win!");
            return; // Stop further turns
        }

        // Check if it's a player's or an enemy's turn
        if (currentTurnIndex < players.length) {
            // It's a player's turn
            if (players[currentTurnIndex].isAlive()) {
                logBattleMessage("Player " + (currentTurnIndex + 1) + "'s turn.");
                enableButtons(); // Enable buttons for player input
            } else {
                logBattleMessage("Player " + (currentTurnIndex + 1) + " is defeated!");
                // Skip to the next turn if the player is defeated
                nextTurn();
            }
        } else {
            // It's an enemy's turn
            int enemyIndex = currentTurnIndex - players.length; // Adjust the index for the enemy
            if (enemies[enemyIndex].isAlive()) {
                logBattleMessage("Enemy " + (enemyIndex + 1) + "'s turn.");
                enemyAttack(); // Enemy automatically attacks

            } else {
                logBattleMessage("Enemy " + (enemyIndex + 1) + " is defeated!");
                // Skip to the next turn if the enemy is defeated
                nextTurn();
            }
        }
    }

    private boolean allEnemiesDefeated() {
        for (CreatureEntity enemy : enemies) {
            if (enemy.isAlive()) {
                return false;
            }
        }
        return true;
    }

    private boolean allPlayersDefeated() {
        for (Player player : players) {
            if (player.isAlive()) {
                return false;
            }
        }
        return true;
    }

    private void nextTurn() {
        // Move to the next turn
        currentTurnIndex = (currentTurnIndex + 1) % (players.length + enemies.length);
        // Call updateTurn() to continue the battle loop
        updateTurn();
    }


    private void playerAttack() {
        // Ensure currentTurnIndex is within bounds
        if (currentTurnIndex >= players.length + enemies.length) {
            return; // Index out of bounds, so exit the method
        }

        if (currentTurnIndex >= players.length) {
            logBattleMessage("Invalid turn index for player.");
            return; // Return early if it's not the player's turn
        }

        // Attack an enemy
        int enemyIndex = currentTurnIndex % enemies.length; // Get the correct enemy index

        if (enemyIndex < enemies.length && players[currentTurnIndex].isAlive()) {
            CreatureEntity target = enemies[enemyIndex];
            target.takeDamage(players[currentTurnIndex].stats.Attack);
            logBattleMessage("Player " + (currentTurnIndex + 1) + " attacked! Enemy " + (enemyIndex + 1) + " has " + target.getHealth() + " health left.");

            if (!target.isAlive()) {
                logBattleMessage("Enemy " + (enemyIndex + 1) + " is defeated!");
            }
        }

        disableButtons(); // After attacking, disable buttons
        nextTurn();
    }

    private void enemyAttack() {
        int enemyIndex = currentTurnIndex - players.length;

        if (enemyIndex < enemies.length && enemies[enemyIndex].isAlive()) {
            Player target = players[currentTurnIndex % players.length];
            target.takeDamage(enemies[enemyIndex].stats.Attack);
            logBattleMessage("Enemy " + (enemyIndex + 1) + " attacked! Player " + ((currentTurnIndex % players.length) + 1) + " has " + target.getHealth() + " health left.");

            if (!target.isAlive()) {
                logBattleMessage("Player " + (currentTurnIndex + 1) + " is defeated!");
            }
        } else {
            logBattleMessage("Invalid enemy index.");
        }

        nextTurn();
    }

    private void enableButtons() {
        attackButton.setEnabled(true);
    }

    private void disableButtons() {
        attackButton.setEnabled(false);
    }

    private void logBattleMessage(String message) {
        // Add the new message to the log
        battleLog.add(message);

        // If the log has more than 10 messages, remove the oldest one
        if (battleLog.size() > 10) {
            battleLog.remove(0); // Remove the first (oldest) message
        }

        // Build the string to display the last 10 messages
        StringBuilder logText = new StringBuilder();
        for (String logEntry : battleLog) {
            logText.append(logEntry).append("\n");
        }

        // Set the text to the TextView
        battleLogTextView.setText(logText.toString().trim());
    }
}
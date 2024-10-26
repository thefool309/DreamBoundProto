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
import java.util.Queue;
import java.util.Random;

public class BattleFragment extends Fragment implements BattleGameView.OnEnemySelectedListener {
    private Player[] players;
    private CreatureEntity[] enemies;
    private int currentTurnIndex = 0;
    private BattleGameView battleGameView;
    private Button attackButton, nextbutton;
    private TextView battleLogTextView;
    private Queue<String> battleLog = new LinkedList<>();
    private boolean selectingTarget = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battle, container, false);
        battleGameView = view.findViewById(R.id.battleGameView);
        battleLogTextView = view.findViewById(R.id.battleLogTextView);
        attackButton = view.findViewById(R.id.buttonAttack);
        nextbutton = view.findViewById(R.id.buttonNextLog);

        // Set up players and enemies
        players = new Player[] {new Player(100, 600, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE)
                , new Player(100, 700, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE)
                , new Player(100, 800, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE)
        };
        enemies = new CreatureEntity[] {new CreatureEntity(2200, 600, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE)
                , new CreatureEntity(2200, 700, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE)
                , new CreatureEntity(2200, 800, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE)
        };

        // Set up the game view
        if (battleGameView != null) {
            battleGameView.setCreatures(enemies);
            battleGameView.setPlayers(players);
            battleGameView.setOnEnemySelectedListener(this);
        } else {
            Log.d("BattleFragment", "BattleGameView is null");
        }

        // Set up the next button listener for the log
        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNextMessage();
                battleGameView.invalidate();
            }
        });

        // Set up the attack button listener
        attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNextMessage();
                enableTargetSelection();
            }
        });

        // Add initial message to the queue
        battleLog.add("FIGHT!!");
        displayNextMessage(); // Show the first message and wait for input
        disableButtons(); // Disable attack button initially to show first log message

        startBattle();

        return view;
    }


    public void onEnemySelected(int enemyIndex) {
        if (selectingTarget) {
            playerAttack(enemyIndex);
            disableTargetSelection();
            displayNextMessage();
        }
    }

    private void enableTargetSelection() {
        logBattleMessage("Select your target");
        displayNextMessage();
        selectingTarget = true;
        disableButtons(); // Disable attack button while selecting
    }

    private void disableTargetSelection() {
        selectingTarget = false;
    }

    private void displayNextMessage() {
        if (!battleLog.isEmpty()) {
            // Display the next message in the queue
            String message = battleLog.poll();
            battleLogTextView.setText(message);
        }

        // Disable attack button until all messages are displayed
        if (!battleLog.isEmpty()) {
            disableButtons(); // Keep attack button disabled while there is messages to be displayed
        } else {
            // If no more messages, enable the attack button for next action
            enableButtons();
            nextbutton.setEnabled(false); // Disable next button if no more messages are in the queue

            // Check if battle is over and all enemies are defeated
            if (allEnemiesDefeated()) {
                endBattle(); // End battle and transition back to GameFragment
            }
        }
    }

    private void startBattle() {
        updateTurn();
    }

    private void updateTurn() {
        // Check if the battle is over
        if (allPlayersDefeated()) {
            logBattleMessage("All players are defeated. Game Over.");
            disableButtons();
            logBattleMessage("Filler for endBattle transition");
            return; // Stop further turns
        }

        if (allEnemiesDefeated()) {
            logBattleMessage("All enemies are defeated. You win!");
            disableButtons();
            logBattleMessage("Filler for endBattle transition");
            return; // Stop further turns
        }

        // Check if it's a player's or an enemy's turn
        if (currentTurnIndex < players.length) {
            // It's a player's turn
            if (players[currentTurnIndex].isAlive()) {
                logBattleMessage("Player " + (currentTurnIndex + 1) + "'s turn.");
            } else {
                nextTurn();// Skip to the next turn if the player is defeated
            }
        } else {
            // It's an enemy's turn
            int enemyIndex = currentTurnIndex - players.length; // Adjust the index for the enemy
            if (enemies[enemyIndex].isAlive()) {
                logBattleMessage("Enemy " + (enemyIndex + 1) + "'s turn.");
                enemyAttack(); // Enemy automatically attacks
            } else {
                nextTurn();// Skip to the next turn if the enemy is defeated
            }
        }
    }

    private boolean allEnemiesDefeated() {
        // Check if enemies array is null
        if (enemies == null) {
            return true; // If enemies array is null, consider all enemies defeated
        }

        for (CreatureEntity enemy : enemies) {
            if (enemy != null && enemy.isAlive()) {
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


    private void playerAttack(int enemyIndex) {
        if (enemyIndex < 0 || enemyIndex >= enemies.length) {
            logBattleMessage("Invalid enemy target!");
            return; // Invalid index, exit method
        }

        CreatureEntity target = enemies[enemyIndex];
        if (target != null && target.isAlive()) {
            target.takeDamage(players[currentTurnIndex].stats.Attack);
            logBattleMessage("Player " + (currentTurnIndex + 1) + " attacked! Enemy " + (enemyIndex + 1) + " has " + target.getHealth() + " health left.");

            if (!target.isAlive()) {
                logBattleMessage("Enemy " + (enemyIndex + 1) + " is defeated!");
            }
        }

        nextTurn();
    }

    private void enemyAttack() {
        int enemyIndex = currentTurnIndex - players.length;

        if (enemyIndex < enemies.length && enemies[enemyIndex].isAlive()) {
            int playerIndex = getRandomAlivePlayerIndex();
            if (playerIndex != -1) {
                Player target = players[playerIndex];
                target.takeDamage(enemies[enemyIndex].stats.Attack);
                logBattleMessage("Enemy " + (enemyIndex + 1) + " attacked! Player " + (playerIndex + 1) + " has " + target.getHealth() + " health left.");

                if (!target.isAlive()) {
                    logBattleMessage("Player " + (playerIndex + 1) + " is defeated!");
                }
            }
        }else {
            logBattleMessage("Invalid enemy index.");
        }

        nextTurn();
    }

    private int getRandomAlivePlayerIndex() {
        Random random = new Random();
        int attempts = 0;
        int maxAttempts = players.length; // Limit attempts to avoid infinite loops

        while (attempts < maxAttempts) {
            int randomIndex = random.nextInt(players.length);
            Player target = players[randomIndex];
            if (target != null && target.isAlive()) {
                return randomIndex; // Return the index of the alive player
            }
            attempts++;
        }

        // If no alive player is found, return -1 to indicate failure
        return -1;
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

        // Enable the next button if there are messages in the queue
        if (!battleLog.isEmpty()) {
            nextbutton.setEnabled(true);
        }
    }

    private void endBattle() {
        // Replace the BattleFragment with the GameFragment
        Fragment gameFragment = new GameFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("All enemies are defeated. You win!", true);
        gameFragment.setArguments(bundle);

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, gameFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
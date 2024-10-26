package com.example.dreambound;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class GameFragment extends Fragment {

    private GameView gameView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        gameView = new GameView(getActivity());

        // Check if an enemy was defeated
        Bundle args = getArguments();
        if (args != null && args.getBoolean("All enemies are defeated. You win!", false)) {
            // Update the game state to remove the defeated enemy
            if (gameView != null) {
                gameView.removeDefeatedEnemy();
            }
        }

        return gameView;
    }

    @Override
    public void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        gameView.pause();
    }
}
package com.example.dreambound;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements CollisionHandler.CollisionListener {

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new GameFragment())
                .commit();
    }

    @Override
    public void onCollisionWithCreature() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("Collision Trigger", "Transitioning to BattleFragment");

                // Hide or pause the GameView to ensure it doesn't interfere with BattleFragment
                if (gameView != null) {
                    gameView.pause();
                    gameView.setVisibility(View.GONE);

                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new BattleFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }


    protected void onPause() {
        super.onPause();
        try {
        gameView.pause();
        }
        catch (Exception e) {
            Toast.makeText(MainActivity.this,  e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            gameView.resume();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

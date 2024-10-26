package com.example.dreambound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    private FaeDex faeDex;
    private LinearLayout bagMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);

        // Overlay UI setup
        FrameLayout overlayLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.activity_main, null);
        addContentView(overlayLayout, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        // Initialize FaeDex
        faeDex = new FaeDex();

        // Create Faes Type Friendly
        Fae fae1 = new Fae("Aeloria", "Healing Light", "A gentle Fae with healing powers.", 100, 150, 50, 50);
        Fae fae2 = new Fae("Nerion" , "Shadow Strike", "A cunning Fae that attacks from the shadows.", 200, 250, 50, 50);
        faeDex.addFae(fae1);
        faeDex.addFae(fae2);

        // Set up Bag button and submenu
        bagMenu = findViewById(R.id.bagMenu);
        ImageButton bagButton = findViewById(R.id.bagButton);
        bagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bagMenu.getVisibility() == View.GONE) {
                    onPause();
                    bagMenu.setVisibility(View.VISIBLE);
                } else {
                    bagMenu.setVisibility(View.GONE);
                    onResume();
                }
            }
        });

        // FaeDex button to launch FaeDexActivity
        Button faeDexButton = findViewById(R.id.faeDexButton);
        faeDexButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FaeDexActivity.class);
                intent.putExtra("faeDex", faeDex);
                startActivity(intent);
            }
        });

        //TODO Implement Items
        //TODO Implement Party
        //TODO Implement Save/Load
        //TODO Implement Settings

    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}


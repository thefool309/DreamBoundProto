package com.example.dreambound;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class FaeDexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faedex);

        FaeDex faeDex = (FaeDex) getIntent().getSerializableExtra("faeDex");
        TextView faeListTextView = findViewById(R.id.faeListTextView);

        StringBuilder faeNames = new StringBuilder();
        for (Fae fae : faeDex.getFaeList()) {
            faeNames.append(fae.getName()).append("\n");
            faeNames.append("Ability: ").append(fae.getAbility()).append("\n");
            faeNames.append("Description: ").append(fae.getDescription()).append("\n\n");
        }
        faeListTextView.setText(faeNames.toString());
    }
}


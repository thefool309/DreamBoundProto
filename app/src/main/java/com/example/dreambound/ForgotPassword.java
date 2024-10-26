package com.example.dreambound;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import org.jetbrains.annotations.NotNull;

public class ForgotPassword extends AppCompatActivity {

    EditText mEmail;
    Button mSendCode;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.forgot_password);

        mSendCode = findViewById(R.id.verification_button);
        mEmail = findViewById(R.id.email_input);
        fAuth = FirebaseAuth.getInstance();

        mSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String email = mEmail.getText().toString().trim();

               if(email.isEmpty()){
                   mEmail.setError("Email is required");
                   return;
               }
               fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull @NotNull Task<Void> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(ForgotPassword.this, "Email sent", Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(ForgotPassword.this, MainActivity.class);
                           startActivity(intent);
                       }
                       else{
                           Toast.makeText(ForgotPassword.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
            }
        });


    }
}
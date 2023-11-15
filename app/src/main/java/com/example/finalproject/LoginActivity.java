package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform login authentication
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                if (isValidLogin(username, password)) {
                    // Successful login, launch MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Optional: Close the login activity to prevent going back
                } else {
                    // Display an error message for unsuccessful login
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Replace this method with your actual login logic
    private boolean isValidLogin(String username, String password) {
        // Add your authentication logic (e.g., check against a database or API)
        // For simplicity, this example allows any non-empty username and password
        return !username.isEmpty() && !password.isEmpty();
    }
}

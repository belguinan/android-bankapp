package com.example.bankapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AuthenticationActivity extends AppCompatActivity {

    Button loginBtn;
    EditText username;
    EditText password;
    ToggleButton saveCredentials;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authentication);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        // Check if we're coming back from a "logout" action
        boolean isLogout = getIntent().getBooleanExtra("logout", false);
        if (isLogout) {
            sharedPreferences.edit().remove("login").apply();
            sharedPreferences.edit().remove("password").apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loginBtn = findViewById(R.id.login_button);
        username = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
        saveCredentials = findViewById(R.id.save_credentials);

        if (! getIntent().getBooleanExtra("logout", false) &&
                !sharedPreferences.getString("login", "").isEmpty() &&
                !sharedPreferences.getString("password", "").isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
            startActivity(intent);
            return;
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userStr = username.getText().toString().trim();
                String passStr = password.getText().toString().trim();

                // Validate inputs
                if (TextUtils.isEmpty(userStr) || TextUtils.isEmpty(passStr)) {
                    Toast.makeText(AuthenticationActivity.this,
                            "Username and password cannot be empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (saveCredentials.isChecked()) {
                    sharedPreferences.edit().putString("login", userStr).apply();
                    sharedPreferences.edit().putString("password", passStr).apply();
                }

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
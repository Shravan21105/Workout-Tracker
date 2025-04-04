package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        EditText regName = findViewById(R.id.regName);
        EditText regEmail = findViewById(R.id.regEmail);
        EditText regPhone = findViewById(R.id.regPhone);
        EditText regPassword = findViewById(R.id.regPassword);
        EditText regConfirmPassword = findViewById(R.id.regConfirmPassword);
        EditText regWeight = findViewById(R.id.regWeight);
        EditText regHeight = findViewById(R.id.regHeight);
        EditText regAge = findViewById(R.id.regAge);
        Button registerBtn = findViewById(R.id.registerSubmitBtn);
        TextView loginRedirect = findViewById(R.id.loginRedirect);

        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        registerBtn.setOnClickListener(v -> {
            String name = regName.getText().toString().trim();
            String email = regEmail.getText().toString().trim();
            String phone = regPhone.getText().toString().trim();
            String password = regPassword.getText().toString().trim();
            String confirmPassword = regConfirmPassword.getText().toString().trim();
            String weightStr = regWeight.getText().toString().trim();
            String heightStr = regHeight.getText().toString().trim();
            String ageStr = regAge.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() ||
                    confirmPassword.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty() || ageStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                regConfirmPassword.setError("Passwords do not match");
                return;
            }

            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);
            int age = Integer.parseInt(ageStr);

            if (dbHelper.registerUser(name, email, phone, password, weight, height, age)) {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, "User already exists!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;
    TextView userName, totalWorkouts;
    ProgressBar progressBar;
    Button btnStartWorkout, btnViewProgress, btnSettings, btnLogout;

    String userEmail = ""; // Added to store the email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        String savedEmail = sharedPreferences.getString("userEmail", "");

        // Get email from intent if passed
        if (getIntent().hasExtra("userEmail")) {
            userEmail = getIntent().getStringExtra("userEmail");
        } else {
            userEmail = savedEmail;
        }

        if (!isLoggedIn || userEmail.isEmpty()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        db = new DatabaseHelper(this);
        userName = findViewById(R.id.userName);
        totalWorkouts = findViewById(R.id.totalWorkouts);
        progressBar = findViewById(R.id.progressBar);
        btnStartWorkout = findViewById(R.id.btnStartWorkout);
        btnViewProgress = findViewById(R.id.btnViewProgress);
        btnSettings = findViewById(R.id.btnSettings);
        btnLogout = findViewById(R.id.btnLogout);

        loadUserData(sharedPreferences, userEmail);

        btnStartWorkout.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, WorkoutSelectionActivity.class)));

        btnViewProgress.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProgressActivity.class);
            intent.putExtra("userEmail", userEmail); // Pass email to ProgressActivity
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadUserData(SharedPreferences sharedPreferences, String email) {
        Cursor res = db.getUserByEmail(email);
        if (res != null && res.moveToFirst()) {
            String name = res.getString(res.getColumnIndex("name"));
            userName.setText("Welcome, " + name);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userName", name);
            editor.apply();

            res.close();
        }

        int workoutCount = db.getWorkoutCount();
        totalWorkouts.setText("Total Workouts: " + workoutCount);
        progressBar.setProgress(workoutCount * 10);
    }
}

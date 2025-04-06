package com.example.project;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;
    TextView userName, totalWorkouts, motivationText, unitTextView, timerText;
    ProgressBar progressBar;
    Button btnStartWorkout, btnViewProgress, btnSettings;

    String userEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        String savedEmail = sharedPreferences.getString("userEmail", "");

        SharedPreferences sharedPreference = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean notificationsEnabled = sharedPreference.getBoolean("notificationsEnabled", false);
        if (notificationsEnabled) {
            showNotification("Stay Fit!", "Don't forget to workout today! 💪");
        }

        // Check login
        if (getIntent().hasExtra("userEmail")) {
            userEmail = getIntent().getStringExtra("userEmail");
        } else {
            userEmail = savedEmail;
        }

        if (!isLoggedIn || userEmail.isEmpty()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        // UI init
        db = new DatabaseHelper(this);
        userName = findViewById(R.id.userName);
        totalWorkouts = findViewById(R.id.totalWorkouts);
        motivationText = findViewById(R.id.motivationText);
        unitTextView = findViewById(R.id.unitTextView);
        progressBar = findViewById(R.id.progressBar);
        btnStartWorkout = findViewById(R.id.btnStartWorkout);
        btnViewProgress = findViewById(R.id.btnViewProgress);
        btnSettings = findViewById(R.id.btnSettings);

        loadUserData(sharedPreferences, userEmail);
        applyUserSettings(sharedPreferences);

        // Navigation
        btnStartWorkout.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, WorkoutSelectionActivity.class)));

        btnViewProgress.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProgressActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
    }
    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "workout_channel")
                .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Check permission for Android 13+
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            return;
        }

        notificationManager.notify(1, builder.build());
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
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "WorkoutReminderChannel";
            String description = "Channel for workout reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("workout_channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void applyUserSettings(SharedPreferences prefs) {
        // Units
        String units = prefs.getString("units", "metric");
        if (units.equals("metric")) {
            unitTextView.setText("Using Metric (kg, cm)");
        } else {
            unitTextView.setText("Using Imperial (lbs, ft)");
        }

        // Motivation
        boolean showMotivation = prefs.getBoolean("daily_motivation", false);
        if (showMotivation) {
            motivationText.setVisibility(View.VISIBLE);
            motivationText.setText("💪 Stay strong and crush it today!");
        } else {
            motivationText.setVisibility(View.GONE);
        }


        }
    }


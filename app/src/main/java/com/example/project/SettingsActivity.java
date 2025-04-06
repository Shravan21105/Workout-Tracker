package com.example.project;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SettingsActivity extends AppCompatActivity {

    private CheckBox enableNotifications, shareData, dailyMotivation;
    private RadioGroup unitsRadioGroup;
    private Spinner languageSpinner, timerSpinner;
    private Button btnSaveSettings, btnLogout;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        enableNotifications = findViewById(R.id.enable_notifications);
        shareData = findViewById(R.id.share_data);
        dailyMotivation = findViewById(R.id.daily_motivation);
        unitsRadioGroup = findViewById(R.id.units_radio_group);
        languageSpinner = findViewById(R.id.language_spinner);
        timerSpinner = findViewById(R.id.timer_spinner);
        btnSaveSettings = findViewById(R.id.save_button);
        btnLogout = findViewById(R.id.btnLogout);

        setupSpinners();
        loadPreferences();

        btnSaveSettings.setOnClickListener(v -> savePreferences());
        enableNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("enableNotifications", isChecked);
            editor.apply();

            if (isChecked) {
                sendMotivationalNotification(); // 👈 Show notification instantly
            }
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finish();
        });
    }
    private void sendMotivationalNotification() {
        // Permission check (for Android 13+)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "workout_channel")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("💪 You’re on fire!")
                .setContentText("Stay motivated — your best workout is yet to come!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1001, builder.build());
    }



    private void setupSpinners() {
        ArrayAdapter<CharSequence> langAdapter = ArrayAdapter.createFromResource(this,
                R.array.language_options, android.R.layout.simple_spinner_item);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(langAdapter);

        ArrayAdapter<CharSequence> timerAdapter = ArrayAdapter.createFromResource(this,
                R.array.timer_options, android.R.layout.simple_spinner_item);
        timerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timerSpinner.setAdapter(timerAdapter);
    }

    private void loadPreferences() {
        enableNotifications.setChecked(sharedPreferences.getBoolean("notifications_enabled", false));
        shareData.setChecked(sharedPreferences.getBoolean("share_data", false));
        dailyMotivation.setChecked(sharedPreferences.getBoolean("daily_motivation", false));

        String unit = sharedPreferences.getString("units", "metric");
        if (unit.equals("metric")) {
            unitsRadioGroup.check(R.id.metric_units);
        } else {
            unitsRadioGroup.check(R.id.imperial_units);
        }

        languageSpinner.setSelection(sharedPreferences.getInt("language_index", 0));
        timerSpinner.setSelection(sharedPreferences.getInt("timer_index", 0));
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("notifications_enabled", enableNotifications.isChecked());
        editor.putBoolean("share_data", shareData.isChecked());
        editor.putBoolean("daily_motivation", dailyMotivation.isChecked());

        int checkedId = unitsRadioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.metric_units) {
            editor.putString("units", "metric");
        } else {
            editor.putString("units", "imperial");
        }

        editor.putInt("language_index", languageSpinner.getSelectedItemPosition());
        editor.putInt("timer_index", timerSpinner.getSelectedItemPosition());

        editor.apply();

        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
    }
}

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

public class SettingsActivity extends AppCompatActivity {

    private CheckBox enableNotifications, shareData, dailyMotivation;
    private RadioGroup unitsRadioGroup;
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
        btnSaveSettings = findViewById(R.id.save_button);
        btnLogout = findViewById(R.id.btnLogout);

        loadPreferences();

        btnSaveSettings.setOnClickListener(v -> {
            savePreferences();
            Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
        });

        enableNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("enableNotifications", isChecked).apply();
            if (isChecked) sendMotivationalNotification();
        });

        btnLogout.setOnClickListener(v -> {
            sharedPreferences.edit().clear().apply();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadPreferences() {
        enableNotifications.setChecked(sharedPreferences.getBoolean("enableNotifications", false));
        shareData.setChecked(sharedPreferences.getBoolean("share_data", false));
        dailyMotivation.setChecked(sharedPreferences.getBoolean("daily_motivation", false));

        String unit = sharedPreferences.getString("units", "metric");
        if (unit.equals("metric")) {
            unitsRadioGroup.check(R.id.metric_units);
        } else {
            unitsRadioGroup.check(R.id.imperial_units);
        }
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("enableNotifications", enableNotifications.isChecked());
        editor.putBoolean("share_data", shareData.isChecked());
        editor.putBoolean("daily_motivation", dailyMotivation.isChecked());

        int checkedId = unitsRadioGroup.getCheckedRadioButtonId();
        editor.putString("units", (checkedId == R.id.metric_units) ? "metric" : "imperial");

        editor.apply();
    }

    private void sendMotivationalNotification() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "workout_channel")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("💪 Stay Motivated!")
                .setContentText("Consistency is key. Keep pushing forward!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1001, builder.build());
    }
}

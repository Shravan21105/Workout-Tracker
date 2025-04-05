package com.example.project;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkoutDetailActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        TextView workoutTitle = findViewById(R.id.workoutTitle);
        EditText repsInput = findViewById(R.id.repsInput);
        EditText setsInput = findViewById(R.id.setsInput);
        Button startWorkoutBtn = findViewById(R.id.startWorkoutBtn);

        String workoutType = getIntent().getStringExtra("workout_type");
        workoutTitle.setText(workoutType);

        startWorkoutBtn.setOnClickListener(v -> {
            String reps = repsInput.getText().toString();
            String sets = setsInput.getText().toString();

            if (!reps.isEmpty() && !sets.isEmpty()) {
                String email = sharedPreferences.getString("userEmail", null);
                if (email == null) {
                    Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                String details = "Workout: " + workoutType + "\nReps: " + reps + "\nSets: " + sets;

                boolean inserted = dbHelper.insertWorkout(email, todayDate, details);
                if (inserted) {
                    Toast.makeText(this, "Workout Saved!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to previous activity
                } else {
                    Toast.makeText(this, "Failed to save workout.", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "Please enter reps and sets", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

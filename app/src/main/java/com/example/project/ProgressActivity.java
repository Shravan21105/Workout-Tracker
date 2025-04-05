package com.example.project;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class ProgressActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private String userEmail;

    private CalendarView calendarView;
    private TextView workoutDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        db = new DatabaseHelper(this);
        userEmail = getIntent().getStringExtra("userEmail"); // FIXED KEY

        calendarView = findViewById(R.id.calendarView);
        workoutDetails = findViewById(R.id.workoutDetails);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = formatDate(year, month, dayOfMonth);
            String workout = db.getWorkoutByDate(userEmail, selectedDate);
            if (workout != null) {
                workoutDetails.setText("Workout on " + selectedDate + ":\n" + workout);
            } else {
                workoutDetails.setText("No workout found on " + selectedDate);
            }
        });
    }

    private String formatDate(int year, int month, int day) {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
    }
}

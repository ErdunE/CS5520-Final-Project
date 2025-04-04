package edu.northeastern.finalproject_group_1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private TextView usernameTV, goldTV, totalHabitsTV, currentStreakTV, longestStreakTV, completedHabitsTV, levelTV;
    private BarChart barChart;
    private ProgressBar xpProgressBar;
    private TextView playerName, playerLevel;
    private String currentUser;
    private ImageView levelBadge;
    private DatabaseReference habitsRef, gardenRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stats);

        // Retrieve username from Intent
        currentUser = getIntent().getStringExtra("USERNAME");
        if (currentUser == null) currentUser = "testUser1";

        usernameTV = findViewById(R.id.usernameTV);
        levelTV = findViewById(R.id.levelTV);

        totalHabitsTV = findViewById(R.id.totalHabitsTV);
        currentStreakTV = findViewById(R.id.currentStreakTV);
        longestStreakTV = findViewById(R.id.longestStreakTV);
        completedHabitsTV = findViewById(R.id.completedHabitsTV);
        barChart = findViewById(R.id.barChart);
        xpProgressBar = findViewById(R.id.xpProgressBar);
        levelBadge = findViewById(R.id.levelBadgeImage);

        gardenRef = FirebaseDatabase.getInstance().getReference("GARDENDATA").child(currentUser);
        habitsRef = FirebaseDatabase.getInstance().getReference("HABITS").child(currentUser);

        usernameTV.setText("Hi, " + currentUser);
        loadHabitStats();
        setupBarChart();
    }

    private void loadHabitStats() {
        habitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                int completed = 0;
                for (DataSnapshot habitSnap : snapshot.getChildren()) {
                    if (habitSnap.exists() && habitSnap.child("isCompleted").getValue() != null) {
                        total++;
                        boolean isCompleted = Boolean.TRUE.equals(habitSnap.child("isCompleted").getValue(Boolean.class));
                        if (isCompleted) completed++;
                    }
                }

                int xp = completed * 100; // 100 XP per completed habit
                int level = xp / 500;     // 500 XP per level
                int progress = xp % 500;

                totalHabitsTV.setText(String.valueOf(total));
                completedHabitsTV.setText("Habits Completed: " + completed);
                levelTV.setText("Level " + level);
                xpProgressBar.setMax(500);
                xpProgressBar.setProgress(progress);

                // Set badge based on level
                if (level >= 10) {
                    levelBadge.setImageResource(R.drawable.gold_medal);
                } else if (level >= 5) {
                    levelBadge.setImageResource(R.drawable.silver_medal);
                } else if (level >= 0){
                    levelBadge.setImageResource(R.drawable.bronze_medal);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 3)); // Mon
        entries.add(new BarEntry(1, 1)); // Tue
        entries.add(new BarEntry(2, 4)); // Wed
        entries.add(new BarEntry(3, 2)); // Thu
        entries.add(new BarEntry(4, 3)); // Fri
        entries.add(new BarEntry(5, 2)); // Sat
        entries.add(new BarEntry(6, 5)); // Sun

        BarDataSet dataSet = new BarDataSet(entries, "Habits Completed This Week");
        dataSet.setColor(Color.parseColor("#679273"));

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}));
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        barChart.getAxisRight().setEnabled(false);

        barChart.invalidate();
    }
}
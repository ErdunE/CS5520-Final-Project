package edu.northeastern.finalproject_group_1;

import android.graphics.Color;
import android.os.Bundle;
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

    private TextView totalHabitsTV, currentStreakTV, longestStreakTV, topHabitTV;
    private BarChart barChart;
    private String userId = "testUser1";
    private DatabaseReference habitsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stats);

        totalHabitsTV = findViewById(R.id.totalHabitsTV);
        currentStreakTV = findViewById(R.id.currentStreakTV);
        longestStreakTV = findViewById(R.id.longestStreakTV);
        topHabitTV = findViewById(R.id.topHabitTV);
        barChart = findViewById(R.id.barChart);

        habitsRef = FirebaseDatabase.getInstance().getReference("HABITS").child(userId);

        loadHabitStats();
        setupBarChart();
    }

    private void loadHabitStats() {
        habitsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                for (DataSnapshot habitSnap : snapshot.getChildren()) {
                    if (habitSnap.getValue() != null) {
                        total++;
                    }
                }
                totalHabitsTV.setText("Total Habits: " + total);

                // Placeholder values
                currentStreakTV.setText("Current Streak: 3 days");
                longestStreakTV.setText("Longest Streak: 10 days");
                topHabitTV.setText("Most Completed: Drink Water");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StatsActivity.this, "Failed to load stats", Toast.LENGTH_SHORT).show();
            }
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
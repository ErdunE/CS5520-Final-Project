package edu.northeastern.finalproject_group_1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class DashboardActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private DashboardPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new DashboardPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
    }
}
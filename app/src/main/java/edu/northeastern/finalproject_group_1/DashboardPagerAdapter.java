package edu.northeastern.finalproject_group_1;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DashboardPagerAdapter extends FragmentStateAdapter {

    private GardenFragment gardenFragment = new GardenFragment();
    private HabitListFragment habitListFragment = new HabitListFragment();
    public DashboardPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new GardenFragment();
            case 1:
                return new HabitListFragment();
            default:
                return new GardenFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public HabitListFragment getHabitListFragment() {
        return habitListFragment;
    }
}
package edu.northeastern.finalproject_group_1;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DashboardPagerAdapter extends FragmentStateAdapter {

    private GardenFragment gardenFragment;
    private HabitListFragment habitListFragment;
    private String user;
    public DashboardPagerAdapter(@NonNull FragmentActivity activity, String user) {
        super(activity);
        this.user = user;
        gardenFragment = new GardenFragment();
        gardenFragment.setUser(user);
        //HabitListFragment habitListFragment = new HabitListFragment();
        habitListFragment = HabitListFragment.newInstance(user, gardenFragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return gardenFragment;
            case 1:
                return habitListFragment;
            default:
                return gardenFragment;
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
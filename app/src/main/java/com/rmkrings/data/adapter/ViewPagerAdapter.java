package com.rmkrings.data.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.rmkrings.fragments.preferences.AboutPreferencesFragment;
import com.rmkrings.fragments.preferences.GeneralPreferencesFragment;
import com.rmkrings.fragments.preferences.StaffListPreferencesFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private static final int TAB_COUNT = 3;

    public ViewPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new GeneralPreferencesFragment();
            case 1:
                return new StaffListPreferencesFragment();
            case 2:
                return new AboutPreferencesFragment();
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}

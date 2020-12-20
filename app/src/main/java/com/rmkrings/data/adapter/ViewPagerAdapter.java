package com.rmkrings.data.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rmkrings.fragments.preferences.AboutPreferencesFragment;
import com.rmkrings.fragments.preferences.GeneralPreferencesFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private static final int TAB_COUNT = 2;
    private final GeneralPreferencesFragment mGeneralPreferencesFragment;
    private final AboutPreferencesFragment mAboutPreferencesFragment;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mGeneralPreferencesFragment = new GeneralPreferencesFragment();
        mAboutPreferencesFragment = new AboutPreferencesFragment();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mGeneralPreferencesFragment;
            case 1:
                return mAboutPreferencesFragment;
            default:
                return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mGeneralPreferencesFragment.getTitle();
            case 1:
                return mAboutPreferencesFragment.getTitle();

        }
        return super.getPageTitle(position);
    }
}

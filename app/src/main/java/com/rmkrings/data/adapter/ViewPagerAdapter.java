package com.rmkrings.data.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.rmkrings.fragments.preferences.AboutFragment;
import com.rmkrings.fragments.preferences.GeneralFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static int TAB_COUNT = 2;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return GeneralFragment.newInstance();
            case 1:
                return AboutFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return GeneralFragment.getTitle();
            case 1:
                return AboutFragment.getTitle();

        }
        return super.getPageTitle(position);
    }
}

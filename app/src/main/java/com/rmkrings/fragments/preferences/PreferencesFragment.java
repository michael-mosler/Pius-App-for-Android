package com.rmkrings.fragments.preferences;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.rmkrings.activities.R;
import com.rmkrings.data.adapter.ViewPagerAdapter;

/**
 * Preferences fragment which holds a tabview with app preferences like login data and
 * grade on the one hand and "About App" info on the other.
 */
public class PreferencesFragment extends Fragment {

    public PreferencesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_preferences, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewPager(view);
    }

    @Override
    public void onResume() {
        super.onResume();

        requireActivity().setTitle(R.string.title_settings);
        BottomNavigationView mNavigationView = getActivity().findViewById(R.id.navigation);
        mNavigationView.getMenu().getItem(4).setChecked(true);
    }

    /**
     * Sets up tab view in given view.
     * @param view View which holds tab/pager view.
     */
    private void setViewPager(View view) {
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getActivity());

        ViewPager2 mViewPager = view.findViewById(R.id.pager);
        mViewPager.setAdapter(mViewPagerAdapter);

        TabLayout mTabLayout = view.findViewById(R.id.tab);
        new TabLayoutMediator(
                mTabLayout,
                mViewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText(GeneralPreferencesFragment.getTitle());
                            break;
                        case 1:
                            tab.setText(StaffListPreferencesFragment.getTitle());
                            break;
                        case 2:
                            tab.setText(AboutPreferencesFragment.getTitle());
                            break;
                    }
                }
        ).attach();
    }
}
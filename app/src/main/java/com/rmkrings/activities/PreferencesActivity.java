package com.rmkrings.activities;

import androidx.fragment.app.Fragment;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.rmkrings.data.adapter.ViewPagerAdapter;
import com.rmkrings.interfaces.IOnBackPressed;

public class PreferencesActivity extends AppCompatActivity {

    private ViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        setViewPager();
    }

    /**
     * This handler delegates onBackPressed event to all embedded fragments.
     */
    @Override public void onBackPressed() {
        super.onBackPressed();

        for (Fragment f : mViewPagerAdapter.getFragments()) {
            if ((f instanceof IOnBackPressed)){
                ((IOnBackPressed) f).onBackPressed();
            }
        }
    }

    private void setViewPager() {
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(mViewPagerAdapter);

        TabLayout mTabLayout = findViewById(R.id.tab);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}

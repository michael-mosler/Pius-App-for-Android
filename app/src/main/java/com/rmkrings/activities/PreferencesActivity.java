package com.rmkrings.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.rmkrings.data.adapter.ViewPagerAdapter;
import com.rmkrings.interfaces.IOnBackPressed;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        setViewPager();
    }

    @Override public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.preferences_general);
        if(!(fragment instanceof IOnBackPressed)){
            super.onBackPressed();
        }
    }

    private void setViewPager() {
        ViewPager mViewPager = findViewById(R.id.pager);
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

        TabLayout mTabLayout = findViewById(R.id.tab);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}

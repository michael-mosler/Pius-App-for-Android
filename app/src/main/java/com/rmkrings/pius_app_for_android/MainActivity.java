package com.rmkrings.pius_app_for_android;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.content.Intent;

import com.rmkrings.PiusApp.VertretungsplanFragment;

public class MainActivity extends AppCompatActivity implements VertretungsplanFragment.OnFragmentInteractionListener {

    private FrameLayout mFrameLayout;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setTitle(R.string.title_home);
                    return true;
                case R.id.navigation_substitution_schedule:
                    setTitle(R.string.title_substitution_schedule);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, new VertretungsplanFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    setTitle(getResources().getString(R.string.title_dashboard));
                    return true;
                case R.id.navigation_calendar:
                    setTitle(R.string.title_calendar);
                    return true;
                case R.id.navigation_settings:
                    //mTextMessage.setText(R.string.title_settings);
                    Intent a = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(a);
                    return false;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_home);

        mFrameLayout = findViewById(R.id.frameLayout);

        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

package com.rmkrings.pius_app_for_android;

import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.content.Intent;

import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Reachability;
import com.rmkrings.interfaces.ReachabilityChangeCallback;
import com.rmkrings.main.pius_app.CalendarFragment;
import com.rmkrings.main.pius_app.DashboardFragment;
import com.rmkrings.main.pius_app.PiusApplication;
import com.rmkrings.main.pius_app.VertretungsplanFragment;
import com.rmkrings.main.pius_app.TodayFragment;


public class MainActivity extends AppCompatActivity implements ReachabilityChangeCallback
{

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home: {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, new TodayFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                }

                case R.id.navigation_substitution_schedule: {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, new VertretungsplanFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                }

                case R.id.navigation_dashboard: {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, new DashboardFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                }

                case R.id.navigation_calendar: {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, new CalendarFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                }

                case R.id.navigation_settings:
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
        setContentView(R.layout.activity_main);

        // Add navigation bar.
        BottomNavigationView navigation = findViewById(R.id.navigation);
        ColorStateList tint = (Reachability.isReachable())
                ? PiusApplication.getAppContext().getResources().getColorStateList(R.color.nav_bar_colors)
                : PiusApplication.getAppContext().getResources().getColorStateList(R.color.nav_bar_offline_colors);
        navigation.setItemIconTintList(tint);
        navigation.setItemTextColor(tint);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Reachability.getInstance().setReachabilityChangeCallback(this);

        // Show Today fragment initially.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, new TodayFragment());
        transaction.commit();

        // If App is used for the very first time show information
        // that user should log in to Pius website in Settings.
        if (AppDefaults.hasShowIntro()) {
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setTitle(getResources().getString(R.string.title_welcome))
                    .setMessage(getResources().getString(R.string.text_intro))
                    .setPositiveButton(getResources().getString(R.string.label_start_now), null)
                    .show();
            AppDefaults.setShowIntro(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.title_home);

        try {
            int currentVersionCode = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
            if (currentVersionCode > AppDefaults.getVersionCode()) {
                // Any thing that need to be migrated goes in here.
                AppDefaults.setVersionCode(currentVersionCode);
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(boolean isReachable) {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        ColorStateList tint = (isReachable)
                ? PiusApplication.getAppContext().getResources().getColorStateList(R.color.nav_bar_colors)
                : PiusApplication.getAppContext().getResources().getColorStateList(R.color.nav_bar_offline_colors);
        navigation.setItemIconTintList(tint);
        navigation.setItemTextColor(tint);

        navigation.refreshDrawableState();
    }
}

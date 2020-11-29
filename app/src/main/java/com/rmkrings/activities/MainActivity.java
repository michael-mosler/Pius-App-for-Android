package com.rmkrings.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rmkrings.fragments.CalendarFragment;
import com.rmkrings.fragments.DashboardFragment;
import com.rmkrings.fragments.TodayFragment;
import com.rmkrings.fragments.VertretungsplanFragment;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Config;
import com.rmkrings.helper.Reachability;
import com.rmkrings.interfaces.ReachabilityChangeCallback;
import com.rmkrings.loader.StaffLoader;
import com.rmkrings.pius_app_for_android;

public class MainActivity extends AppCompatActivity implements ReachabilityChangeCallback
{
    Dialog myDialog;

    @SuppressWarnings("SameReturnValue")
    public static String getTargetDashboard() {
        return "dashboard";
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            try {
                switch (item.getItemId()) {
                    case R.id.navigation_home: {
                        startFragment(new TodayFragment());
                        return true;
                    }

                    case R.id.navigation_substitution_schedule: {
                        startFragment(new VertretungsplanFragment());
                        return true;
                    }

                    case R.id.navigation_dashboard: {
                        startFragment(new DashboardFragment());
                        return true;
                    }

                    case R.id.navigation_calendar: {
                        startFragment(new CalendarFragment());
                        return true;
                    }

                    case R.id.navigation_settings:
                        Intent a = new Intent(MainActivity.this, PreferencesActivity.class);
                        startActivity(a);
                        return false;
                }
                return false;
            }
            catch(IllegalStateException e) {
                e.printStackTrace();
                return false;
            }
        }
    };

    private void startFragment(Fragment f, Boolean withBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, f);

        if (withBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    private void startFragment(Fragment f) {
        this.startFragment(f, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDialog = new Dialog(this);

        // Add navigation bar.
        BottomNavigationView navigation = findViewById(R.id.navigation);
        ColorStateList tint;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            tint = (Reachability.isReachable())
                    ? pius_app_for_android.getAppContext().getResources().getColorStateList(R.color.nav_bar_colors, null)
                    : pius_app_for_android.getAppContext().getResources().getColorStateList(R.color.nav_bar_offline_colors, null);
        } else {
            tint = (Reachability.isReachable())
                    ? AppCompatResources.getColorStateList(pius_app_for_android.getAppContext(), R.color.nav_bar_colors)
                    : AppCompatResources.getColorStateList(pius_app_for_android.getAppContext(), R.color.nav_bar_offline_colors);
        }
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
        int versionCode;
        try {
            versionCode = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
        }
        catch(PackageManager.NameNotFoundException e) {
            versionCode = 0;
        }

        if (versionCode > AppDefaults.getSavedVersionCode() || Config.getAlwaysShowWelcome()) {
            AppDefaults.setSavedVersionCode(versionCode);

            Button btnFollow;
            myDialog.setContentView(R.layout.popup_changelog);
            btnFollow = (Button) myDialog.findViewById(R.id.btnfollow);
            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();
        }

        final StaffLoader staffLoader = new StaffLoader();
        staffLoader.load();
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

        // Start alternate fragment on request.
        String target = getIntent().getStringExtra("target");
        if (target != null && target.equals(getTargetDashboard())) {
            startFragment(new DashboardFragment(), false);
        }
    }

    @Override
    public void execute(boolean isReachable) {
        try {
            BottomNavigationView navigation = findViewById(R.id.navigation);
            ColorStateList tint;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                tint = (isReachable)
                        ? pius_app_for_android.getAppContext().getResources().getColorStateList(R.color.nav_bar_colors, null)
                        : pius_app_for_android.getAppContext().getResources().getColorStateList(R.color.nav_bar_offline_colors, null);
            } else {
                tint = (isReachable)
                        ? AppCompatResources.getColorStateList(pius_app_for_android.getAppContext(), R.color.nav_bar_colors)
                        : AppCompatResources.getColorStateList(pius_app_for_android.getAppContext(), R.color.nav_bar_offline_colors);
            }
            navigation.setItemIconTintList(tint);
            navigation.setItemTextColor(tint);

            navigation.refreshDrawableState();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

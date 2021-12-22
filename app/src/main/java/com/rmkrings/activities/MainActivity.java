package com.rmkrings.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rmkrings.fragments.CalendarFragment;
import com.rmkrings.fragments.DashboardFragment;
import com.rmkrings.fragments.TodayFragment;
import com.rmkrings.fragments.VertretungsplanFragment;
import com.rmkrings.fragments.preferences.PreferencesFragment;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Config;
import com.rmkrings.helper.Reachability;
import com.rmkrings.interfaces.IOnBackPressed;
import com.rmkrings.interfaces.ReachabilityChangeCallback;
import com.rmkrings.loader.StaffLoader;
import com.rmkrings.pius_app_for_android;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ReachabilityChangeCallback
{
    Dialog myDialog;

    @SuppressWarnings("SameReturnValue")
    public static String getTargetDashboard() {
        return "dashboard";
    }

    private void startFragment(Fragment f, Boolean withBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, f);

        if (withBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_main);
        myDialog = new Dialog(this);

        // Add navigation bar.
        ColorStateList tint;
        tint = (Reachability.isReachable())
                ? pius_app_for_android.getAppContext().getResources().getColorStateList(R.color.nav_bar_colors, null)
                : pius_app_for_android.getAppContext().getResources().getColorStateList(R.color.nav_bar_offline_colors, null);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setItemIconTintList(tint);
        navigation.setItemTextColor(tint);
        navigation.setOnItemSelectedListener(item -> {
            try {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    startFragment(new TodayFragment(), true);
                    return true;
                } else if (itemId == R.id.navigation_substitution_schedule) {
                    startFragment(new VertretungsplanFragment(), true);
                    return true;
                } else if (itemId == R.id.navigation_dashboard) {
                    startFragment(new DashboardFragment(), true);
                    return true;
                } else if (itemId == R.id.navigation_calendar) {
                    startFragment(new CalendarFragment(), true);
                    return true;
                } else if (itemId == R.id.navigation_settings) {
                    startFragment(new PreferencesFragment(), true);
                    return true;
                } else {
                    return false;
                }
            }
            catch(IllegalStateException e) {
                e.printStackTrace();
                return false;
            }
        });

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
            btnFollow = myDialog.findViewById(R.id.btnfollow);
            btnFollow.setOnClickListener(v -> myDialog.dismiss());
            Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
            tint = (isReachable)
                    ? pius_app_for_android.getAppContext().getResources().getColorStateList(R.color.nav_bar_colors, null)
                    : pius_app_for_android.getAppContext().getResources().getColorStateList(R.color.nav_bar_offline_colors, null);
            navigation.setItemIconTintList(tint);
            navigation.setItemTextColor(tint);

            navigation.refreshDrawableState();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

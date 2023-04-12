package com.rmkrings.activities;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
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
import com.rmkrings.helper.Reachability;
import com.rmkrings.interfaces.ReachabilityChangeCallback;
import com.rmkrings.loader.StaffLoader;
import com.rmkrings.pius_app_for_android;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ReachabilityChangeCallback {

    @IntDef({ NONE, HOME, SUBSTITUTION_SCHEDULE, DASHBOARD, CALENDAR, SETTINGS })
    public @interface TAB_TYPE {}
    public static final int NONE = -1;
    public static final int HOME = 0;
    public static final int SUBSTITUTION_SCHEDULE = 1;
    public static final int DASHBOARD = 2;
    public static final int CALENDAR = 3;
    public static final int SETTINGS = 4;

    private @TAB_TYPE int savedTab = NONE;

    @SuppressWarnings("SameReturnValue")
    public static String getTargetDashboard() {
        return "dashboard";
    }

    public void startFragment(Fragment f, Boolean withBackStack) {
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

        if (savedInstanceState != null) {
            savedTab = savedInstanceState.getInt("ACTIVE_TAB", NONE);
        }

        setContentView(R.layout.activity_main);
        configureNavBar();
        Reachability.getInstance().setReachabilityChangeCallback(this);

        final StaffLoader staffLoader = new StaffLoader();
        staffLoader.load();
    }

    @Override
    protected void onStart() {
        super.onStart();

        setTitle(R.string.title_home);
        versionCheck();

        // Start alternate fragment on request.
        String target = getIntent().getStringExtra("target");
        if (target != null && target.equals(getTargetDashboard())) {
            navigateToItem(R.id.navigation_dashboard);
        } else {
            // Active tab defaults to HOME. If active tab has been restored
            // from state then use it if current tab is NONE.
            @TAB_TYPE int currentTab = getCurrentTab();
            if (currentTab == NONE && savedTab == NONE) {
                currentTab = HOME;
            } else if (currentTab == NONE) {
                currentTab = savedTab;
            }

            int itemId = mapTabToItemId(currentTab);
            navigateToItem(itemId);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("ACTIVE_TAB", getCurrentTab());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void execute(boolean isReachable) {
        try {
            BottomNavigationView navigation = findViewById(R.id.navigation);
            ColorStateList tint = getNavBarIconTint(isReachable);
            navigation.setItemIconTintList(tint);
            navigation.setItemTextColor(tint);
            navigation.refreshDrawableState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private ColorStateList getNavBarIconTint(boolean isReachable) {
        return isReachable
                ? pius_app_for_android.getAppContext().getResources().getColorStateList(R.color.nav_bar_colors, null)
                : pius_app_for_android.getAppContext().getResources().getColorStateList(R.color.nav_bar_offline_colors, null);
    }

    private int mapTabToItemId(@TAB_TYPE int tab) {
        int itemId;
        switch (tab) {
            case SUBSTITUTION_SCHEDULE:
                itemId = R.id.navigation_substitution_schedule;
                break;
            case DASHBOARD:
                itemId = R.id.navigation_dashboard;
                break;
            case CALENDAR:
                itemId = R.id.navigation_calendar;
                break;
            case SETTINGS:
                itemId = R.id.navigation_settings;
                break;
            case NONE:
            case HOME:
            default:
                itemId = R.id.navigation_home;
                break;
        }
        return itemId;
    }

    private int mapItemIdToTab(int itemId) {
        int tab;
        if (itemId == R.id.navigation_home) {
            return HOME;
        }

        if (itemId == R.id.navigation_substitution_schedule) {
            return SUBSTITUTION_SCHEDULE;
        }

        if (itemId == R.id.navigation_dashboard) {
            return DASHBOARD;
        }

        if (itemId == R.id.navigation_calendar) {
            return CALENDAR;
        }

        if (itemId == R.id.navigation_settings) {
            return SETTINGS;
        }

        return NONE;
    }

    private @TAB_TYPE int getCurrentTab() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        int itemId = navigation.getSelectedItemId();
        return mapItemIdToTab(itemId);
    }

    private void versionCheck() {
        try {
            int currentVersionCode = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
            if (currentVersionCode > AppDefaults.getVersionCode()) {
                // Any thing that need to be migrated goes in here.
                AppDefaults.setVersionCode(currentVersionCode);

                final Dialog myDialog = new Dialog(this);
                Button btnFollow;
                myDialog.setContentView(R.layout.popup_changelog);
                btnFollow = myDialog.findViewById(R.id.btnfollow);
                btnFollow.setOnClickListener(v -> myDialog.dismiss());
                Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void configureNavBar() {
        // Add navigation bar.
        ColorStateList tint = getNavBarIconTint(Reachability.isReachable());
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setItemIconTintList(tint);
        navigation.setItemTextColor(tint);
        navigation.setOnItemSelectedListener(item -> navigateToItem(item.getItemId()));
    }

    private boolean navigateToItem(int itemId) {
        try {
            if (itemId == R.id.navigation_home) {
                startFragment(new TodayFragment(), true);
                return true;
            }

            if (itemId == R.id.navigation_substitution_schedule) {
                startFragment(new VertretungsplanFragment(), true);
                return true;
            }

            if (itemId == R.id.navigation_dashboard) {
                startFragment(new DashboardFragment(), true);
                return true;
            }
            if (itemId == R.id.navigation_calendar) {
                startFragment(new CalendarFragment(), true);
                return true;
            }
            if (itemId == R.id.navigation_settings) {
                startFragment(new PreferencesFragment(), true);
                return true;
            }

            return false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        }
    }

}

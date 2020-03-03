package com.rmkrings.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.rmkrings.pius_app_for_android;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AppDefaults {
    static private final SharedPreferences sharedPreferences = pius_app_for_android.getAppContext().getSharedPreferences("com.rmkrings.pius_app", Context.MODE_PRIVATE);
    static private final SharedPreferences.Editor edit = sharedPreferences.edit();

    static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    static SharedPreferences.Editor getEdit() {
        return edit;
    }


    public static String getBaseUrl() {
        try {
            ApplicationInfo ai = pius_app_for_android.getAppContext().getPackageManager().getApplicationInfo(pius_app_for_android.getAppContext().getPackageName(), PackageManager.GET_META_DATA);
            return (String)ai.metaData.get("host");
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "https://pius-gateway-ng.eu-gb.mybluemix.net";
        }
    }

    /*
     * Authenticated flag
     */
    public static boolean isAuthenticated() {
        boolean authenticated = sharedPreferences.getBoolean("authenticated", false);
        return (authenticated && !"".equals(getPassword()));
    }

    public static void setAuthenticated(boolean authenticated) {
        edit.putBoolean("authenticated", authenticated);
        edit.commit();
    }

    /*
     * Version Code
     */
    public static int getSavedVersionCode() {
        return sharedPreferences.getInt("savedVersionCode", 0);
    }

    public static void setSavedVersionCode(int versionCode) {
        edit.putInt("savedVersionCode", versionCode);
        edit.commit();
    }

    /*
     * Grade and Class
     */
    public static int getSelectedGradeRow() {
        final Config config = new Config();
        int selectedGradeRow = sharedPreferences.getInt("selectedGradeRow", 0);

        // With app version 1.5 IKD and IKE has been removed. If anybody should still be using
        // one of these reset grade to none.
        if (selectedGradeRow >= config.getShortGrades().length) {
            selectedGradeRow = 0;
            AppDefaults.setSelectedGradeRow(0);
            AppDefaults.setSelectedClassRow(0);
        }

        return selectedGradeRow;
    }

    public static void setSelectedGradeRow(int gradeRow) {
        edit.putInt("selectedGradeRow", gradeRow);
        edit.commit();
    }

    public static boolean hasGrade() {
        return AppDefaults.getSelectedGradeRow() != 0;
    }

    static boolean hasLowerGrade() {
        Config config = new Config();
        int selectedGradeRow = getSelectedGradeRow();
        return config.isLowerGrade(config.getGrades()[selectedGradeRow]);
    }

    public static boolean hasUpperGrade() {
        Config config = new Config();
        int selectedGradeRow = getSelectedGradeRow();
        return config.isUpperGrade(config.getGrades()[selectedGradeRow]);
    }

    public static int getSelectedClassRow() {
        return sharedPreferences.getInt("selectedClassRow", 0);
    }

    public static void setSelectedClassRow(int classRow) {
        edit.putInt("selectedClassRow", classRow);
        edit.commit();
    }

    public static String getGradeSetting() {
        int selectedGradeRow = AppDefaults.getSelectedGradeRow();
        int selectedClassRow = AppDefaults.getSelectedClassRow();

        if (selectedGradeRow != 0) {
            Config config = new Config();
            return config.getShortGrades()[selectedGradeRow] + config.getShortClasses()[selectedClassRow];
        }

        return "";
    }

    /*
     * Username and Password
     */
    public static String getUsername() {
        return sharedPreferences.getString("username", "");
    }

    public static void setUsername(String username) {
        edit.putString("username", username);
        edit.commit();
    }

    public static String getPassword() {
        try {
            Crypto crypto = new Crypto();
            String encryptedPassword = sharedPreferences.getString("password", "");
            if ("".equals(encryptedPassword)) {
                return "";
            }

            return crypto.decrypt(encryptedPassword);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void setPassword(String password) {
        try {
            Crypto crypto = new Crypto();
            String encryptedPassword = crypto.encrypt(password);
            edit.putString("password", encryptedPassword);
            edit.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Course List
     */
    public static void setCourseList(ArrayList<String> courseList) {
        Set<String> s = new HashSet<>(courseList);
        edit.putStringSet("courseList", s);
        edit.commit();
    }

    public static ArrayList<String> getCourseList() {
        Set<String> s = sharedPreferences.getStringSet("courseList", null);
        return (s != null) ? new ArrayList<>(s) : new ArrayList<String>();
    }

    /*
     * Version Code
     */
    public static void setVersionCode(int versionCode) {
        edit.putInt("versionCode", versionCode);
        edit.commit();
    }

    public static int getVersionCode() {
        return sharedPreferences.getInt("versionCode", -1);
    }
}

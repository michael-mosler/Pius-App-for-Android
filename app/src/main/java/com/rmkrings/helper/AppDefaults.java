package com.rmkrings.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.rmkrings.main.pius_app.PiusApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AppDefaults {
    static private SharedPreferences sharedPreferences = PiusApplication.getAppContext().getSharedPreferences("com.rmkrings.pius_app", Context.MODE_PRIVATE);
    static private SharedPreferences.Editor edit = sharedPreferences.edit();

    static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    static SharedPreferences.Editor getEdit() {
        return edit;
    }


    static public String getBaseUrl() {
        return "https://pius-gateway-ng.eu-gb.mybluemix.net";
    }

    /*
     * Authenticated flag
     */
    static public boolean isAuthenticated() {
        boolean authenticated = sharedPreferences.getBoolean("authenticated", false);
        return (authenticated && !"".equals(getPassword()));
    }

    static public void setAuthenticated(boolean authenticated) {
        edit.putBoolean("authenticated", authenticated);
        edit.commit();
    }

    /*
     * Grade and Class
     */
    public static int getSelectedGradeRow() {
        return sharedPreferences.getInt("selectedGradeRow", 0);
    }

    public static void setSelectedGradeRow(int gradeRow) {
        edit.putInt("selectedGradeRow", gradeRow);
        edit.commit();
    }

    public static boolean hasGrade() {
        return AppDefaults.getSelectedGradeRow() != 0;
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
}

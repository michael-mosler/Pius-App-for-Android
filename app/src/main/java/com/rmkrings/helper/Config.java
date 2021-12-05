package com.rmkrings.helper;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.rmkrings.pius_app_for_android;

import java.util.Arrays;

public class Config {
    private final String[] grades = { "keine", "Klasse 5", "Klasse 6", "Klasse 7", "Klasse 8", "Klasse 9", "EF", "Q1", "Q2" };

    private final String[] upperGrades = { "EF", "Q1", "Q2" };
    private final String[] lowerGrades = { "Klasse 5", "Klasse 6", "Klasse 7", "Klasse 8", "Klasse 9" };
    private final String[] shortGrades = { "", "5", "6", "7", "8", "9", "EF", "Q1", "Q2" };

    private final String[] courses = { "Mathematik", "Deutsch", "Englisch", "Französisch", "Latein", "Spanisch", "Hebräisch", "Erdkunde", "Biologie", "Physik", "Chemie", "Informatik", "Geschichte", "Religion", "Philosophie", "Musik", "Kunst", "Sport", "Literatur", "SOWI", "IV" };
    private final String[] coursesShortNames = { "M", "D", "E", "F", "L", "S", "H", "EK", "BI", "PH", "CH", "IF", "GE", "KR", "PL", "MU", "KU", "SP", "LI", "SW", "IV" };
    private final String[] courseTypes = { "GK", "LK", "ZK", "V", "P" };
    private final String[] courseNumbers = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

    private final String [] classes = { "keine", "a", "b", "c", "d", "e" };
    private final String[] shortClasses =  { "", "A", "B", "C", "D", "E" };

    private final String[] lessonStartTimes = { "07:55", "08:40", "09:45", "10:35", "11:25", "12:40", "13:25", "14:30", "15:15", "16:00", "16:45" };

    /**
     * Get always show welcome screen information. This should return true for debug build only.
     * @return "true" if welcome screen should be shown, even if version code has not changed.
     */
    public static Boolean getAlwaysShowWelcome() {
        try {
            return (Boolean)AppDefaults.getApplicationInfo().metaData.get("alwaysShowWelcome");
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get aleays show staff helper popover information. This should return true in debug
     * mode only.
     * @return "true" if popover is to be shown.
     */
    public static Boolean getAlwaysShowStaffPopoverHelper() {
        try {
            return (Boolean)AppDefaults.getApplicationInfo().metaData.get("alwaysShowStaffHelperPopover");
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This is a shortcut for String.format("%s.md5", pattern)
     * @param pattern - Variable part of filename
     * @return - Filename
     */
    public static String digestFilename(String pattern) { return String.format("%s.md5", pattern); }

    /**
     * This is a shortcut for String.format("%s.json", pattern)
     * @param pattern - Variable part of filename
     * @return - Filename
     */
    public static String cacheFilename(String pattern) { return String.format("%s.json", pattern); }

    /**
     * Checks if dashboard can be used. If it can than widget also can show data.
     * @return - Returns true if dashboard can be used.
     */
    public static boolean canUseDashboard(){
        return (AppDefaults.isAuthenticated() && (AppDefaults.hasLowerGrade() || (AppDefaults.hasUpperGrade() && AppDefaults.getCourseList().size() > 0)));
    }

    /**
     * Checks if grade is upper grade.
     * @param grade - The grade to check.
     * @return - true when upper grade
     */
    public boolean isUpperGrade(String grade)  {
        return Arrays.asList(upperGrades).contains(grade);
    }

    /**
     * Checks if grade is a lower grade.
     * @param grade - The grade to check.
     * @return - true when lower grade
     */
    public boolean isLowerGrade(String grade) {
        return Arrays.asList(lowerGrades).contains(grade);
    }

    /*
     * Getters...
     */
    public String[] getLessonStartTimes() {
        return lessonStartTimes;
    }

    public String[] getGrades() {
        return grades;
    }

    String[] getShortGrades() {
        return shortGrades;
    }

    public String[] getClasses() {
        return classes;
    }

    String[] getShortClasses() {
        return shortClasses;
    }

    public String[] getCourses() {
        return courses;
    }

    public String[] getCoursesShortNames() {
        return coursesShortNames;
    }

    public String[] getCourseTypes() {
        return courseTypes;
    }

    public String[] getCourseNumbers() {
        return courseNumbers;
    }
}

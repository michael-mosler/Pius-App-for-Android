package com.rmkrings.helper;

import java.util.Arrays;

public class Config {
    private final String[] grades = { "keine", "Klasse 5", "Klasse 6", "Klasse 7", "Klasse 8", "Klasse 9", "EF", "Q1", "Q2", "IKD", "IKE" };

    private final String[] upperGrades = { "EF", "Q1", "Q2" };
    private final String[] lowerGrades = { "Klasse 5", "Klasse 6", "Klasse 7", "Klasse 8", "Klasse 9" };
    private final String[] shortGrades = { "", "5", "6", "7", "8", "9", "EF", "Q1", "Q2", "IKD", "IKE" };

    private final String[] courses = { "Mathematik", "Deutsch", "Englisch", "Französisch", "Latein", "Spanisch", "Hebräisch", "Erdkunde", "Biologie", "Physik", "Chemie", "Informatik", "Geschichte", "Religion", "Philosophie", "Musik", "Kunst", "Sport", "Literatur", "SOWI", "IV" };
    private final String[] coursesShortNames = { "M", "D", "E", "F", "L", "S", "H", "EK", "BI", "PH", "CH", "IF", "GE", "KR", "PL", "MU", "KU", "SP", "LI", "SW", "IV" };
    private final String[] courseTypes = { "GK", "LK", "ZK", "V", "P" };
    private final String[] courseNumbers = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

    private final String [] classes = { "keine", "a", "b", "c", "d", "e" };
    private final String[] shortClasses =  { "", "A", "B", "C", "D", "E" };

    private final String[] lessonStartTimes = { "07:55", "08:40", "09:45", "10:35", "11:25", "12:40", "13:25", "14:30", "15:15", "16:00", "16:45" };

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

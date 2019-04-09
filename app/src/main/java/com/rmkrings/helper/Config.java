package com.rmkrings.helper;

import java.util.Arrays;

public class Config {
    private String[] grades = { "keine", "Klasse 5", "Klasse 6", "Klasse 7", "Klasse 8", "Klasse 9", "EF", "Q1", "Q2", "IKD", "IKE" };

    private String[] upperGrades = { "EF", "Q1", "Q2" };
    private String[] lowerGrades = { "Klasse 5", "Klasse 6", "Klasse 7", "Klasse 8", "Klasse 9" };
    private String[] shortGrades = { "", "5", "6", "7", "8", "9", "EF", "Q1", "Q2", "IKD", "IKE" };

    private String[] courses = { "Mathematik", "Deutsch", "Englisch", "Französisch", "Latein", "Spanisch", "Hebräisch", "Erdkunde", "Biologie", "Physik", "Chemie", "Informatik", "Geschichte", "Religion", "Philosophie", "Musik", "Kunst", "Sport", "Literatur", "SOWI", "IV" };
    private String[] coursesShortNames = { "M", "D", "E", "F", "L", "S", "H", "EK", "BI", "PH", "CH", "IF", "GE", "KR", "PL", "MU", "KU", "SP", "LI", "SW", "IV" };
    private String[] courseTypes = { "GK", "LK", "ZK", "V", "P" };
    private String[] courseNumbers = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

    private String [] classes = { "keine", "a", "b", "c", "d", "e" };
    private String[] shortClasses =  { "", "A", "B", "C", "D", "E" };

    public String[] getGrades() {
        return grades;
    }

    public String[] getShortGrades() {
        return shortGrades;
    }

    public String[] getClasses() {
        return classes;
    }

    public String[] getShortClasses() {
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

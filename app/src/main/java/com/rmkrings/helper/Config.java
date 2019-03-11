package com.rmkrings.helper;

import java.util.Arrays;

public class Config {
    private String[] grades = { "keine", "Klasse 5", "Klasse 6", "Klasse 7", "Klasse 8", "Klasse 9", "EF", "Q1", "Q2", "IKD", "IKE" };
    private String [] classes = { "keine", "a", "b", "c", "d", "e" };

    private String[] upperGrades = { "EF", "Q1", "Q2" };
    private String[] lowerGrades = { "Klasse 5", "Klasse 6", "Klasse 7", "Klasse 8", "Klasse 9" };

    public String[] getGrades() {
        return grades;
    }

    public String[] getClasses() {
        return classes;
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
}

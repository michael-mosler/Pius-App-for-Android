package com.rmkrings.data.vertretungsplan;

import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Config;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VertretungsplanHeaderItem extends VertretungsplanListItem {

    private String course;
    private String lesson;

    public VertretungsplanHeaderItem(String course, String lesson) {
        this.course = course;
        this.lesson = lesson;
    }

    public String getCourse() {
        return course;
    }

    public String getLesson() {
        return lesson;
    }

    @Override
    public int getType() {
        return courseHeader;
    }

    /**
     * Extracts 2nd course item for a pattern like "a→b". In this case 2nd item is
     * b. If b is not a course name or does not exist at all nil is returned.
     * @param course Original course check for 2nd item.
     * @return 2nd course item or empty string when none exists.
     */
    private static String extract2ndCourse(String course) {
        int rarrPos = course.indexOf("→");
        if (rarrPos > -1) {
            // If 2nd item exists and starts with a letter then keep it.
            String secondItem = course.substring(rarrPos+1);
            if (secondItem.length() > 0 && "ABCDEFGHIJKLMNOPQRSTUVXYZ".contains(secondItem.substring(0, 1))) {
                return secondItem;
            }

            return "";
        }

        return "";
    }

    /**
     * Checks if item must be displayed in dashboard. Outcome depends on user's grade
     * and course settings.
     * @return true if item must be displayed.
     */
    public boolean accept() {
        Config config = new Config();

        // If not an upper grade do not check course list.
        if (!config.isUpperGrade(AppDefaults.getGradeSetting())) {
            return true;
        }

        // If no course list set or list is empty accept any item.
        ArrayList<String> courseList = AppDefaults.getCourseList();
        if (courseList.size() == 0) {
            return true;
        }

        // This is the item to check, remove all blanks first.
        String currentCourse = getCourse().replace(" ", "");

        // "Sondereinsatz": course is empty.
        if (currentCourse.length() == 0) {
            return true;
        }

        // "Messe": starts with "Mes", then get optional second course from and continue with this.
        if (currentCourse.startsWith("Mes")) {
            String secondCourse = extract2ndCourse(currentCourse);
            if (secondCourse.length() > 0) {
                currentCourse = secondCourse;
            } else {
                return true;
            }
        }

        // Alternate definition of empty course?
        if (currentCourse.matches("^[A-Z]")) {
            return true;
        }

        // Current course is on users course list?
        for (String course: courseList) {
            String mappedCourse = course
                    .replace(" ", "")
                    .replace("GK", "G")
                    .replace("LK", "L");

            if (currentCourse.equals(mappedCourse)) {
                return true;
            }
        }

        return false;
    }

    public Date realLessonStartDate(String forDate) {
        String lessonsDate = "";
        Pattern datePattern = Pattern.compile("(\\d{2}.\\d{2}.\\d{4})");
        Matcher dateMatcher = datePattern.matcher(forDate);

        if (dateMatcher.find()) {
            lessonsDate = dateMatcher.group(1);
            lessonsDate += '-';

            Pattern firstNumberPattern = Pattern.compile("(\\d+)");
            Matcher firstNumberMatcher = firstNumberPattern.matcher(lesson);

            if (firstNumberMatcher.find()) {
                Config config = new Config();
                String[] lessonStartTimes = config.getLessonStartTimes();

                try {
                    lessonsDate += lessonStartTimes[Integer.parseInt(firstNumberMatcher.group(1))];
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        if (lessonsDate.length() > 0) {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy'-'HH:mm", Locale.GERMANY);

            try {
                return dateFormat.parse(lessonsDate);
            }
            catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }
}

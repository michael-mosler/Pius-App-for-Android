package com.rmkrings.data.vertretungsplan;

import androidx.annotation.Nullable;

import com.rmkrings.helper.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Vertretungsplan {
    private String tickerText;
    private String additionalText;
    private String lastUpdate;
    private ArrayList<VertretungsplanForDate> vertretungsplaene;
    private String digest;

    public Vertretungsplan(JSONObject data) throws RuntimeException {
        try {
            tickerText = data.getString("tickerText");
        } catch (JSONException e) {
            e.printStackTrace();
            throw(new RuntimeException("Expected property tickerText not available in Vertetungsplan data"));
        }

        try {
            additionalText = data.getString("_additionalText");
        } catch (JSONException e) {
            e.printStackTrace();
            additionalText = null;
        }

        try {
            lastUpdate = data.getString("lastUpdate");
        } catch (JSONException e) {
            e.printStackTrace();
            throw(new RuntimeException("Expected property lastUpdate not available in Vertetungsplan data"));
        }

        try {
            vertretungsplaene = new ArrayList<>();
            JSONArray jsonDateItems = data.getJSONArray("dateItems");
            for (int i = 0; i < jsonDateItems.length(); i++) {
                JSONObject jsonDateItem = jsonDateItems.getJSONObject(i);
                VertretungsplanForDate vertretungsplanForDate = new VertretungsplanForDate(jsonDateItem);
                vertretungsplaene.add(vertretungsplanForDate);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new RuntimeException(("Failed to process date items from Vertretungsplan")));
        }

        try {
            digest = data.getString("_digest");
        } catch (JSONException e) {
            e.printStackTrace();
            digest = null;
        }
    }

    public String getTickerText() {
        return tickerText;
    }

    public String getAdditionalText() {
        return additionalText;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public ArrayList<VertretungsplanForDate> getVertretungsplaene() {
        return vertretungsplaene;
    }

    @Nullable
    public String getDigest() {
        return digest;
    }

    /**
     * @return Substitutions for current date.
     */
    @Nullable
    public VertretungsplanForDate getTodaysSchedule() {
        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        final String today = dateFormat.format(new Date());

        for (VertretungsplanForDate vertretungsplanForDate: vertretungsplaene) {
            String date = vertretungsplanForDate.getDate();
            if (date.substring(date.length() - 10).equals(today)) {
                return vertretungsplanForDate;
            }
        }

        return null;
    }

    /**
     * Returns next substitution record starting from now. If there is no next substitution null
     * is returned. Otherwise the result object contains details of next record, only.
     *
     * This function supposes that the object it is called on is a Vertretungsplan that is filtered
     * by grade.
     * @return Next substitution record as a filtered VertretungsplanForDate object.
     */
    @Nullable
    public VertretungsplanForDate next() {
        try {
            final Date currentDate = new Date();
            final Config config = new Config();
            final Pattern datePattern = Pattern.compile("(\\d{2}.\\d{2}.\\d{4})");
            final Pattern firstNumberPattern = Pattern.compile("\\d+");

            final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy'-'HH:mm", Locale.GERMANY);

            // Scan all dates. Extract date string, we will need it later on when it comes
            // to checking the exact time.
            for (VertretungsplanForDate vertretungsplanForDate : vertretungsplaene) {
                String forDate = vertretungsplanForDate.getDate();
                final Matcher dateMatcher = datePattern.matcher(forDate);
                if (dateMatcher.find()) {
                    final int dateStartIndex = dateMatcher.start();
                    final int dateEndIndex = dateMatcher.end();
                    forDate = forDate.substring(dateStartIndex, dateEndIndex).concat("-");

                    // Get the only grade item and scan substitutions for next record.
                    final ArrayList<GradeItem> gradeItems = vertretungsplanForDate.getGradeItems();
                    if (!gradeItems.isEmpty()) {
                        final GradeItem gradeItem = gradeItems.get(0);
                        for (String[] vertretungsplanItem : gradeItem.getVertretungsplanItems()) {
                            // Lesson range: We are interested in first number as this defines
                            // actual start time.
                            // If forDate + lesson start time is after currentDate then we have
                            // a match and must construct result object.
                            final String lessonRange = vertretungsplanItem[0];
                            final Matcher startLessonMatcher = firstNumberPattern.matcher(lessonRange);
                            if (startLessonMatcher.find()) {
                                final int lessonStartIndex = startLessonMatcher.start();
                                final int lessonEndIndex = startLessonMatcher.end();
                                final int startLesson = Integer.parseInt(lessonRange.substring(lessonStartIndex, lessonEndIndex));
                                final String lessonStartTime = config.getLessonStartTimes()[startLesson - 1];
                                final Date lessonStartDateAndTime = dateFormat.parse(forDate.concat(lessonStartTime));

                                if (Objects.requireNonNull(lessonStartDateAndTime).after(currentDate)) {
                                    GradeItem filteredGradeItem = new GradeItem(gradeItem, vertretungsplanItem);
                                    return new VertretungsplanForDate(vertretungsplanForDate, filteredGradeItem);
                                }
                            }
                        }
                    }
                }
            }

            return null;
        }
        catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}

package com.rmkrings.loader;

import com.rmkrings.helper.AppDefaults;

import java.net.URL;

public class EvaLoader extends HttpAuthenticatedGetLoader {

    private final String forGrade;

    public EvaLoader(String forGrade) {
        super();
        this.forGrade = forGrade;
    }

    protected URL getURL(String digest) throws java.net.MalformedURLException {
        String separator = "?";
        String urlString = String.format("%s/v2/eva", AppDefaults.getBaseUrl());

        if (digest != null) {
            urlString += String.format("%sdigest=%s", separator, digest);
            separator = "&";
        }

        if (forGrade != null) {
            urlString += String.format("%sgrade=%s", separator, forGrade);
        }

        if (AppDefaults.getCourseList().size() > 0) {
            StringBuilder mappedCourseList = new StringBuilder();
            for (String course: AppDefaults.getCourseList()) {
                String s = course
                        .replace(" ", "")
                        .replace("GK", "G")
                        .replace("LK", "L")
                        .replace("ZK", "Z");
                mappedCourseList.append((mappedCourseList.length() == 0) ? s : String.format(",%s", s));
            }

            urlString += String.format("%scourseList=%s", separator, mappedCourseList.toString());
        }

        return new URL(urlString);
    }
}

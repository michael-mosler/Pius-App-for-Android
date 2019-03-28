package com.rmkrings.helper;

public class StringHelper {
    public static String replaceHtmlEntities(String input) {
        if (input == null) {
            return "";
        }

        return input
                .replace("&auml;", "ä")
                .replace("&uuml;", "ü")
                .replace("&ouml;", "ö")
                .replace("&rarr;", "→")
                .replace("&nbsp;", "")
                .replace("\\", "")
                .trim();
    }
}

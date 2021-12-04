package com.rmkrings.helper;

import android.graphics.Point;
import android.text.Spannable;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.BackgroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.rmkrings.pius_app_for_android;
import com.rmkrings.activities.R;

import java.util.ArrayList;

public class FormatHelper {

    /**
     * Takes a text view displaying room substitution and, if → is contained in text, strikes
     * through text right before →
     * @param v - Textview holding room substitution text.
     */
    public static void roomText(TextView v) {
        if (v.length() == 0) {
            return;
        }

        CharSequence text = v.getText();
        int pos = text.toString().indexOf("→");

        if (pos > 1) {
            Spannable spannable = (Spannable)text;
            spannable.setSpan(new StrikethroughSpan(), 0, pos - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * Takes a room substitution string and if → is contained then strikes through
     * text right before →.
     * @param s - Room substitution string
     * @return Formatted room substitution string.
     */
    public static Spanned roomText(String s) {
        if (s.isEmpty()) {
            return new SpannedString("");
        }

        int pos = s.indexOf("→");
        if (pos > 1) {
            s = String.format("<strike>%s</strike>%s", s.substring(0, pos - 1), s.substring(pos));
        }

        return HtmlCompat.fromHtml(s, HtmlCompat.FROM_HTML_MODE_LEGACY);
    }

    /**
     * Highlights positions in text of a given text view.
     * @param v - Textview to highlight text in.
     * @param spans - Positions to highlight.
     */
    public static void highlight(TextView v, ArrayList<Point> spans) {
        if (v.length() == 0) {
            return;
        }

        CharSequence text = v.getText();
        Spannable spannable = (Spannable)text;
        for (Point span: spans) {
            spannable.setSpan(
                    new BackgroundColorSpan(ContextCompat.getColor(pius_app_for_android.getAppContext(), R.color.colorHighlight)),
                    span.x, span.y, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
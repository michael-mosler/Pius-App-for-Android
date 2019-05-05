package com.rmkrings.helper;

import android.graphics.Point;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.widget.TextView;

import com.rmkrings.main.pius_app.PiusApplication;
import com.rmkrings.pius_app_for_android.R;

import java.util.ArrayList;

public class FormatHelper {

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

    public static void highlight(TextView v, ArrayList<Point> spans) {
        if (v.length() == 0) {
            return;
        }

        CharSequence text = v.getText();
        Spannable spannable = (Spannable)text;
        for (Point span: spans) {
            spannable.setSpan(
                    new BackgroundColorSpan(PiusApplication.getAppContext().getResources().getColor(R.color.colorHighlight)),
                    span.x, span.y, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
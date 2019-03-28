package com.rmkrings.helper;

import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.widget.TextView;

public class FormatHelper {

    public static void roomText(TextView v) {
        CharSequence text = v.getText();

        if (v.length() == 0) {
            return;
        }

        int pos = text.toString().indexOf("→");

        if (pos > 1) {
            Spannable spannable = (Spannable)text;
            spannable.setSpan(new StrikethroughSpan(), 0, pos - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
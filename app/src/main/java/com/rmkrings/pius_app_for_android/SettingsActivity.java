package com.rmkrings.pius_app_for_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.NumberPicker;
import com.rmkrings.helper.Config;

public class SettingsActivity extends AppCompatActivity {

    private void titlesForPicker(NumberPicker mPicker, String[] titles) {
        Config config = new Config();

        mPicker.setMinValue(0);
        mPicker.setMaxValue(titles.length - 1);
        mPicker.setDisplayedValues(titles);
    }

    private void titlesForGradePicker() {
        Config config = new Config();
        titlesForPicker((NumberPicker)findViewById(R.id.gradePicker), config.getGrades());
    }

    private void titlesForClassPicker() {
        Config config = new Config();
        titlesForPicker((NumberPicker)findViewById(R.id.classPicker), config.getClasses());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("Einstellungen");
        titlesForGradePicker();
        titlesForClassPicker();
    }
}

package com.rmkrings.pius_app_for_android;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.NumberPicker;
import android.widget.EditText;
import android.widget.Button;
import com.rmkrings.helper.Config;

public class SettingsActivity extends AppCompatActivity {
    // Outlets
    private EditText mUserName;
    private EditText mPassword;
    private Button mLoginButton;
    private Button mCoursesButton;
    private NumberPicker mGradePicker;
    private NumberPicker mClassPicker;

    // Internal state.
    private Config config = new Config();
    private String sUserName = "";
    private String sPassword = "";

    private boolean isUpperGradeSelected(int val) {
        return config.isUpperGrade(config.getGrades()[val]);
    }

    private boolean isLowerGradeSelected(int val) {
        return config.isLowerGrade(config.getGrades()[val]);
    }

    private void setElementStates(int forSelectedGradeVal) {
        // If grade "None" is selected class picker also is set to None.
        if (forSelectedGradeVal == 0) {
            mClassPicker.setValue(0);
            // AppDefaults.selectedClassRow = 0;

            mClassPicker.setEnabled(false);
            mCoursesButton.setEnabled(false);
        }

        // When user has selected EF, Q1 or Q2 set class picker view to "None" and disable.
        // Enable "Meine Kurse" button.
        else if (isUpperGradeSelected(forSelectedGradeVal)) {
            mClassPicker.setValue(0);
            //AppDefaults.selectedClassRow = 0;

            mClassPicker.setEnabled(false);
            mCoursesButton.setEnabled(true);
        }

        // When a lower grade is selected disable "Meine Kurse" button and make sure
        // that class is defined.
        else if (isLowerGradeSelected(forSelectedGradeVal)) {
            if (mClassPicker.getValue() == 0) {
                mClassPicker.setValue(1);
                // AppDefaults.selectedClassRow = 1;
            }

            mClassPicker.setEnabled(true);
            mCoursesButton.setEnabled(false);
        }

        // Neither
        else {
            mClassPicker.setValue(0);
            // AppDefaults.selectedClassRow = 0;
            mCoursesButton.setEnabled(false);
        }
    }


    private void titlesForPicker(NumberPicker mPicker, String[] titles) {
        mPicker.setMinValue(0);
        mPicker.setMaxValue(titles.length - 1);
        mPicker.setDisplayedValues(titles);
    }

    private void titlesForGradePicker() {
        titlesForPicker(mGradePicker, config.getGrades());
    }

    private void titlesForClassPicker() {
        titlesForPicker(mClassPicker, config.getClasses());
    }

    private void gradePickerDidSelectRow(int val) {
        if (!isUpperGradeSelected(val) /* && && isUpperGradeSelected(AppDefaults.selectedGradeRow! */) {
            mClassPicker.setValue(1);
            // AppDefaults.selectedClassRow = 1;
        }

        // AppDefaults.selectedGradeRow = row;
        setElementStates(val);
    }

    private void classPickerDidSelect(int val) {
        if (val == 0 && !isUpperGradeSelected(mGradePicker.getValue())) {
            mClassPicker.setValue(1);
            // AppDefaults.selectedClassRow = 1;
        } else {
            // AppDefaults.selectedClassRow = row;
        }

    }

    private void setLoginButtonState() {
        boolean enabled = sUserName.length() > 0 && sPassword.length() > 0;
        mLoginButton.setEnabled(enabled);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mGradePicker = findViewById(R.id.gradePicker);
        mClassPicker = findViewById(R.id.classPicker);
        mCoursesButton = findViewById(R.id.mycourses);
        mUserName = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.login);

        mGradePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                gradePickerDidSelectRow(newVal);
            }
        });

        mClassPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                classPickerDidSelect(newVal);
            }
        });

        // Check if login button must be enabled or disabled while typing on username or
        // password field.
        mUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                sUserName = mUserName.getText().toString();
                setLoginButtonState();
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                sPassword = mPassword.getText().toString();
                setLoginButtonState();
            }
        });

        // Hide soft keyboard when login button clicked.
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mLoginButton.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
            }
        });

        setTitle("Einstellungen");
        titlesForGradePicker();
        titlesForClassPicker();
    }
}

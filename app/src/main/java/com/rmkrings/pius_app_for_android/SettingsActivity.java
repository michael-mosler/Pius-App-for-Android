package com.rmkrings.pius_app_for_android;

import android.content.Context;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.NumberPicker;
import android.widget.EditText;
import android.widget.Button;

import com.rmkrings.PiusApp;
import com.rmkrings.helper.Config;
import com.rmkrings.http.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.vertretungsplandata.VertretungsplanLoader;

public class SettingsActivity extends AppCompatActivity implements HttpResponseCallback {

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

    /**
     * Check if grade picker index value identifies an upper grade row.
     * @param val - Grade picker index value.
     * @return - true when upper grade is selected.
     */
    private boolean isUpperGradeSelected(int val) {
        return config.isUpperGrade(config.getGrades()[val]);
    }

    /**
     * Check if grade picker index value identifies a lower grade row.
     * @param val - Grade picker index value.
     * @return - true when lower grade is selected.
     */
    private boolean isLowerGradeSelected(int val) {
        return config.isLowerGrade(config.getGrades()[val]);
    }

    /**
     * Brings grade, class picker and login button into a consistent state depending
     * on the current grade selection.
     * @param forSelectedGradeVal - Currently selected grade index value.
     */
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

    /**
     * Sets titles for a picker.
     * @param mPicker - Picker which titles are set for
     * @param titles - The titles to set.
     */
    private void titlesForPicker(NumberPicker mPicker, String[] titles) {
        mPicker.setMinValue(0);
        mPicker.setMaxValue(titles.length - 1);
        mPicker.setDisplayedValues(titles);
    }

    /**
     * Set titles for grade picker.
     */
    private void titlesForGradePicker() {
        titlesForPicker(mGradePicker, config.getGrades());
    }

    /**
     * Set titles for class picker.
     */
    private void titlesForClassPicker() {
        titlesForPicker(mClassPicker, config.getClasses());
    }

    /**
     * Grade picker selection has changed callback.
     * @param val - New value (index not actual text).
     */
    private void gradePickerDidSelectRow(int val) {
        if (!isUpperGradeSelected(val) /* && && isUpperGradeSelected(AppDefaults.selectedGradeRow! */) {
            mClassPicker.setValue(1);
            // AppDefaults.selectedClassRow = 1;
        }

        // AppDefaults.selectedGradeRow = row;
        setElementStates(val);
    }

    /**
     * Class picker selection has changed callback.
     * @param val - New value (index not the actual text).
     */
    private void classPickerDidSelect(int val) {
        if (val == 0 && !isUpperGradeSelected(mGradePicker.getValue())) {
            mClassPicker.setValue(1);
            // AppDefaults.selectedClassRow = 1;
        } else {
            // AppDefaults.selectedClassRow = row;
        }

    }

    /**
     * Enables or disables Login Button depending on username and password EditText
     * content.
     */
    private void setLoginButtonState() {
        boolean enabled = sUserName.length() > 0 && sPassword.length() > 0;
        mLoginButton.setEnabled(enabled);

    }

    // Update Login button text depending on authentication state.
    private void updateLoginButtonText(boolean authenticated) {
        if (authenticated) {
            mLoginButton.setText("Abmelden");
        } else {
            mLoginButton.setText("Anmelden");
        }
    }


    /**
     * The iOS viewDidLoad equivalent. Do all the initialisation stuff.
     * @param savedInstanceState - Saved instance state to return to.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialise outlets.
        mGradePicker = findViewById(R.id.gradePicker);
        mClassPicker = findViewById(R.id.classPicker);
        mCoursesButton = findViewById(R.id.mycourses);
        mUserName = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.login);

        // Register listeners for all interactive components.
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
        final SettingsActivity sa = this;
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mLoginButton.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);

                mLoginButton.setEnabled(false);
                VertretungsplanLoader.validateLogin("Papst", "PiusX", sa);
            }
        });

        setTitle("Einstellungen");
        titlesForGradePicker();
        titlesForClassPicker();
    }

    @Override
    public void execute(boolean statusOk, boolean isError, HttpResponseData data) {
        mLoginButton.setEnabled(true);

        // create the alert
        String message;
        if (isError) {
            message = "Es ist ein Fehler aufgetreten. Bitte überprüfe Deine Internetverbindung und versuche es noch einmal.";
        } else {
            message = (statusOk) ? "Du bist nun angemeldet." : "Die Anmeldedaten sind ungültig.";
        }

        new AlertDialog.Builder(PiusApp.getAppContext())
                .setTitle("Anmeldung")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();

        // Store current authentication state in user settings and update text of
        // login button.
        if (statusOk) {
            // AppDefaults.authenticated = true;
            mUserName.setEnabled(false);
            mPassword.setEnabled(false);
        } else {
            // AppDefaults.authenticated = false;
        }

        updateLoginButtonText(statusOk);
    }
}

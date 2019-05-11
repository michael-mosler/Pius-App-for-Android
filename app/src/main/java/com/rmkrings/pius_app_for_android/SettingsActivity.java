package com.rmkrings.pius_app_for_android;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.NumberPicker;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;

import com.rmkrings.helper.Config;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.loader.VertretungsplanLoader;

public class SettingsActivity extends AppCompatActivity
        implements HttpResponseCallback
{

    // Outlets
    private EditText mUserName;
    private EditText mPassword;
    private Button mLoginButton;
    private Button mCoursesButton;
    private NumberPicker mGradePicker;
    private NumberPicker mClassPicker;
    private ProgressBar mProgressBar;

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
            AppDefaults.setSelectedClassRow(0);

            mClassPicker.setEnabled(false);
            mCoursesButton.setEnabled(false);
        }

        // When user has selected EF, Q1 or Q2 set class picker view to "None" and disable.
        // Enable "Meine Kurse" button.
        else if (isUpperGradeSelected(forSelectedGradeVal)) {
            mClassPicker.setValue(0);
            AppDefaults.setSelectedClassRow(0);

            mClassPicker.setEnabled(false);
            mCoursesButton.setEnabled(true);
        }

        // When a lower grade is selected disable "Meine Kurse" button and make sure
        // that class is defined.
        else if (isLowerGradeSelected(forSelectedGradeVal)) {
            if (mClassPicker.getValue() == 0) {
                mClassPicker.setValue(1);
                AppDefaults.setSelectedClassRow(1);
            }

            mClassPicker.setEnabled(true);
            mCoursesButton.setEnabled(false);
        }

        // Neither
        else {
            mClassPicker.setValue(0);
            AppDefaults.setSelectedClassRow(0);
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
            AppDefaults.setSelectedClassRow(1);
        }

        AppDefaults.setSelectedGradeRow(val);
        setElementStates(val);
    }

    /**
     * Class picker selection has changed callback.
     * @param val - New value (index not the actual text).
     */
    private void classPickerDidSelect(int val) {
        if (val == 0 && !isUpperGradeSelected(mGradePicker.getValue())) {
            mClassPicker.setValue(1);
            AppDefaults.setSelectedClassRow(1);
        } else {
            AppDefaults.setSelectedClassRow(val);
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
            mLoginButton.setText(getResources().getString(R.string.button_logout));
        } else {
            mLoginButton.setText(getResources().getString(R.string.button_login));
        }
    }

    /**
     * When not logged on reads username and password from input fields and starts validation.
     * This will cause execute()-callback to be called. Username an password are stored in
     * preferences before hand.
     * When logged on credentials are deleted and user is informed on logout.
     */
    private void saveCredentials() {
        // User is not authenticated; in this case we want to set credentials.
        if (!AppDefaults.isAuthenticated()) {
            // Save credentials in user defaults.
            AppDefaults.setUsername(mUserName.getText().toString());
            AppDefaults.setPassword(mPassword.getText().toString());

            // Show activity indicator and disable user interaction.
            mLoginButton.setEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // Validate credentials; this will also update authenticated state
            // of the app.
            VertretungsplanLoader.validateLogin(AppDefaults.getUsername(), AppDefaults.getPassword(), this);
        } else {
            // User is authenticated and wants to logout.
            mUserName.setText("");
            mPassword.setText("");

            // Delete credential from from user settings and clear text of username
            // and password field.
            AppDefaults.setUsername("");
            AppDefaults.setPassword("");
            AppDefaults.setAuthenticated(false);
            updateLoginButtonText(false);

            // Inform user on new login state.
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.title_logon))
                    .setMessage(getResources().getString(R.string.text_logged_out))
                    .setPositiveButton(android.R.string.ok, null)
                    .show();

            mUserName.setEnabled(true);
            mPassword.setEnabled(true);
        }
    }

    private void showCredentials() {
        sUserName = AppDefaults.getUsername();
        sPassword = AppDefaults.getPassword();
        mUserName.setText(sUserName);
        mPassword.setText(sPassword);

        updateLoginButtonText(AppDefaults.isAuthenticated());
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
        mProgressBar = findViewById((R.id.progressBar));

        titlesForGradePicker();
        titlesForClassPicker();

        boolean isAuthenticated = AppDefaults.isAuthenticated();
        mUserName.setEnabled(!isAuthenticated);
        mPassword.setEnabled(!isAuthenticated);

        mClassPicker.setValue(AppDefaults.getSelectedClassRow());
        mGradePicker.setValue(AppDefaults.getSelectedGradeRow());

        setElementStates(AppDefaults.getSelectedGradeRow());
        showCredentials();

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
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mLoginButton.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
                saveCredentials();
            }
        });

        mCoursesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(SettingsActivity.this, CourseListActivity.class);
                startActivity(a);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(getResources().getString(R.string.title_settings));
        setLoginButtonState();
    }

    /**
     * Submit login request to middleware callback: Checks response data for outcome
     * and informs user by popup. In case of success authenticated flag is set in
     * AppDefaults.
     * @param data - Response data from Validate Login request.
     */
    @Override
    public void execute(HttpResponseData data) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressBar.setVisibility(View.GONE);
        mLoginButton.setEnabled(true);

        // Show popup with info outcome.
        String message;
        if (data.isError() || (data.getHttpStatusCode() != 200 && data.getHttpStatusCode() != 401)) {
            message = getResources().getString(R.string.text_logon_error);
        } else {
            message = getResources().getString((data.getHttpStatusCode() == 200) ? R.string.text_logged_on : R.string.text_invalid_credentials);
        }

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.title_logon))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();

        // Store current authentication state in user settings and update text of
        // login button.
        if (data.getHttpStatusCode() == 200) {
            AppDefaults.setAuthenticated(true);
            mUserName.setEnabled(false);
            mPassword.setEnabled(false);
            updateLoginButtonText(true);
        } else {
            AppDefaults.setAuthenticated(false);
            updateLoginButtonText(false);
        }
    }
}

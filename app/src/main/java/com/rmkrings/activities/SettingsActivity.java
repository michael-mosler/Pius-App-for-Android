package com.rmkrings.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.TextView;

import com.rmkrings.helper.Config;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.loader.VertretungsplanLoader;
import com.rmkrings.notifications.DashboardWidgetUpdateService;
import com.rmkrings.notifications.PiusAppMessageService;
import com.rmkrings.pius_app_for_android;

import cdflynn.android.library.checkview.CheckView;

public class SettingsActivity extends AppCompatActivity implements HttpResponseCallback
{
    // Outlets
    private EditText mUserName;
    private EditText mPassword;
    private Button mLoginButton;
    private Button mCoursesButton;
    private NumberPicker mGradePicker;
    private NumberPicker mClassPicker;
    private ProgressBar mProgressBar;
    private CheckView mSuccessCheckMark;

    // Internal state.
    private final Config config = new Config();
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

    /**
     * Update Login button text depending on authentication state.
     * @param authenticated - Indicates if user authenticated or not.
     */
    private void updateLoginButtonText(boolean authenticated) {
        if (authenticated) {
            mLoginButton.setText(getResources().getString(R.string.button_logout));
            mLoginButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            mLoginButton.setText(getResources().getString(R.string.button_login));
            mLoginButton.setBackgroundColor(getResources().getColor(R.color.colorPiusBlue));
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
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setTitle(getResources().getString(R.string.title_logout))
                    .setMessage(getResources().getString(R.string.text_confirm_logout))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // User is authenticated and wants to logout.
                            mUserName.setText("");
                            mPassword.setText("");

                            // Delete credential from from user settings and clear text of username
                            // and password field.
                            AppDefaults.setUsername("");
                            AppDefaults.setPassword("");
                            AppDefaults.setAuthenticated(false);
                            updateLoginButtonText(false);

                            mUserName.setEnabled(true);
                            mPassword.setEnabled(true);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
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
     * The iOS viewDidLoad equivalent. Do all the initialization stuff.
     * @param savedInstanceState - Saved instance state to return to.
     */
    @SuppressLint("SetTextI18n")
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
        mSuccessCheckMark = findViewById(R.id.successCheckMark);
        mSuccessCheckMark.setVisibility(View.GONE);

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

        TextView mVersionInfo = findViewById(R.id.version);
        try {
            String versionName = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            mVersionInfo.setText(versionName);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            mVersionInfo.setText("Dev");
        }
    }

    /**
     * Resumes activity after it has been suspended whenever user returns.
     */
    @Override
    public void onResume() {
        super.onResume();
        setTitle(getResources().getString(R.string.title_settings));
        setLoginButtonState();
    }

    /**
     * Back button callback: Saves configuration and re-registers device token
     * information in backend.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Update widget when new data has been loaded.
        Context context = pius_app_for_android.getAppContext();
        Intent intent = new Intent(context, DashboardWidgetUpdateService.class);
        context.startService(intent);

        // When still not authenticated then clear username and password.
        if (!AppDefaults.isAuthenticated()) {
            AppDefaults.setUsername("");
            AppDefaults.setPassword("");
        }

        PiusAppMessageService piusAppMessageService = new PiusAppMessageService();
        piusAppMessageService.updateDeviceToken();
    }

    /**
     * Submit login request to middleware callback: Checks response data for outcome
     * and informs user by popup. In case of success authenticated flag is set in
     * AppDefaults.
     * @param data - Response data from Validate Login request.
     */
    @Override
    public void execute(HttpResponseData data) {
        try {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mProgressBar.setVisibility(View.GONE);
            mLoginButton.setEnabled(true);

            // Store current authentication state in user settings and update text of
            // login button.
            if (data.getHttpStatusCode() == 200) {
                AppDefaults.setAuthenticated(true);
                mUserName.setEnabled(false);
                mPassword.setEnabled(false);
                updateLoginButtonText(true);

                mSuccessCheckMark.setVisibility(View.VISIBLE);
                mSuccessCheckMark.check();
                mSuccessCheckMark.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSuccessCheckMark.setVisibility(View.GONE);
                    }
                }, 2000);
            } else {
                AppDefaults.setAuthenticated(false);
                updateLoginButtonText(false);
            }

            // Show popup with info outcome.
            // Check if app is finishing. Only if not we may show popup.
            // Otherwise app is likely to crash as context is getting lost.
            String message;
            if (!isFinishing() && data.getHttpStatusCode() != 200) {
                if (data.isError()) {
                    message = getResources().getString(R.string.text_logon_error);
                } else if (data.getHttpStatusCode() != 401) {
                    message = getResources().getString(R.string.text_logon_error);
                } else {
                    message = getResources().getString(R.string.text_invalid_credentials);
                }

                new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                        .setTitle(getResources().getString(R.string.title_logon))
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        } catch (Exception e) {
            // There is not much we can do when an exception occus. Most likely user
            // has dismissed activity while we were waiting for REST request reply.
            // In this case no UI operations are permitted and can easily cause
            // invalid state exceptions. Compared to iOS this one of the biggest
            // design flaws in Android. I would expect OS to handle this transparently.
            e.printStackTrace();
        }
    }
}

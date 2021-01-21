package com.rmkrings.fragments.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.rmkrings.activities.CourseListActivity;
import com.rmkrings.activities.R;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Config;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.interfaces.IOnBackPressed;
import com.rmkrings.loader.VertretungsplanLoader;
import com.rmkrings.notifications.DashboardWidgetUpdateService;
import com.rmkrings.notifications.PiusAppMessageService;
import com.rmkrings.pius_app_for_android;

import java.util.Objects;

import cdflynn.android.library.checkview.CheckView;

@SuppressWarnings("Convert2Lambda")
public class GeneralPreferencesFragment extends Fragment implements HttpResponseCallback, IOnBackPressed {
    // Outlets
    private EditText mUserName;
    private EditText mPassword;
    private Button mLoginButton;
    private Button mCoursesButton;
    private NumberPicker mGradePicker;
    private NumberPicker mClassPicker;
    private ProgressBar mProgressBar;
    private CheckView mSuccessCheckMark;

    // Internal state
    private final Config config = new Config();
    private String sUserName = "";
    private String sPassword = "";
    private boolean isAuthenticated = false;

    public GeneralPreferencesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preferences_general, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mGradePicker = view.findViewById(R.id.gradePicker);
        mClassPicker = view.findViewById(R.id.classPicker);
        mCoursesButton = view.findViewById(R.id.mycourses);
        mUserName = view.findViewById(R.id.username);
        mPassword = view.findViewById(R.id.password);
        mLoginButton = view.findViewById(R.id.login);
        mProgressBar = view.findViewById((R.id.progressBar));
        mSuccessCheckMark = view.findViewById(R.id.successCheckMark);
        mSuccessCheckMark.setVisibility(View.GONE);

        titlesForGradePicker();
        titlesForClassPicker();

        isAuthenticated = AppDefaults.isAuthenticated();
        mUserName.setEnabled(!isAuthenticated);
        mPassword.setEnabled(!isAuthenticated);

        mClassPicker.setValue(AppDefaults.getSelectedClassRow());
        mGradePicker.setValue(AppDefaults.getSelectedGradeRow());

        setElementStates(AppDefaults.getSelectedGradeRow());
        System.out.println(isAuthenticated + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                sUserName = mUserName.getText().toString();
                setLoginButtonState();
            }
        });


        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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
                InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mLoginButton.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
                saveCredentials();
            }
        });

        mCoursesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(getActivity(), CourseListActivity.class);
                startActivity(a);
            }
        });
    }

    /**
     * Check if grade picker index value identifies an upper grade row.
     *
     * @param val - Grade picker index value.
     * @return - true when upper grade is selected.
     */
    private boolean isUpperGradeSelected(int val) {
        return config.isUpperGrade(config.getGrades()[val]);
    }

    /**
     * Check if grade picker index value identifies a lower grade row.
     *
     * @param val - Grade picker index value.
     * @return - true when lower grade is selected.
     */
    private boolean isLowerGradeSelected(int val) {
        return config.isLowerGrade(config.getGrades()[val]);
    }

    /**
     * Brings grade, class picker and login button into a consistent state depending
     * on the current grade selection.
     *
     * @param forSelectedGradeVal - Currently selected grade index value.
     */
    private void setElementStates(int forSelectedGradeVal) {
        System.out.println("?????????????????????????????????????????????" + forSelectedGradeVal);
        // If grade "None" is selected class picker also is set to None.
        if (forSelectedGradeVal == 0) {
            mClassPicker.setValue(0);
            AppDefaults.setSelectedClassRow(0);

            mClassPicker.setEnabled(false);
            mCoursesButton.setEnabled(false);
            mCoursesButton.setBackgroundResource(R.drawable.button_disabled);
        }

        // When user has selected EF, Q1 or Q2 set class picker view to "None" and disable.
        // Enable "Meine Kurse" button.
        else if (isUpperGradeSelected(forSelectedGradeVal)) {
            mClassPicker.setValue(0);
            AppDefaults.setSelectedClassRow(0);

            mClassPicker.setEnabled(false);
            mCoursesButton.setEnabled(isAuthenticated);
            if (isAuthenticated){
                mCoursesButton.setBackgroundResource(R.drawable.button_default);
            }else {
                mCoursesButton.setBackgroundResource(R.drawable.button_disabled);
            }

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
            mCoursesButton.setBackgroundResource(R.drawable.button_disabled);
        }

        // Neither
        else {
            mClassPicker.setValue(0);
            AppDefaults.setSelectedClassRow(0);
            mCoursesButton.setEnabled(false);
            mCoursesButton.setBackgroundResource(R.drawable.button_disabled);
        }
    }

    /**
     * Sets titles for a picker.
     *
     * @param mPicker - Picker which titles are set for
     * @param titles  - The titles to set.
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
     *
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
     *
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
     *
     * @param authenticated - Indicates if user authenticated or not.
     */
    private void updateLoginButtonText(boolean authenticated) {
        if (authenticated) {
            mLoginButton.setText(getResources().getString(R.string.button_logout));
//            mLoginButton.setBackground(getResources().getDrawable(R.drawable.button_default_red));
            mLoginButton.setBackgroundResource(R.drawable.button_default_red);
            AppDefaults.setAuthenticated(true);
            isAuthenticated = true;
            setElementStates(AppDefaults.getSelectedGradeRow());
        } else {
            mLoginButton.setText(getResources().getString(R.string.button_login));
            mLoginButton.setBackgroundResource(R.drawable.button_default);
            AppDefaults.setAuthenticated(false);
            isAuthenticated = false;
            setElementStates(AppDefaults.getSelectedGradeRow());
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
            Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // Validate credentials; this will also update authenticated state
            // of the app.
            VertretungsplanLoader.validateLogin(AppDefaults.getUsername(), AppDefaults.getPassword(), this);
        } else {
            new AlertDialog.Builder(Objects.requireNonNull(getActivity()), R.style.AlertDialogTheme)
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


    public String getTitle() {
        return pius_app_for_android.getAppContext().getResources().getString(R.string.title_peferences_general);
    }

    /**
     * Resumes activity after it has been suspended whenever user returns.
     */
    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getActivity()).setTitle(getResources().getString(R.string.title_settings));
        setLoginButtonState();
    }

    /**
     * Back button callback: Saves configuration and re-registers device token
     * information in backend.
     */
    @Override
    public void onBackPressed() {

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

    @Override
    public void execute(HttpResponseData data) {
        try {
            Objects.requireNonNull(getActivity()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
            if (!Objects.requireNonNull(getActivity()).isFinishing() && data.getHttpStatusCode() != 200) {
                if (data.isError()) {
                    message = getResources().getString(R.string.text_logon_error);
                } else if (data.getHttpStatusCode() != 401) {
                    message = getResources().getString(R.string.text_logon_error);
                } else {
                    message = getResources().getString(R.string.text_invalid_credentials);
                }

                new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme)
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

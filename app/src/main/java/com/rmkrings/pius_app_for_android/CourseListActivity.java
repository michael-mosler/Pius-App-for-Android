package com.rmkrings.pius_app_for_android;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.rmkrings.data.adapter.CourseListAdapter;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Config;
import com.rmkrings.main.pius_app.PiusApplication;

import java.util.ArrayList;

public class CourseListActivity extends AppCompatActivity {

    private EditText mEditText;
    private NumberPicker mCoursePicker;
    private NumberPicker mCourseTypePicker;
    private NumberPicker mCourseNumberPicker;
    private RecyclerView mCourseList;
    private RecyclerView.Adapter mAdapter;

    // Internal state.
    private final Config config = new Config();
    private final ArrayList<String> courseList = new ArrayList<>();

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
     * Set titles for course picker.
     */
    private void titlesForCoursePicker() {
        titlesForPicker(mCoursePicker, config.getCourses());
    }

    /**
     * Set titles for course type picker.
     */
    private void titlesForCourseTypePicker() {
        titlesForPicker(mCourseTypePicker, config.getCourseTypes());
    }

    /**
     * Set titles for course number picker.
     */
    private void titlesForCourseNumberPicker() {
        titlesForPicker(mCourseNumberPicker, config.getCourseNumbers());
    }

    private void dismissKeyboard() {
        if (this.getCurrentFocus() != null) {
            InputMethodManager inputManager = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        mEditText.clearFocus();
    }

    private void addCourseFromPickers() {
        String realCourseName;
        String courseNameFromEdit = mEditText.getText().toString();

        if (courseNameFromEdit.length() > 0) {
            realCourseName = courseNameFromEdit;
            mEditText.setText("");
            dismissKeyboard();
        } else {
            String courseName = config.getCoursesShortNames()[mCoursePicker.getValue ()];
            String courseType = config.getCourseTypes()[mCourseTypePicker.getValue()];
            String courseNumber = config.getCourseNumbers()[mCourseNumberPicker.getValue()];

            realCourseName = (courseType.equals("P") || courseType.equals("V"))
                    ? String.format("%s%s%s", courseType, courseName, courseNumber)
                    : String.format("%s %s%s", courseName, courseType, courseNumber);
        }

        courseList.add(realCourseName);
        mAdapter.notifyDataSetChanged();
        mCourseList.scrollToPosition(courseList.size() - 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_course_list);

        // Outlets
        // Outlets
        Button mAddCourseButton = findViewById(R.id.addCourseButton);
        Button mDeleteAllButton = findViewById(R.id.deleteAll);
        mEditText = findViewById(R.id.editText);
        mCoursePicker = findViewById(R.id.coursePicker);
        mCourseTypePicker = findViewById(R.id.courseTypePicker);
        mCourseNumberPicker = findViewById(R.id.courseNumberPicker);
        mCourseList = findViewById(R.id.courseList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PiusApplication.getAppContext(), LinearLayoutManager.VERTICAL, false);
        mCourseList.setLayoutManager(mLayoutManager);
        mAdapter = new CourseListAdapter(courseList);
        mCourseList.setAdapter(mAdapter);

        titlesForCoursePicker();
        titlesForCourseTypePicker();
        titlesForCourseNumberPicker();

        mAddCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourseFromPickers();
            }
        });

        final CourseListActivity self = this;

        mDeleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inform user on new login state.
                new AlertDialog.Builder(self)
                        .setTitle(getResources().getString(R.string.title_delete_course_list_all))
                        .setMessage(getResources().getString(R.string.text_confirm_delete_course_list))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                courseList.clear();
                                mAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppDefaults.setCourseList(courseList);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        AppDefaults.setCourseList(courseList);
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.title_add_courses);

        courseList.addAll(AppDefaults.getCourseList());
        mAdapter.notifyDataSetChanged();
    }
}

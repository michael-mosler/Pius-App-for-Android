package com.rmkrings.fragments.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.rmkrings.activities.R;
import com.rmkrings.data.adapter.CourseListAdapter;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Config;
import com.rmkrings.pius_app_for_android;

import java.util.ArrayList;

/**
 */
public class ContentCourseListFragment extends Fragment {

    private EditText mEditText;
    private NumberPicker mCoursePicker;
    private NumberPicker mCourseTypePicker;
    private NumberPicker mCourseNumberPicker;
    private RecyclerView mCourseList;
    private RecyclerView.Adapter<?> mAdapter;

    // Internal state.
    private final Config config = new Config();
    private final ArrayList<String> courseList = new ArrayList<>();

    public ContentCourseListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_preferences_course_list, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Outlets
        Button mAddCourseButton = view.findViewById(R.id.addCourseButton);
        Button mDeleteAllButton = view.findViewById(R.id.deleteAll);
        mEditText = view.findViewById(R.id.editText);
        mCoursePicker = view.findViewById(R.id.coursePicker);
        mCourseTypePicker = view.findViewById(R.id.courseTypePicker);
        mCourseNumberPicker = view.findViewById(R.id.courseNumberPicker);
        mCourseList = view.findViewById(R.id.courseList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(pius_app_for_android.getAppContext(), LinearLayoutManager.VERTICAL, false);
        mCourseList.setLayoutManager(mLayoutManager);
        mAdapter = new CourseListAdapter(courseList);
        mCourseList.setAdapter(mAdapter);

        titlesForCoursePicker();
        titlesForCourseTypePicker();
        titlesForCourseNumberPicker();

        mAddCourseButton.setOnClickListener(v -> addCourseFromPickers());

        mDeleteAllButton.setOnClickListener(v -> {
            // Inform user on new login state.
            new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                    .setTitle(getResources().getString(R.string.title_delete_course_list_all))
                    .setMessage(getResources().getString(R.string.text_confirm_delete_course_list))
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        courseList.clear();
                        mAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        AppDefaults.setCourseList(courseList);
    }

    @Override
    public void onStop() {
        super.onStop();
        AppDefaults.setCourseList(courseList);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        // setTitle(R.string.title_add_courses);
        courseList.clear();
        courseList.addAll(AppDefaults.getCourseList());
        mAdapter.notifyDataSetChanged();

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
        final View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager)requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        mEditText.clearFocus();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addCourseFromPickers() {
        String realCourseName;
        String courseNameFromEdit = mEditText.getText().toString();

        if (courseNameFromEdit.length() > 0) {
            realCourseName = courseNameFromEdit;
            mEditText.setText("");
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

        dismissKeyboard();
    }
}
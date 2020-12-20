package com.rmkrings.fragments.preferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rmkrings.activities.R;
import com.rmkrings.pius_app_for_android;

public class AboutPreferencesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preferences_about, container, false);
    }

    public String getTitle() {
        return pius_app_for_android.getAppContext().getResources().getString(R.string.title_peferences_about);
    }
}

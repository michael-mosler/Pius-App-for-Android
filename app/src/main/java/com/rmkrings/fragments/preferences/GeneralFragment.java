package com.rmkrings.fragments.preferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rmkrings.activities.R;

public class GeneralFragment extends Fragment {

    public static GeneralFragment newInstance() {

        return new GeneralFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preferences_general, container, false);
    }

    public static String getTitle() {
        //TODO get title from strings.xml
        return "Allgemein";
    }
}

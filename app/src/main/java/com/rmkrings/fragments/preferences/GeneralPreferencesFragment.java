package com.rmkrings.fragments.preferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.rmkrings.activities.R;
import com.rmkrings.pius_app_for_android;

/**
 * This class implements a container for general preferences. Initially view container is
 * filled with general preferences fragment. Container content gets replaced when navigating
 * to sub-views.
 */
public class GeneralPreferencesFragment extends Fragment {

    public GeneralPreferencesFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preferences_general, container, false);

        FragmentTransaction t = requireActivity().getSupportFragmentManager().beginTransaction();
        t.add(R.id.generalPreferencesFrameLayout, new ContentGeneralPreferencesFragment(this));
        t.commit();

        return view;
    }

    /**
     * Navigates to the given fragment.
     * @param fragment Fragment to navigate to.
     */
    public void navigateTo(Fragment fragment) {
        FragmentTransaction t = requireActivity().getSupportFragmentManager().beginTransaction();
        t.replace(R.id.generalPreferencesFrameLayout, fragment);
        t.addToBackStack(null);
        t.commit();
    }
    /**
     * Gets title.
     * @return Title
     */
    public static String getTitle() {
        return pius_app_for_android.getAppContext().getResources().getString(R.string.title_peferences_general);
    }
}

package com.rmkrings.fragments.preferences;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Outlets
        TextView mVersion = view.findViewById(R.id.version);

        try{
            String version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            mVersion.setText(getResources().getText(R.string.label_app_name).toString().replaceAll("%v", version));
        } catch (PackageManager.NameNotFoundException ex){
            ex.printStackTrace();
            mVersion.setText(getResources().getText(R.string.label_app_name).toString().replaceAll("%v", "Dev"));
        }
    }

    public String getTitle() {
        return pius_app_for_android.getAppContext().getResources().getString(R.string.title_peferences_about);
    }
}

package com.rmkrings.fragments.preferences;

import static com.rmkrings.fragments.StaffListAdapter.SUPPLEMENT;
import static com.rmkrings.fragments.StaffListAdapter.TEACHERS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.rmkrings.activities.R;
import com.rmkrings.data.staff.StaffDictionary;
import com.rmkrings.data.staff.StaffMember;
import com.rmkrings.fragments.StaffListAdapter;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.loader.StaffLoader;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StaffListPreferencesFragment extends Fragment implements HttpResponseCallback {

    private ExpandableListView staffListListView;
    private StaffListAdapter staffListAdapter;
    private final HashMap<String, StaffDictionary> listDataChild = new HashMap<>(0);

    private StaffDictionary staffDictionary;
    private final StaffLoader staffLoader = new StaffLoader();
    private String searchTerm;

    public StaffListPreferencesFragment() {
        // Required constructor, empty.
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preferences_staff_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StaffListPreferencesFragment self = this;

        staffListListView = view.findViewById(R.id.staffListView);
        staffListAdapter = new StaffListAdapter(getActivity(), listDataChild);
        staffListListView.setAdapter(staffListAdapter);

        SearchView mSearchView = view.findViewById(R.id.staffSearchInput);
        mSearchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                self.expandGroupsIfNeeed();
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                self.expandGroupsIfNeeed();
                searchTerm = query;
                notifyChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                self.expandGroupsIfNeeed();
                searchTerm = newText;
                notifyChanged();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    /**
     * Returns tab title.
     *
     * @return Tab title.
     */
    public static String getTitle() {
        return "Mitarbeiter";
    }

    /**
     * Reloads staff data.
     */
    private void reload() {
        staffLoader.load(this);
    }

    private void notifyChanged() {
        // Guard, should not be fulfilled.
        if (staffDictionary == null) {
            return;
        }

        // Build teacher directory.
        Map<String, StaffMember> filteredMap = staffDictionary
                .entrySet()
                .stream()
                .filter(member -> member.getValue().matches(searchTerm))
                .filter(member -> member.getValue().getIsTeacher())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        StaffDictionary teachers = new StaffDictionary();
        teachers.putAll(filteredMap);

        // Build supplement directory.
        filteredMap = staffDictionary
                .entrySet()
                .stream()
                .filter(member -> member.getValue().matches(searchTerm))
                .filter(member -> !member.getValue().getIsTeacher())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        StaffDictionary supplement = new StaffDictionary();
        supplement.putAll(filteredMap);

        // Update adapter data and refresh.
        listDataChild.clear();
        listDataChild.put(TEACHERS, teachers);
        listDataChild.put(SUPPLEMENT, supplement);

        staffListAdapter.notifyDataSetChanged();
    }

    @Override
    public void execute(HttpResponseData responseData) {
        if (responseData.isError()) {
            new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                    // TODO: Set correct title.
                    .setTitle(getResources().getString(R.string.title_calendar))
                    .setMessage(getResources().getString(R.string.error_failed_to_load_data))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> getParentFragmentManager().popBackStack())
                    .show();
            return;
        }

        try {
            // We must keep this for searchching.
            staffDictionary = new StaffDictionary(responseData.getData());
            notifyChanged();

        } catch (JSONException e) {

            e.printStackTrace();
            // TODO: Set correct title.
            new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                    .setTitle(getResources().getString(R.string.title_calendar))
                    .setMessage(getResources().getString(R.string.error_failed_to_load_data))
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> getParentFragmentManager().popBackStack())
                    .show();

        }
    }

    private void expandGroupsIfNeeed() {
        for (int i = 0; i < staffListAdapter.getGroupCount(); i += 1) {
            if (!staffListListView.isGroupExpanded(i)) {
                staffListListView.expandGroup(i);
            }
        }
    }
}

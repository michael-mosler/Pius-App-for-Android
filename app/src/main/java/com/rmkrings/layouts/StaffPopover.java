package com.rmkrings.layouts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.rmkrings.activities.R;
import com.rmkrings.data.staff.StaffDictionary;
import com.rmkrings.data.staff.StaffMember;
import com.rmkrings.loader.StaffLoader;


/**
 * Popover which shows information on substiting teacher.
 */
public class StaffPopover {
    private final Context context;
    private final View view;
    private final String staffMemberShortcutName;

    /**
     * Creates a new staff helper popover which displays information on
     * a teacher who is identified by shortname in substitution schedule. Opens
     * on long touch gesture.
     *
     * @param context                 - Context to open popover in.
     * @param onView                  - View on which popover shall be shown.
     * @param staffMemberShortcutName - Shortname to decode in popover.
     */
    public StaffPopover(Context context, View onView, String staffMemberShortcutName) {
        this.context = context;
        this.view = onView;
        this.staffMemberShortcutName = staffMemberShortcutName;
    }

    /**
     * Show the popover with information given in constructor.
     */
    public void show() {
        StaffLoader staffLoader = new StaffLoader();
        StaffDictionary staffDictionary = staffLoader.loadFromCache();
        StaffMember staffMember = staffDictionary.get(staffMemberShortcutName);

        // If shortcut name is not available then don't show anything.
        if (staffMember == null) {
            return;
        }

        String subjects;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            subjects = String.join(", ", staffMember.getSubjects());
        } else {
            String delimiter = "";
            subjects = "";
            for (String s : staffMember.getSubjects()) {
                subjects = subjects.concat(delimiter).concat(s);
                delimiter = ", ";
            }
        }

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View inflatedView = layoutInflater.inflate(R.layout.popover_staff, (ViewGroup) view.getParent(), false);
        // setContentView(inflatedView);

        TextView v1 = inflatedView.findViewById(R.id.popoverShortcutName);
        v1.setText(staffMember.getName());
        TextView v2 = inflatedView.findViewById(R.id.popoverSubjects);
        v2.setText(subjects);
        TextView v3 = inflatedView.findViewById(R.id.popoverEmail);
        String mail = staffMember.getEmail();
        if (mail != null) {
            v3.setText(mail);
        } else {
            v3.setVisibility(View.GONE);
        }


        PopupWindow popupWindow = new PopupWindow(inflatedView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAsDropDown(view);
    }
}

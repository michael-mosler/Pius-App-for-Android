package com.rmkrings.layouts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.rmkrings.activities.R;
import com.rmkrings.helper.AppDefaults;

/**
 * Popover which shows information on substiting teacher.
 */
public class StaffHelperPopover {
    private final Context context;
    private final View view;

    /**
     * Shows a popover which explains how to show staff helper popover. This popover
     * presents some information on teacher when only short name is given.
     * @param context Context to show popover in.
     * @param onView View to present popover on.
     */
    public StaffHelperPopover(Context context, View onView) {
        this.context = context;
        this.view = onView;
    }

    /**
     * Show the popover with information given in constructor.
     */
    public void show() {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View inflatedView = layoutInflater.inflate(R.layout.popover_staff_helper, (ViewGroup)view.getParent(),false);

        // Don't know why but we have to set image programmatically. Image from designer will not
        // show up.
        final ImageView imageView = inflatedView.findViewById(R.id.staff_helper_image);
        imageView.setImageResource(R.drawable.subst_schedule_long_push_helper);

        // Create the popover and add confirm button action to close. Tapping outside
        // will also close but this might not be obvious.
        final PopupWindow popupWindow = new PopupWindow(inflatedView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                AppDefaults.setHasConfirmedStaffHelper(true);
            }
        });

        final Button confirmButton = inflatedView.findViewById(R.id.staff_helper_confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        // Post runnable to show popover when activity has started.
        view.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(view, Gravity.TOP, 0, 100);

            }
        });

    }
}

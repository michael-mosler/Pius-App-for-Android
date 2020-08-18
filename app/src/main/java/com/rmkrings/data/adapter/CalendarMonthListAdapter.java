package com.rmkrings.data.adapter;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rmkrings.interfaces.ViewSelectedCallback;
import com.rmkrings.activities.R;

import java.util.ArrayList;

public class CalendarMonthListAdapter extends RecyclerView.Adapter<CalendarMonthListAdapter.MonthListViewHolder> {

    private final ViewSelectedCallback fragment;
    private final ArrayList<String> monthList;
    private int selectedButtonIndex = -1;

    static class MonthListViewHolder extends RecyclerView.ViewHolder {
        final Button buttonView;
        MonthListViewHolder(@NonNull ConstraintLayout itemView) {
            super(itemView);
            buttonView = itemView.findViewById(R.id.button);
        }
    }

    public CalendarMonthListAdapter(ArrayList<String> monthList, ViewSelectedCallback fragment) {
        this.fragment = fragment;
        this.monthList = monthList;
    }

    @NonNull
    @Override
    public CalendarMonthListAdapter.MonthListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ConstraintLayout v = (ConstraintLayout)LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.month__selector_item, viewGroup, false);
        return new MonthListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MonthListViewHolder viewHolder, int i) {
        viewHolder.buttonView.setText(monthList.get(i));

        viewHolder.buttonView.setSelected(false);
        viewHolder.buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the button that is selectedButtonIndex right now.
                selectedButtonIndex = viewHolder.getAdapterPosition();
                fragment.notifySelectionChanged(viewHolder.buttonView, viewHolder.buttonView.getText().toString());
            }
        });

        // On startup initialise by selecting first month.
        if (selectedButtonIndex == -1 && i == 0) {
            selectedButtonIndex = 0;
        }

        // If button gets into view, should actually be selectedButtonIndex but is not then select id.
        if (i == selectedButtonIndex && !viewHolder.buttonView.isSelected()) {
            fragment.notifySelectionChanged(viewHolder.buttonView, monthList.get(selectedButtonIndex));
        }
    }

    @Override
    public int getItemCount() {
        return monthList.size();
    }
}

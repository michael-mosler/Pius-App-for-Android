package com.rmkrings.data.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.rmkrings.activities.R;


public class CourseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<String> courseList;

    static class CourseListItemViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        final ImageButton deleteButton;

        CourseListItemViewHolder(LinearLayout l) {
            super(l);
            textView = l.findViewById(R.id.courseItem);
            deleteButton = l.findViewById(R.id.deleteButton);
        }
    }

    public CourseListAdapter(ArrayList<String> courseList) {
        this.courseList = courseList;
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mLayoutInflater = LayoutInflater.from(parent.getContext());
        LinearLayout linearLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.course_list_item, parent, false);

        return new CourseListItemViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String course = courseList.get(position);
        CourseListItemViewHolder courseListItemViewHolder = (CourseListItemViewHolder) holder;
        courseListItemViewHolder.textView.setText(course);

        final RecyclerView.ViewHolder vh = holder;
        courseListItemViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If remove should throw we safely may ignore this.
                try {
                    int adapterPosition = vh.getAdapterPosition();
                    if (adapterPosition < courseList.size()) {
                        courseList.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                    }
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
package com.rmkrings.data.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.rmkrings.pius_app_for_android.R;


public class CourseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> courseList;

    public static class CourseListItemViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageButton deleteButton;

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        String course = courseList.get(position);
        CourseListItemViewHolder courseListItemViewHolder = (CourseListItemViewHolder) holder;
        courseListItemViewHolder.textView.setText(course);

        courseListItemViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseList.remove(position);
                notifyItemRemoved(position);
            }
        });
    }
}
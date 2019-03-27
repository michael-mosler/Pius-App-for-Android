package com.rmkrings.data.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmkrings.pius_app_for_android.R;

public class MetaDataAdapter extends RecyclerView.Adapter<MetaDataAdapter.MetaDataViewHolder> {
    private String[] dataset;

    public static class MetaDataViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public MetaDataViewHolder(TextView v) {
            super(v);
            textView = v;
            textView.setMovementMethod(new ScrollingMovementMethod());
        }
    }

    public MetaDataAdapter(String[] dataset) {
        this.dataset = dataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MetaDataAdapter.MetaDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView)LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.metadata_view_item, parent, false);

        MetaDataViewHolder vh = new MetaDataViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MetaDataViewHolder holder, int position) {
        holder.textView.setText(dataset[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        // Ticker text not set => 0 items.
        if (dataset[0] == null || dataset[0] == "") {
            return 0;
        }

        // Additional text set => 2 items.
        // Otherwise we have just one item.
        return (dataset[1] != null && dataset[1] != "") ? 2 : 1;
    }

}

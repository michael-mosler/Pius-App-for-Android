package com.rmkrings.data.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmkrings.helper.StringHelper;
import com.rmkrings.activities.R;

public class MetaDataAdapter extends RecyclerView.Adapter<MetaDataAdapter.MetaDataViewHolder> {
    private final String[] dataset;

    static class MetaDataViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        MetaDataViewHolder(TextView v) {
            super(v);
            textView = v;
            textView.setMovementMethod(new ScrollingMovementMethod());
        }
    }

    public MetaDataAdapter(String[] dataset) {
        this.dataset = dataset;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MetaDataAdapter.MetaDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView)LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.metadata_view_item, parent, false);

        return new MetaDataViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MetaDataViewHolder holder, int position) {
        holder.textView.setText(StringHelper.replaceHtmlEntities(dataset[position]));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        // Ticker text not set => 0 items.
        if (dataset[0] == null || dataset[0].length() == 0) {
            return 0;
        }

        // Additional text set => 2 items.
        // Otherwise we have just one item.
        return (dataset[1] != null && dataset[1].length() != 0) ? 2 : 1;
    }

}

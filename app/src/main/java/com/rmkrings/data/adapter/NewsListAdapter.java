package com.rmkrings.data.adapter;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rmkrings.data.BaseListItem;
import com.rmkrings.data.MessageItem;
import com.rmkrings.data.news.NewsListItem;
import com.rmkrings.interfaces.ViewSelectedCallback;
import com.rmkrings.activities.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter for news items on Today view.
 */
public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<BaseListItem> listItems;
    private final ViewSelectedCallback fragment;

    static class TextViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        TextViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    static class NewsListViewHolder extends RecyclerView.ViewHolder {
        final TextView headingView;
        final TextView textView;
        final ImageView imageView;

        NewsListViewHolder(ConstraintLayout itemView) {
            super(itemView);
            headingView = itemView.findViewById(R.id.heading);
            textView = itemView.findViewById(R.id.newstext);
            imageView = itemView.findViewById(R.id.newsimage);
        }
    }

    public NewsListAdapter(ArrayList<BaseListItem> newsListItems, ViewSelectedCallback fragment) {
        this.listItems = newsListItems;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final RecyclerView.ViewHolder vh;

        LayoutInflater mLayoutInflater = LayoutInflater.from(viewGroup.getContext());
        if (i == NewsListItem.news) {
            ConstraintLayout v = (ConstraintLayout)mLayoutInflater.inflate(R.layout.news_item, viewGroup, false);
            vh = new NewsListViewHolder(v);

            v.setOnClickListener(v1 -> {
                final int index = vh.getBindingAdapterPosition();

                // Only if index is valid.
                if (index != NO_POSITION && index < listItems.size()) {
                    NewsListItem newsListItem = (NewsListItem) listItems.get(vh.getBindingAdapterPosition());
                    fragment.notifySelectionChanged(v1, newsListItem.getNewsItem().getHref());
                }
            });
        } else {
            TextView itemView = (TextView)mLayoutInflater.inflate(R.layout.vertretungsplan_remark_item, viewGroup, false);
            vh = new TextViewHolder(itemView);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int type = getItemViewType(i);

        if (type == NewsListItem.news) {
            NewsListItem newsListItem = (NewsListItem) listItems.get(i);
            NewsListViewHolder newsListViewHolder = (NewsListViewHolder) viewHolder;
            newsListViewHolder.headingView.setText(newsListItem.getNewsItem().getHeading());
            newsListViewHolder.textView.setText(newsListItem.getNewsItem().getText());

            // On Kitkat we need to provide a SSL Socket factory which enables TLS 1.1/1.2
            // to Picasso. This makes things much more complicated.
            // From API level 20 onwards it gets simpler.
            Picasso.get()
                    .load(newsListItem.getNewsItem().getImg())
                    .resize(64, 64)
                    .centerCrop()
                    .into(newsListViewHolder.imageView);
        } else {
            MessageItem messageItem = (MessageItem)listItems.get(i);
            ((TextViewHolder)viewHolder).textView.setGravity(messageItem.getGravity());
            ((TextViewHolder)viewHolder).textView.setText(messageItem.getMessageText());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return listItems.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}

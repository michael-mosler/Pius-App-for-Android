package com.rmkrings.data.adapter;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmkrings.data.BaseListItem;
import com.rmkrings.data.MessageItem;
import com.rmkrings.data.postings.Posting;
import com.rmkrings.activities.R;

import java.util.ArrayList;

public class PostingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final ArrayList<BaseListItem> listItems;

    static class TextViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        TextViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    static class PostingsViewHolder extends RecyclerView.ViewHolder {
        final TextView messageView;
        final TextView timestampView;

        PostingsViewHolder(ConstraintLayout itemView) {
            super(itemView);
            messageView = itemView.findViewById(R.id.postingmessage);
            messageView.setMovementMethod(LinkMovementMethod.getInstance());
            timestampView = itemView.findViewById(R.id.postingtimestamp);
        }
    }

    public PostingsAdapter(ArrayList<BaseListItem> listItems) {
        this.listItems = listItems;
    }

    @Override
    public int getItemViewType(int position) {
        return listItems.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final RecyclerView.ViewHolder vh;

        LayoutInflater mLayoutInflater = LayoutInflater.from(viewGroup.getContext());

        if (i == Posting.postingItem) {
            ConstraintLayout v = (ConstraintLayout)mLayoutInflater.inflate(R.layout.postings_item, viewGroup, false);
            vh = new PostingsViewHolder(v);
        } else {
            TextView itemView = (TextView)mLayoutInflater.inflate(R.layout.vertretungsplan_remark_item, viewGroup, false);
            vh = new TextViewHolder(itemView);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int type = getItemViewType(i);

        if (type == Posting.postingItem) {
            Posting posting = (Posting)listItems.get(i);
            PostingsViewHolder postingsViewHolder = (PostingsViewHolder)viewHolder;
            postingsViewHolder.messageView.setText(Html.fromHtml(posting.getPostingMessage()));
            postingsViewHolder.timestampView.setText(posting.getTimestamp());
        } else {
            MessageItem messageItem = (MessageItem) listItems.get(i);
            ((TextViewHolder) viewHolder).textView.setGravity(messageItem.getGravity());
            ((TextViewHolder) viewHolder).textView.setText(messageItem.getMessageText());
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}

package com.rmkrings.data.news;

import com.rmkrings.data.BaseListItem;

public class NewsListItem extends BaseListItem {
    public static final int news = 0;

    private final NewsItem newsItem;

    public NewsListItem(NewsItem newsItem) {
        this.newsItem = newsItem;
    }

    public NewsItem getNewsItem() {
        return newsItem;
    }

    @Override
    public int getType() {
        return news;
    }
}

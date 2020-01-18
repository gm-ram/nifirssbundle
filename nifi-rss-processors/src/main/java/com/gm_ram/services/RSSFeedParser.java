package com.gm_ram.services;

import com.gm_ram.models.RSSItem;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

import java.util.ArrayList;
import java.util.List;

public class RSSFeedParser {
    public RSSFeedParser() {
    }

    public List<RSSItem> extractItems(SyndFeed syndFeed)
    {
        String feedTitle = syndFeed.getTitle();
        String feedUri = syndFeed.getLink();

        List<SyndEntry> entries = syndFeed.getEntries();

        List<RSSItem> items = new ArrayList<>();

        for (SyndEntry syndEntry : entries) {
            RSSItem rssItem = new RSSItem(syndEntry, feedTitle, feedUri);
            items.add(rssItem);
        }

        return items;
    }
}

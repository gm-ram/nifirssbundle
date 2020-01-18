package com.gm_ram.models;

import com.sun.syndication.feed.synd.SyndEntry;

/**
 * @author Ralph Mansson
 */
public class RSSItem {
    private String publicationDate;
    private String author;
    private String title;
    private String description;
    private String url;
    private String feedTitle;
    private String feedUrl;

    public RSSItem(SyndEntry syndEntry, String feedTitle, String feedUri) {
        this.publicationDate = syndEntry.getPublishedDate() == null ? "" : syndEntry.getPublishedDate().toString();
        this.author = syndEntry.getAuthor();
        this.title = syndEntry.getTitle();
        this.description = syndEntry.getDescription().getValue();
        this.url = syndEntry.getLink();
        this.feedTitle = feedTitle;
        this.feedUrl = feedUri;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getFeedTitle() {
        return feedTitle;
    }

    public String getFeedUrl() {
        return feedUrl;
    }
}

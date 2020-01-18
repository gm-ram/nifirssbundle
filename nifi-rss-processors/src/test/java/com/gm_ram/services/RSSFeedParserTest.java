package com.gm_ram.services;

import com.gm_ram.models.RSSItem;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RSSFeedParserTest {

    private static final String FEED_TITLE = "RSS Feed Example";
    private static final String FEED_URL = "https://www.rss-feed-example.com";

    private static final String PUBLICATION_DATE_0 = "Sat Jan 01 00:00:00 GMT 2000";
    private static final String AUTHOR_0 = "John Doe";
    private static final String TITLE_0 = "Feed Item One";
    private static final String DESCRIPTION_0 = "Description for feed item one.";
    private static final String URL_0 = "https://www.rss-feed-example.com/item/item_one.html";

    private static final String PUBLICATION_DATE_1 = "Sat Jan 01 00:00:00 GMT 2001";
    private static final String AUTHOR_1 = "Jane Doe";
    private static final String TITLE_1 = "Feed Item Two";
    private static final String DESCRIPTION_1 = "Description for feed item two.";
    private static final String URL_1 = "https://www.rss-feed-example.com/item/item_two.html";

    @Test
    public void testParseRSSFeed() {
        Date publishedDate0 = mock(Date.class);
        when(publishedDate0.toString()).thenReturn(PUBLICATION_DATE_0);

        SyndContent description0 = mock(SyndContent.class);
        when(description0.getValue()).thenReturn(DESCRIPTION_0);

        SyndEntry syndEntry0 = mock(SyndEntry.class);
        when(syndEntry0.getPublishedDate()).thenReturn(publishedDate0);
        when(syndEntry0.getAuthor()).thenReturn(AUTHOR_0);
        when(syndEntry0.getTitle()).thenReturn(TITLE_0);
        when(syndEntry0.getDescription()).thenReturn(description0);
        when(syndEntry0.getLink()).thenReturn(URL_0);

        Date publishedDate1 = mock(Date.class);
        when(publishedDate1.toString()).thenReturn(PUBLICATION_DATE_1);

        SyndContent description1 = mock(SyndContent.class);
        when(description1.getValue()).thenReturn(DESCRIPTION_1);

        SyndEntry syndEntry1 = mock(SyndEntry.class);
        when(syndEntry1.getPublishedDate()).thenReturn(publishedDate1);
        when(syndEntry1.getAuthor()).thenReturn(AUTHOR_1);
        when(syndEntry1.getTitle()).thenReturn(TITLE_1);
        when(syndEntry1.getDescription()).thenReturn(description1);
        when(syndEntry1.getLink()).thenReturn(URL_1);

        List<SyndEntry> entries = new ArrayList<>();
        entries.add(syndEntry0);
        entries.add(syndEntry1);

        SyndFeed syndFeed = mock(SyndFeed.class);
        when(syndFeed.getTitle()).thenReturn(FEED_TITLE);
        when(syndFeed.getLink()).thenReturn(FEED_URL);
        when(syndFeed.getEntries()).thenReturn(entries);

        RSSFeedParser rssFeedParser = new RSSFeedParser();

        List<RSSItem> items = rssFeedParser.extractItems(syndFeed);

        assertEquals(PUBLICATION_DATE_0, items.get(0).getPublicationDate());
        assertEquals(AUTHOR_0, items.get(0).getAuthor());
        assertEquals(TITLE_0, items.get(0).getTitle());
        assertEquals(DESCRIPTION_0, items.get(0).getDescription());
        assertEquals(URL_0, items.get(0).getUrl());
        assertEquals(FEED_TITLE, items.get(0).getFeedTitle());
        assertEquals(FEED_URL, items.get(0).getFeedUrl());

        assertEquals(PUBLICATION_DATE_1, items.get(1).getPublicationDate());
        assertEquals(AUTHOR_1, items.get(1).getAuthor());
        assertEquals(TITLE_1, items.get(1).getTitle());
        assertEquals(DESCRIPTION_1, items.get(1).getDescription());
        assertEquals(URL_1, items.get(1).getUrl());
        assertEquals(FEED_TITLE, items.get(1).getFeedTitle());
        assertEquals(FEED_URL, items.get(1).getFeedUrl());
    }

}

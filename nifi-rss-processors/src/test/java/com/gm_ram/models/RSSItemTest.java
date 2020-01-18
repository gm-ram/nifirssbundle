package com.gm_ram.models;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Ralph Mansson
 */
public class RSSItemTest {

    static private final String PUBLICATION_DATE = "2000-01-01 00:00:00";
    static private final String AUTHOR = "John Doe";
    static private final String TITLE = "Story";
    static private final String DESCRIPTION = "A story of interest.";
    static private final String URL = "http://www.story.com/story";
    static private final String FEED_TITLE = "Story Feed";
    static private final String FEED_URL = "http://www.story.com/feed.xml";

    @Test
    public void createInitRSSItem()
    {
        Date publishedDate = mock(Date.class);
        when(publishedDate.toString()).thenReturn(PUBLICATION_DATE);

        SyndContent description = mock(SyndContent.class);
        when(description.getValue()).thenReturn(DESCRIPTION);

        SyndEntry syndEntry = mock(SyndEntry.class);
        when(syndEntry.getPublishedDate()).thenReturn(publishedDate);
        when(syndEntry.getAuthor()).thenReturn(AUTHOR);
        when(syndEntry.getTitle()).thenReturn(TITLE);
        when(syndEntry.getDescription()).thenReturn(description);
        when(syndEntry.getLink()).thenReturn(URL);

        RSSItem rssItem = new RSSItem(syndEntry, FEED_TITLE, FEED_URL);
        assertEquals(PUBLICATION_DATE, rssItem.getPublicationDate());
        assertEquals(AUTHOR, rssItem.getAuthor());
        assertEquals(TITLE, rssItem.getTitle());
        assertEquals(DESCRIPTION, rssItem.getDescription());
        assertEquals(URL, rssItem.getUrl());
        assertEquals(FEED_TITLE, rssItem.getFeedTitle());
        assertEquals(FEED_URL, rssItem.getFeedUrl());
    }
}

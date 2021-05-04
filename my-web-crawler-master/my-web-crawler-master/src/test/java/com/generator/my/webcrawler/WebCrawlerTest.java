package com.generator.my.webcrawler;

import com.generator.my.webcrawler.pojo.UserInput;
import com.generator.my.webcrawler.pojo.Page;
import com.google.common.collect.Maps;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WebCrawlerTest {

    @Test
    public void alreadyVisited() {
        Map<String, Page> sitemap = Maps.newHashMap();

        WebCrawler crawler = new WebCrawler(new UserInput());

        // Visit first page
        String firstUrl = "http://www.wiprodigital.com";
        assertFalse(crawler.alreadyVisited(sitemap, firstUrl));

        // Add page visited to list
        Page validPage1 = new Page(firstUrl);
        sitemap.put(validPage1.getUrl(), validPage1);

        // Visit second page
        String secondUrl = "http://wiprodigital.com/who-we-are/";
        assertFalse(crawler.alreadyVisited(sitemap, secondUrl));

        // Add page visited to list
        Page validPage2 = new Page(secondUrl);
        sitemap.put(validPage2.getUrl(), validPage2);

        // Pages have been visited already
        assertTrue(crawler.alreadyVisited(sitemap, validPage1.getUrl()));
        assertTrue(crawler.alreadyVisited(sitemap, validPage2.getUrl()));
    }

    @Test
    public void isSameDomain_VisitSubdomains() {
        UserInput input = new UserInput();
        input.setVisitSubDomains(true);
        WebCrawler crawler = new WebCrawler(input);
        URL home = null;
        try {
            home = new URL("http://wiprodigital.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assertTrue(crawler.isSameDomain(home, "http://wiprodigital.com/who-we-are/"));
        assertTrue(crawler.isSameDomain(home, "http://wiprodigital.com/"));
        assertTrue(crawler.isSameDomain(home, "http://wiprodigital.com/what-we-do/"));
        assertFalse(crawler.isSameDomain(home, "http://www.google.com"));
    }


    @Test
    public void isSameDomain_DontVisitSubdomains() {
        UserInput input = new UserInput();
        input.setVisitSubDomains(false);
        WebCrawler crawler = new WebCrawler(input);
        URL home = null;
        try {
            home = new URL("http://wiprodigital.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assertTrue(crawler.isSameDomain(home, "http://wiprodigital.com/who-we-are/"));
        assertTrue(crawler.isSameDomain(home, "http://wiprodigital.com/"));
        assertFalse(crawler.isSameDomain(home, "http://services.wiprodigital.com/"));
        assertFalse(crawler.isSameDomain(home, "http://test.wiprodigital.com"));
        assertFalse(crawler.isSameDomain(home, "http://www.google.com"));
    }

    /**
     * The getDocument test set needs to be able to connect to the internet - start
     */
    @Test
    public void getDocument() {
        WebCrawler crawler = new WebCrawler(new UserInput());
        String urlToVisit = "https://www.google.com";
        Page currentPage = null;
        Map<String, Page> sitemap = null;
        Document doc = crawler.getDocument(urlToVisit, currentPage, sitemap);
        assertNotNull(doc);
        // Correct as of 28/01/2018
        assertEquals("Google", doc.title());
    }

    @Test
    public void getDocument_ErrorUnknown() {
        WebCrawler crawler = new WebCrawler(new UserInput());
        String urlToVisit = "https://www.thisdomaindonotexistandshouldcontinuenotexisting.com";
        Page currentPage = new Page(urlToVisit);
        Map<String, Page> sitemap = Maps.newHashMap();
        Document doc = crawler.getDocument(urlToVisit, currentPage, sitemap);
        assertNull(doc);
        assertEquals("java.net.UnknownHostException: www.thisdomaindonotexistandshouldcontinuenotexisting.com",
                currentPage.getError());
        assertEquals(1, sitemap.size());
    }

    @Test
    public void getDocument_Error404() {
        WebCrawler crawler = new WebCrawler(new UserInput());
        String urlToVisit = "http://wiprodigital.com/thispagedonotexistandshouldcontinuenotexisting.html";
        Page currentPage = new Page(urlToVisit);
        Map<String, Page> sitemap = Maps.newHashMap();
        Document doc = crawler.getDocument(urlToVisit, currentPage, sitemap);
        assertNull(doc);
        assertEquals("org.jsoup.HttpStatusException: HTTP error fetching URL. Status=404, " +
                "URL=http://wiprodigital.com/thispagedonotexistandshouldcontinuenotexisting.html", currentPage.getError());
        assertEquals(1, sitemap.size());
    }

    @Test
    public void getDocument_ErrorWrongMimetype() {
        WebCrawler crawler = new WebCrawler(new UserInput());
        String urlToVisit = "http://17776-presscdn-0-6.pagely.netdna-cdn.com/wp-content/themes/wiprodigital/images/logo.png";
        Page currentPage = new Page(urlToVisit);
        Map<String, Page> sitemap = Maps.newHashMap();
        Document doc = crawler.getDocument(urlToVisit, currentPage, sitemap);
        assertNull(doc);
        assertEquals("org.jsoup.UnsupportedMimeTypeException: Unhandled content type. " +
                "Must be text/*, application/xml, or application/xhtml+xml. Mimetype=image/png, " +
                "URL=http://17776-presscdn-0-6.pagely.netdna-cdn.com/wp-content/themes/wiprodigital/images/logo.png",
                currentPage.getError());
        assertEquals(1, sitemap.size());
    }
    /**
     * The getDocument test set needs to be able to connect to the internet - end
     */

    @Test
    public void normaliseAnchorLink_fullpath() {
        WebCrawler crawler = new WebCrawler(new UserInput());
        URL home = null;
        try {
            home = new URL("http://wiprodigital.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Element link = Mockito.mock(Element.class);
        when(link.attr("href")).thenReturn("http://wiprodigital.com/who-we-are/");

        String expected = "http://wiprodigital.com/who-we-are/";
        String actual = crawler.normaliseAnchorLink(home, link);
        assertEquals(expected, actual);
    }

    @Test
    public void normaliseAnchorLink_relativeProtocol() {
        WebCrawler crawler = new WebCrawler(new UserInput());
        URL home = null;
        try {
            home = new URL("http://wiprodigital.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Element link = Mockito.mock(Element.class);
        when(link.attr("href")).thenReturn("//wiprodigital.com/who-we-are/");

        String expected = "http://wiprodigital.com/who-we-are/";
        String actual = crawler.normaliseAnchorLink(home, link);
        assertEquals(expected, actual);
    }

    @Test
    public void normaliseAnchorLink_relativeDomain() {
        WebCrawler crawler = new WebCrawler(new UserInput());
        URL home = null;
        try {
            home = new URL("http://wiprodigital.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Element link = Mockito.mock(Element.class);
        when(link.attr("href")).thenReturn("/who-we-are/");

        String expected = "http://wiprodigital.com/who-we-are/";
        String actual = crawler.normaliseAnchorLink(home, link);
        assertEquals(expected, actual);
    }

    @Test
    public void normaliseAnchorLink_ignoreAnchors() {
        WebCrawler crawler = new WebCrawler(new UserInput());
        URL home = null;
        try {
            home = new URL("http://wiprodigital.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Element link = Mockito.mock(Element.class);
        when(link.attr("href")).thenReturn("#who-we-are");

        String actual = crawler.normaliseAnchorLink(home, link);
        assertNull(actual);
    }

    @Test
    public void normaliseImageLink_fullpath() {
        WebCrawler crawler = new WebCrawler(new UserInput());
        URL home = null;
        try {
            home = new URL("http://wiprodigital.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Element link = Mockito.mock(Element.class);
        when(link.attr("src")).thenReturn("http://wiprodigital.com/images/logo.png");

        String expected = "http://wiprodigital.com/images/logo.png";
        String actual = crawler.normaliseImageLink(home, link);
        assertEquals(expected, actual);
    }

    @Test
    public void normaliseImageLink_relativeProtocol() {
        WebCrawler crawler = new WebCrawler(new UserInput());
        URL home = null;
        try {
            home = new URL("http://wiprodigital.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Element link = Mockito.mock(Element.class);
        when(link.attr("src")).thenReturn("//wiprodigital.com/images/logo.png");

        String expected = "http://wiprodigital.com/images/logo.png";
        String actual = crawler.normaliseImageLink(home, link);
        assertEquals(expected, actual);
    }

    @Test
    public void normaliseImageLink_relativeDomain() {
        WebCrawler crawler = new WebCrawler(new UserInput());
        URL home = null;
        try {
            home = new URL("http://wiprodigital.com");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Element link = Mockito.mock(Element.class);
        when(link.attr("src")).thenReturn("/images/logo.png");

        String expected = "http://wiprodigital.com/images/logo.png";
        String actual = crawler.normaliseImageLink(home, link);
        assertEquals(expected, actual);
    }

    @Test
    public void setPageTitle() {
        WebCrawler crawler = new WebCrawler(new UserInput());
        Page page = new Page("http://wiprodigital.com");

        Document doc = Mockito.mock(Document.class);
        when(doc.title()).thenReturn("Wipro");

        crawler.setPageTitle(page, doc);
        assertEquals("Wipro", page.getTitle());
    }

}
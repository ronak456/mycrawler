package com.generator.my.webcrawler.pojo;

import org.junit.Test;

import static org.junit.Assert.*;

public class PageTest {

    @Test
    public void equalsTest() {
        Page page1 = new Page("http://www.wiprodigital.com");
        Page page2 = new Page("http://www.wiprodigital.com");
        Page page3 = new Page("http://www.google.com");
        assertEquals(page1, page2);
        assertNotEquals(page1, page3);
        assertNotEquals(page2, page3);
    }

    @Test
    public void hashCodeTest() {
        Page page1 = new Page("http://www.wiprodigital.com");
        Page page2 = new Page("http://www.wiprodigital.com");
        Page page3 = new Page("http://www.google.com");
        assertEquals(page1.hashCode(), page2.hashCode());
        assertNotEquals(page1.hashCode(), page3.hashCode());
        assertNotEquals(page2.hashCode(), page3.hashCode());
    }

    @Test
    public void toStringTest() {
        Page page1 = new Page("http://www.wiprodigital.com");
        page1.setTitle("home page");
        Page page2 = new Page("http://www.wiprodigital.com/pagenotfound");
        page2.setError("404 - Page not found");
        Page page3 = new Page("http://www.google.com");
        page3.setTitle("Google");
        assertEquals("home page: http://www.wiprodigital.com", page1.toString());
        assertEquals("http://www.wiprodigital.com/pagenotfound: 404 - Page not found", page2.toString());
        assertEquals("Google: http://www.google.com", page3.toString());
    }
}
package com.generator.my;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertTrue;

import com.generator.my.App;
import com.generator.my.webcrawler.WebCrawler;
import org.junit.After;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

public class AppTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(System.out);
        System.setErr(System.err);
    }

    @Test
    public void testApp() {
        File tmp = new File("./example.map");
        tmp.deleteOnExit();
        String[] args = {"http://www.example.com", tmp.getPath()};
        WebCrawler mock = Mockito.mock(WebCrawler.class);

        App.main(args);
        verify(mock, atMost(1)).crawl();

        assertTrue(outContent.toString().contains("Crawled http://www.example.com in "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApp_invalidUrl() {
        String[] args = {"something invalid", "mysite.map"};
        App.main(args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApp_NullUrl() {
        String[] args = {null, "mysite.map"};
        App.main(args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApp_NulFilename() {
        String[] args = {"https://www.wiprodigital", null};
        App.main(args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testApp_EmptyFilename() {
        String[] args = {null, ""};
        App.main(args);
    }
}

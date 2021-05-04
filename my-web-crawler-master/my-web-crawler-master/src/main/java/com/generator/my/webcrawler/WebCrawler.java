package com.generator.my.webcrawler;

import com.generator.my.webcrawler.pojo.UserInput;
import com.generator.my.webcrawler.pojo.Page;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WebCrawler implements Crawler {

    private final UserInput input;

    public WebCrawler(UserInput input) {
        this.input = input;
    }

    /**
     * Method to crawl a website and retrieve all it's links and images.
     */
    public void crawl() {
        URL home = input.getDomainToCrawl();
        Page homepage = new Page(home.toString());

        // Hashmaps have constant time search.
        // I'm using this map instead of a list because of it.
        Map<String, Page> sitemap = Maps.newHashMap();

        // Create list of pages to visit and add home page
        List<String> toVisit = Lists.newArrayList();
        toVisit.add(homepage.getUrl());

        // Because the list of URLs will keep increasing for every page visited
        // I'm creating the cursor below, for my loop. This avoids
        // ConcurrentModificationException
        int cursor = 0;

        while (cursor < toVisit.size()) {
            String urlToVisit = toVisit.get(cursor);
            cursor++;

            //Skips pages in different domains
            if (!isSameDomain(home, urlToVisit)) continue;

            // Skips pages already visited.
            if (alreadyVisited(sitemap, urlToVisit)) continue;

            Page currentPage = new Page(urlToVisit);

            Document doc = getDocument(urlToVisit, currentPage, sitemap);

            if (doc == null) continue;

            setPageTitle(currentPage, doc);

            processLinks(home, toVisit, currentPage, doc);

            processImages(home, currentPage, doc, currentPage.getLinks());

            sitemap.put(currentPage.getUrl(), currentPage);
        }

        saveToFile(sitemap, sitemap.get(home.toString()));
    }

    public void saveToFile(Map<String, Page> sitemap, Page home) {
        try (FileWriter fw = new FileWriter(input.getSitemapPath());
             BufferedWriter bw = new BufferedWriter(fw)) {

            StringBuilder identation = new StringBuilder();
            identation.append("  ");

            if (home == null) {
                return;
            }
            sitemap.remove(home);

            bw.write("- ");
            bw.write(home.toString());
            bw.newLine();

            if (home.getLinks() == null) {
                return;
            }

            for (String s : home.getLinks()) {
                Page currPage = writePage(sitemap, bw, identation, s);
                if (currPage == null) continue;

                identation.append("  ");

                for (String link : currPage.getLinks()) {
                    writePage(sitemap, bw, identation, link);
                }
                identation.delete(0, 2);
                bw.newLine();
                bw.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Page writePage(Map<String, Page> sitemap, BufferedWriter bw, StringBuilder identation, String url) throws IOException {
        Page currPage = sitemap.get(url);
        if (currPage == null) {
            if (Strings.isNullOrEmpty(url)) {
                return null;
            }
            bw.write(identation.toString());
            bw.write("- ");
            bw.write(url);
            bw.newLine();
            return null;
        }
        sitemap.remove(currPage);

        if (Strings.isNullOrEmpty(currPage.toString())) {
            return null;
        }
        bw.write(identation.toString());
        bw.write("- ");
        bw.write(currPage.toString());
        bw.newLine();
        if (currPage.getLinks() == null) {
            bw.newLine();
            bw.newLine();
            return null;
        }
        return currPage;
    }

    private void processImages(URL home, Page currentPage, Document doc, Set<String> linksSet) {
        Elements imgs = doc.select("img[src]");
        for (Element image : imgs) {
            String linkUrl = normaliseImageLink(home, image);
            if (linkUrl != null) {
                linksSet.add(linkUrl);
            }
        }
        currentPage.setLinks(linksSet);
    }

    private void processLinks(URL home, List<String> toVisit, Page currentPage, Document doc) {
        Elements links = doc.select("a[href]");
        HashSet<String> linksSet = Sets.newHashSet();
        for (Element link : links) {
            String linkUrl = normaliseAnchorLink(home, link);
            if (linkUrl == null) {
                continue;
            }
            linksSet.add(linkUrl);
            toVisit.add(linkUrl);
        }
        currentPage.setLinks(linksSet);
    }

    /**
     * Normalises all links to the full path URL.
     *
     * @param home the home page url
     * @param link the link element from the html
     * @return string with the normalised full path link or null if the link was an anchor.
     */
    public String normaliseAnchorLink(URL home, Element link) {
        String linkUrl = link.attr("href").trim();

        return normaliseLink(home, linkUrl);
    }

    /**
     * Normalises all image links to the full path URL.
     *
     * @param home  the home page url
     * @param image the image element from the html
     * @return string with the normalised full path image.
     */
    public String normaliseImageLink(URL home, Element image) {
        String linkUrl = image.attr("src").trim();

        return normaliseLink(home, linkUrl);
    }

    private String normaliseLink(URL home, String linkUrl) {
        if (linkUrl.startsWith("//")) { // handle relative protocols
            linkUrl = home.getProtocol() + ":" + linkUrl;
        } else if (linkUrl.startsWith("/")) { // handle relative domains
            linkUrl = home.toString() + linkUrl;
        } else if (linkUrl.startsWith("#")) { // ignore anchors
            linkUrl = null;
        }
        return linkUrl;
    }

    void setPageTitle(Page currentPage, Document doc) {
        String title = doc.title();
        currentPage.setTitle(title);
    }

    public Document getDocument(String urlToVisit, Page currentPage, Map<String, Page> sitemap) {
        Document doc;
        try {
            doc = Jsoup.connect(urlToVisit).get();
        } catch (Exception e) {//This will capture 404, TLS/SSL errors, mimetype errors and other errors returned by connect() and get()
            currentPage.setError(e.toString());
            sitemap.put(currentPage.getUrl(), currentPage);
            return null;
        }
        return doc;
    }

    boolean isSameDomain(URL home, String urlToVisit) {
        URL currentUrl;
        try {
            currentUrl = new URL(urlToVisit);
            if (input.isVisitSubDomains()) {
                if (currentUrl.getHost().endsWith(home.getHost())) {
                    return true;
                }
            } else {
                if (currentUrl.getHost().equals(home.getHost())) {
                    return true;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();//log error and continue
            return false;
        }
        return false;
    }

    public boolean alreadyVisited(Map<String, Page> sitemap, String url) {
        return sitemap.containsKey(url);
    }
}

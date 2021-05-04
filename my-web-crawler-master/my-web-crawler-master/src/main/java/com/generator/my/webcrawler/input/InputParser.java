package com.generator.my.webcrawler.input;

import com.generator.my.webcrawler.pojo.UserInput;
import com.google.common.base.Strings;

import java.net.MalformedURLException;
import java.net.URL;

public class InputParser {

    public static UserInput parse(String[] args) {
        UserInput input = new UserInput();
        if (args == null || args.length < 2) {
            throw new IllegalArgumentException("Expects a domain and a filename to create");
        }

        String domain = args[0];
        if (Strings.isNullOrEmpty(domain)) {
            throw new IllegalArgumentException("Expects a valid domain");
        }
        try {
            URL site = new URL(domain);
            input.setDomainToCrawl(site);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Expects a valid URL");
        }

        String sitemapFile = args[1];
        //This can be modified to escape special chars in strings t oavoid issues.
        if (Strings.isNullOrEmpty(sitemapFile)) {
            throw new IllegalArgumentException("Expects a valid filename");
        }
        input.setSitemapPath(sitemapFile);

        return input;
    }
}

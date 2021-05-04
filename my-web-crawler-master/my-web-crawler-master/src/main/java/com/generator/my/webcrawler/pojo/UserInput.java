package com.generator.my.webcrawler.pojo;

import java.net.URL;
import java.util.Objects;

public class UserInput {

    private URL domainToCrawl;
    private String sitemapPath;
    private boolean visitSubDomains;

    public boolean isVisitSubDomains() {
        return visitSubDomains;
    }

    public UserInput setVisitSubDomains(boolean visitSubDomains) {
        this.visitSubDomains = visitSubDomains;
        return this;
    }

    public URL getDomainToCrawl() {
        return domainToCrawl;
    }

    public UserInput setDomainToCrawl(URL domainToCrawl) {
        this.domainToCrawl = domainToCrawl;
        return this;
    }

    public String getSitemapPath() {
        return sitemapPath;
    }

    public UserInput setSitemapPath(String sitemapPath) {
        this.sitemapPath = sitemapPath;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInput)) return false;
        UserInput userInput = (UserInput) o;
        return Objects.equals(getDomainToCrawl(), userInput.getDomainToCrawl()) &&
                Objects.equals(getSitemapPath(), userInput.getSitemapPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDomainToCrawl(), getSitemapPath());
    }
}

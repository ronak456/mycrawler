
package com.generator.my.webcrawler.pojo;

import java.util.Objects;
import java.util.Set;

public class Page {

    private final String url;
    private String title;
    private String error;
    private Set<String> links;

    public Page(String url) {
        this.url = url;
    }

    public Set<String> getLinks() {
        return links;
    }

    public Page setLinks(Set<String> links) {
        this.links = links;
        return this;
    }

    public String getError() {
        return error;
    }

    public Page setError(String error) {
        this.error = error;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public Page setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return Objects.equals(url, page.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.title != null) {
            builder.append(this.title);
            builder.append(": ");
        }
        builder. append(this.url);
        if (this.error != null) {
            builder.append(": ");
            builder.append(this.error);
        }
        return builder.toString();
    }
}

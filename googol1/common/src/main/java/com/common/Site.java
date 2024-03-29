package com.common;

import java.io.Serializable;

public class Site implements Serializable {
    long id;
    String url;
    String title;
    int occurrences;
    String[] pagesThatContain;

    public Site(long id, String url, String title) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.occurrences = 0;
    }

    public Site(long id, String url, String title, int occurrences) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.occurrences = occurrences;
    }

    public String getPagesThatContain() {
        StringBuilder result = new StringBuilder();
        for (String page : pagesThatContain) {
            result.append(page).append("\n");
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return "Site{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", occurrences=" + occurrences +
                '}';
    }
}

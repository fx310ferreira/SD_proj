package com.common;

import java.io.Serializable;

public class Site implements Serializable {
    String url;
    String title;
    int occurrences;
    String[] pagesThatContain;

    public Site(String url, String title) {
        this.url = url;
        this.title = title;
        this.occurrences = 0;
    }

    public Site(String url, String title, int occurrences) {
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

    public String getLink(){
        return url;

    }

    @Override
    public String toString() {
        return "Site{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", referencedBy=" + occurrences +
                '}';
    }
}

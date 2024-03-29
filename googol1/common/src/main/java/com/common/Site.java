package com.common;

import java.io.Serializable;

public class Site implements Serializable {
    String url;
    String title;

    public Site(String url, String title) {
        this.url = url;
        this.title = title;
    }

    @Override
    public String toString() {
        return "Site{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}

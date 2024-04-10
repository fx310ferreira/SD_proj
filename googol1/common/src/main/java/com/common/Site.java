/**
 * @author Frederico Xavier de Araújo Ferreira
 */

package com.common;

import java.io.Serializable;

/**
 * Represents a site that has been indexed by the search engine.
 */
public class Site implements Serializable {
    String url;
    String title;
    int occurrences;
    String[] pagesThatContain;

    /**
     * Constructs a Site object with the given URL and title.
     *
     * @param url   The URL of the web page.
     * @param title The title of the web page.
     */
    public Site(String url, String title) {
        this.url = url;
        this.title = title;
        this.occurrences = 0;
    }

    /**
     * Constructs a Site object with the given URL, title, and number of occurrences.
     *
     * @param url         The URL of the web page.
     * @param title       The title of the web page.
     * @param occurrences The number of times the site is referenced.
     */
    public Site(String url, String title, int occurrences) {
        this.url = url;
        this.title = title;
        this.occurrences = occurrences;
    }

    /**
     * Retrieves the pages that contain the current web page.
     *
     * @return A string representing the pages that contain the current web page.
     */
    public String getPagesThatContain() {
        StringBuilder result = new StringBuilder();
        for (String page : pagesThatContain) {
            result.append(page).append("\n");
        }
        return result.toString();
    }

    /**
     * Retrieves the URL of the web page.
     *
     * @return The URL of the web page.
     */
    public String getLink(){
        return url;

    }

    /**
     * Returns a string representation of the Site object.
     *
     * @return A string representation of the Site object.
     */
    @Override
    public String toString() {
        return "Site{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", referencedBy=" + occurrences +
                '}';
    }
}

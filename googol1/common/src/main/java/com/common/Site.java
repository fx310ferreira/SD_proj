/**
 * @author Frederico Xavier de Araújo Ferreira
 */

package com.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a site that has been indexed by the search engine.
 */
public class Site implements Serializable {
    String url;
    String title;
    String description;
    int occurrences;
    Site[] pagesThatContain;

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
    public ArrayList<Site> getPagesThatContain() {
        ArrayList<Site> sites = new ArrayList<>();
        Collections.addAll(sites, pagesThatContain);
        return sites;
    }

    public void setPagesThatContain(Site[] pagesThatContain) {
        this.pagesThatContain = pagesThatContain;
    }

    /**
     * Retrieves the URL of the web page.
     *
     * @return The URL of the web page.
     */
    public String getUrl(){
        return url;

    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
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

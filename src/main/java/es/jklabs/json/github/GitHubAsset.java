package es.jklabs.json.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitHubAsset {

    private String name;

    @JsonProperty("browser_download_url")
    private String browserDownloadUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrowserDownloadUrl() {
        return browserDownloadUrl;
    }

    public void setBrowserDownloadUrl(String browserDownloadUrl) {
        this.browserDownloadUrl = browserDownloadUrl;
    }
}

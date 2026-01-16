package es.jklabs.json.github;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GitHubRelease {

    @JsonProperty("tag_name")
    private String tagName;

    private List<GitHubAsset> assets;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<GitHubAsset> getAssets() {
        return assets;
    }

    public void setAssets(List<GitHubAsset> assets) {
        this.assets = assets;
    }
}

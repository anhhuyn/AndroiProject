package vn.iotstar.ecoveggieapp.models;

public class BlogModel {
    private String title;
    private String description;
    private String imageUrl;
    private String publishDate;

    public BlogModel(String title, String description, String imageUrl, String publishDate) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.publishDate = publishDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
}

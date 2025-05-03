package vn.iotstar.ecoveggieapp.models;

import com.google.gson.annotations.SerializedName;

public class MediaModel {

    @SerializedName("id")
    private int id;

    @SerializedName("mediaUrl")
    private String mediaUrl;

    @SerializedName("mediaType")
    private String mediaType;

    @SerializedName("uploadedAt")
    private String uploadedAt;

    // Getters & Setters
    public int getId() {
        return id;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getUploadedAt() {
        return uploadedAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setUploadedAt(String uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}

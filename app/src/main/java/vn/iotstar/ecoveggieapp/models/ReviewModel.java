package vn.iotstar.ecoveggieapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReviewModel {

    @SerializedName("reviewId")
    private int id;

    private UserModel user;

    @SerializedName("productId")
    private int productId;

    @SerializedName("rating")
    private int rating;

    @SerializedName("comment")
    private String comment;

    @SerializedName("createdAt")
    private String createdAt;

    // mediaList chứa danh sách mediaUrl bạn có thể ánh xạ như sau:
    @SerializedName("mediaList")
    private List<MediaModel> mediaList;

    public int getId() {
        return id;
    }

    public UserModel getUser() {
        return user;
    }

    public int getProductId() {
        return productId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public List<MediaModel> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<MediaModel> mediaList) {
        this.mediaList = mediaList;
    }
}

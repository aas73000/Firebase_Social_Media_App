package com.example.administrator.firebase_social_media_app;

public class DataSet {
    private String imageDescription,imageDownloadLink;

    public DataSet(String imageDescription, String imageDownloadLink) {
        this.imageDescription = imageDescription;
        this.imageDownloadLink = imageDownloadLink;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public String getImageDownloadLink() {
        return imageDownloadLink;
    }
}

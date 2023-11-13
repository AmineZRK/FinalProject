package com.example.finalproject.models;

import java.util.List;

import okhttp3.MultipartBody;

public class Waste {
    private double latitude;
    private double longitude;
    private String user;
    private String wasteType;
    private String weightEstimation;
    private String photoUrl;
    List<MultipartBody.Part> photos;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getWasteType() {
        return wasteType;
    }

    public void setWasteType(String wasteType) {
        this.wasteType = wasteType;
    }

    public String getWeightEstimation() {
        return weightEstimation;
    }

    public void setWeightEstimation(String weightEstimation) {
        this.weightEstimation = weightEstimation;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<MultipartBody.Part> getPhotos() {
        return photos;
    }

    public void setPhotos(List<MultipartBody.Part>  photos) {
        this.photos = photos;
    }
}

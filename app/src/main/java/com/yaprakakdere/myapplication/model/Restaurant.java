package com.yaprakakdere.myapplication.model;

/**
 * Created by yaprakakdere on 5/4/17.
 */



public class Restaurant {
    String id;
    String name;
    String description;
    String cover_img_url;
    String status;
    String delivery_fee;
    String average_rating;

    public String getCover_img_url() {
        return cover_img_url;
    }

    public String getDelivery_fee() {
        return delivery_fee;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getAverage_rating() {
        return average_rating;
    }
}

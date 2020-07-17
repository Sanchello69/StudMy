package com.example.studmy.models;

public class Like {
    private String name_like;
    private String address_like;
    private double latitude_like;
    private double longitude_like;

    public Like(String name_like, String address_like, double latitude_like, double longitude_like) {
        this.name_like = name_like;
        this.address_like = address_like;
        this.latitude_like = latitude_like;
        this.longitude_like = longitude_like;
    }

    public String getName_like() {
        return name_like;
    }

    public String getAddress_like() {
        return address_like;
    }

    public double getLatitude_like() {
        return latitude_like;
    }

    public double getLongitude_like() {
        return longitude_like;
    }
}

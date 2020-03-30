package com.example.studmy;

public class Like {
    private String name_like;
    private String address_like;

    public Like(String name_like, String address_like) {
        this.name_like = name_like;
        this.address_like = address_like;
    }

    public String getName_like() {
        return this.name_like;
    }

    public String getAddress_like() {
        return this.address_like;
    }
}

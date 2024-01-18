package com.toy.barterx.model;
import com.google.firebase.firestore.Exclude;

public class Profile {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String imageUrl;
    private double latitude;
    private double longitude; // Fixed typo from "logitude" to "longitude"

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() { // Fixed typo from "getLogitude" to "getLongitude"
        return longitude;
    }

    public Profile(String id, String firstname, String lastname, String email, String phone, String imageUrl, double latitude, double longitude) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id='" + id + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public Profile() {
    }

    public Profile(String id, String firstname, String lastname, String email, String phone, String imageUrl) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}

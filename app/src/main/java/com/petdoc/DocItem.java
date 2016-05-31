package com.petdoc;

/**
 * Created by STU on 2016-05-02.
 */
public class DocItem {

    private int id;
    private String title;
    private String address;
    private String phone;
    private double latitude;
    private double longitude;
    private double distance;
    private float rating;
    private int count;

    public DocItem(int id, String title, String address, String phone, double latitude, double longitude) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public DocItem(int id, String title, String address, String phone, double latitude, double longitude, double distance, float rating, int count) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.rating = rating;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

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
}

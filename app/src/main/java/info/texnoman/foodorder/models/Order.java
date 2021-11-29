package info.texnoman.foodorder.models;

import java.io.Serializable;

public class Order implements Serializable {
    int id;
    int customer_id;
    String  name;
    int restaurant_id;
    int delivery_price;
    int total_price;
    String address;
    int phone;
    String comment;
    int status;
    double latitude;
    double longitude;
    String created_at;
    String restaurant_name;
    Restaurant restaurant;

    public Order(int id, int customer_id, String name, int restaurant_id, int delivery_price, int total_price, String address, int phone, String comment, int status, double latitude, double longitude, String created_at,String restaurant_name,Restaurant restaurant) {
        this.id = id;
        this.customer_id = customer_id;
        this.name = name;
        this.restaurant_id = restaurant_id;
        this.delivery_price = delivery_price;
        this.total_price = total_price;
        this.address = address;
        this.phone = phone;
        this.comment = comment;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.created_at = created_at;
        this.restaurant_name = restaurant_name;
        this.restaurant = restaurant;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public int getDelivery_price() {
        return delivery_price;
    }

    public void setDelivery_price(int delivery_price) {
        this.delivery_price = delivery_price;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}

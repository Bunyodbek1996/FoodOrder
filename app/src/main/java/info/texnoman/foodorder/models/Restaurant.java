package info.texnoman.foodorder.models;

import java.io.Serializable;

public class Restaurant implements Serializable {
    int id;
    String name;
    String logo;
    String image;
    int min_order_amount;
    String working_hours_from;
    String working_hours_to;
    int delivery_time_min;
    int delivery_time_max;
    int delivery_price;
    double latitude;
    double longtitude;
    int is_top;
    int status;
    boolean heart;

    public Restaurant(int id, String name, String logo, String image, int min_order_amount, String working_hours_from, String working_hours_to, int delivery_time_min, int delivery_time_max, int delivery_price, double latitude, double longtitude, int is_top, int status,boolean heart) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.image = image;
        this.min_order_amount = min_order_amount;
        this.working_hours_from = working_hours_from;
        this.working_hours_to = working_hours_to;
        this.delivery_time_min = delivery_time_min;
        this.delivery_time_max = delivery_time_max;
        this.delivery_price = delivery_price;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.is_top = is_top;
        this.status = status;
        this.heart = heart;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getMin_order_amount() {
        return min_order_amount;
    }

    public void setMin_order_amount(int min_order_amount) {
        this.min_order_amount = min_order_amount;
    }

    public String getWorking_hours_from() {
        return working_hours_from;
    }

    public void setWorking_hours_from(String working_hours_from) {
        this.working_hours_from = working_hours_from;
    }

    public String getWorking_hours_to() {
        return working_hours_to;
    }

    public void setWorking_hours_to(String working_hours_to) {
        this.working_hours_to = working_hours_to;
    }

    public int getDelivery_time_min() {
        return delivery_time_min;
    }

    public void setDelivery_time_min(int delivery_time_min) {
        this.delivery_time_min = delivery_time_min;
    }

    public int getDelivery_time_max() {
        return delivery_time_max;
    }

    public void setDelivery_time_max(int delivery_time_max) {
        this.delivery_time_max = delivery_time_max;
    }

    public int getDelivery_price() {
        return delivery_price;
    }

    public void setDelivery_price(int delivery_price) {
        this.delivery_price = delivery_price;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public int getIs_top() {
        return is_top;
    }

    public void setIs_top(int is_top) {
        this.is_top = is_top;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isHeart() {
        return heart;
    }

    public void setHeart(boolean heart) {
        this.heart = heart;
    }
}

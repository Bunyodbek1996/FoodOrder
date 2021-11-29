package info.texnoman.foodorder.models;

import java.util.List;

public class FoodType {
    int id;
    int restaurant_id;
    String name;
    String icon;
    int status;
    List<Product> productList;

    public FoodType(int id, int restaurant_id, String name, String icon, int status, List<Product> productList) {
        this.id = id;
        this.restaurant_id = restaurant_id;
        this.name = name;
        this.icon = icon;
        this.status = status;
        this.productList = productList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
}

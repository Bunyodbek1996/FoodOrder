package info.texnoman.foodorder.models;

import java.io.Serializable;

public class CartProduct implements Serializable {
    int id;
    int foodtype_id;
    int parent_id;
    String name;
    String description;
    String image;
    int price;
    int quantity;


    public CartProduct(int id, int foodtype_id, int parent_id, String name, String description, String image, int price, int quantity) {
        this.id = id;
        this.foodtype_id = foodtype_id;
        this.parent_id = parent_id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFoodtype_id() {
        return foodtype_id;
    }

    public void setFoodtype_id(int foodtype_id) {
        this.foodtype_id = foodtype_id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}
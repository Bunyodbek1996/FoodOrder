package info.texnoman.foodorder.models;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    int id;
    int foodtype_id;
    String name;
    String description;
    String image;
    int price;
    String unity;
    int status;
    List<Product> children;

    public Product(int id, int foodtype_id, String name,
                   String description, String image, int price,
                   String unity, int status,
                   List<Product> children) {
        this.id = id;
        this.foodtype_id = foodtype_id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.unity = unity;
        this.status = status;
        this.children = children;
    }

    public List<Product> getChildren() {
        return children;
    }

    public void setChildren(List<Product> children) {
        this.children = children;
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

    public String getUnity() {
        return unity;
    }

    public void setUnity(String unity) {
        this.unity = unity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

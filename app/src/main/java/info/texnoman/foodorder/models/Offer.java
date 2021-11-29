package info.texnoman.foodorder.models;

public class Offer {
    int id;
    String name;
    String text;
    String image;
    String type;
    String link;
    String restaurant_id;

    public Offer(int id, String name, String text, String image, String type,String link, String restaurant_id) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.image = image;
        this.type = type;
        this.link = link;
        this.restaurant_id = restaurant_id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }
}

package info.texnoman.foodorder.models;

public class City {
    int id;
    String name;
    String lattitude;
    String longtitude;

    public City(int id, String name, String lattitude, String longtitude) {
        this.id = id;
        this.name = name;
        this.lattitude = lattitude;
        this.longtitude = longtitude;
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

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }


}

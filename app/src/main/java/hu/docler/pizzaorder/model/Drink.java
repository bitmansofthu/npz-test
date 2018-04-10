package hu.docler.pizzaorder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Drink {

    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private Integer id;

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
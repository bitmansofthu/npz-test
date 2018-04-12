package hu.docler.pizzaorder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Drink implements CartItem {

    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private Long id;

    @Override
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
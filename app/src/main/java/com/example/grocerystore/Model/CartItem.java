package com.example.grocerystore.Model;

public class CartItem {
    String id , proId , name , price , number , totalPrice ;

    public CartItem(){}

    public CartItem(String id, String proId, String name, String price, String number, String totalPrice) {
        this.id = id;
        this.proId = proId;
        this.name = name;
        this.price = price;
        this.number = number;
        this.totalPrice = totalPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getProId() {
        return proId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}

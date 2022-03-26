package com.example.grocerystore.Model;

public class OrderedItemUser {
    String pId , name , price , quantyti , totalPrice ;

    public OrderedItemUser(){}

    public OrderedItemUser(String pId, String name, String price, String quantyti, String totalPrice) {
        this.pId = pId;
        this.name = name;
        this.price = price;
        this.quantyti = quantyti;
        this.totalPrice = totalPrice;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
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

    public String getQuantyti() {
        return quantyti;
    }

    public void setQuantyti(String quantyti) {
        this.quantyti = quantyti;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}

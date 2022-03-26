package com.example.grocerystore.Model;

public class ShopModel {
    String uId , img_Profile , name , shop_name , email ,phone , address_shop , delivery
            ,timeStamp , accountType , online , open ;
    double  latitue , longtitue;

    public ShopModel(){}

    public ShopModel(String uId, String img_Profile, String name, String shop_name, String email, String phone, String address_shop, String delivery, String timeStamp, String accountType,
                     String online, String open, double latitue, double longtitue) {
        this.uId = uId;
        this.img_Profile = img_Profile;
        this.name = name;
        this.shop_name = shop_name;
        this.email = email;
        this.phone = phone;
        this.address_shop = address_shop;
        this.delivery = delivery;
        this.timeStamp = timeStamp;
        this.accountType = accountType;
        this.online = online;
        this.open = open;
        this.latitue = latitue;
        this.longtitue = longtitue;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getImg_Profile() {
        return img_Profile;
    }

    public void setImg_Profile(String img_Profile) {
        this.img_Profile = img_Profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress_shop() {
        return address_shop;
    }

    public void setAddress_shop(String address_shop) {
        this.address_shop = address_shop;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public double getLatitue() {
        return latitue;
    }

    public void setLatitue(double latitue) {
        this.latitue = latitue;
    }

    public double getLongtitue() {
        return longtitue;
    }

    public void setLongtitue(double longtitue) {
        this.longtitue = longtitue;
    }
}

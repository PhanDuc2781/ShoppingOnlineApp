package com.example.grocerystore.Model;

public class ProductSeller {
    String productId , name , description, category , quantity ,
            realPrice , discount , discountNote , img_Url ,discountAvailable , timeStamp , uId;

    public ProductSeller(){}

    public ProductSeller(String productId, String name, String description, String category, String quantity,
                         String realPrice, String discount, String discountNote,
                         String img_Url, String discountAvailable, String timeStamp, String uId) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.quantity = quantity;
        this.realPrice = realPrice;
        this.discount = discount;
        this.discountNote = discountNote;
        this.img_Url = img_Url;
        this.discountAvailable = discountAvailable;
        this.timeStamp = timeStamp;
        this.uId = uId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(String realPrice) {
        this.realPrice = realPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscountNote() {
        return discountNote;
    }

    public void setDiscountNote(String discountNote) {
        this.discountNote = discountNote;
    }

    public String getImg_Url() {
        return img_Url;
    }

    public void setImg_Url(String img_Url) {
        this.img_Url = img_Url;
    }

    public String getDiscountAvailable() {
        return discountAvailable;
    }

    public void setDiscountAvailable(String discountAvailable) {
        this.discountAvailable = discountAvailable;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}

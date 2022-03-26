package com.example.grocerystore.Model;

public class OrderItemUser {
    String orderId , name_StoreOrderUser , orderTime , totalBill , status , orderBy , orderTo ;

    public OrderItemUser(){}

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName_StoreOrderUser() {
        return name_StoreOrderUser;
    }

    public void setName_StoreOrderUser(String name_StoreOrderUser) {
        this.name_StoreOrderUser = name_StoreOrderUser;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(String totalBill) {
        this.totalBill = totalBill;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderTo() {
        return orderTo;
    }

    public void setOrderTo(String orderTo) {
        this.orderTo = orderTo;
    }

    public OrderItemUser(String orderId, String name_StoreOrderUser, String orderTime, String totalBill, String status, String orderBy, String orderTo) {
        this.orderId = orderId;
        this.name_StoreOrderUser = name_StoreOrderUser;
        this.orderTime = orderTime;
        this.totalBill = totalBill;
        this.status = status;
        this.orderBy = orderBy;
        this.orderTo = orderTo;


    }
}

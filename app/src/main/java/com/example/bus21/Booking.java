package com.example.bus21;

public class Booking {

    private String firstname;
    private String lastname;
    private String contact;
    private String destination;
    private String time;
    private String price;
    private String status;
    private String bid;

    public Booking(){}

    public Booking(String bid, String firstname, String lastname, String contact, String destination,String time, String price, String status) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.contact = contact;
        this.destination = destination;
        this.time = time;
        this.price = price;
        this.status = status;
        this.bid = bid;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }
}

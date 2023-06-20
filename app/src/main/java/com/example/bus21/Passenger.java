package com.example.bus21;

public class Passenger {

    private String firstname;
    private String lastname;
    private String contact;
    private String password;
    private String usertype;
    private String pid;

    public Passenger(){}

    public Passenger(String pid, String firstname, String lastname, String contact, String password,String usertype) {
        this.pid = pid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.contact = contact;
        this.password = password;
        this.usertype = usertype;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsertype() {
        return usertype;
    }

    public void getUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}

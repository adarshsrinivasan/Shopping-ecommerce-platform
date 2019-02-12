package com.example.userservice.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class User {

    @Id
    private String id;
    private String userId; // should be unique and will be used to login
    private String userName;
    private Address userAddress;
    private String mailId;
    private String phoneNumber;
    private boolean isVendor = false;

    public User() {
    }

    public User(String id, String userId, String userName, Address userAddress, String mailId, String phoneNumber, boolean isVendor) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userAddress = userAddress;
        this.mailId = mailId;
        this.phoneNumber = phoneNumber;
        this.isVendor = isVendor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Address getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(Address userAddress) {
        this.userAddress = userAddress;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isVendor() {
        return isVendor;
    }

    public void setVendor(boolean vendor) {
        isVendor = vendor;
    }
}

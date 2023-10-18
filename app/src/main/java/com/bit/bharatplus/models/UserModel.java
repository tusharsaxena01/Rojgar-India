package com.bit.bharatplus.models;

import java.net.URI;

public class UserModel {
    private String uid;
    private URI profilePictureURI;
    private String profilePictureURL;
    private String name;
    private String profession;
    private String gender;
    private String phoneNumber;

    private String location;

    public UserModel() {
    }

    public UserModel(String uid, URI profileURI, String name, String profession, String gender, String phoneNumber) {
        this.uid = uid;
        this.profilePictureURI = profileURI;
        this.name = name;
        this.profession = profession;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UserModel(String uid, String profileURL, String name, String profession, String gender, String phoneNumber) {
        this.uid = uid;
        this.profilePictureURL = profileURL;
        this.name = name;
        this.profession = profession;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public URI getProfilePictureURI() {
        return profilePictureURI;
    }

    public void setProfilePictureURI(URI profilePictureURI) {
        this.profilePictureURI = profilePictureURI;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public void setProfilePictureURL(String profilePictureURL) {
        this.profilePictureURL = profilePictureURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

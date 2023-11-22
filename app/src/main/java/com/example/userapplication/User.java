package com.example.userapplication;

public class User {
    private long id;
    private String name;
    private String email;
    private String date;
    private String gender;
    private byte[] image;

    public User() {
    }

    public User(long id, String name, String email, String date, String gender, byte[] image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.date = date;
        this.gender = gender;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}


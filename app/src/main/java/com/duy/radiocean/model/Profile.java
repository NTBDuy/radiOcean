package com.duy.radiocean.model;

public class Profile {
    String email, name, gender;
    String id;

    public String getId() {
        return id;
    }

    public Profile(String id, String email, String name, String gender) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.gender = gender;
    }
    public Profile(){}

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }
}

package com.duy.radiocean.model;

public class Profile {
    String email, name, gender;



    public Profile( String email, String name, String gender) {
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

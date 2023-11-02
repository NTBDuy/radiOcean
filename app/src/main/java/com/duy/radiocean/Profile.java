package com.duy.radiocean;

public class Profile {
    String email, name, gender;

    public Profile(String email, String name, String gender) {
        this.email = email;
        this.name = name;
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

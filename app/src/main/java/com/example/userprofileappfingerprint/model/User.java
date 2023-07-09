package com.example.userprofileappfingerprint.model;

public class User {
    private String email,name,mobile,gender,password,DOB;

    public User(){

    }

    public User(String email, String name, String mobile, String gender, String password,String DOB) {
        this.email = email;
        this.name = name;
        this.mobile = mobile;
        this.gender = gender;
        this.password = password;
        this.DOB=DOB;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }
}

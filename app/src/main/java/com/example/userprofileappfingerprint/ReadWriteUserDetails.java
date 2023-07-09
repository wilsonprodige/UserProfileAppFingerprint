package com.example.userprofileappfingerprint;

public class ReadWriteUserDetails {
    public String dob,mobile,gender;
    //constructor , enables to grap a snapshot of data
    public ReadWriteUserDetails(){}
    public ReadWriteUserDetails(String textDOB,String textGender,String textMobile){
        this.dob=textDOB;
        this.gender=textGender;
        this.mobile=textMobile;
    }

}

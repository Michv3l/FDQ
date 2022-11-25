package com.example.firstdatequestions;

import com.google.firebase.firestore.PropertyName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Profile {

    @PropertyName("Display_Name")
    private String display_Name;

    @PropertyName("Age")
    private String birthday;

    @PropertyName("Occupation")
    private String occupation;

    @PropertyName("School")
    private String school;

    @PropertyName("About")
    private String about;

    @PropertyName("Gender")
    private String gender;

    @PropertyName("Orientation")
    private String orientation;

    public String getAge() {
        return birthday;
    }

    public void setAge(String birthday) {
        this.birthday = birthday;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }


    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }


    public String getDisplay_Name() {
        return display_Name;
    }

    public void setDisplay_Name(String dname) {
        this.display_Name = dname;
    }

}

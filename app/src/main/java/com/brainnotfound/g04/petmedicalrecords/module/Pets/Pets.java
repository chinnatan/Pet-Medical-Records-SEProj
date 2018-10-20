package com.brainnotfound.g04.petmedicalrecords.module.Pets;

import android.net.Uri;

public class Pets  {

    private String pet_name;
    private String pet_type;
    private String pet_sex;
    private String pet_ageDay;
    private String pet_ageMonth;
    private String pet_ageYear;
    private String urlImage;
    private static Pets getPetsInstance;

    private Pets() {}

    private Pets(String pet_name, String pet_type, String pet_sex, String pet_ageDay, String pet_ageMonth, String pet_ageYear, String urlImage) {
        this.setPet_name(pet_name);
        this.setPet_type(pet_type);
        this.setPet_sex(pet_sex);
        this.setPet_ageDay(pet_ageDay);
        this.setPet_ageMonth(pet_ageMonth);
        this.setPet_ageYear(pet_ageYear);
        this.setUrlImage(urlImage);
    }

    public static Pets getGetPetsInstance() {
        if(getPetsInstance == null) {
            getPetsInstance = new Pets();
        }
        return getPetsInstance;
    }

    public static Pets getGetPetsInstance(String pet_name, String pet_type, String pet_sex, String pet_ageDay, String pet_ageMonth, String pet_ageYear, String urlImage) {
        if(getPetsInstance == null) {
            getPetsInstance = new Pets(pet_name, pet_type, pet_sex, pet_ageDay, pet_ageMonth, pet_ageYear, urlImage);
        }
        return getPetsInstance;
    }

    public String getPet_name() {
        return pet_name;
    }

    public void setPet_name(String pet_name) {
        this.pet_name = pet_name;
    }

    public String getPet_type() {
        return pet_type;
    }

    public void setPet_type(String pet_type) {
        this.pet_type = pet_type;
    }

    public String getPet_sex() {
        return pet_sex;
    }

    public void setPet_sex(String pet_sex) {
        this.pet_sex = pet_sex;
    }

    public String getPet_ageDay() {
        return pet_ageDay;
    }

    public void setPet_ageDay(String pet_ageDay) {
        this.pet_ageDay = pet_ageDay;
    }

    public String getPet_ageMonth() {
        return pet_ageMonth;
    }

    public void setPet_ageMonth(String pet_ageMonth) {
        this.pet_ageMonth = pet_ageMonth;
    }

    public String getPet_ageYear() {
        return pet_ageYear;
    }

    public void setPet_ageYear(String pet_ageYear) {
        this.pet_ageYear = pet_ageYear;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}

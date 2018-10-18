package com.brainnotfound.g04.petmedicalrecords.module.Pets;

import java.util.Date;

public class Pets  {

    private String pet_name;
    private String pet_type;
    private String pet_sex;
    private Date pet_age;
    private static Pets getPetsInstance;

    private Pets() {}

    private Pets(String pet_name, String pet_type, String pet_sex, Date pet_age) {
        this.setPet_name(pet_name);
        this.setPet_type(pet_type);
        this.setPet_sex(pet_sex);
        this.setPet_age(pet_age);
    }

    public static Pets getGetPetsInstance() {
        if(getPetsInstance == null) {
            getPetsInstance = new Pets();
        }
        return getPetsInstance;
    }

    public static Pets getGetPetsInstance(String pet_name, String pet_type, String pet_sex, Date pet_age) {
        if(getPetsInstance == null) {
            getPetsInstance = new Pets(pet_name, pet_type, pet_sex, pet_age);
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

    public Date getPet_age() {
        return pet_age;
    }

    public void setPet_age(Date pet_age) {
        this.pet_age = pet_age;
    }
}

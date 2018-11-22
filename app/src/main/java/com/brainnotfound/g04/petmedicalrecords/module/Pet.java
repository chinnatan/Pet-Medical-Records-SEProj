package com.brainnotfound.g04.petmedicalrecords.module;

public class Pet {

    private String petkey;
    private String petname;
    private String pettype;
    private String petsex;
    private String petyear;
    private String petmonth;
    private String petday;
    private String petimage;
    private String petownerUid;

    private static Pet petInstance;

    private Pet() {

    }

    public static Pet getPetInstance() {
        if (petInstance == null) {
            petInstance = new Pet();
        }
        return petInstance;
    }

    public String getPetkey() {
        return petkey;
    }

    public void setPetkey(String petkey) {
        this.petkey = petkey;
    }

    public String getPetname() {
        return petname;
    }

    public void setPetname(String petname) {
        this.petname = petname;
    }

    public String getPettype() {
        return pettype;
    }

    public void setPettype(String pettype) {
        this.pettype = pettype;
    }

    public String getPetsex() {
        return petsex;
    }

    public void setPetsex(String petsex) {
        this.petsex = petsex;
    }

    public String getPetyear() {
        return petyear;
    }

    public void setPetyear(String petyear) {
        this.petyear = petyear;
    }

    public String getPetmonth() {
        return petmonth;
    }

    public void setPetmonth(String petmonth) {
        this.petmonth = petmonth;
    }

    public String getPetday() {
        return petday;
    }

    public void setPetday(String petday) {
        this.petday = petday;
    }

    public String getPetownerUid() {
        return petownerUid;
    }

    public void setPetownerUid(String petownerUid) {
        this.petownerUid = petownerUid;
    }

    public String getPetimage() {
        return petimage;
    }

    public void setPetimage(String petimage) {
        this.petimage = petimage;
    }

    public void clear() {
        petInstance = null;
    }
}

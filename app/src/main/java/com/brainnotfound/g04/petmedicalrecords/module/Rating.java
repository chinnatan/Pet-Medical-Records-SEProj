package com.brainnotfound.g04.petmedicalrecords.module;

public class Rating {

    private String petownerid;
    private float rating;

    public Rating() {

    }

    public Rating(String petownerid, float rating) {
        this.setPetownerid(petownerid);
        this.setRating(rating);
    }

    public String getPetownerid() {
        return petownerid;
    }

    public void setPetownerid(String petownerid) {
        this.petownerid = petownerid;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}

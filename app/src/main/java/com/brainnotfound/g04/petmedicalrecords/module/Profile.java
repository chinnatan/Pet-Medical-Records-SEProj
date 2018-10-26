package com.brainnotfound.g04.petmedicalrecords.module;

public class Profile {

    private String firstname = null;
    private String lastname;
    private String phonenumber;
    private String account_type;
    private String urlImage;

    private static Profile profileInstance;

    private Profile() {}

    public Profile(String firstname, String lastname, String phonenumber, String account_type, String urlImage) {
        this.setFirstname(firstname);
        this.setLastname(lastname);
        this.setPhonenumber(phonenumber);
        this.setAccount_type(account_type);
        this.setUrlImage(urlImage);
    }

    public static Profile getProfileInstance() {
        if(profileInstance == null) {
            profileInstance = new Profile();
        }
        return profileInstance;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}

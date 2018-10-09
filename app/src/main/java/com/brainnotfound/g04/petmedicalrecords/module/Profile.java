package com.brainnotfound.g04.petmedicalrecords.module;

public class Profile {

    private String firstname;
    private String lastname;
    private String phonenumber;

    public Profile() {}

    public Profile(String firstname, String lastname, String phonenumber) {
        this.setFirstname(firstname);
        this.setLastname(lastname);
        this.setPhonenumber(phonenumber);
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
}

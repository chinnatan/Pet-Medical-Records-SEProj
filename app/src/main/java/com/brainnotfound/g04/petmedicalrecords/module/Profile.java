package com.brainnotfound.g04.petmedicalrecords.module;

public class Profile {

    private String firstname;
    private String lastname;
    private String phonenumber;
    private String account_type;

    public Profile() {}

    public Profile(String firstname, String lastname, String phonenumber, String account_type) {
        this.setFirstname(firstname);
        this.setLastname(lastname);
        this.setPhonenumber(phonenumber);
        this.setAccount_type(account_type);
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
}

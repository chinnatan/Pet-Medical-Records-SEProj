package com.brainnotfound.g04.petmedicalrecords.module;

public class Request {

    private String requestdate;
    private String requestkey;
    private String petkey;
    private String customeruid;
    private String veterinaryuid;
    private String status;

    public Request() {

    }

    public Request(String petkey, String customeruid, String veterinaryuid, String status) {
        this.petkey = petkey;
        this.customeruid = customeruid;
        this.veterinaryuid = veterinaryuid;
        this.status = status;
    }

    public String getPetkey() {
        return petkey;
    }

    public void setPetkey(String petkey) {
        this.petkey = petkey;
    }

    public String getCustomeruid() {
        return customeruid;
    }

    public void setCustomeruid(String customeruid) {
        this.customeruid = customeruid;
    }

    public String getVeterinaryuid() {
        return veterinaryuid;
    }

    public void setVeterinaryuid(String veterinaryuid) {
        this.veterinaryuid = veterinaryuid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestkey() {
        return requestkey;
    }

    public void setRequestkey(String requestkey) {
        this.requestkey = requestkey;
    }

    public String getRequestdate() {
        return requestdate;
    }

    public void setRequestdate(String requestdate) {
        this.requestdate = requestdate;
    }
}

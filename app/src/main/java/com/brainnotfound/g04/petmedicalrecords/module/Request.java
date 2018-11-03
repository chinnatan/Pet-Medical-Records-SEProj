package com.brainnotfound.g04.petmedicalrecords.module;

public class Request {

    private String status;
    private String uid;
    private String key;

    private static Request requestInstance;

    private Request() {

    }

    public static Request getRequestInstance(){
        if(requestInstance == null) {
            requestInstance = new Request();
        }
        return requestInstance;
    }

    public Request(String status, String uid, String key) {
        this.setStatus(status);
        this.setUid(uid);
        this.setKey(key);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

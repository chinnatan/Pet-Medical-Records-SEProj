package com.brainnotfound.g04.petmedicalrecords.module;

public class History {

    private String petid;

    private String historyid;
    private String title;
    private String detail;
    private String addby;
    private String date;
    private String datetime;

    public History() {
    }

    public History(String petid, String historyid, String title, String detail, String addby, String date, String datetime) {
        this.setPetid(petid);
        this.setHistoryid(historyid);
        this.setTitle(title);
        this.setDetail(detail);
        this.setAddby(addby);
        this.setDate(date);
        this.setDatetime(datetime);
    }

    public String getPetid() {
        return petid;
    }

    public void setPetid(String petid) {
        this.petid = petid;
    }

    public String getHistoryid() {
        return historyid;
    }

    public void setHistoryid(String historyid) {
        this.historyid = historyid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAddby() {
        return addby;
    }

    public void setAddby(String addby) {
        this.addby = addby;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}

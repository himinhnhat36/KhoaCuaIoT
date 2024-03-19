package com.example.firebase.Log;

public class LogSK {
    private int malog;
    private String myDate,myTime,myTrangThai;



    public LogSK(int malog, String myDate, String myTime, String myTrangThai) {
        this.malog = malog;
        this.myDate = myDate;
        this.myTime = myTime;
        this.myTrangThai = myTrangThai;
    }

    public LogSK(String myDate, String myTime, String myTrangThai) {
        this.myDate = myDate;
        this.myTime = myTime;
        this.myTrangThai = myTrangThai;
    }

    public LogSK() {

    }

    public LogSK(String myTime, String myTrangThai) {
        this.myTime = myTime;
        this.myTrangThai = myTrangThai;
    }

    public int getMalog() {
        return malog;
    }

    public void setMalog(int malog) {
        this.malog = malog;
    }

    public String getMyDate() {
        return myDate;
    }

    public void setMyDate(String myDate) {
        this.myDate = myDate;
    }

    public String getMyTime() {
        return myTime;
    }

    public void setMyTime(String myTime) {
        this.myTime = myTime;
    }

    public String getMyTrangThai() {
        return myTrangThai;
    }

    public void setMyTrangThai(String myTrangThai) {
        this.myTrangThai = myTrangThai;
    }
}

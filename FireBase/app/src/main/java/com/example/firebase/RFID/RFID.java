package com.example.firebase.RFID;

public class RFID {
    private int maRFID;
    private String myIDRFID,myUserRFID,myEmailRFID;

    public RFID(int maRFID, String myIDRFID, String myUserRFID, String myEmailRFID) {
        this.maRFID = maRFID;
        this.myIDRFID = myIDRFID;
        this.myUserRFID = myUserRFID;
        this.myEmailRFID = myEmailRFID;
    }

    public RFID(String myIDRFID, String myUserRFID, String myEmailRFID) {
        this.myIDRFID = myIDRFID;
        this.myUserRFID = myUserRFID;
        this.myEmailRFID = myEmailRFID;
    }

    public RFID() {

    }

    public int getMaRFID() {
        return maRFID;
    }

    public void setMaRFID(int maRFID) {
        this.maRFID = maRFID;
    }

    public String getMyIDRFID() {
        return myIDRFID;
    }

    public void setMyIDRFID(String myIDRFID) {
        this.myIDRFID = myIDRFID;
    }

    public String getMyUserRFID() {
        return myUserRFID;
    }

    public void setMyUserRFID(String myUserRFID) {
        this.myUserRFID = myUserRFID;
    }

    public String getMyEmailRFID() {
        return myEmailRFID;
    }

    public void setMyEmailRFID(String myEmailRFID) {
        this.myEmailRFID = myEmailRFID;
    }
}

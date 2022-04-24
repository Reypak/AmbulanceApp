package com.matt.ambulance.object;

import java.util.Date;

public class Stat {

    String ambNo;
    String location;
    String uname;
    Date timestamp;

    public Stat(String ambNo, String location, String uname, Date timestamp) {
        this.ambNo = ambNo;
        this.location = location;
        this.uname = uname;
        this.timestamp = timestamp;
    }

    public Stat() {
    }

    public String getLocation() {
        return location;
    }

    public String getUname() {
        return uname;
    }

    public String getAmbNo() {
        return ambNo;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}

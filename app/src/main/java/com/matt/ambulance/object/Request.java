package com.matt.ambulance.object;

import java.util.Date;

public class Request {
    String location;
    String aid;
    Date timestamp;
    int status;

    public Request() {
    }

    public String getLocation() {
        return location;
    }

    public String getAid() {
        return aid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

    /*KEY
    1-Requesting
    2-Matched / On the Way
    3-Arrived
    4-Start Trip
    5-End Trip*/

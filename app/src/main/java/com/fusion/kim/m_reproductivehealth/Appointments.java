package com.fusion.kim.m_reproductivehealth;

public class Appointments {


    //declare appointment layout variables
    private String key, hospital, doctor, purpose, date, time;

    public Appointments() {
    }

    public Appointments(String key, String hospital, String doctor, String purpose, String date, String time) {
        this.key = key;
        this.hospital = hospital;
        this.doctor = doctor;
        this.purpose = purpose;
        this.date = date;
        this.time = time;
    }


    //get and set the variables
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

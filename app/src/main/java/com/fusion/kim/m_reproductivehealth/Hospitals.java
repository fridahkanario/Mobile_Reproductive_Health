package com.fusion.kim.m_reproductivehealth;

public class Hospitals {

    private String hospitalName, key;

    public Hospitals() {
    }

    public Hospitals(String hospitalName, String key) {
        this.hospitalName = hospitalName;
        this.key = key;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

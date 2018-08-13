package com.fusion.kim.m_reproductivehealth;

public class Regions {

    private String key, regionName;

    public Regions(String key, String regionName) {
        this.key = key;
        this.regionName = regionName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }
}

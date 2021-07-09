package com.example.unccrudapp.model;

public class Store {
    String id;
    String name;
    String site;
    String type;
    String city;
    String state;

    public Store() {
    }

    public Store(String id, String name, String site, String type, String city, String state) {
        this.id = id;
        this.name = name;
        this.site = site;
        this.type = type;
        this.city = city;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

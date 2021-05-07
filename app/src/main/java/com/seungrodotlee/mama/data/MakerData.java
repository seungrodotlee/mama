package com.seungrodotlee.mama.data;

import java.util.ArrayList;

public class MakerData {
    public String name;
    public String description;
    public String dependency;
    public String date;
    public ArrayList<String> tags = new ArrayList<String>();

    public MakerData() {}

    public MakerData(String name, String description, String dependency, String date, ArrayList<String> tags) {
        this.name = name;
        this.description = description;
        this.dependency = dependency;
        this.date = date;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDependency() {
        return dependency;
    }

    public String getDate() {
        return date;
    }

    public ArrayList<String> getTags() {
        return tags;
    }
}
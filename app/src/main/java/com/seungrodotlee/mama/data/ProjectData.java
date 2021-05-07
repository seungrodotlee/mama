package com.seungrodotlee.mama.data;

import java.util.ArrayList;

public class ProjectData {
    public String name;
    public String description;
    public String startpoint;
    public String endpoint;
    public ArrayList<String> tags = new ArrayList<String>();

    public ProjectData() {}

    public ProjectData(String name, String description, String startpoint, String endpoint, ArrayList<String> tags) {
        this.name = name;
        this.description = description;
        this.startpoint = startpoint;
        this.endpoint = endpoint;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStartpoint() {
        return startpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public ArrayList<String> getTags() {
        return tags;
    }
}
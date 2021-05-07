package com.seungrodotlee.mama.data;

import java.util.ArrayList;

public class Post {
    String name;
    ArrayList<ProjectData> projects;
    ArrayList<MakerData> schedules;
    ArrayList<MakerData> goals;

    public Post() {

    }

    public Post(String name, ArrayList<ProjectData> projects, ArrayList<MakerData> schedules, ArrayList<MakerData> goals) {
        this.name = name;
        this.projects = projects;
        this.schedules = schedules;
        this.goals = goals;
    }

    public String getName() {
        return name;
    }

    public ArrayList<ProjectData> getProjects() {
        return projects;
    }

    public ArrayList<MakerData> getSchedules() {
        return schedules;
    }

    public ArrayList<MakerData> getGoals() {
        return goals;
    }
}
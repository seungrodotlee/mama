package com.seungrodotlee.mama.element;

public class CalendarItem {
    public static final int NORMAL = -1;
    public static final int SAT_DAY = 0;
    public static final int SUN__DAY = 1;
    public static final int PROJECT_PERIOD = 2;
    public static final int IMPORTANT_DAY = 3;
    public static final int IMPORTANT_DAY_IN_PROJECT = 4;

    public static final int SELECTION_START = 0;
    public static final int SELECTION_BETWEEN = 1;
    public static final int SELECTION_END = 2;
    public static final int SELECTION_ONLY = 3;

    public boolean isSelection() {
        return selection;
    }

    public void setSelection(boolean selection) {
        this.selection = selection;
    }

    private boolean selection = false;

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    private int event = -1;

    private int year;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getBackground() {
        return background;
    }

    private int month;
    private int day;
    private int date;
    private String msg = "";
    private int background;

    public CalendarItem() {}


}

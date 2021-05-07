package com.seungrodotlee.mama.adapter;

public class SettingListItem {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public SettingListAdapter.OnItemEventListener getListener() {
        return listener;
    }

    public void setListener(SettingListAdapter.OnItemEventListener listener) {
        this.listener = listener;
    }

    private String current;
    private int type;
    private SettingListAdapter.OnItemEventListener listener;
}

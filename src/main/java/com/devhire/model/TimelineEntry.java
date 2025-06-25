package com.devhire.model;

public class TimelineEntry {
    private String week;
    private String focus;

    public TimelineEntry() {}

    public TimelineEntry(String week, String focus) {
        this.week = week;
        this.focus = focus;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }
}

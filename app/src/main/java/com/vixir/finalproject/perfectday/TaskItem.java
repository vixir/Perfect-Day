package com.vixir.finalproject.perfectday;

public class TaskItem {

    public TaskItem() {
    }

    private String description;
    private int color;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    //Don't ask why no boolean...because ContentProvider
    private int isFinished;
    private int isToday;
    private String createdOn;


    public int getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(int isFinished) {
        this.isFinished = isFinished;
    }

    public int getIsToday() {
        return isToday;
    }

    public void setIsToday(int isToday) {
        this.isToday = isToday;
    }
}
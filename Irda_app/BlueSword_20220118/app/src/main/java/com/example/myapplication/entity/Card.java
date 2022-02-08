package com.example.myapplication.entity;

public class Card {

    /**网关标示ID*/
    private String markId ;
    /**网关标示符*/
    private String mark;
    /**网关状态*/
    private String markState;

    private String tag;

    public String getMarkId() {
        return markId;
    }

    public void setMarkId(String markId) {
        this.markId = markId;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getMarkState() {
        return markState;
    }

    public void setMarkState(String markState) {
        this.markState = markState;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}

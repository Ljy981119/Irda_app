package com.example.myapplication.param;

import java.io.Serializable;

public class CodeParam implements Serializable {
    private static final long serialVersionUID = 1084768212489800079L;
    private  long tag;
    private String uid;
    private String  id;
    private  String dec;

    private  String state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDec() {
        return dec;
    }

    public void setDec(String dec) {
        this.dec = dec;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTag() {
        return tag;
    }

    public void setTag(long tag) {
        this.tag = tag;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

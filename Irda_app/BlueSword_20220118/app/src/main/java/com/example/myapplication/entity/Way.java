package com.example.myapplication.entity;

import java.util.List;

public class Way {

    /**网关编号*/
    private String wayCode ;

    private String cardCode;


    /**板卡*/
    private List<Card> cars ;

    /**网关编号*/
    private String wayState ;
    public String getWayCode() {
        return wayCode;
    }

    public void setWayCode(String wayCode) {
        this.wayCode = wayCode;
    }

    public List<Card> getCars() {
        return cars;
    }

    public void setCars(List<Card> cars) {
        this.cars = cars;
    }

    public String getWayState() {
        return wayState;
    }

    public void setWayState(String wayState) {
        this.wayState = wayState;
    }


    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }
}

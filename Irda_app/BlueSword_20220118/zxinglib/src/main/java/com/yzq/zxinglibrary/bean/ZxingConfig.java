package com.yzq.zxinglibrary.bean;

import java.io.Serializable;

/**
 * Created by yzq on 2017/10/19.
 * <p>
 * zxing配置类
 */

public class ZxingConfig implements Serializable {


    /*是否播放声音*/
    private boolean isPlayBeep = true;
    /*是否震动*/
    private boolean isShake = false;
    /*是否显示下方的其他功能布局*/
    private boolean isShowbottomLayout = true;
    /*是否显示闪光灯按钮*/
    private boolean isShowFlashLight = true;
    /*是否显示相册按钮*/
    private boolean isShowAlbum = true;

    private    long  POSITION=0;

    private    int  LOCATION=0;

    public long getPOSITION() {
        return POSITION;
    }

    public void setPOSITION(long POSITION) {
        this.POSITION = POSITION;
    }

    public int getLOCATION() {
        return LOCATION;
    }

    public void setLOCATION(int LOCATION) {
        this.LOCATION = LOCATION;
    }

    public boolean isPlayBeep() {
        return isPlayBeep;
    }

    public void setPlayBeep(boolean playBeep) {
        isPlayBeep = playBeep;
    }

    public boolean isShake() {
        return isShake;
    }

    public void setShake(boolean shake) {
        isShake = shake;
    }

    public boolean isShowbottomLayout() {
        return isShowbottomLayout;
    }

    public void setShowbottomLayout(boolean showbottomLayout) {
        isShowbottomLayout = showbottomLayout;
    }

    public boolean isShowFlashLight() {
        return isShowFlashLight;
    }

    public void setShowFlashLight(boolean showFlashLight) {
        isShowFlashLight = showFlashLight;
    }

    public boolean isShowAlbum() {
        return isShowAlbum;
    }

    public void setShowAlbum(boolean showAlbum) {
        isShowAlbum = showAlbum;
    }



}

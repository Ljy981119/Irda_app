package com.example.myapplication.mynative;public class ConNetJni {    static {        System.loadLibrary("ConNetJni");    }    //native    public static native String sayHello();    //将字符串中的10进制数转化为16进制数    public static native String  getHexSixteen(int Sixteen);    ////将字符串中的16进制数转化为10进制数    public static native String  getcdHexDecimal(String Sixteen);}
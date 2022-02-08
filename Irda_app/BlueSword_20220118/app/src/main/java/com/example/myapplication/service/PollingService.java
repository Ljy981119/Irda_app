package com.example.myapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PollingService extends Service {

    public static final String ACTION = "com.ryantang.service.PollingService";
    int count = 0;
    private boolean threadDisable=false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // new PollingThread().start();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        new PollingThread().start();
    }
    /**
     * Polling thread
     * 模拟向Server轮询的异步线程
     * @Author Ryan
     * @Create 2013-7-13 上午10:18:34
     */


    class PollingThread extends Thread {
        @Override
        public void run() {
            System.out.println("Polling...");

//            while (!threadDisable) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//               count++;
//                   if (count % 5 == 0) {
            System.out.println("New message!");
                    //发送广播
                    Intent intent = new Intent();
                    intent.setAction("android.net.conn.CONNECTIVITY_CHANGE");
                    intent.setAction("android.hardware.usb.action.USB_STATE");
                    intent.setAction("com.android.ACTION_CLOSE_OTG");
                    sendBroadcast(intent);

             //  }

//            }
//
        };

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        count=0;
        threadDisable = true;

        System.out.println("Service:onDestroy");
    }

}
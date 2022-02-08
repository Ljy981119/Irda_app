package com.example.myapplication.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.BaseActivity;
import com.example.myapplication.utils.USBUtil;
import com.example.myapplication.utils.UsbConnectUtil;

import tw.com.prolific.driver.pl2303.PL2303Driver;

/**
 * Author: lenovo
 * Date: 2018/2/26
 * email: 1271396448@qq.com
 */

public class NetBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

    }
//    public NetEvevt evevt = BaseActivity.evevt;
//
//    public NetUsbEvevt usdevevt = BaseActivity.usedevevt;
//    private final static String ACTION ="android.hardware.usb.action.USB_STATE";
//    private static final String ACTION_CLOSE_OTG = "com.android.ACTION_CLOSE_OTG";
//
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // TODO Auto-generated method stub
//        // 如果相等的话就说明网络状态发生了变化
//        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//            int netWorkState = UsbConnectUtil.getNetWorkState(context);
//            // 接口回调传过去状态的类型
//            evevt.onNetChange(netWorkState);
//        }
////        Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT)
////                .show();
//
//        Log.d("输出通知=======", intent.getAction());
//
//       if (intent.getAction().equals(ACTION_CLOSE_OTG)) {
//
//
////            boolean connected = intent.getExtras().getBoolean("connected");
////            if (connected) {
//                boolean isUser= USBUtil.isUsbNet(context);
//
//                usdevevt.onNetUsbChange(isUser,USBUtil.getUsbSerial());
////           if (connected) {
////            } else {
////                Toast.makeText(context, "USB DisConnected!", Toast.LENGTH_SHORT)
////                        .show();
////            }
//
//         }
//
//
//
//
//    }
//
//
//
//    // 自定义接口
//    public interface NetEvevt {
//        public void onNetChange(int netMobile);
//    }
//
//    // 自定义接口
//    public interface NetUsbEvevt {
//        public void onNetUsbChange(boolean isUser,PL2303Driver  driver);
//    }
}
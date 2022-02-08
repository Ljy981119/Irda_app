package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



import com.example.myapplication.entity.Way;
import com.example.myapplication.service.PollingService;
import com.example.myapplication.utils.ByteUtils;
import com.example.myapplication.utils.Hex;
import com.example.myapplication.utils.HexConvert;
import com.example.myapplication.utils.PollingUtils;
import com.example.myapplication.utils.USBUtil;
import com.example.myapplication.utils.UsbConnectUtil;


import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tw.com.prolific.driver.pl2303.PL2303Driver;
import tw.com.prolific.driver.pl2303.PL2303Driver.DataBits;
import tw.com.prolific.driver.pl2303.PL2303Driver.FlowControl;
import tw.com.prolific.driver.pl2303.PL2303Driver.Parity;
import tw.com.prolific.driver.pl2303.PL2303Driver.StopBits;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;


/**
 * Author: lenovo
 * Date: 2018/2/26
 * email: 1271396448@qq.com
 */

abstract public class BaseActivity extends AppCompatActivity{

    private static final String ACTION_USB_PERMISSION = "com.example.myapplication.USB_PERMISSION";
    public static String OlDBJ_CODE = "";
    private static String NEWBJ_CODE = "";

    public static String OLDNET_CODE = "";
    private static String NENET_CODE = "";
    private static final boolean SHOW_DEBUG = true;

    // Defines of Display Settings
    private static final int DISP_CHAR = 0;

    public static boolean ISGETETCODE = false;
    public static boolean ISUSB=false;
    private static int netISMobile;

    public PL2303Driver mSerial;

    private static final String NULL = null;
    String TAG = "PL2303HXD_APLog";

    private Handler handler;
    private int counter;


    //BaudRate.B4800, DataBits.D8, StopBits.S1, Parity.NONE, FlowControl.RTSCTS
    private PL2303Driver.BaudRate mBaudrate = PL2303Driver.BaudRate.B9600;
    private PL2303Driver.DataBits mDataBits = PL2303Driver.DataBits.D8;
    private PL2303Driver.Parity mParity = PL2303Driver.Parity.NONE;
    private PL2303Driver.StopBits mStopBits = PL2303Driver.StopBits.S1;
    private PL2303Driver.FlowControl mFlowControl = PL2303Driver.FlowControl.OFF;




    @Override
    protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//
//        NetGBroadcastReceiver netBroadcastReceiver = new NetGBroadcastReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        filter.addAction("android.hardware.usb.action.USB_STATE");
//        filter.addAction("com.android.ACTION_CLOSE_OTG");
//        filter.setPriority(100);
//        BaseActivity.this.registerReceiver(netBroadcastReceiver, filter);
//        System.out.println("Start polling service...");
//        PollingUtils.startPollingService(this, 1, PollingService.class, PollingService.ACTION);


//        // get service
//        // get service
//        mSerial = new PL2303Driver((UsbManager) getSystemService(Context.USB_SERVICE),
//                this, ACTION_USB_PERMISSION);
//
//        // check USB host function.
//        if (!mSerial.PL2303USBFeatureSupported()) {
//
//            Toast.makeText(this, "No Support USB host API", Toast.LENGTH_SHORT)
//                    .show();
//
//            Log.d(TAG, "No Support USB host API");
//
//            mSerial = null;
//
//        }
//
//        if (!mSerial.enumerate()) {
//            Toast.makeText(this, "no more devices found", Toast.LENGTH_SHORT).show();
//        }
//
//        try {
//            Thread.sleep(1500);
//            openUsbSerial();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }

//    class NetGBroadcastReceiver extends BroadcastReceiver {
//
//        private final static String ACTION = "android.hardware.usb.action.USB_STATE";
//        private static final String ACTION_CLOSE_OTG = "com.android.ACTION_CLOSE_OTG";
//
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // TODO Auto-generated method stub
//            // 如果相等的话就说明网络状态发生了变化
//            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//                int netWorkState = UsbConnectUtil.getNetWorkState(context);
//                // 接口回调传过去状态的类型
//                onNetChange(netWorkState);
//            }
//
//            if (intent.getAction().equals(ACTION_CLOSE_OTG)) {
//
//                boolean isUser = openUsbSerial();
//
//                onNetUsbChange(isUser);
//
//
//            }
//
//
//        }   // 自定义接口
//
//    }


        private boolean openUsbSerial() {
        Log.d(TAG, "Enter  openUsbSerial");

        if(mSerial==null) {

            Log.d(TAG, "No mSerial");
            return false ;

        }


        if (mSerial.isConnected()) {
            if (SHOW_DEBUG) {
                Log.d(TAG, "openUsbSerial : isConnected ");
                return false ;

            }
            String str ="115200";
            int baudRate= Integer.parseInt(str);
            switch (baudRate) {
                case 9600:
                    mBaudrate = PL2303Driver.BaudRate.B9600;
                    break;
                case 19200:
                    mBaudrate =PL2303Driver.BaudRate.B19200;
                    break;
                case 115200:
                    mBaudrate =PL2303Driver.BaudRate.B115200;
                    break;
                default:
                    mBaudrate =PL2303Driver.BaudRate.B9600;
                    break;
            }
            Log.d(TAG, "baudRate:"+baudRate);
            // if (!mSerial.InitByBaudRate(mBaudrate)) {
            if (!mSerial.InitByBaudRate(mBaudrate,700)) {
                if(!mSerial.PL2303Device_IsHasPermission()) {
                    Toast.makeText(this, "cannot open, maybe no permission", Toast.LENGTH_SHORT).show();
                    return false ;
                }

                if(mSerial.PL2303Device_IsHasPermission() && (!mSerial.PL2303Device_IsSupportChip())) {
                    Toast.makeText(this, "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.");
                    return false ;
                }

            } else {

                Toast.makeText(this, "connected : OK" , Toast.LENGTH_SHORT).show();
                Log.d(TAG, "connected : OK");
                Log.d(TAG, "Exit  openUsbSerial");
                return true ;


            }
        }//isConnected
        else {
            Toast.makeText(this, "Connected failed, Please plug in PL2303 cable again!" , Toast.LENGTH_SHORT).show();
            Log.d(TAG, "connected failed, Please plug in PL2303 cable again!");
            return false;



        }
            return false;
    }//openUsbSerial

    /**
     * 网络变化之后的类型
     */

    public static void onNetChange(int netMobile) {
        // TODO Auto-generated method stub
        netISMobile = netMobile;

    }

    /**
     * 网络变化之后的类型
     */

    public void onNetUsbChange(boolean isUser) {
        Toast.makeText(this,isUser+"兰剑", Toast.LENGTH_SHORT).show();
        // TODO Auto-generated method stub
        ISUSB=isUser;

    }



//获取网关
    public void gateWayInfo(){
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: counter = "+ counter);
                try {
                    readDataFromSerial("01");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //设置延时5s再运行一次线程，构成了循环的效果
                handler.postDelayed(this,5000);
                handler.removeCallbacks(this);

            }
        };

        handler.post(runnable); //启动线程

    }

  //操控网关操作
   public void getNetControlCode(){
       // Toast.makeText(this,"开始执行",Toast.LENGTH_LONG).show();
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: counter = "+ counter);
                try {
                    readData2FromSerial("02");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                counter++;
                //设置延时5s再运行一次线程，构成了循环的效果
                handler.postDelayed(this,5000);
                if(counter > 5){
                    Log.i(TAG, "run: counter > 4 ? = "+ counter);
                    handler.removeCallbacks(this);
                }

            }
        };

        handler.post(runnable); //启动线程

    }



    private void readDataFromSerial(String code) throws Exception {

        int len;
        // byte[] rbuf = new byte[4096];
        byte[] rBuf = new byte[1];
        StringBuffer sbHex=new StringBuffer();

        Log.d(TAG, "Enter readDataFromSerial");

        if(null==mSerial)
            return;

        if(!mSerial.isConnected())
            return;

        StringBuilder sb = new StringBuilder();
        ByteUtils.geCreateComType(sb,"[");
        ByteUtils.geCreateComType(sb,code);
        ByteUtils.geCreateComType(sb,"]");
        byte[] utiLle= ByteUtils.hexTobytes(sb.toString());

        int res = mSerial.write(utiLle,utiLle.length);
        if( res<0 ) {
            Log.d(TAG, "setup2: fail to controlTransfer: "+ res);
            return;
        }


           String codes= HexConvert.readFromWay(mSerial);

            Way way = null;

            if(!codes.equals("")) {
                String listCode = ByteUtils.getUsbToStrings(codes);
                 way= ByteUtils.getUsbToLists(listCode,code);
            }
            if (way!=null) {
                NEWBJ_CODE = way.getWayCode();
                if (OlDBJ_CODE.equals("")) {
                    OlDBJ_CODE = NEWBJ_CODE;
                } else if (!OlDBJ_CODE.equals(NEWBJ_CODE)) {
                    OlDBJ_CODE = NEWBJ_CODE;
                }
                ISGETETCODE = true;
            } else {
                ISGETETCODE = false;

            }


        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Leave readDataFromSerial");
    }//readDataFromSerial


    private void readData2FromSerial(String code) throws Exception {


        StringBuffer sbHex=new StringBuffer();

        Log.d(TAG, "Enter readDataFromSerial");

        if(null==mSerial)
            return;

        if(!mSerial.isConnected())
            return;

        StringBuilder sb = new StringBuilder();
        ByteUtils.geCreateComType(sb,"[");
        ByteUtils.geCreateComType(sb,code);
        ByteUtils.geCreateComType(sb,"]");
        byte[] utiLle= ByteUtils.hexTobytes(sb.toString());
        int res = mSerial.write(utiLle,utiLle.length);
        if( res<0 ) {
            Log.d(TAG, "setup2: fail to controlTransfer: "+ res);
            return;
        }
            String codes= HexConvert.readFromWay(mSerial);


//            //rbuf[len] = 0;
//            for (int j = 0; j < len; j++) {
//                //String temp=Integer.toHexString(rbuf[j]&0x000000FF);
//                //Log.i(TAG, "str_rbuf["+j+"]="+temp);
//                //int decimal = Integer.parseInt(temp, 16);
//                //Log.i(TAG, "dec["+j+"]="+decimal);
//                //sbHex.append((char)decimal);
//                //sbHex.append(temp);
//                sbHex.append((char) (rBuf[j]&0x000000FF));
//            }
            Way way = null;


            if(!codes.equals("")) {
                String listCode = ByteUtils.getUsbToStrings(codes);
                way= ByteUtils.getUsbToLists(listCode,code);
            }
            if (way!=null) {
                NENET_CODE = way.getWayState();
                if (OLDNET_CODE.equals("")) {
                    OLDNET_CODE = NENET_CODE;
                } else if (!OLDNET_CODE.equals(NENET_CODE)) {
                    OLDNET_CODE = NENET_CODE;
                }

            }
            // Toast.makeText(this, "len="+len, Toast.LENGTH_SHORT).show();


        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Leave readDataFromSerial");
    }//readDataFromSerial





    public String getStateFromCode(String code) throws Exception {


       String state="";
        StringBuffer sbHex=new StringBuffer();

        Log.d(TAG, "Enter readDataFromSerial");

        if(null==mSerial)
            return "";

        if(!mSerial.isConnected())
            return "";

        StringBuilder sb = new StringBuilder();
        ByteUtils.geCreateComType(sb,"[");
        ByteUtils.geCreateComType(sb,code);
        ByteUtils.geCreateComType(sb,"]");
        byte[] utiLle= ByteUtils.hexTobytes(sb.toString());
        int res = mSerial.write(utiLle,utiLle.length);
        if( res<0 ) {
            Log.d(TAG, "setup2: fail to controlTransfer: "+ res);
            return "";
        }


            String codes= HexConvert.readFromWay(mSerial);


//            //rbuf[len] = 0;
//            for (int j = 0; j < len; j++) {
//                //String temp=Integer.toHexString(rbuf[j]&0x000000FF);
//                //Log.i(TAG, "str_rbuf["+j+"]="+temp);
//                //int decimal = Integer.parseInt(temp, 16);
//                //Log.i(TAG, "dec["+j+"]="+decimal);
//                //sbHex.append((char)decimal);
//                //sbHex.append(temp);
//                sbHex.append((char) (rBuf[j]&0x000000FF));
//            }
            Way way = null;


            if(!codes.equals("")) {
                String listCode = ByteUtils.getUsbToStrings(codes);
                way= ByteUtils.getUsbToLists(listCode,code);
            }
            if (way!=null) {
                state = way.getWayState();


            }


        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return state;

    }//readDataFromSerial









    //获取列表数据
    public Way getListData(StringBuilder sb) throws Exception {

        int len;
        String reList="";
        Way way = null;

        byte[] utiLle= ByteUtils.hexTobytes(sb.toString());
        int res = mSerial.write(utiLle,utiLle.length);
        if( res<0 ) {
            Log.d(TAG, "setup2: fail to controlTransfer: "+ res);

        }
            String codes= HexConvert.readFromWay(mSerial);


//            //rbuf[len] = 0;
//            for (int j = 0; j < len; j++) {
//                //String temp=Integer.toHexString(rbuf[j]&0x000000FF);
//                //Log.i(TAG, "str_rbuf["+j+"]="+temp);
//                //int decimal = Integer.parseInt(temp, 16);
//                //Log.i(TAG, "dec["+j+"]="+decimal);
//                //sbHex.append((char)decimal);
//                //sbHex.append(temp);
//                sbHex.append((char) (rBuf[j]&0x000000FF));
//            }


            if(!codes.equals("")) {
                String listCode = ByteUtils.getUsbToStrings(codes);
                way= ByteUtils.getUsbToLists(listCode,listCode);
            }


        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

     return way;
    }







    private void writeDataToSerial(String code) {

        if (ISUSB) {
            if (null == mSerial) {
                makeText(this, "驱动实例化未成功", LENGTH_SHORT).show();
                return;
            }

            if (!mSerial.isConnected()) {
                makeText(this, "驱动实例连接失败", LENGTH_SHORT).show();
                return;
            }

            String strWrite =code;
		/*
        //strWrite="012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
       // strWrite = changeLinefeedcode(strWrite);
         strWrite="012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
         if (SHOW_DEBUG) {
            Log.d(TAG, "PL2303Driver Write(" + strWrite.length() + ") : " + strWrite);
        }
        int res = mSerial.write(strWrite.getBytes(), strWrite.length());
		if( res<0 ) {
			Log.d(TAG, "setup: fail to controlTransfer: "+ res);
			return;
		}

		Toast.makeText(this, "Write length: "+strWrite.length()+" bytes", Toast.LENGTH_SHORT).show();
		 */
            // test data: 600 byte
            //strWrite="AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            if (SHOW_DEBUG) {
                Log.d(TAG, "PL2303Driver Write 2(" + strWrite.length() + ") : " + strWrite);
            }
            int res = mSerial.write(strWrite.getBytes(), strWrite.length());
            if( res<0 ) {
                Log.d(TAG, "setup2: fail to controlTransfer: "+ res);
                return;
            }

            Toast.makeText(this, "Write length: "+strWrite.length()+" bytes", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "Leave writeDataToSerial");
        } else {

            makeText(this, "驱动未启动", LENGTH_SHORT).show();
        }
    }//writeDataToSerial






    private void ShowPL2303HXD_SerialNmber() {



        Log.d(TAG, "Enter ShowPL2303HXD_SerialNmber");

        if(null==mSerial)
            return;

        if(!mSerial.isConnected())
            return;

        if(mSerial.PL2303Device_GetSerialNumber()!=NULL) {
           // tvShowSN.setText(mSerial.PL2303Device_GetSerialNumber());

        }
        else{
           // tvShowSN.setText("No SN");

        }

        Log.d(TAG, "Leave ShowPL2303HXD_SerialNmber");
    }//ShowPL2303HXD_SerialNmber

    //------------------------------------------------------------------------------------------------//
    //--------------------------------------LoopBack function-----------------------------------------//
    //------------------------------------------------------------------------------------------------//
    private static final int START_NOTIFIER = 0x100;
    private static final int STOP_NOTIFIER = 0x101;
    private static final int PROG_NOTIFIER_SMALL = 0x102;
    private static final int PROG_NOTIFIER_LARGE = 0x103;
    private static final int ERROR_BAUDRATE_SETUP = 0x8000;
    private static final int ERROR_WRITE_DATA = 0x8001;
    private static final int ERROR_WRITE_LEN = 0x8002;
    private static final int ERROR_READ_DATA = 0x8003;
    private static final int ERROR_READ_LEN = 0x8004;
    private static final int ERROR_COMPARE_DATA = 0x8005;

    Handler myMessageHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what){
                case START_NOTIFIER:
//                    pbLoopBack.setProgress(0);
//                    tvLoopBack.setText("LoopBack Test start...");
//                    btWrite.setEnabled(false);
//                    btRead.setEnabled(false);
                    break;
                case STOP_NOTIFIER:
//                    pbLoopBack.setProgress(pbLoopBack.getMax());
//                    tvLoopBack.setText("LoopBack Test successfully!");
//                    btWrite.setEnabled(true);
//                    btRead.setEnabled(true);
                    break;
                case PROG_NOTIFIER_SMALL:
//                    pbLoopBack.incrementProgressBy(5);
                    break;
                case PROG_NOTIFIER_LARGE:
                   // pbLoopBack.incrementProgressBy(10);
                    break;
                case ERROR_BAUDRATE_SETUP:
                   // tvLoopBack.setText("Fail to setup:baudrate "+msg.arg1);
                    break;
                case ERROR_WRITE_DATA:
                    //tvLoopBack.setText("Fail to write:"+ msg.arg1);
                    break;
                case ERROR_WRITE_LEN:
                   // tvLoopBack.setText("Fail to write len:"+msg.arg2+";"+ msg.arg1);
                    break;
                case ERROR_READ_DATA:
                   // tvLoopBack.setText("Fail to read:"+ msg.arg1);
                    break;
                case ERROR_READ_LEN:
                   // tvLoopBack.setText("Length("+msg.arg2+") is wrong! "+ msg.arg1);
                    break;
                case ERROR_COMPARE_DATA:
                   // tvLoopBack.setText("wrong:"+
                          //  String.format("rbuf=%02X,byteArray1=%02X", msg.arg1, msg.arg2));
                    break;

            }
            super.handleMessage(msg);
        }//handleMessage
    };

    private void Send_Notifier_Message(int mmsg) {
        Message m= new Message();
        m.what = mmsg;
        myMessageHandler.sendMessage(m);
        Log.d(TAG, String.format("Msg index: %04x", mmsg));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void Send_ERROR_Message(int mmsg, int value1, int value2) {
        Message m= new Message();
        m.what = mmsg;
        m.arg1 = value1;
        m.arg2 = value2;
        myMessageHandler.sendMessage(m);
        Log.d(TAG, String.format("Msg index: %04x", mmsg));
    }

    private Runnable tLoop = new Runnable() {
        public void run() {

            int res = 0, len, i;
            Time t = new Time();
            byte[] rbuf = new byte[4096];
            final int mBRateValue[] = {9600, 19200, 115200};
            PL2303Driver.BaudRate mBRate[] = {PL2303Driver.BaudRate.B9600, PL2303Driver.BaudRate.B19200, PL2303Driver.BaudRate.B115200};

            if(null==mSerial)
                return;

            if(!mSerial.isConnected())
                return;

            t.setToNow();
            Random mRandom = new Random(t.toMillis(false));

            byte[] byteArray1 = new byte[256]; //test pattern-1
            mRandom.nextBytes(byteArray1);//fill buf with random bytes
            Send_Notifier_Message(START_NOTIFIER);

            for(int WhichBR=0;WhichBR<mBRate.length;WhichBR++) {

                try {
                    res = mSerial.setup(mBRate[WhichBR], mDataBits, mStopBits, mParity, mFlowControl);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if( res<0 ) {
                    Send_Notifier_Message(START_NOTIFIER);
                    Send_ERROR_Message(ERROR_BAUDRATE_SETUP, mBRateValue[WhichBR], 0);
                    Log.d(TAG, "Fail to setup="+res);
                    return;
                }
                Send_Notifier_Message(PROG_NOTIFIER_LARGE);

                for(int times=0;times<2;times++) {

                    len = mSerial.write(byteArray1, byteArray1.length);
                    if( len<0 ) {
                        Send_ERROR_Message(ERROR_WRITE_DATA, mBRateValue[WhichBR], 0);
                        Log.d(TAG, "Fail to write="+len);
                        return;
                    }

                    if( len!=byteArray1.length ) {
                        Send_ERROR_Message(ERROR_WRITE_LEN, mBRateValue[WhichBR], len);
                        return;
                    }
                    Send_Notifier_Message(PROG_NOTIFIER_SMALL);

                    len = mSerial.read(rbuf);
                    if(len<0) {
                        Send_ERROR_Message(ERROR_READ_DATA, mBRateValue[WhichBR], 0);
                        return;
                    }
                    Log.d(TAG, "read length="+len+";byteArray1 length="+byteArray1.length);

                    if(len!=byteArray1.length) {
                        Send_ERROR_Message(ERROR_READ_LEN, mBRateValue[WhichBR], len);
                        return;
                    }
                    Send_Notifier_Message(PROG_NOTIFIER_SMALL);

                    for(i=0;i<len;i++) {
                        if(rbuf[i]!=byteArray1[i]) {
                            Send_ERROR_Message(ERROR_COMPARE_DATA, rbuf[i], byteArray1[i]);
                            Log.d(TAG, "Data is wrong at "+
                                    String.format("rbuf[%d]=%02X,byteArray1[%d]=%02X", i, rbuf[i], i, byteArray1[i]));
                            return;
                        }//if
                    }//for
                    Send_Notifier_Message(PROG_NOTIFIER_LARGE);

                }//for(times)

            }//for(WhichBR)

            try {
                res = mSerial.setup(mBaudrate, mDataBits, mStopBits, mParity, mFlowControl);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if( res<0 ) {
                Send_ERROR_Message(ERROR_BAUDRATE_SETUP, 0, 0);
                return;
            }
            Send_Notifier_Message(STOP_NOTIFIER);

        }//run()
    };//Runnable tLoop

    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if(null==mSerial)
                return;

            if(!mSerial.isConnected())
                return;

            int baudRate=0;
            String newBaudRate;
            Toast.makeText(parent.getContext(), "newBaudRate is-" + parent.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();
            newBaudRate= parent.getItemAtPosition(position).toString();

            try	{
                baudRate= Integer.parseInt(newBaudRate);
            }
            catch (NumberFormatException e)	{
                System.out.println(" parse int error!!  " + e);
            }

            switch (baudRate) {
                case 9600:
                    mBaudrate =PL2303Driver.BaudRate.B9600;
                    break;
                case 19200:
                    mBaudrate =PL2303Driver.BaudRate.B19200;
                    break;
                case 115200:
                    mBaudrate =PL2303Driver.BaudRate.B115200;
                    break;
                default:
                    mBaudrate =PL2303Driver.BaudRate.B9600;
                    break;
            }

            int res = 0;
            try {
                res = mSerial.setup(mBaudrate, mDataBits, mStopBits, mParity, mFlowControl);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if( res<0 ) {
                Log.d(TAG, "fail to setup");
                return;
            }
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing.
        }
    }//MyOnItemSelectedListener



    protected void onStop() {
        Log.d(TAG, "Enter onStop");
        super.onStop();
        Log.d(TAG, "Leave onStop");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Enter onDestroy");

        if(mSerial!=null) {
            mSerial.end();
            mSerial = null;
        }


        super.onDestroy();

        System.out.println("Stop polling service...");
        PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);
    }

    public void onStart() {
        Log.d(TAG, "Enter onStart");
        super.onStart();
        Log.d(TAG, "Leave onStart");
    }

    public void onResume() {
        Log.d(TAG, "Enter onResume");
        super.onResume();
        String action = getIntent().getAction();
        Log.d(TAG, "onResume:" + action);

        //if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))
        if(mSerial!=null) {
            if (!mSerial.isConnected()) {
                if (SHOW_DEBUG) {
                    Log.d(TAG, "New instance : " + mSerial);
                }

                if (!mSerial.enumerate()) {

                    Toast.makeText(this, "no more devices found", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Log.d(TAG, "onResume:enumerate succeeded!");
                }
            }//if isConnected
        }
        Toast.makeText(this, "attached", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Leave onResume");
    }



}
package com.example.myapplication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.adapter.BaseFragmentPagerAdapter;
import com.example.myapplication.entity.Card;
import com.example.myapplication.entity.Way;
import com.example.myapplication.fragment.CommunityFragment;
import com.example.myapplication.fragment.ConnectFragment;
import com.example.myapplication.fragment.ScanCodeFragment;
import com.example.myapplication.fragment.ShopCartFragment;
import com.example.myapplication.fragment.WayFragment;
import com.example.myapplication.handler.ChangeOrientationHandler;
import com.example.myapplication.listeners.OrientationSensorListener;
import com.example.myapplication.service.PollingService;
import com.example.myapplication.utils.ActivityUtils;
import com.example.myapplication.utils.ByteUtils;
import com.example.myapplication.utils.Hex;
import com.example.myapplication.utils.HexConvert;
import com.example.myapplication.utils.PollingUtils;
import com.example.myapplication.utils.UsbConnectUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tw.com.prolific.driver.pl2303.PL2303Driver;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.navigation)
    BottomNavigationView navigation;//???????????????

    @BindView(R.id.viewpager)       //????????????
    ViewPager viewpager;
    private MenuItem menuItem;

    private boolean mIsExit;
    public static String OlDBJ_CODE = "";
    private static String NEWBJ_CODE = "";

    public static String OLDNET_CODE = "";
    public static String NENET_CODE = "";
    List<Fragment> mFragmentList;   //Fragment????????????????????????????????????UI??????
    public BaseFragmentPagerAdapter mAdapter;

    private static final String ACTION_USB_PERMISSION = "com.example.myapplication.USB_PERMISSION";

    public FragmentManager fm;
    FragmentTransaction ft;
    Fragment mCurrentFragment;

    private OrientationSensorListener listener;
    private SensorManager sm;
    private Sensor sensor;
    private Handler handler;

    private static final boolean SHOW_DEBUG = true;
    //BaudRate.B4800, DataBits.D8, StopBits.S1, Parity.NONE, FlowControl.RTSCTS
    private PL2303Driver.BaudRate mBaudrate = PL2303Driver.BaudRate.B9600;
    private PL2303Driver.DataBits mDataBits = PL2303Driver.DataBits.D8;
    private PL2303Driver.Parity mParity = PL2303Driver.Parity.NONE;
    private PL2303Driver.StopBits mStopBits = PL2303Driver.StopBits.S1;
    private PL2303Driver.FlowControl mFlowControl = PL2303Driver.FlowControl.OFF;

    private OnButtonClickedListener buttonClickedListener;
    private int counter;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        handler = new ChangeOrientationHandler(this);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new OrientationSensorListener(handler);
        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);

        ActivityUtils.StatusBarLightMode(this);
        ActivityUtils.setStatusBarColor(this, R.color.white);//?????????????????????

        fm = getSupportFragmentManager();

        MainActivity.NetGBroadcastReceiver netBroadcastReceiver = new MainActivity.NetGBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.hardware.usb.action.USB_STATE");
        filter.addAction("com.android.ACTION_CLOSE_OTG");
        filter.setPriority(100);
        MainActivity.this.registerReceiver(netBroadcastReceiver, filter);
        System.out.println("Start polling service...");
        PollingUtils.startPollingService(this, 1, PollingService.class, PollingService.ACTION);


        // get service
        // get service
        mSerial = new PL2303Driver((UsbManager) getSystemService(Context.USB_SERVICE),
                this, ACTION_USB_PERMISSION);

        // check USB host function.
        if (!mSerial.PL2303USBFeatureSupported()) {

            Toast.makeText(this, "No Support USB host API", Toast.LENGTH_SHORT)
                    .show();

            Log.d(TAG, "No Support USB host API");

            mSerial = null;

        }

        if (!mSerial.enumerate()) {
            Toast.makeText(this, "no more devices found", Toast.LENGTH_SHORT).show();
        }


        initView();

        try {
            Thread.sleep(1500);
            openUsbSerial();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private String curFragmentTag = "ConnectFragment";


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (int i = 0; i < mFragmentList.size(); i++) {
            Fragment fragment1 = mFragmentList.get(i);
            fragment1.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*????????????????????????????????????????????????????????????????????????api???????????????????????????????????????????????????????????????????????????api??? ????????????????????????@RequiresApi???*/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initView() {

        //?????? >3 ????????????????????????ViewPager???????????????????????????????????????????????????
        //BottomNavigationViewHelper.disableShiftMode(navigation);

        navigation.setItemIconTintList(null);   //???????????????????????????????????????

        mFragmentList = new ArrayList<>();
        mFragmentList.add(new ConnectFragment());   //??????????????????
        mFragmentList.add(new ScanCodeFragment());
        mFragmentList.add(new CommunityFragment());
        mFragmentList.add(new ShopCartFragment());  //??????????????????
        mFragmentList.add(new WayFragment());       //????????????
        mAdapter = new BaseFragmentPagerAdapter(fm, mFragmentList);


        viewpager.setAdapter(mAdapter);
        viewpager.setCurrentItem(0);    //????????????????????????


        //??????3????????????????????????????????????????????????????????????????????????
        // ???????????????????????????????????????????????????????????????????????????????????????eventbus?????????????????????????????????
        //viewpager.setOffscreenPageLimit(3);

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.connect_selected:
                        viewpager.setCurrentItem(0);//??????
                        curFragmentTag = "ConnectFragment";
                        return true;
                    case R.id.scanupload_selected:
                        viewpager.setCurrentItem(1);//??????
                        curFragmentTag = "ScanCodeFragment";
                        return true;
                    case R.id.read_selected:
                        viewpager.setCurrentItem(2);//??????
                        curFragmentTag = "CommunityFragment";
                        return true;
                    case R.id.change_selected:
                        viewpager.setCurrentItem(3);//????????????
                        curFragmentTag = "ShopCartFragment";
                        return true;
                    case R.id.way_selected:
                        viewpager.setCurrentItem(4);//????????????
                        curFragmentTag = "WayFragment";
                        return true;
                }
                return false;
            }
        });
//        Button mButton01 = (Button)findViewById(R.id.open1);
//        mButton01.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                openUsbSerial();
//            }
//        });

//        Button mButton02 = (Button)findViewById(R.id.open2);
//        mButton02.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                writeDataToSerial();
//            }
//        });
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                menuItem = navigation.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

     /*   //??????ViewPager??????
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });*/

    }



    class NetGBroadcastReceiver extends BroadcastReceiver {

        private final static String ACTION = "android.hardware.usb.action.USB_STATE";
        private static final String ACTION_CLOSE_OTG = "com.android.ACTION_CLOSE_OTG";


        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // ??????????????????????????????????????????????????????
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                int netWorkState = UsbConnectUtil.getNetWorkState(context);
                // ????????????????????????????????????
                onNetChange(netWorkState);
            }

            if (intent.getAction().equals(ACTION_CLOSE_OTG)) {

                openUsbSerial();

                viewpager.setCurrentItem(viewpager.getCurrentItem());//??????


            }


        }   // ???????????????

    }


    private void writeDataToSerial() {

        Log.d(TAG, "Enter writeDataToSerial");

        if (null == mSerial)
            return;

        if (!mSerial.isConnected())
            return;

        String strWrite = "??????|";
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
        if (res < 0) {
            Log.d(TAG, "setup2: fail to controlTransfer: " + res);
            return;
        }

        Toast.makeText(this, "Write length: " + strWrite.length() + " bytes", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Leave writeDataToSerial");
    }//writeDataToSerial


    @Override
    protected void onPause() {
        sm.unregisterListener(listener);
        super.onPause();
    }

    @Override
    public void onResume() {
        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    private void openUsbSerial() {
        Log.d(TAG, "Enter  openUsbSerial");

        if (mSerial == null) {

            Log.d(TAG, "No mSerial");
            return;

        }


        if (mSerial.isConnected()) {
            if (SHOW_DEBUG) {
                Log.d(TAG, "openUsbSerial : isConnected ");
                ISUSB = false;
            }
            String str = "115200";
            int baudRate = Integer.parseInt(str);
            switch (baudRate) {
                case 9600:
                    mBaudrate = PL2303Driver.BaudRate.B9600;
                    break;
                case 19200:
                    mBaudrate = PL2303Driver.BaudRate.B19200;
                    break;
                case 115200:
                    mBaudrate = PL2303Driver.BaudRate.B115200;
                    break;
                default:
                    mBaudrate = PL2303Driver.BaudRate.B9600;
                    break;
            }
            Log.d(TAG, "baudRate:" + baudRate);
            // if (!mSerial.InitByBaudRate(mBaudrate)) {
            if (!mSerial.InitByBaudRate(mBaudrate, 700)) {
                if (!mSerial.PL2303Device_IsHasPermission()) {
                    Toast.makeText(this, "cannot open, maybe no permission", Toast.LENGTH_SHORT).show();
                    ISUSB = false;
                }

                if (mSerial.PL2303Device_IsHasPermission() && (!mSerial.PL2303Device_IsSupportChip())) {
                    Toast.makeText(this, "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.");
                    ISUSB = false;

                }
            } else {

                Toast.makeText(this, "connected : OK", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "connected : OK");
                Log.d(TAG, "Exit  openUsbSerial");
                ISUSB = true;

            }
        }//isConnected
        else {
            Toast.makeText(this, "Connected failed, Please plug in PL2303 cable again!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "connected failed, Please plug in PL2303 cable again!");
            ISUSB = false;

        }
        if (buttonClickedListener != null) {
            buttonClickedListener.onclicked(ISUSB);
        }

        //Toast.makeText(this, ""+ISUSB, Toast.LENGTH_SHORT).show();
    }//openUsbSerial


    /**
     * ??????????????????
     *
     * @author zqy
     */
    public interface OnButtonClickedListener {
        /**
         * ???????????????
         *
         * @param s
         */
        public void onclicked(boolean s);
    }

    /**
     * @param buttonClickedListener ??????????????????????????????
     */
    public void setButtonClickedListener(OnButtonClickedListener buttonClickedListener) {
        this.buttonClickedListener = buttonClickedListener;
    }


    //????????????
    public void gateWayInfo() {
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: counter = " + counter);
                try {
                    readDataFromSerial("01");
                    //readData2FromSerial("02");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //????????????5s????????????????????????????????????????????????
                handler.postDelayed(this, 300);
                handler.removeCallbacks(this);

            }
        };

        handler.post(runnable); //????????????

    }

    //??????????????????
    public void getNetControlCode() {
        // Toast.makeText(this,"????????????",Toast.LENGTH_LONG).show();
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: counter = " + counter);
                try {
                    readData2FromSerial("02");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // counter++;
                //????????????5s????????????????????????????????????????????????
                handler.postDelayed(this, 500);
                handler.removeCallbacks(this);


            }
        };

        handler.post(runnable); //????????????

    }


    //??????????????????
    public void readDataFromSerial(String code) throws Exception {

        int len;
        // byte[] rbuf = new byte[4096];
        byte[] rBuf = new byte[1];
        StringBuffer sbHex = new StringBuffer();

        Log.d(TAG, "Enter readDataFromSerial");

        if (null == mSerial)
            return;

        if (!mSerial.isConnected())
            return;

        StringBuilder sb = new StringBuilder();         //?????????????????????
        // geCreateComType   ---???  ??????16???????????????????????????
        ByteUtils.geCreateComType(sb, "[");
        ByteUtils.geCreateComType(sb, code);
        ByteUtils.geCreateComType(sb, "]");

        byte[] utiLle = ByteUtils.hexTobytes(sb.toString());

        int res = mSerial.write(utiLle, utiLle.length);
        if (res < 0) {
            Log.d(TAG, "setup2: fail to controlTransfer: " + res);
            return;
        }

        String codes = HexConvert.readFromWay(mSerial);

//        Toast.makeText(this, codes, Toast.LENGTH_SHORT).show();
        Way way = null;

        if (!codes.equals("")) {
            String listCode = ByteUtils.getUsbToStrings(codes);
            way = ByteUtils.getUsbToLists(listCode, code);
        }
        if (way != null) {
            OlDBJ_CODE = "";
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
        // Toast.makeText(this, OlDBJ_CODE, Toast.LENGTH_SHORT).show();
//
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        Log.d(TAG, "Leave readDataFromSerial");
    }//readDataFromSerial


    public void readData2FromSerial(String code) throws Exception {


        StringBuffer sbHex = new StringBuffer();

        Log.d(TAG, "Enter readDataFromSerial");

        if (null == mSerial)
            return;

        if (!mSerial.isConnected())
            return;

        StringBuilder sb = new StringBuilder();
        ByteUtils.geCreateComType(sb, "[");
        ByteUtils.geCreateComType(sb, code);
        ByteUtils.geCreateComType(sb, "]");
        byte[] utiLle = ByteUtils.hexTobytes(sb.toString());
        int res = mSerial.write(utiLle, utiLle.length);
        if (res < 0) {
            Log.d(TAG, "setup2: fail to controlTransfer: " + res);
            return;
        }
        String codes = HexConvert.readFromWay(mSerial);


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


        if (!codes.equals("")) {
            String listCode = ByteUtils.getUsbToStrings(codes);
            way = ByteUtils.getUsbToLists(listCode, code);
        }
        if (way != null) {
            OLDNET_CODE = "";
            NENET_CODE = way.getWayState();


            //   if (OLDNET_CODE.equals("")) {
            OLDNET_CODE = NENET_CODE;
            //  }
            //  Toast.makeText(this, OLDNET_CODE, Toast.LENGTH_SHORT).show();

        }
        // Toast.makeText(this, "len="+len, Toast.LENGTH_SHORT).show();


//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        Log.d(TAG, "Leave readDataFromSerial");
    }//readDataFromSerial


    public String getStateFFromCode(StringBuilder sb) {


        String state = "";

//        StringBuilder sb = new StringBuilder();
//        ByteUtils.geCreateComType(sb,"[");
//        ByteUtils.geCreateComType(sb,code);
//        ByteUtils.geCreateComType(sb,"]");


        byte[] utiLle = ByteUtils.hexToByte(sb.toString());

        System.out.println("06????????????" + utiLle);
        int res = mSerial.write(utiLle, utiLle.length);
        if (res < 0) {
            Log.d(TAG, "setup2: fail to controlTransfer: " + res);
            return "";
        }


        String codes = HexConvert.readFromWay(mSerial);
        //Toast.makeText(this, "len="+codes, Toast.LENGTH_SHORT).show();
        //System.out.println("?????????"+codes);
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

        System.out.println("05=======????????????" + sb.toString());
        if (!codes.equals("")) {
            String listCode = ByteUtils.getUsbToStrings(codes);
            way = ByteUtils.getUsbToLists(listCode, listCode);
        }
        if (way != null) {
            state = way.getWayState();

        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return state;

    }//readDataFromSerial


    public String getStateFromCode(StringBuilder sb) {


        String state = "";

//        StringBuilder sb = new StringBuilder();
//        ByteUtils.geCreateComType(sb,"[");
//        ByteUtils.geCreateComType(sb,code);
//        ByteUtils.geCreateComType(sb,"]");


        byte[] utiLle = ByteUtils.hexTTobytes(sb.toString());

        System.out.println("06????????????" + utiLle);
        int res = mSerial.write(utiLle, utiLle.length);
        if (res < 0) {
            Log.d(TAG, "setup2: fail to controlTransfer: " + res);
            return "";
        }


        String codes = HexConvert.readFromWay(mSerial);
        //Toast.makeText(this, "len="+codes, Toast.LENGTH_SHORT).show();
        //System.out.println("?????????"+codes);
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

        System.out.println("05=======????????????" + sb.toString());
        if (!codes.equals("")) {
            String listCode = ByteUtils.getUsbToStrings(codes);
            way = ByteUtils.getUsbToLists(listCode, listCode);
        }
        if (way != null) {
            state = way.getWayState();

        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return state;

    }//readDataFromSerial


    //??????????????????
    public Way getListData(StringBuilder sb) throws Exception {

        int len;
        String reList = "";

        List<Card> list = new ArrayList<Card>();
        Way way = new Way();

        byte[] utiLle = ByteUtils.hexTobytes(sb.toString());
        int res = mSerial.write(utiLle, utiLle.length);
        if (res < 0) {
            Log.d(TAG, "setup2: fail to controlTransfer: " + res);

        }
        String codes = HexConvert.readFromWay(mSerial);

        // Toast.makeText(this, "len="+codes, Toast.LENGTH_SHORT).show();

        //  String codes="5B012C01014750F9022C0200000000022C0300000000022C0400000000022C0500000000022C0600000000022C0700000000022C0800000000022C0900000000022C0A00000000022C0B00000000022C0C00000000022C0D00000000022C0E00000000022C0F00000000022C1000000000022C1100000000022C1200000000022C1300000000022C1400000000022C1500000000022C1600000000022C1700000000022C1800000000022C1900000000025D";


        if (!codes.equals("")) {
            // String codes="5B 02 2C 01 00 00 00 00 02 2C 00 2C 02 00 00 02 2C 00 00 00 2C 03 02 2C 03 00 00 00 00 02 2C 00 2C 04 00 00 02 2C 00 00 00 2C 05 02 2C 05 00 00 00 00 02 2C 00 2C 06 00 00 02 2C 00 00 00 2C 07 02 2C 07 00 00 00 00 02 2C 00 2C 08 00 00 02 2C 00 00 00 2C 09 02 2C 09 00 00 00 00 02 2C 00 2C 0A 00 00 02 2C 00 00 00 2C 0B 02 2C 0B 00 00 00 00 02 2C 00 2C 0C 00 00 02 2C 00 00 00 2C 0D 02 2C 0D 00 00 00 00 02 2C 00 2C 0E 00 00 02 2C 00 00 00 2C 0F 02 2C 0F 00 00 00 00 02 2C 00 2C 10 00 00 02 2C 00 00 00 2C 11 02 2C 11 00 00 00 00 02 5D";
            byte[] data = Hex.hexStringToBytes(codes);

            int shif = 0;

            for (int i = 0; i < 25; i++) {

                byte bz = data[shif + 3 + 7 * i];

                byte biz = data[shif + 4 + 7 * i];

                byte biz1 = data[shif + 5 + 7 * i];
                byte biz2 = data[shif + 6 + 7 * i];
                byte biz3 = data[shif + 7 + 7 * i];
                byte[] a = new byte[4];
                a[0] = biz;
                a[1] = biz1;
                a[2] = biz2;
                a[3] = biz3;
                String sl = Hex.bytesToHexStr(a, true);
                byte zt = data[shif + 8 + 7 * i];
                System.out.println("??????=======????????????" + sl);
                Card card = new Card();
                card.setTag(i + "");
                card.setMarkId("" + Hex.hexToDecimal(String.format("%02X", bz)));
                card.setMark(Hex.hexToDec(sl));
                card.setMarkState(String.format("%02X", zt));
                list.add(card);
            }


//          String listCode = ByteUtils.getUsbToStrings(codes);
//
            //         way= ByteUtils.getUsbToLists(listCode,listCode);
//
//

            way.setCars(list);

        }


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return way;
    }


    /**
     * ??????????????????
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                this.finish();
            } else {
                Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }
}
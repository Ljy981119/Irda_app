package com.example.myapplication.utils;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myapplication.broadcast.NetBroadcastReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import tw.com.prolific.driver.pl2303.PL2303Driver;


/**
 * Created by xxj on 01/15.
 */

public class USBUtil {
	private static USBUtil instance;

	private PendingIntent mPermissionIntent;
	private UsbManager usbManager;
	private Context context;
	private NetBroadcastReceiver usbReceiver;
	private UsbInterface usbInterface;
	private UsbEndpoint usbEndpointIn;
	private UsbEndpoint usbEndpointOut;
	private UsbDeviceConnection usbConnection;


	// debug settings
	// private static final boolean SHOW_DEBUG = false;

	private static final boolean SHOW_DEBUG = true;

	// Defines of Display Settings
	private static final int DISP_CHAR = 0;

	// Linefeed Code Settings
	//    private static final int LINEFEED_CODE_CR = 0;
	private static final int LINEFEED_CODE_CRLF = 1;
	private static final int LINEFEED_CODE_LF = 2;

	static PL2303Driver mSerial;

	//    private ScrollView mSvText;
	//   private StringBuilder mText = new StringBuilder();

	static String TAG = "PL2303HXD_APLog";

	private Button btWrite;
	private EditText etWrite;

	private Button btRead;
	private EditText etRead;

	private Button btLoopBack;
	private ProgressBar pbLoopBack;
	private TextView tvLoopBack;

	private Button btGetSN;
	private TextView tvShowSN;

	private int mDisplayType = DISP_CHAR;
	private int mReadLinefeedCode = LINEFEED_CODE_LF;
	private int mWriteLinefeedCode = LINEFEED_CODE_LF;

	//BaudRate.B4800, DataBits.D8, StopBits.S1, Parity.NONE, FlowControl.RTSCTS
	private static PL2303Driver.BaudRate mBaudrate = PL2303Driver.BaudRate.B9600;
	private PL2303Driver.DataBits mDataBits = PL2303Driver.DataBits.D8;
	private PL2303Driver.Parity mParity = PL2303Driver.Parity.NONE;
	private PL2303Driver.StopBits mStopBits = PL2303Driver.StopBits.S1;
	private PL2303Driver.FlowControl mFlowControl = PL2303Driver.FlowControl.OFF;


	private static final String ACTION_USB_PERMISSION = "com.example.myapplication.USB_PERMISSION";


	private static final String NULL = null;

	// Linefeed
	//    private final static String BR = System.getProperty("line.separator");

	//public static Spinner PL2303HXD_BaudRate_spinner;
	public int PL2303HXD_BaudRate;
	public String PL2303HXD_BaudRate_str = "B4800";

	private String strStr;


	//public static NetBroadcastReceiver.NetEvevt evevt;
	/**
	 * 网络类型
	 */
	private int netMobile;


	private USBUtil() {
	}





	public static boolean isUsbNet(Context context) {

		// get service
		// get service
		mSerial = new PL2303Driver((UsbManager) context.getSystemService(Context.USB_SERVICE),
				context, ACTION_USB_PERMISSION);

		// check USB host function.
		if (!mSerial.PL2303USBFeatureSupported()) {

			Toast.makeText(context, "No Support USB host API", Toast.LENGTH_SHORT)
					.show();

			Log.d(TAG, "No Support USB host API");

			mSerial = null;

		}

		if( !mSerial.enumerate() ) {
			Toast.makeText(context, "no more devices found", Toast.LENGTH_SHORT).show();
		}

//		try {
//			Thread.sleep(1500);
//			openUsbSerial();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}


		if(mSerial==null) {

			Log.d(TAG, "No mSerial");
			return false;

		}

		if (mSerial.isConnected()) {
			if (SHOW_DEBUG) {
				Log.d(TAG, "openUsbSerial : isConnected ");
				return false;
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
					Toast.makeText(context, "cannot open, maybe no permission", Toast.LENGTH_SHORT).show();
					return false;
				}

				if(mSerial.PL2303Device_IsHasPermission() && (!mSerial.PL2303Device_IsSupportChip())) {
					Toast.makeText(context, "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.", Toast.LENGTH_SHORT).show();
					Log.d(TAG, "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.");
					return false;
				}
			} else {

				Toast.makeText(context, "connected : OK" , Toast.LENGTH_SHORT).show();
				Log.d(TAG, "connected : OK");
				Log.d(TAG, "Exit  openUsbSerial");
				return true;

			}
		}//isConnected
		else {

			Toast.makeText(context, "Connected failed, Please plug in PL2303 cable again!" , Toast.LENGTH_SHORT).show();
			Log.d(TAG, "connected failed, Please plug in PL2303 cable again!");
			return false;

		}
		return false;
	}

	public static PL2303Driver getUsbSerial() {

		return mSerial;

	}//openUsbSerial





}
package com.example.myapplication.fragment;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.utils.ByteUtils;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tw.com.prolific.driver.pl2303.PL2303Driver;


/**
 * 购物车
 */
public class ShopCartFragment extends Fragment implements MainActivity.OnButtonClickedListener {
    static int LOCATION = 2;   // 扫码区分

    @BindView(R.id.gundongthtext)
    TextView gundongthtext;

    @BindView(R.id.oldimg)
    ImageView oldimg;

    @BindView(R.id.newimg)
    ImageView newimg;

    @BindView(R.id.oldguntcode)
    EditText oldguntcode;

    @BindView(R.id.newguntcode)
    EditText newguntcode;


    @BindView(R.id.sendupdate)
    TextView sendupdate;


    @BindView(R.id.cleardata)
    TextView cleardata;


    Unbinder unbinder;
    PL2303Driver mSerial;
    private boolean netConnect = false;

    private int REQUEST_CODE_SCAN = 111;
    private MainActivity mainActivity;

    /******************************************************/
    public static final int CAMERA_REQ_CODE = 111;
    public static final int DECODE = 1;
    private static final int REQUEST_CODE_SCAN_ONE = 0X01;

    /******************************************************/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shop_cart, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        initView();
        initData();

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        //mainActivity.setButtonClickedListener(ShopCartFragment.this);

//        MainActivity activity = (MainActivity) getActivity();
///**
// * 加监听
// */
//        activity.setButtonClickedListener(new MainActivity.OnButtonClickedListener() {
//
//            @Override
//            public void onclicked(boolean sj) {
//
//                if (sj) {
//                    if (mainActivity.mSerial.isConnected()) {
//                        Drawable leftDrawable = getResources().getDrawable(R.drawable.selecttag);
//                        leftDrawable.setBounds(0, 0, 40, 40);
//                        if (gundongthtext!=null) {
//                            gundongthtext.setCompoundDrawables(null, null, leftDrawable, null);
//                        }
//                    }
//
//                } else {
//
//                    Drawable leftDrawable = getResources().getDrawable(R.drawable.tag);
//                    leftDrawable.setBounds(0, 0, 40, 40);
//                    if (gundongthtext!=null) {
//                        gundongthtext.setCompoundDrawables(null, null, leftDrawable, null);
//                    }
//                }
//            }

        //  });
    }

    private void initView() {

        if (mainActivity.ISUSB && mainActivity.mSerial.isConnected()) {
            if (mainActivity.mSerial.isConnected()) {
                Drawable leftDrawable = getResources().getDrawable(R.drawable.selecttag);
                leftDrawable.setBounds(0, 0, 40, 40);
                gundongthtext.setCompoundDrawables(null, null, leftDrawable, null);
            }

        } else {

            Drawable leftDrawable = getResources().getDrawable(R.drawable.tag);
            leftDrawable.setBounds(0, 0, 40, 40);
            gundongthtext.setCompoundDrawables(null, null, leftDrawable, null);
        }

        sendupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mainActivity.OlDBJ_CODE.equals("")) {
                    StringBuilder sb = new StringBuilder();
                    ByteUtils.geCreateComType(sb, "[");
                    ByteUtils.geCreateComType(sb, "06");
                    ByteUtils.geCreateComType(sb, ",");
                    byte[] Utile = ByteUtils.StringToSixteen(mainActivity.OlDBJ_CODE);
                    sb.append(ByteUtils.byteArrayToHexString(Utile));
                    ByteUtils.geCreateComType(sb, ",");
                    if (!oldguntcode.getText().toString().equals("")) {
//                            char[] passwordInCharArray = oldguntcode.getText().toString().toCharArray();
//                     for (char temp : passwordInCharArray) {
//                                ByteUtils.geCreateComType(sb, Character.toString(temp));
//                            }

                        //oldguntcode.getText().toString().getBytes();

                        System.out.println("旧数据" + oldguntcode.getText().toString());

                        //sb.append(ByteUtils.byteToHex(oldguntcode.getText().toString().getBytes(StandardCharsets.UTF_8)));
                        sb.append(ByteUtils.numToHex(Integer.parseInt(oldguntcode.getText().toString())));

                        ByteUtils.geCreateComType(sb, ",");
                    }
                    if (!newguntcode.getText().toString().equals("")) {
//                            char[] passwordInCharArray = newguntcode.getText().toString().toCharArray();
//                            for (char temp : passwordInCharArray) {
//                                ByteUtils.geCreateComType(sb, Character.toString(temp));
//                            }

                        System.out.println("新数据" + newguntcode.getText().toString());

                        sb.append(ByteUtils.numToHex(Integer.parseInt(newguntcode.getText().toString())));

                    }

                    ByteUtils.geCreateComType(sb, "]");

                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println("06数据发送" + sb.toString());
                                String state = mainActivity.getStateFFromCode(sb);
                                String xl = "更新板卡";
                                if (state != "" && state.equals("01")) {
                                    xl += "保存成功!";
                                    Toast toast = Toast.makeText(getContext(),
                                            xl, Toast.LENGTH_LONG);
                                    ByteUtils.showMyToast(toast, 5 * 1000);
                                }
                                if (state != "" && state.equals("02")) {
                                    xl += "保存失败!";
                                    Toast toast = Toast.makeText(getContext(),
                                            xl, Toast.LENGTH_LONG);
                                    ByteUtils.showMyToast(toast, 5 * 1000);

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //设置延时5s再运行一次线程，构成了循环的效果
                            handler.postDelayed(this, 5000);

                            handler.removeCallbacks(this);


                        }
                    };

                    handler.post(runnable); //启动线程

                    // }
                }


            }
        });

        cleardata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldguntcode.setText("");
                newguntcode.setText("");

            }
        });


        oldimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                AndPermission.with(getActivity())
//                        .permission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
//                        .callback(new PermissionListener() {
//                            @Override
//                            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
//                                Intent intent = new Intent(getActivity(), CaptureActivity.class);
//
//                                ZxingConfig config = new ZxingConfig();
//                                config.setPlayBeep(true);
//                                config.setShake(true);
//                                config.setLOCATION(1);
//                                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
//                                startActivityForResult(intent, REQUEST_CODE_SCAN);
//
//                            }
//
//                            @Override
//                            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
//
//                            }
//                        }).start();
            /************************/
            /************************/
                LOCATION = 1;
                requestPermission(CAMERA_REQ_CODE, DECODE);

            }
        });

        newimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  AndPermission.with(getActivity())
                        .permission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .callback(new PermissionListener() {
                            @Override
                            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                Intent intent = new Intent(getActivity(), CaptureActivity.class);

                                ZxingConfig config = new ZxingConfig();
                                config.setPlayBeep(true);
                                config.setShake(true);
                                config.setLOCATION(2);
                                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                                startActivityForResult(intent, REQUEST_CODE_SCAN);

                            }

                            @Override
                            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

                            }
                        }).start();*/
/********************************************************************/
                Toast.makeText(getActivity(), "新扫码", Toast.LENGTH_SHORT).show();

//                ScanUtil.startScan(getActivity(), REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().create());
                LOCATION = 0;
                requestPermission(CAMERA_REQ_CODE, DECODE);
/********************************************************************/

            }
        });
    }

    /*********************************************************************/
    //编辑请求权限
    public void requestPermission(int requestCode, int mode) {
        //ActivityCompat

        Toast.makeText(getActivity(), "相机权限申请", Toast.LENGTH_SHORT).show();

        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "打开相机扫码", Toast.LENGTH_SHORT).show();
            ScanUtil.startScan(getActivity(), REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().create());
        } else {
            ActivityCompat.requestPermissions(
                    /***************this*/
                    /***************this*/
                    // getActivity(),
                    getActivity(),
//                ShopCartFragment.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    requestCode);
        }

    }

    //权限申请返回
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Toast.makeText(getActivity(), "相机权限返回", Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "相机权限返回2222", Toast.LENGTH_SHORT).show();
        if (permissions == null || grantResults == null) {
            return;
        }
//                                                           PackageManager
        if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (requestCode == CAMERA_REQ_CODE) {
            //启动扫描Acticity
//                  ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().create());
            /*************************this*/
            /*************************this*/
//            ScanUtil.startScan(getActivity(), REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().create());
            Toast.makeText(getActivity(), "打开相机扫码", Toast.LENGTH_SHORT).show();
            ScanUtil.startScan(getActivity(), REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().create());
        }
    }


    /*********************************************************************/

    private void initData() {


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    //如果你需要考虑更好的体验，可以这么操作
    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        // mBanner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        // mBanner.stopAutoPlay();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*******************************************i**************************/
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
//            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj != null) {
                /*************************this*/
                /*************************this*/
//                /**/newguntcode.setText(obj.originalValue);

//                Toast.makeText(getActivity(), obj.originalValue, Toast.LENGTH_SHORT).show();
//                Toast.makeText(this,obj.originalValue,Toast.LENGTH_SHORT).show();
            }

            /**复制下侧*/
            if (ByteUtils.judgeContainsStr(obj.originalValue)) {
                Toast toast = Toast.makeText(getContext(),
                        "获取的码值必须位1-65535数字！", Toast.LENGTH_LONG);
                ByteUtils.showMyToast(toast, 3 * 1000);
            } else {

//                int LOCATION = data.getIntExtra(Constant.LOCATION, 0);

                //int POSITION = new Long(data.getLongExtra(Constant.POSITION, 0)).intValue();

                Bundle bundle = new Bundle();
                bundle.putString("code", obj.originalValue);
                if (LOCATION == 1) {

                    oldguntcode.setText(obj.originalValue);

                } else if (LOCATION == 0) {
                    newguntcode.setText(obj.originalValue);
                }
                LOCATION = 2;

            }
        }


        /*********************************************************************/

        // 扫描二维码/条码回传
//        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
//            if (data != null) {
//
//                String content = data.getStringExtra(Constant.CODED_CONTENT);
//
////                if(!content.equals("")){
////                    int length=content.length();
////
////                    if(length<5){
////
////                        Toast toast=Toast.makeText(getContext(),
////                                "获取的码值必须大于5位数！", Toast.LENGTH_LONG);
////                        ByteUtils.showMyToast(toast, 3*1000);
////                    }else {
////
////                        content = content.substring(length - 5, length);
////                    }
//
//
////                }
//                if (ByteUtils.judgeContainsStr(content)) {
//                    Toast toast = Toast.makeText(getContext(),
//                            "获取的码值必须位1-65535数字！", Toast.LENGTH_LONG);
//                    ByteUtils.showMyToast(toast, 3 * 1000);
//                } else {
//
//                    int LOCATION = data.getIntExtra(Constant.LOCATION, 0);
//
//                    //int POSITION = new Long(data.getLongExtra(Constant.POSITION, 0)).intValue();
//
//                    Bundle bundle = new Bundle();
//                    bundle.putString("code", content);
//                    if (LOCATION == 1) {
//
//                        oldguntcode.setText(content);
//
//                    } else {
//                        newguntcode.setText(content);
//                    }
//                }
//                //result.setText("扫描结果为：" + content);
//            }
//        }


    }


    @Override
    public void onclicked(boolean sj) {
        if (sj) {
            if (mainActivity.mSerial.isConnected()) {
                Drawable leftDrawable = getResources().getDrawable(R.drawable.selecttag);
                leftDrawable.setBounds(0, 0, 40, 40);
                if (gundongthtext != null) {
                    gundongthtext.setCompoundDrawables(null, null, leftDrawable, null);
                }
            }

        } else {

            Drawable leftDrawable = getResources().getDrawable(R.drawable.tag);
            leftDrawable.setBounds(0, 0, 40, 40);
            if (gundongthtext != null) {
                gundongthtext.setCompoundDrawables(null, null, leftDrawable, null);
            }
        }
    }
}

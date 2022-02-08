package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.Manifest;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;


import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.List;

public class EncodeActivity extends AppCompatActivity {


    private int REQUEST_CODE_SCAN = 111;
    private int POSITION=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        initView();
    }


    private void initView() {

        Intent intent = getIntent();
        POSITION= intent.getIntExtra("position",0);

        AndPermission.with(this)
                .permission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        Intent intent = new Intent(EncodeActivity.this, CaptureActivity.class);

                        ZxingConfig config = new ZxingConfig();
                        config.setPlayBeep(true);
                        config.setShake(true);
                        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                        startActivityForResult(intent, REQUEST_CODE_SCAN);

                    }

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

                    }
                }).start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);


                Bundle bundle = new Bundle();
                bundle.putString("code", content);
                bundle.putInt("position", POSITION);
                POSITION=0;
                setResult(RESULT_CANCELED, this.getIntent().putExtras(bundle));

                this.finish();


                //result.setText("扫描结果为：" + content);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    //    /**
//     * 重写返回键
//     */
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            EncodeActivity.this.finish();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


}
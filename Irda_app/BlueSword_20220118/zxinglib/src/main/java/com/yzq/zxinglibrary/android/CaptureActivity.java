package com.yzq.zxinglibrary.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.zxing.Result;
import com.yzq.zxinglibrary.R;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.camera.CameraManager;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.decode.DecodeImgCallback;
import com.yzq.zxinglibrary.decode.DecodeImgThread;
import com.yzq.zxinglibrary.decode.ImageUtil;
import com.yzq.zxinglibrary.view.ViewfinderView;

import java.io.IOException;


public class CaptureActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    private ZxingConfig config;
    private SurfaceView preview_view;
    //private Toolbar toolbar;
    private ViewfinderView viewfinder_view;
    private AppCompatImageView flashLightIv;
    private TextView flashLightTv;
    private LinearLayout flashLightLayout;
    private LinearLayout albumLayout;
    private LinearLayoutCompat bottomLayout;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private SurfaceHolder surfaceHolder;


    public ViewfinderView getViewfinderView() {
        return viewfinder_view;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void drawViewfinder() {
        viewfinder_view.drawViewfinder();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ??????Activity??????????????????
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_capture);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        /*?????????????????????*/
        try {
            config = (ZxingConfig) getIntent().getExtras().get(Constant.INTENT_ZXING_CONFIG);
        } catch (Exception e) {
            config = new ZxingConfig();
        }

        System.out.println("??????===="+config.getLOCATION()+"??????====="+config.getPOSITION());


        initView();


        hasSurface = false;

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        beepManager.setPlayBeep(config.isPlayBeep());
        beepManager.setVibrate(config.isShake());


    }


    private void initView() {
        preview_view = (SurfaceView) findViewById(R.id.preview_view);
        preview_view.setOnClickListener(this);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);

        viewfinder_view = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinder_view.setOnClickListener(this);
        flashLightIv = (AppCompatImageView) findViewById(R.id.flashLightIv);
        flashLightTv = (TextView) findViewById(R.id.flashLightTv);
        flashLightLayout = (LinearLayout) findViewById(R.id.flashLightLayout);
        flashLightLayout.setOnClickListener(this);
        albumLayout = (LinearLayout) findViewById(R.id.albumLayout);
        albumLayout.setOnClickListener(this);
        bottomLayout = (LinearLayoutCompat) findViewById(R.id.bottomLayout);



        switchVisibility(bottomLayout, config.isShowbottomLayout());
        switchVisibility(flashLightLayout, config.isShowFlashLight());
        switchVisibility(albumLayout, config.isShowAlbum());
//
//        toolbar.setTitle("?????????");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /*????????????????????????????????????  ???????????????*/
        if (isSupportCameraLedFlash(getPackageManager())) {
            flashLightLayout.setVisibility(View.VISIBLE);
        } else {
            flashLightLayout.setVisibility(View.GONE);
        }

    }

    /*?????????????????????????????????*/
    public static boolean isSupportCameraLedFlash(PackageManager pm) {
        if (pm != null) {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            if (features != null) {
                for (FeatureInfo f : features) {
                    if (f != null && PackageManager.FEATURE_CAMERA_FLASH.equals(f.name))
                        return true;
                }
            }
        }
        return false;
    }

    /*?????????????????????*/

    public void switchFlashImg(int flashState) {

        if (flashState == Constant.FLASH_OPEN) {
            flashLightIv.setImageResource(R.drawable.ic_open);
            flashLightTv.setText("???????????????");
        } else {
            flashLightIv.setImageResource(R.drawable.ic_close);
            flashLightTv.setText("???????????????");
        }

    }

    /**
     * ?????????????????????????????????
     *
     * @param rawResult
     */
    public void handleDecode(Result rawResult) {

        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();

        Intent intent = getIntent();
        intent.putExtra(Constant.LOCATION,config.getLOCATION());
        intent.putExtra(Constant.POSITION,config.getPOSITION());
        intent.putExtra(Constant.CODED_CONTENT, rawResult.getText());
        //      intent.putExtra(Constant.CODED_BITMAP, barcode);
        setResult(RESULT_OK, intent);
        this.finish();


    }


    /*??????view?????????*/
    private void switchVisibility(View view, boolean b) {
        if (b) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        cameraManager = new CameraManager(getApplication());

        viewfinder_view.setCameraManager(cameraManager);
        handler = null;

        surfaceHolder = preview_view.getHolder();
        if (hasSurface) {

            initCamera(surfaceHolder);
        } else {
            // ??????callback?????????surfaceCreated()????????????camera
            surfaceHolder.addCallback(this);
        }

        beepManager.updatePrefs();
        inactivityTimer.onResume();

    }

    /**
     * ?????????Camera
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            // ??????Camera????????????
            cameraManager.openDriver(surfaceHolder);
            // ????????????handler????????????????????????????????????????????????
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager);
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    /**
     * ??????????????????
     */
    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("?????????");
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();

        if (!hasSurface) {

            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    /*????????????*/
    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.flashLightLayout) {
            /*???????????????*/
            cameraManager.switchFlashLight(handler);
        } else if (id == R.id.albumLayout) {
            /*????????????*/
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, Constant.REQUEST_IMAGE);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_IMAGE && resultCode == RESULT_OK) {
            String path = ImageUtil.getImageAbsolutePath(this, data.getData());

            new DecodeImgThread(path, new DecodeImgCallback() {
                @Override
                public void onImageDecodeSuccess(Result result) {
                    handleDecode(result);
                }

                @Override
                public void onImageDecodeFailed() {
                    Toast.makeText(CaptureActivity.this, "?????????????????????,??????????????????.", Toast.LENGTH_SHORT).show();
                }
            }).run();


        }
    }
    /**
     * ???????????????
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CaptureActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

package com.example.myapplication.fragment;



import android.graphics.drawable.Drawable;
import android.os.Bundle;


import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.utils.ByteUtils;

import java.util.concurrent.CountDownLatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 我的
 */
public class ConnectFragment extends Fragment implements MainActivity.OnButtonClickedListener{

    @BindView(R.id.netsjbutton)
    Button netsjbutton;         //连接网关按钮

    @BindView(R.id.kzbutton)
    Button kzbutton;


    @BindView(R.id.wayName)
    EditText wayName;

    @BindView(R.id.wayState)
    EditText wayState;


    @BindView(R.id.netdatalist)
    TextView netdatalist;

    Unbinder unbinder;
    private MainActivity mainActivity;

    private final static CountDownLatch mCountDownLatch = new CountDownLatch(3);


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        mainActivity.setButtonClickedListener(ConnectFragment.this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.fragment_connet_way, container, false);

        unbinder = ButterKnife.bind(this, rootView);
        //启动时判断网络状态

        initView();

        return rootView;
    }





//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        Log.e("=========onViewCreated","onViewCreated");
//
//        if (mainActivity.ISUSB&&mainActivity.mSerial.isConnected()) {
//            if (mainActivity.mSerial.isConnected()) {
//                Drawable leftDrawable = getResources().getDrawable(R.drawable.selecttag);
//                leftDrawable.setBounds(0, 0, 40, 40);
//                netdatalist.setCompoundDrawables(null, null, leftDrawable, null);
//            }
//
//
//        } else {
//            Drawable leftDrawable = getResources().getDrawable(R.drawable.tag);
//            leftDrawable.setBounds(0, 0, 40, 40);
//            netdatalist.setCompoundDrawables(null, null, leftDrawable, null);
//        }
//
//
//    }


    private void initView() {

        if (mainActivity.ISUSB&&mainActivity.mSerial.isConnected()) {
            if (mainActivity.mSerial.isConnected()) {
                Drawable leftDrawable = getResources().getDrawable(R.drawable.selecttag);
                leftDrawable.setBounds(0, 0, 40, 40);
                netdatalist.setCompoundDrawables(null, null, leftDrawable, null);
            }


        } else {
            Drawable leftDrawable = getResources().getDrawable(R.drawable.tag);
            leftDrawable.setBounds(0, 0, 40, 40);
            netdatalist.setCompoundDrawables(null, null, leftDrawable, null);
        }







//
        netsjbutton.setOnClickListener(v -> {


            initData();//   <---<---<---<----
            if(!mainActivity.OlDBJ_CODE.equals("")) {
                wayName.setText(mainActivity.OlDBJ_CODE);

            }


        });

        kzbutton.setOnClickListener(v -> {

            initCatData();//   <---<---<---<----
            if(mainActivity.OLDNET_CODE.equals("01")) {
                wayState.setText("连接成功");
            }else if (mainActivity.OLDNET_CODE.equals("02")){
                wayState.setText("连接异常");
            }else{
                wayState.setText("等待处理中...");

            }




        });
    }





    private  class WorkingThread extends Thread {
        private final String mThreadName;
        private final int mSleepTime;
        public WorkingThread(String name, int sleepTime) {
            mThreadName = name;
            mSleepTime = sleepTime;
        }

        @Override
        public void run() {
            try {
                mCountDownLatch.countDown();

                mainActivity.readDataFromSerial("01");

                Thread.sleep(mSleepTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCountDownLatch.countDown();
            System.out.println("[" + mThreadName + "] end!");
        }
    }

    private  class WorkingThread1 extends Thread {
        private final String mThreadName;
        private final int mSleepTime;
        public WorkingThread1(String name, int sleepTime) {
            mThreadName = name;
            mSleepTime = sleepTime;
        }

        @Override
        public void run() {
            try {
                mCountDownLatch.countDown();
                mainActivity.readData2FromSerial("02");

                Thread.sleep(mSleepTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCountDownLatch.countDown();
            System.out.println("[" + mThreadName + "] end!");
        }
    }
    private  class SampleThread extends Thread {

        @Override
        public void run() {
            try {

                // 会阻塞在这里等待 mCountDownLatch 里的count变为0；
                // 也就是等待另外的WorkingThread调用countDown()
                mCountDownLatch.await();
            } catch (Exception e) {

            }
            System.out.println("[SampleThread] end!");
        }
    }


//    // 定义打印任务（此对象只是打印任务，不是线程）
//    class PrintRunnable implements Runnable {
//
//        @Override
//        public void run() {
//            try {
//                mainActivity.readDataFromSerial("01");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    // 定义打印任务（此对象只是打印任务，不是线程）
//    class PrintRunnable1 implements Runnable {
//
//        @Override
//        public void run() {
//            try {
//                mainActivity.readData2FromSerial("02");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
    private void initData() {

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    mainActivity.readDataFromSerial("01");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //设置延时5s再运行一次线程，构成了循环的效果
                handler.postDelayed(this,200);

                handler.removeCallbacks(this);


            }
        };

        handler.post(runnable); //启动线程


    }

    private void initCatData() {

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    mainActivity.readData2FromSerial("02");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //设置延时5s再运行一次线程，构成了循环的效果
                handler.postDelayed(this,200);

                handler.removeCallbacks(this);


            }
        };

        handler.post(runnable); //启动线程


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onclicked(boolean sj) {
        if(sj) {
                Drawable leftDrawable = getResources().getDrawable(R.drawable.selecttag);
                leftDrawable.setBounds(0, 0, 40, 40);
                netdatalist.setCompoundDrawables(null, null, leftDrawable, null);

        } else {

            Drawable leftDrawable = getResources().getDrawable(R.drawable.tag);
            leftDrawable.setBounds(0, 0, 40, 40);
            netdatalist.setCompoundDrawables(null, null, leftDrawable, null);
        }

    }
}

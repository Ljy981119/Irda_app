package com.example.myapplication.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.BaseFragmentPagerAdapter;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 我的
 */
public class WayFragment extends Fragment implements MainActivity.OnButtonClickedListener{


    @BindView(R.id.updateinfoway)
    TextView updateinfoway;


    @BindView(R.id.roundButton)
    Button roundButton;


    Unbinder unbinder;
    private MainActivity mainActivity;

    public BaseFragmentPagerAdapter fm;
    FragmentTransaction ft;
    private Fragment WayDeailFragment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_update_way, container, false);


        unbinder = ButterKnife.bind(this, rootView);

        WayDeailFragment=new WayDeailFragment();

        fm = mainActivity.mAdapter;

        initView();
        initData();

        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        mainActivity.setButtonClickedListener(WayFragment.this);

    }

    @SuppressLint("ResourceType")
    private void initView() {

        if (mainActivity.ISUSB&&mainActivity.mSerial.isConnected()) {
            if (mainActivity.mSerial.isConnected()) {
                Drawable leftDrawable = getResources().getDrawable(R.drawable.selecttag);
                leftDrawable.setBounds(0, 0, 40, 40);
                updateinfoway.setCompoundDrawables(null, null, leftDrawable, null);
            }


        } else {
            Drawable leftDrawable = getResources().getDrawable(R.drawable.tag);
            leftDrawable.setBounds(0, 0, 40, 40);
            updateinfoway.setCompoundDrawables(null, null, leftDrawable, null);
        }



        roundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mainActivity.switchFragment(WayFragment.this,WayDeailFragment);
                // switchContent(WayFragment.this,WayDeailFragment,ft);
//             ft.replace(R.layout.fragment_community, WayDeailFragment);
//               ft.commit();
                fm.replaceFragment(WayFragment.this,WayDeailFragment);

            }
        });





    }
    private void initData() {
    }



    /**
     * 判断是否添加了界面，以保存当前状态
     */
    @SuppressLint("ResourceType")
    public void switchContent(Fragment from, Fragment to,
                              FragmentTransaction transaction) {

        if (!to.isAdded()) { // 先判断是否被add过

            transaction.hide(from).add(R.layout.fragment_community, to)
                    .commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
        }

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
    public void onclicked(boolean sj) {
        if (sj) {
            if (mainActivity.mSerial.isConnected()) {
                Drawable leftDrawable = getResources().getDrawable(R.drawable.selecttag);
                leftDrawable.setBounds(0, 0, 40, 40);
                updateinfoway.setCompoundDrawables(null, null, leftDrawable, null);
            }

        } else {
            Drawable leftDrawable = getResources().getDrawable(R.drawable.tag);
            leftDrawable.setBounds(0, 0, 40, 40);

            if (updateinfoway!=null) {
                updateinfoway.setCompoundDrawables(null, null, leftDrawable, null);
            }
        }

    }
}

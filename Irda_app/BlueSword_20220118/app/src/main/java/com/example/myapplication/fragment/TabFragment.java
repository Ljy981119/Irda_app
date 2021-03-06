package com.example.myapplication.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.myapplication.EncodeActivity;
import com.example.myapplication.R;

import com.example.myapplication.adapter.RecycleViewAdapter;
import com.example.myapplication.param.CodeParam;
import com.example.myapplication.utils.GlideImageLoader;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017/12/19 0019.
 */

public class TabFragment extends Fragment {

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    Unbinder unbinder;
    private List<CodeParam> mdata = new ArrayList<CodeParam>();
    private List<String> imageUrl = new ArrayList<>();
    int mPosition;
    private RecycleViewAdapter mAdapter;
    private Banner mBanner;
    private Long firstBackTime=(long)0;

    private int REQUEST_CODE_SCAN = 111;
    private int POSITION=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragmeny_tab, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        mPosition = getArguments().getInt("position");
        initView();
        initData();

        return rootView;
    }


    private void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecycleViewAdapter(getContext(),R.layout.home_item_view, mdata);

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                CodeParam selectBean = (CodeParam) adapter.getItem(position); //??????Item??????

                // Toast.makeText(getContext(), "????????????" + selectBean.getId() + "??????????????????", Toast.LENGTH_SHORT).show();

//                if (view.getId() == R.id.iv_img) {
//                    Log.i("tag", "????????????" + position + "???????????? ??????");
//                } else if (view.getId() == R.id.tv_title) {
//                    Log.i("tag", "????????????" + position + "???????????? ??????");
//                }
                // Intent Intent = new Intent(getActivity(), EncodeActivity.class);
//                Intent.putExtra("position", position);
//
//                startActivityForResult(Intent, Activity.RESULT_FIRST_USER);
                POSITION=position;

                AndPermission.with(getActivity())
                        .permission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .callback(new PermissionListener() {
                            @Override
                            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                Intent intent = new Intent(getActivity(), CaptureActivity.class);

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


        });


        //???????????????????????????
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        //  divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        mRecyclerView.addItemDecoration(divider);

        mRecyclerView.setAdapter(mAdapter);


    }

    private void initData() {


        for (int i = 0; i < 20; i++) {
            CodeParam  codeParam=new CodeParam();
            codeParam.setId("" + (i + 1));
            codeParam.setDec("");
            mdata.add(codeParam);
        }
        mAdapter.setNewData(mdata);//????????????????????????????????????????????????????????????


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //?????????????????????????????????????????????????????????
    @Override
    public void onStart() {
        super.onStart();
        //????????????
        // mBanner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //????????????
        // mBanner.stopAutoPlay();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        // ???????????????/????????????
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);


                Bundle bundle = new Bundle();
                bundle.putString("code", content);

                CodeParam codeParam1 = mdata.get(POSITION);
                codeParam1.setDec(content);
                mdata.set(POSITION, codeParam1);
                mAdapter.notifyItemChanged(POSITION);

//                bundle.putInt("position", POSITION);
//                POSITION=0;
//                setResult(RESULT_CANCELED, this.getIntent().putExtras(bundle));
//
//              //  this.finish();


                //result.setText("??????????????????" + content);
            }
        }




//
//        if (requestCode == Activity.RESULT_FIRST_USER) {
//
//            if (resultCode == RESULT_CANCELED) {
//              if(data!=null) {
//                  Bundle bundle = data.getExtras();
//                  if (bundle.getString("code") != "") {
//
//                      CodeParam codeParam1 = mdata.get(bundle.getInt("position"));
//                      codeParam1.setDec(bundle.getString("code"));
//                      mdata.set(bundle.getInt("position"), codeParam1);
//                      mAdapter.notifyItemChanged(bundle.getInt("position"));
//
//                  }
//              }
//
//            }
//        }
    }





}

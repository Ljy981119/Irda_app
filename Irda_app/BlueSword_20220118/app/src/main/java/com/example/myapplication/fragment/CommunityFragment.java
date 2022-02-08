package com.example.myapplication.fragment;



import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.CustomAdapter;
import com.example.myapplication.adapter.RecycleViewAdapter;
import com.example.myapplication.components.SegmentView;
import com.example.myapplication.entity.Card;
import com.example.myapplication.entity.Way;
import com.example.myapplication.param.CodeParam;
import com.example.myapplication.utils.ByteUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.youth.banner.Banner;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tw.com.prolific.driver.pl2303.PL2303Driver;

import static android.app.Activity.RESULT_OK;


/**
 * 社区
 */
public class CommunityFragment extends Fragment implements MainActivity.OnButtonClickedListener{

	@BindView(R.id.readrecycler)
	RecyclerView mRecyclerView;


	@BindView(R.id.readmegmentview)
	SegmentView segmentView;

	@BindView(R.id.comthtext)
	TextView comthtext;


	@BindView(R.id.refresh)
	TextView refresh;




	PL2303Driver mSerial;
	private boolean netConnect=false;

	Unbinder unbinder;
	private List<CodeParam> mdata = new ArrayList<CodeParam>();
	private List<CodeParam> mdata2 = new ArrayList<CodeParam>();

	private RecycleViewAdapter mAdapter;
	private RecycleViewAdapter mAdapter2;
	private Banner mBanner;
	private Long firstBackTime=(long)0;

	private int REQUEST_CODE_SCAN = 111;
	private MainActivity mainActivity;
	private String LOCAlTAG ="01";
	private int POSITION=0;
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_community, container, false);
		unbinder = ButterKnife.bind(this, rootView);

		initView();
		initData();

		return rootView;
	}
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mainActivity = (MainActivity) getActivity();
		mainActivity.setButtonClickedListener(CommunityFragment.this);


	}


	private void initView() {

		if (mainActivity.ISUSB&&mainActivity.mSerial.isConnected()) {
			if (mainActivity.mSerial.isConnected()) {
				Drawable leftDrawable = getResources().getDrawable(R.drawable.selecttag);
				leftDrawable.setBounds(0, 0, 40, 40);
				comthtext.setCompoundDrawables(null, null, leftDrawable, null);
			}

		} else {

			Drawable leftDrawable = getResources().getDrawable(R.drawable.tag);
			leftDrawable.setBounds(0, 0, 40, 40);
			comthtext.setCompoundDrawables(null, null, leftDrawable, null);
		}
		segmentView.contentView(new String[]{"线路一","线路二"},16);
		segmentView.setOnSegmentViewClickListener(new SegmentView.onSegmentViewClickListener() {
			@Override
			public void onSegmentViewClick(View view, int postion) {

				switch (postion){
					case 0:
						LOCAlTAG ="01";
						initData();
						break;
					case 1:
						LOCAlTAG ="02";
						initData2();
						break;

				}

			}

		});


		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(layoutManager);
		mAdapter = new RecycleViewAdapter(getContext(),R.layout.read_item_view, mdata);
		mAdapter2 =new RecycleViewAdapter(getContext(),R.layout.read_item_view, mdata2);;

		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//		DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//		//  divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
//		mRecyclerView.addItemDecoration(divider);
//		mRecyclerView.setAdapter(mAdapter);
		refresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
               if(LOCAlTAG.equals("01")) {
				   initData();
			   }else{
				   initData2();
			   }

			}
		});


	}

	private void initData() {
		mdata.clear();
		if (!mainActivity.OlDBJ_CODE.equals("")) {

			//if (LOCAlTAG.equals("01")) {

				StringBuilder sb = new StringBuilder();
				ByteUtils.geCreateComType(sb,"[");
				ByteUtils.geCreateComType(sb,"03");
				ByteUtils.geCreateComType(sb,",");
				ByteUtils.geCreateComType(sb,LOCAlTAG);
				ByteUtils.geCreateComType(sb,",");
				byte[] Utile= ByteUtils.StringToSixteen(mainActivity.OlDBJ_CODE);
				sb.append(ByteUtils.byteArrayToHexString(Utile));
				ByteUtils.geCreateComType(sb,"]");

				Handler handler = new Handler();
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						try {

							Way way=  mainActivity.getListData(sb);

							System.out.println("数据==="+way.getCars().size());
							if(way!=null){
								if (way.getCars().size()>0){
									for (Card card:way.getCars()) {
										CodeParam codeParam = new CodeParam();
										codeParam.setId(card.getMarkId());
										codeParam.setTag(Long.parseLong(card.getTag()));
										codeParam.setUid(card.getTag());
										codeParam.setDec(card.getMark());
										codeParam.setState(card.getMarkState());
										mdata.add(codeParam);
									}

								}

							}
							System.out.println("获取到的数据数据==="+mdata.size());
						} catch (Exception e) {
							e.printStackTrace();
						}

						//设置延时5s再运行一次线程，构成了循环的效果
						handler.postDelayed(this,500);
						handler.removeCallbacks(this);

					}
				};

				handler.post(runnable); //启动线程

			}
		//}

		mAdapter.setNewData(mdata);//模拟网络请求成功后要调用这个方法刷新数据
//		//添加自定义的分割线
		DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
		//  divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
		mRecyclerView.addItemDecoration(divider);
		mRecyclerView.setAdapter(mAdapter);

	}
	private void initData2() {
		mdata2.clear();

		if (!mainActivity.OlDBJ_CODE.equals("")) {

			//if (mainActivity.OLDNET_CODE.equals("")&&mainActivity.OLDNET_CODE.equals("01")) {

				StringBuilder sb = new StringBuilder();
				ByteUtils.geCreateComType(sb,"[");
				ByteUtils.geCreateComType(sb,"03");
				ByteUtils.geCreateComType(sb,",");
				ByteUtils.geCreateComType(sb,LOCAlTAG);
				ByteUtils.geCreateComType(sb,",");
				byte[] Utile= ByteUtils.StringToSixteen(mainActivity.OlDBJ_CODE);
				sb.append(ByteUtils.byteArrayToHexString(Utile));
				ByteUtils.geCreateComType(sb,"]");

				Handler handler = new Handler();
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						try {
							Way way=  mainActivity.getListData(sb);
							if(way!=null){
								if (way.getCars().size()>0){
									for (Card card:way.getCars()) {
										CodeParam codeParam = new CodeParam();
										codeParam.setId(card.getMarkId());
										codeParam.setTag(Long.parseLong(card.getTag()));
										codeParam.setUid(card.getTag());
										codeParam.setDec(card.getMark());
										codeParam.setState(card.getMarkState());
										mdata2.add(codeParam);
									}

								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						//设置延时5s再运行一次线程，构成了循环的效果
						handler.postDelayed(this,500);
						handler.removeCallbacks(this);

					}
				};

				handler.post(runnable); //启动线程

		}
		//}

		mAdapter2.setNewData(mdata2);//模拟网络请求成功后要调用这个方法刷新数据
		//添加自定义的分割线
		DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
		//  divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
		mRecyclerView.addItemDecoration(divider);
		mRecyclerView.setAdapter(mAdapter2);

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
				comthtext.setCompoundDrawables(null, null, leftDrawable, null);
			}

		} else {

			Drawable leftDrawable = getResources().getDrawable(R.drawable.tag);
			leftDrawable.setBounds(0, 0, 40, 40);
			comthtext.setCompoundDrawables(null, null, leftDrawable, null);
		}
	}
}

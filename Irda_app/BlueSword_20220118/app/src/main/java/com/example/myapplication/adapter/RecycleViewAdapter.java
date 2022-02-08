package com.example.myapplication.adapter;



import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.myapplication.R;
import com.example.myapplication.param.CodeParam;


import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2017/12/19 0019.
 */

public class RecycleViewAdapter extends BaseQuickAdapter<CodeParam, BaseViewHolder> {

    private int position = -1;
    private Context mContext;

    public HashMap<Integer, String> contents = new HashMap<>();
    //定义接口
    public static interface OnItemClickListener {
        void onItemClick(Editable editable,int position);
    }
    //声明自定义的监听接口
    private OnItemClickListener monItemClickListener=null;

    //提供set方法
    public void setonUserItemClickListener(OnItemClickListener listener){
        this.monItemClickListener=listener;
    }
    public RecycleViewAdapter(Context context,int layoutResId, @Nullable List<CodeParam> data) {
        super(layoutResId, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, CodeParam item) {

        helper.setText(R.id.textsh,item.getId()).setText(R.id.textcode,item.getDec()).setText(R.id.waystatexs,item.getState());
      if(item.getState().equals("01")){
          helper.setTextColor(R.id.waystatexs, Color.parseColor("#28FF28"));
       }else if (item.getState().equals("02")){
          helper.setTextColor(R.id.waystatexs, Color.parseColor("#FF0000"));

      }

    }





}

package com.example.myapplication.adapter;



import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.myapplication.R;


import java.util.List;


/**
 * Created by Administrator on 2017/12/19 0019.
 */

public class RecycleViewGridAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public RecycleViewGridAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.item_tv,item);
        helper.addOnClickListener(R.id.item_tv);
    }
}

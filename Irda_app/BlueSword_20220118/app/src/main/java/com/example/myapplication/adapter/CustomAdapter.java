package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.example.myapplication.R;
import com.example.myapplication.param.CodeParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> implements View.OnClickListener {

    private Context context;
    private List<CodeParam>  list;
    //声明自定义的监听接口
    private RecycleViewAdapter.OnItemClickListener monItemClickListener=null;
    private OnItemClickListener mOnItemClickListener;//声明自定义的接口

    private ItemClickListener ItemClickListener;//声明自定义的接口
    //定义方法并传给外面的使用者
    public void setOnItemClickListener(OnItemClickListener  listener) {
        this.mOnItemClickListener  = listener;
    }



    public void setItemClickListener(ItemClickListener listener) {
        this.ItemClickListener  = listener;
    }


    public CustomAdapter(Context context, List<CodeParam>  list) {
        this.context = context;

        Collections.sort(list, new PriceComparator()); // 根据价格排序
        this.list = list;



    }



    static class PriceComparator implements Comparator {
        public int compare(Object object1, Object object2) {// 实现接口中的方法
            CodeParam p1 = (CodeParam) object1; // 强制转换
            CodeParam p2 = (CodeParam) object2;
            return p1.getId().compareTo(p1.getId());
        }
    }

    @Override
    public CustomAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_item_view, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onViewRecycled(CustomViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(final CustomAdapter.CustomViewHolder holder, final int position) {
        final CodeParam data = list.get(position);
        holder.et.setTag(data.getTag());
        holder.et.setText(data.getId());
        holder.et.setFilters(new InputFilter[]{new InputFilterMinMax(1,25)});
        holder.textcode.setText(data.getDec());

        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
             //   Log.i("wwwww", "afterTextChanged---");


            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (holder.et.getTag() instanceof Long && (long)holder.et.getTag() == data.getTag() && holder.et.hasFocus()){
                    int adapterPosition = holder.getAdapterPosition();
                    Log.i("获取====",editable.toString()+"是"+holder.getAdapterPosition());
                    //data.setStr(editable.toString());
                    if (ItemClickListener!=null) {
                       // System.out.print("获取===="+editable.toString()+"是"+adapterPosition);
                        //这里使用getTag方法获取position
                        ItemClickListener.onItemClick(editable,adapterPosition);

                    }
                }
            }
        };
        holder.et.addTextChangedListener(textWatcher);

        holder.erwm.setTag(data.getTag());
        holder.erwm.setOnClickListener(CustomAdapter.this);

    }

    //item里面有多个控件可以点击（item+item内部控件）
    public enum ViewName {
        ITEM,
        PRACTISE
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
       Long position = (long) v.getTag();      //getTag()获取数据
        if (mOnItemClickListener != null) {
            switch (v.getId()) {
                case R.id.erwm:
                    mOnItemClickListener.onItemClick(v, ViewName.PRACTISE, position);
                    break;
                default:
                    break;
            }
        }

    }


    public void setNewData(@Nullable List<CodeParam> data) {
        Collections.sort(list, new PriceComparator()); // 根据价格排序
        this.list = data == null ? new ArrayList<CodeParam>() : data;

        notifyDataSetChanged();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private ImageView erwm;
        private EditText et;
        private EditText textcode;

        public CustomViewHolder(View itemView) {
            super(itemView);
            et =  itemView.findViewById(R.id.text);
            textcode =  itemView.findViewById(R.id.textcode);
            erwm =  itemView.findViewById(R.id.erwm);
        }
        public void clear(){
            erwm.setImageResource(0);
            et.setText(null);
            textcode.setText(null);

        }
    }



    //定义接口
    public static interface ItemClickListener {

        void onItemClick(Editable editable, int position);
    }

    //定义接口
    public static interface OnItemClickListener {

        void onItemClick(View v, ViewName practise, long position);
    }


    //提供set方法
    public void setonItemClickListener(RecycleViewAdapter.OnItemClickListener listener){
        this.monItemClickListener=listener;
    }


}

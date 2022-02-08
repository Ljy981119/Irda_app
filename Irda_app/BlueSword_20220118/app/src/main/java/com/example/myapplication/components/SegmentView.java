package com.example.myapplication.components;


import android.content.Context;
import android.content.res.ColorStateList;

import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class SegmentView extends LinearLayout {
    // 实现不同的按钮状态，不同的颜色
    ColorStateList cs1 = getResources().getColorStateList(R.color.white);
    ColorStateList cs2 = getResources().getColorStateList(R.color.colorPrimaryDark);
    private onSegmentViewClickListener segmentListener;
    //存放textview的集合
    List<TextView> views = new ArrayList<TextView>();

    // 这是代码加载ui必须重写的方法
    public SegmentView(Context context) {
        super(context);
    }
    // 这是在xml布局使用必须重写的方法
    public SegmentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SegmentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //主要方法
    public void contentView(String[] text,int dp){
        this.removeAllViews();
        //this.setBackgroundResource(R.drawable.segment_lin_bg);
        for (int i = 0; i < text.length; i++) {
            final TextView textView = new TextView(getContext());
            textView.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
            textView.setText(text[i]);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(5, 14, 5, 14);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dp);
            if (i==0){//第一个
                textView.setBackgroundResource(R.drawable.seg_left);
                textView.setSelected(true);
                textView.setTextColor(cs1);
            }else if(i==text.length-1){//最后一个
                textView.setBackgroundResource(R.drawable.seg_right);
                textView.setTextColor(cs2);
            }
            //设置id方便在设置点击事件的时候区分是那个view
            textView.setId(i);
            views.add(textView);
            this.addView(textView);
            //设置点击事件
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (TextView view:views) {
                        view.setSelected(false);
                        view.setTextColor(cs2);
                    }
                    views.get(v.getId()).setSelected(true);
                    views.get(v.getId()).setTextColor(cs1);
                    if (segmentListener != null) {
                        segmentListener.onSegmentViewClick(textView, v.getId());
                    }
                }
            });

        }
        this.invalidate();//重新draw()

    }

    /**
     * 设置控件显示的文字
     *
     * @param text
     * @param position
     */
    public void setSegmentText(CharSequence text, int position) {
    }

    // 定义一个接口接收点击事件
    public interface onSegmentViewClickListener {
        public void onSegmentViewClick(View view, int postion);
    }

    public void setOnSegmentViewClickListener(onSegmentViewClickListener segmentListener) {
        this.segmentListener = segmentListener;
    }
}
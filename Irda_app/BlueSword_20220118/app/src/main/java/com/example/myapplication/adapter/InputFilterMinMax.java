package com.example.myapplication.adapter;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {
    private float min, max;

    public InputFilterMinMax(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Float.valueOf(min);
        this.max = Float.valueOf(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            //限制小数点位数
            if (source.equals(".") && dest.toString().length() == 0) {
                return "0.";
            }
            if (dest.toString().contains(".")) {
                int index = dest.toString().indexOf(".");
                int mlength = dest.toString().substring(index).length();
                if (mlength == 3) {
                    return "";
                }
            }
            //限制大小
            float input = Float.valueOf(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (Exception nfe) { }
        return "";
    }

    private boolean isInRange(float a, float b, float c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}

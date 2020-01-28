package com.mirvahidagha.betterbet.Others;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.mirvahidagha.betterbet.Fastscroll.Utils;

public class RecyclerAyah extends RecyclerView {

    public RecyclerAyah(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        float dp = 400;
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        int pixel = Math.round(px);
        heightSpec = MeasureSpec.makeMeasureSpec(pixel, MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }
}

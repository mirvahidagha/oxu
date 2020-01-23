package com.mirvahidagha.betterbet.Others;

import android.content.Context;

public class ConvertingBetweenMetricUnits {


    Context context;

    public ConvertingBetweenMetricUnits(Context context) {
        this.context = context;
    }

    public int dpToPx(int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}

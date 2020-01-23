package com.mirvahidagha.betterbet.Others;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.mirvahidagha.betterbet.R;

/**
 * Created by vahid on 2/4/18.
 */

public class Dolduran extends PagerAdapter {

    LottieAnimationView lottie;

    Context context;
    LayoutInflater inflater;
    String[] anims = {
            "plane.json",
            "trophy.json",
            "smiley.json",
            "switch.json",
            "floating_cloud.json"};

    public Dolduran(Context context) {
        this.context = context;

    }


    @Override
    public int getCount() {
        return anims.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.slider, container, false);
        lottie = view.findViewById(R.id.animation);
        lottie.setAnimation(anims[position]);
        lottie.playAnimation();
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
package com.mirvahidagha.betterbet.Activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mirvahidagha.betterbet.Others.Dolduran;
import com.mirvahidagha.betterbet.R;

public class SliderActivity extends AppCompatActivity {

    ViewPager pager;
    RelativeLayout relativeLayout;
    LinearLayout indicator;
    TextView[] dots;
    int currentPosition;
    String headers[], descriptions[];

    TextView back, next, header, description;

    int colors[] = {
            R.color.color1,
            R.color.color2,
            R.color.color3,
            R.color.color4,
            R.color.color5};

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider_main);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        indicator = (LinearLayout) findViewById(R.id.indicator);
        next = (TextView) findViewById(R.id.next);
        back = (TextView) findViewById(R.id.back);
        header = (TextView) findViewById(R.id.header);
        description = (TextView) findViewById(R.id.description);
        back.setVisibility(View.INVISIBLE);
        headers = getResources().getStringArray(R.array.headers);
        descriptions = getResources().getStringArray(R.array.descriptions);

        changeBackground(0);
        setIndicator(0);
        pager = (ViewPager) findViewById(R.id.pager);
        Dolduran dolduran = new Dolduran(this);
        pager.setAdapter(dolduran);
        currentPosition = 0;
        pager.setOnPageChangeListener(listener);
        next.setOnClickListener(nextAction);
        back.setOnClickListener(backAction);
    }


    ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {

        float əvvəlki;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


//if (Math.abs(positionOffset-header.getAlpha())>0)


            if (positionOffset == 0.0) {
                header.setText(headers[position]);
                header.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();

                description.setText(descriptions[position]);
                description.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();

            } else {

                if (əvvəlki > positionOffset) {
                    header.setAlpha(positionOffset);
                    description.setAlpha(positionOffset);
                } else {
                    header.setAlpha(1 - positionOffset);
                    description.setAlpha(1 - positionOffset);
                }
                əvvəlki = positionOffset;

            }
        }

        @Override
        public void onPageSelected(int position) {
            changeBackground(position);
            setIndicator(position);
            changeButtons(position);


        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    void changeBackground(int position) {

        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int colorFrom = getResources().getColor(colors[currentPosition]);
        int colorTo = getResources().getColor(colors[position]);
        currentPosition = position;

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(1000);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                relativeLayout.setBackgroundColor((int) animator.getAnimatedValue());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor((int) animator.getAnimatedValue());
                    window.setNavigationBarColor((int) animator.getAnimatedValue());
                }
            }
        });

        colorAnimation.start();

    }


    void setIndicator(int position) {
        dots = new TextView[5];
        indicator.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            indicator.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(android.R.color.white));
            dots[position].setTextSize(45);
        }
    }

    void changeButtons(int position) {

        switch (position) {

            case 0:
                back.setVisibility(View.INVISIBLE);
                break;
            case 1:
                back.setVisibility(View.VISIBLE);
                break;

            case 3:
                next.setText("Next");
                break;
            case 4:
                next.setText("Finish");
                break;

        }
    }

    Button.OnClickListener backAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    };

    Button.OnClickListener nextAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (pager.getAdapter().getCount() - 1 != pager.getCurrentItem())
                pager.setCurrentItem(pager.getCurrentItem() + 1);
            else {
                SharedPreferences preferences = getSharedPreferences("splashSettings", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("first_time", false);
                editor.apply();
                startActivity(new Intent(SliderActivity.this, Main.class));
            }
        }
    };

}


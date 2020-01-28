package com.mirvahidagha.betterbet.dialog;


import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.mirvahidagha.betterbet.R;


public class SweetAlertDialog extends Dialog {
    private View mDialogView;
    private AnimationSet mModalInAnim;
    private AnimationSet mModalOutAnim;
    private Animation mOverlayOutAnim;
    private TextView mTitleTextView;
    private FrameLayout mCustomViewContainer;
    private View mCustomView;
    private String mTitleText;
    public MaterialButton copy, star;
    Typeface bold, regular, light;

    private boolean mCloseFromCancel;

    public SweetAlertDialog(Context context, int style) {
        super(context, style);

        setCancelable(false);
        setCanceledOnTouchOutside(true); //TODO was false

        mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.modal_in);
        mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.modal_out);
        bold = Typeface.createFromAsset(context.getAssets(), "bold.ttf");
        regular = Typeface.createFromAsset(context.getAssets(), "regular.ttf");
        light = Typeface.createFromAsset(context.getAssets(), "light.ttf");

        mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.setVisibility(View.GONE);
                mDialogView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCloseFromCancel) {
                            SweetAlertDialog.super.cancel();
                        } else {
                            SweetAlertDialog.super.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        // dialog overlay fade out

        mOverlayOutAnim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                WindowManager.LayoutParams wlp = getWindow().getAttributes();
                wlp.alpha = 1 - interpolatedTime;
                getWindow().setAttributes(wlp);
            }
        };
        mOverlayOutAnim.setDuration(120);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog);

        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        mTitleTextView = (TextView) findViewById(R.id.title_text);
        mCustomViewContainer = (FrameLayout) findViewById(R.id.custom_view_container);

        copy = findViewById(R.id.copy);
        star = findViewById(R.id.star);

        copy.setTypeface(bold);
        star.setTypeface(bold);

        setTitleText(mTitleText);
        setCustomView(mCustomView);
    }

    void setOnClick(View.OnClickListener click) {
        copy.setOnClickListener(click);
        star.setOnClickListener(click);
    }

    public SweetAlertDialog setTitleText(String text) {
        mTitleText = text;
        if (mTitleTextView != null && mTitleText != null) {
            if (text.isEmpty()) {
                mTitleTextView.setVisibility(View.GONE);
            } else {
                mTitleTextView.setVisibility(View.VISIBLE);
                mTitleTextView.setTypeface(bold);
                mTitleTextView.setText(mTitleText);
            }
        }
        return this;
    }


    @Override
    public void setTitle(CharSequence title) {
        this.setTitleText(title.toString());
    }

    @Override
    public void setTitle(int titleId) {
        this.setTitleText(getContext().getResources().getString(titleId));
    }


    protected void onStart() {
        mDialogView.startAnimation(mModalInAnim);
    }


    public SweetAlertDialog setCustomView(View view) {
        mCustomView = view;
        if (mCustomView != null && mCustomViewContainer != null) {
            mCustomViewContainer.addView(view);
            mCustomViewContainer.setVisibility(View.VISIBLE);
        }
        return this;
    }

    @Override
    public void cancel() {
        dismissWithAnimation(true);

    }


    private void dismissWithAnimation(boolean fromCancel) {
        mCloseFromCancel = fromCancel;
        mTitleTextView.startAnimation(mOverlayOutAnim);
        mDialogView.startAnimation(mModalOutAnim);
    }


}
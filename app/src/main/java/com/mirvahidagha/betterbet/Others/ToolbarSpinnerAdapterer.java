package com.mirvahidagha.betterbet.Others;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.mirvahidagha.betterbet.Activities.Main;
import com.mirvahidagha.betterbet.R;

import java.util.List;


public class ToolbarSpinnerAdapterer extends BaseAdapter {
    Context context;
    String[] translations;
    LayoutInflater inflter;

    public void setSelected(int selected) {
        this.selected = selected;
    }

    int selected;

    public ToolbarSpinnerAdapterer(Context applicationContext, String[] translations, int selected) {
        this.context = applicationContext;
        this.translations = translations;
        this.selected = selected;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return translations.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {


        View v = super.getDropDownView(position, null, parent);
        if (position == selected)
            v.setBackgroundColor(context.getResources().getColor(R.color.qirmizi));

        else v.setBackgroundColor(context.getResources().getColor(R.color.goy));

        TextView spinnerTextView = v.findViewById(R.id.spinner_textview);

        spinnerTextView.setLayoutParams(new LinearLayout.LayoutParams(600, -2));

        spinnerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        spinnerTextView.setPadding(48, 12, 8, 12);

        spinnerTextView.setMaxLines(2);
        return v;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_items, null);
        TextView names = (TextView) view.findViewById(R.id.spinner_textview);
        names.setText(translations[i]);
        names.setTypeface(Main.bold);
        return view;
    }
}
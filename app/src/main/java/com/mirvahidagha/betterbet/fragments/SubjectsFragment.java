package com.mirvahidagha.betterbet.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mirvahidagha.betterbet.R;

public class SubjectsFragment extends Fragment {

    public SubjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_forecasts, container, false);

        return view;
    }


}
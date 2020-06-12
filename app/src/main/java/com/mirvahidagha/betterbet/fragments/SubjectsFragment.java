package com.mirvahidagha.betterbet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mirvahidagha.betterbet.Entities.SubjectWithIndexes;
import com.mirvahidagha.betterbet.Fastscroll.FastScroller;
import com.mirvahidagha.betterbet.Others.TabFragment;
import com.mirvahidagha.betterbet.R;
import com.mirvahidagha.betterbet.ViewModels.SubjectViewModel;
import com.mirvahidagha.betterbet.expandable.models.ExpandableListPosition;
import com.mirvahidagha.betterbet.sample.expand.GenreAdapter;

import java.util.List;

import static com.mirvahidagha.betterbet.sample.GenreDataFactory.makeGenres;

public class SubjectsFragment extends TabFragment {

    FastScroller fastScroller;
    public GenreAdapter adapter;
    SubjectViewModel viewModel;
    public SubjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_subjects, container, false);
        fastScroller = view.findViewById(R.id.fastscroll);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        //   LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        GridLayoutManager grid = new GridLayoutManager(getContext(), 4);
        // StaggeredGridLayoutManager grid = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);


        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getType(position) == ExpandableListPosition.GROUP) {
                    return 4;
                }
                return 1;
            }
        });

        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }


        viewModel = ViewModelProviders.of(this).get(SubjectViewModel.class);

        viewModel.getSubjects().observe(getViewLifecycleOwner(), new Observer<List<SubjectWithIndexes>>() {
            @Override
            public void onChanged(List<SubjectWithIndexes> subjectWithIndexes) {
                adapter = new GenreAdapter(makeGenres(subjectWithIndexes), subjectWithIndexes);
                recyclerView.setLayoutManager(grid);
                recyclerView.setAdapter(adapter);

            }
        });

        fastScroller.setRecyclerView(recyclerView);

        return view;
    }


    @Override
    public void search(String text) {
      //  Toast.makeText(getActivity(), "subjects "+ text, Toast.LENGTH_SHORT).show();
        adapter.getFilter().filter(text);
    }

}
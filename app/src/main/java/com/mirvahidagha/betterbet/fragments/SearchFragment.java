package com.mirvahidagha.betterbet.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.Surah;
import com.mirvahidagha.betterbet.Others.CombinedLiveData;
import com.mirvahidagha.betterbet.R;
import com.mirvahidagha.betterbet.Search.SearchAdapter;
import com.mirvahidagha.betterbet.Search.NewsItem;
import com.mirvahidagha.betterbet.ViewModels.AyahViewModel;
import com.mirvahidagha.betterbet.ViewModels.SurahViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private AyahViewModel ayahViewModel;
    // private List<Surah> surahs;
    // private List<Ayah> ayahs;
    private RecyclerView recycler;
    private SearchAdapter adapter;
    private ArrayList<NewsItem> items;
    private ConstraintLayout rootLayout;
    private SearchView searchView;
    private String[] tables;
    private SharedPreferences pref;
    MenuItem menuItem;
    SurahViewModel surahViewModel;

    SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        tables = getResources().getStringArray(R.array.table_names);

        //String table = tables[pref.getInt("main", 1)];

        ayahViewModel = ViewModelProviders.of(this).get(AyahViewModel.class);
        surahViewModel = ViewModelProviders.of(this).get(SurahViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_forecasts, container, false);
        //  getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        rootLayout = view.findViewById(R.id.root_layout);
        recycler = view.findViewById(R.id.news_rv);
        items = new ArrayList<>();

        adapter = new SearchAdapter(getContext(), items);
        recycler.setAdapter(adapter);
        StaggeredGridLayoutManager grid = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycler.setLayoutManager(grid);

        CombinedLiveData surahsAndAyahs = new CombinedLiveData(surahViewModel.getSurahs(), ayahViewModel.getAllAyahs());

        surahsAndAyahs.observe((LifecycleOwner) getContext(), pair -> {

            if (pair.second != null && pair.first != null) {
                ayahViewModel.getAllAyahs().observe(this, ayahContents -> {
                    items = new ArrayList<>();
                    for (int i = 0; i < ayahContents.size(); i++) {
                        Ayah ayah = pair.second.get(i);
                        String title = pair.first.get(ayah.getSuraID() - 1).getAzeri();
                        Ayah content = ayahContents.get(i);
                        items.add(new NewsItem(title, ayah, content));
                    }
                    adapter.setAdapterData(items);

                });
            }

        });

        prefListener = (prf, key) ->
        {
            if (key == "main") updateAyahContent(prf.getInt("main", 2));
        };

        pref.registerOnSharedPreferenceChangeListener(prefListener);

        return view;
    }

    private void updateAyahContent(int tableIndex) {

        ayahViewModel.getAllAyahs().observe(this, new Observer<List<Ayah>>() {
            @Override
            public void onChanged(List<Ayah> ayahs) {
                for (int i = 0; i < items.size(); i++)
                    items.get(i).setAyah(ayahs.get(i));
                adapter.setAdapterData(items);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pref.unregisterOnSharedPreferenceChangeListener(prefListener);
    }

    private boolean isFirst = true;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!isFirst) {
            inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_search, menu);

            menuItem = menu.findItem(R.id.menu_action_search);
            searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    adapter.getFilter().filter(newText);

                    return false;
                }
            });

            searchView.setOnSearchClickListener(v -> EventBus.getDefault().post(true));

            searchView.setOnCloseListener(() -> {
                EventBus.getDefault().post(false);
                return false;
            });

        }
        isFirst = false;
    }

}

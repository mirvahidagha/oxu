package com.mirvahidagha.betterbet.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.Surah;
import com.mirvahidagha.betterbet.Others.Just;
import com.mirvahidagha.betterbet.Others.RecyclerAyah;
import com.mirvahidagha.betterbet.R;
import com.mirvahidagha.betterbet.ViewModels.AyahViewModel;
import com.mirvahidagha.betterbet.ViewModels.SurahViewModel;
import com.mirvahidagha.betterbet.dialog.AyahDialog;
import com.mirvahidagha.betterbet.Fastscroll.FastScroller;
import com.mirvahidagha.betterbet.Fastscroll.SectionTitleProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AyahsFragment extends Fragment {

    AyahViewModel viewModel;
    RecyclerView recycler;
    RecycleAdapter adapter;
    Surah currentSurah;
    Ayah ayah;
    SurahViewModel surahViewModel;
    int scrollItem, surahNumber;
    boolean scrolled;
    public AyahsFragment instance;
    GridLayoutManager grid;
    SharedPreferences pref;
    String[] translations, tableNames;
    ArrayList<String> selectedBooks;
    SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    FastScroller fastScroller;
    Typeface bold, regular, light;
    Observer observer;

    public AyahsFragment() {
        // Required empty public constructor
    }

    public AyahsFragment getInstance(int surahId, int scrollPosition) {
        //  instance = new AyahsFragment();
        this.surahNumber = surahId;
        this.scrollItem = scrollPosition;
        scrolled = false;
        return this;
    }

    @Override
    public void onResume() {
        super.onResume();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // grid.scrollToPositionWithOffset(scrollItem, 0);
            }
        };
        runnable.run();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(AyahViewModel.class);
        surahViewModel = ViewModelProviders.of(this).get(SurahViewModel.class);
        pref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        translations = getContext().getResources().getStringArray(R.array.translations);
        tableNames = getContext().getResources().getStringArray(R.array.table_names);

        selectedBooks = getChosenBooks();
        bold = Typeface.createFromAsset(getResources().getAssets(), "bold.ttf");
        regular = Typeface.createFromAsset(getResources().getAssets(), "regular.ttf");
        light = Typeface.createFromAsset(getResources().getAssets(), "light.ttf");

    }

    private boolean[] getCheckedItems() {
        boolean[] reChecked = new boolean[translations.length];
        for (int i = 0; i < translations.length; i++) {
            reChecked[i] = pref.getBoolean(Integer.toString(i), false);
        }
        return reChecked;
    }

    private ArrayList<String> getChosenBooks() {
        ArrayList<String> chosen = new ArrayList<>();
        boolean[] chosenArray = getCheckedItems();
        for (int i = 0; i < chosenArray.length; i++)
            if (chosenArray[i]) {
                chosen.add(tableNames[i]);
            }
        return chosen;
    }

    private ArrayList<String> getTranslations() {
        ArrayList<String> trans = new ArrayList<>();
        boolean[] chosenArray = getCheckedItems();

        for (int i = 0; i < chosenArray.length; i++)
            if (chosenArray[i]) {
                trans.add(translations[i]);
            }
        return trans;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.ayahs_fragment, container, false);

        recycler = view.findViewById(R.id.recycler_ayahs);
        fastScroller = (FastScroller) view.findViewById(R.id.fastscroll);
        adapter = new RecycleAdapter();
        grid = new GridLayoutManager(view.getContext(), 1);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(grid);
        recycler.setAdapter(adapter);
        fastScroller.setRecyclerView(recycler);
        surahViewModel.getSurahs().observe(this, new Observer<List<Surah>>() {
            @Override
            public void onChanged(List<Surah> surah) {
                currentSurah = surah.get(surahNumber - 1);
            }
        });

        observer = new Observer<List<Ayah>>() {
            @Override
            public void onChanged(List<Ayah> ayahs) {
                adapter.setAyahs(ayahs);
                if (!scrolled)
                    grid.scrollToPositionWithOffset(scrollItem, 0);
                scrolled = true;
            }
        };

        viewModel.getAyahs(surahNumber).observe(this, observer);

        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                viewModel.getAyahs(surahNumber).observe(getViewLifecycleOwner(), observer);

            }
        };
        pref.registerOnSharedPreferenceChangeListener(prefListener);
        return view;
    }

    public void update() {
        viewModel.getAyahs(surahNumber).observe(this, observer);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().post("empty");
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> implements SectionTitleProvider {
        List<Ayah> ayahs = new ArrayList<>();

        @NotNull
        @Override
        public RecycleAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.ayah, parent, false);
            return new RecycleAdapter.ViewHolder(view);
        }

        public void setAyahs(List<Ayah> ayahs) {
            this.ayahs = ayahs;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(final RecycleAdapter.ViewHolder holder, final int position) {

            ayah = ayahs.get(position);


            if (pref.getInt("main", 1) == 0) {
                holder.ayah.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                holder.ayah.setTypeface(regular);
            } else {

                holder.ayah.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                holder.ayah.setTypeface(bold);
            }

            String text = String.valueOf(position + 1) + ". " + ayah.getAyahText();
            if (ayah.getStar() == 1)
                text = "★" + text;
            holder.ayah.setText(text);

        }

        @Override
        public int getItemCount() {
            return ayahs.size();
        }

        @Override
        public String getSectionTitle(int position) {
            return ayahs.get(position).getVerseID().toString();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

            TextView ayah;

            ViewHolder(final View itemView) {
                super(itemView);
                ayah = itemView.findViewById(R.id.ayah_text);
                ayah.setOnLongClickListener(this);
            }

            @Override
            public boolean onLongClick(View v) {
                Ayah currentAyah = RecycleAdapter.this.ayahs.get(getAdapterPosition());
                final ArrayList<Ayah> ayahs = new ArrayList<>();
                AyahDialog dialog = new AyahDialog(getContext(), currentSurah, currentAyah, viewModel, getChosenBooks(), getTranslations());
                final RelativeLayout layout = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.ayah_long_press_dialog, null);
                dialog.setCustomView(layout);
                dialog.show();
                return false;
            }
        }
    }

}

package com.mirvahidagha.betterbet.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.Surah;
import com.mirvahidagha.betterbet.Others.MyData;
import com.mirvahidagha.betterbet.R;
import com.mirvahidagha.betterbet.ViewModels.AyahViewModel;
import com.mirvahidagha.betterbet.ViewModels.SurahViewModel;
import com.mirvahidagha.betterbet.dialog.AyahDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class StarFragment extends Fragment {

    AyahViewModel ayahViewModel;
    SurahViewModel surahViewModel;
    RecyclerView recyclerView;
    ArrayList<String> selectedBooks;
    SharedPreferences pref;
    String[] translations;

    public StarFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_star, container, false);
        //   GridLayoutManager grid = new GridLayoutManager(getActivity(), 2);
        recyclerView = rootView.findViewById(R.id.recycler_ayahs);
        StaggeredGridLayoutManager grid = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(grid);
        recyclerView.setHasFixedSize(true);
        final RecycleAdapter adapter = new RecycleAdapter();
        recyclerView.setAdapter(adapter);

        ayahViewModel = ViewModelProviders.of(this).get(AyahViewModel.class);
        surahViewModel = ViewModelProviders.of(this).get(SurahViewModel.class);
        surahViewModel.getSurahs().observe(this, new Observer<List<Surah>>() {
            @Override
            public void onChanged(List<Surah> surahs) {
                adapter.setSurahs(surahs);
            }
        });

        ayahViewModel.getStarredAyahs().observe(this, new Observer<List<Ayah>>() {
            @Override
            public void onChanged(List<Ayah> ayahs) {
                adapter.setAyahs(ayahs);
            }
        });

        pref = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        translations = getContext().getResources().getStringArray(R.array.table_names);
        selectedBooks = getChosenBooks();
        return rootView;
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
            if (chosenArray[i])
                chosen.add(translations[i]);
        return chosen;
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
        List<Ayah> ayahs = new ArrayList<>();
        List<Surah> surahs = new ArrayList<>();

        @Override
        public RecycleAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.starred_ayah_item, parent, false);
            return new RecycleAdapter.ViewHolder(view);
        }

        public void setAyahs(List<Ayah> ayahs) {
            this.ayahs = ayahs;
            notifyDataSetChanged();
        }

        public void setSurahs(List<Surah> surahs) {
            this.surahs = surahs;
        }

        @Override
        public void onBindViewHolder(final RecycleAdapter.ViewHolder holder, final int position) {

            Ayah currentAyah = ayahs.get(position);
            String[] tables = getResources().getStringArray(R.array.table_names);
            int currentTableIndex = pref.getInt("main", 1);
            ayahViewModel.getAyahContent(tables[currentTableIndex], currentAyah.getSuraID(), currentAyah.getVerseID()).observe(getActivity(), new Observer<Ayah>() {
                @Override
                public void onChanged(Ayah ayah) {
                    CharSequence text = ayah.getAyahText();
                    holder.ayah.setText(text);
                    Surah surah = surahs.get(currentAyah.getSuraID() - 1);
                    String headerText = surah.getAzeri() + " - " + currentAyah.getVerseID();
                    holder.header.setText(headerText);
                }
            });

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Ayah ayah = ayahs.get(position);
                    EventBus.getDefault().post(new MyData(ayah.getSuraID(), ayah.getVerseID() - 1));
                }
            });
        }

        @Override
        public int getItemCount() {
            return ayahs.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

            TextView ayah, header;
            CardView cardView;

            ViewHolder(final View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.card);
                ayah = itemView.findViewById(R.id.ayah_text);
                header = itemView.findViewById(R.id.ayah_header);
                cardView.setOnLongClickListener(this);
            }

            @Override
            public boolean onLongClick(View v) {
//                Ayah currentAyah = RecycleAdapter.this.ayahs.get(getAdapterPosition());
//                final ArrayList<Ayah> ayahs = new ArrayList<>();
//
//                AyahDialog dialog = new AyahDialog(getContext(),
//                        surahs.get(
//                                ayahs.get(getAdapterPosition()).getSuraID() - 1),
//                        currentAyah, ayahViewModel, getChosenBooks());
//                final RelativeLayout layout = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.ayah_long_press_dialog, null);
//                dialog.setCustomView(layout);
//                dialog.show();
                return false;
            }
        }
    }

}

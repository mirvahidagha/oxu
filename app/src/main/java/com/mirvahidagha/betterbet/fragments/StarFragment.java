package com.mirvahidagha.betterbet.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.AyahContent;
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
                Log.d("vahuuu", "set ayahs");
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
           int currentTableIndex = pref.getInt("main", 0);
            ayahViewModel.getAyahContent(tables[currentTableIndex], currentAyah.getSura(), currentAyah.getNumber()).observe(getActivity(), new Observer<AyahContent>() {
                @Override
                public void onChanged(AyahContent ayahContent) {
                    CharSequence text = ayahContent.getAyahText();
                    holder.ayah.setText(text);
                    Surah surah = surahs.get(ayahs.get(position).getSura() - 1);
                    String headerText = surah.getAzeri() + " - " + ayahs.get(position).getNumber();
                    holder.header.setText(headerText);
                }
            });


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Ayah ayah = ayahs.get(position);
                    EventBus.getDefault().post(new MyData(ayah.getSura(), ayah.getNumber() - 1));
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
                Ayah currentAyah = ayahs.get(getAdapterPosition());
                final ArrayList<AyahContent> ayahContents = new ArrayList<>();
                AyahDialog dialog = new AyahDialog(getContext(), currentAyah, ayahViewModel, selectedBooks);
                final LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.ayah_long_press_dialog, null);
                dialog.setCustomView(layout);
                dialog.show();
                return false;
            }
        }
    }

}
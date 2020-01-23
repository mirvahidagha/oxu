package com.mirvahidagha.betterbet.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.AyahContent;
import com.mirvahidagha.betterbet.Entities.Surah;
import com.mirvahidagha.betterbet.Others.Just;
import com.mirvahidagha.betterbet.R;
import com.mirvahidagha.betterbet.ViewModels.AyahViewModel;
import com.mirvahidagha.betterbet.ViewModels.SurahViewModel;
import com.mirvahidagha.betterbet.dialog.AyahDialog;

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
    AyahContent ayahContent;
    SurahViewModel surahViewModel;
    int scrollItem, surahNumber;
    boolean scrolled;
    public AyahsFragment instance;
    GridLayoutManager grid;
    SharedPreferences pref;
    String[] translations;
    ArrayList<String> selectedBooks;

    public AyahsFragment() {
        // Required empty public constructor
    }



    public AyahsFragment getInstance(int surahId, int scrollPosition) {
        instance = new AyahsFragment();
        instance.surahNumber = surahId;
        instance.scrollItem = scrollPosition;
        return instance;
    }

    @Override
    public void onResume() {
        super.onResume();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                grid.scrollToPositionWithOffset(scrollItem, 0);
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
        translations = getContext().getResources().getStringArray(R.array.table_names);
        selectedBooks = getChosenBooks();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        recycler = view.findViewById(R.id.recycler_ayahs);
        adapter = new RecycleAdapter();
        grid = new GridLayoutManager(view.getContext(), 1);

        viewModel.getAyahs(surahNumber).observe(this, new Observer<List<Ayah>>() {
            @Override
            public void onChanged(List<Ayah> ayahs) {
                adapter.setAyahs(ayahs);
                if (!scrolled)
                    grid.scrollToPositionWithOffset(scrollItem, 0);
                scrolled = true;
            }
        });

        surahViewModel.getSurahs().observe(this, new Observer<List<Surah>>() {
            @Override
            public void onChanged(List<Surah> surah) {
                currentSurah = surah.get(surahNumber - 1);
                EventBus.getDefault().post(currentSurah.getAzeri());
            }
        });

        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(grid);
        recycler.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().post(new Just());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void customEventReceived(Just just) {
        EventBus.getDefault().post(currentSurah.getAzeri());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().post("empty");
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

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
            viewModel.getAyahContent(translations[pref.getInt("main", 0)], surahNumber, position + 1).observe(getViewLifecycleOwner(), new Observer<AyahContent>() {
                @Override
                public void onChanged(AyahContent ayahContent) {
                    String text = String.valueOf(position + 1) + ". " + ayahContent.getAyahText();
                    if (ayahs.get(position).getStarred() == 1)
                        text = "★" + text;
                    holder.ayah.setText(text);
                }
            });


        }

        @Override
        public int getItemCount() {
            return ayahs.size();
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
                Ayah currentAyah = ayahs.get(getAdapterPosition());
                final ArrayList<AyahContent> ayahContents = new ArrayList<>();
                AyahDialog dialog = new AyahDialog(getContext(), currentAyah, viewModel, selectedBooks);
                final LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.ayah_long_press_dialog, null);
                dialog.setCustomView(layout);

                dialog.show();
                return false;
            }
        }
    }

}

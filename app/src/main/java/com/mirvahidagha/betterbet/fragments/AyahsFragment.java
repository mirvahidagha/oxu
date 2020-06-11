package com.mirvahidagha.betterbet.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mirvahidagha.betterbet.Activities.Main;
import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.StarredAyah;
import com.mirvahidagha.betterbet.Entities.Surah;
import com.mirvahidagha.betterbet.Fastscroll.FastScroller;
import com.mirvahidagha.betterbet.Fastscroll.SectionTitleProvider;
import com.mirvahidagha.betterbet.Others.Just;
import com.mirvahidagha.betterbet.Others.TabFragment;
import com.mirvahidagha.betterbet.R;
import com.mirvahidagha.betterbet.ViewModels.AyahViewModel;
import com.mirvahidagha.betterbet.ViewModels.StarredViewModel;
import com.mirvahidagha.betterbet.ViewModels.SurahViewModel;
import com.mirvahidagha.betterbet.dialog.AyahDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AyahsFragment extends TabFragment {

    AyahViewModel viewModel;
    StarredViewModel starredViewModel;
    RecyclerView recycler;
    RecycleAdapter adapter;
    Surah currentSurah;
    Ayah ayah;
    SurahViewModel surahViewModel;
    int scrollItem, surahNumber, translation;
    boolean scrolled;
    GridLayoutManager grid;
    SharedPreferences pref;
    String[] translations, tableNames;
    ArrayList<String> selectedBooks;
    SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    FastScroller fastScroller;
    Observer observer;
    Parcelable mListState;
    String table, LIST_STATE_KEY = "recycler state";
    private ActionMode actionMode;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mListState = grid.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, mListState);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null)
            mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mListState != null) {
            grid.onRestoreInstanceState(mListState);
        }
    }

    public AyahsFragment() {
        // Required empty public constructor
    }

    public AyahsFragment getInstance(int surahId, int scrollPosition, int translation) {
        this.surahNumber = surahId;
        this.scrollItem = scrollPosition;
        this.translation = translation;
        scrolled = false;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(AyahViewModel.class);
        surahViewModel = ViewModelProviders.of(this).get(SurahViewModel.class);
        starredViewModel = ViewModelProviders.of(this).get(StarredViewModel.class);
        pref = Objects.requireNonNull(getContext()).getSharedPreferences("settings", Context.MODE_PRIVATE);

        translations = getContext().getResources().getStringArray(R.array.translations);
        tableNames = getContext().getResources().getStringArray(R.array.table_names);

        selectedBooks = getChosenBooks();
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {

            actionMode = ((AppCompatActivity) Objects.requireNonNull(getActivity())).startSupportActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.menu_copy, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.action_copy) {
                        //copy ayahs
                        copySelectedAyahs();
                        mode.finish();
                        return true;
                    } else if (id == R.id.action_star) {
                        starSelected();
                        mode.finish();
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    adapter.clearSelections();
                    actionMode = null;
                }
            });


        }
        toggleSelection(position);
    }

    private void copySelectedAyahs() {
        StringBuilder copiedAyahs = new StringBuilder();
        List<Integer> selectedItemPositions = adapter.getSelectedItems();
        for (int i = 0; i <= selectedItemPositions.size() - 1; i++) {
            copiedAyahs.append(adapter.getSelectedAyahText(selectedItemPositions.get(i)));
        }

        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text label", copiedAyahs);
        clipboard.setPrimaryClip(clip);
        Snackbar.make(getView(), selectedItemPositions.size() + " ayə kopyalandı.", Snackbar.LENGTH_SHORT).show();
    }


    private void starSelected() {

        // ArrayList<StarredAyah> starredAyahs = new ArrayList<>();

        List<Integer> selectedItemPositions = adapter.getSelectedItems();
        StarredAyah[] starredAyahs = new StarredAyah[selectedItemPositions.size()];
        for (int i = 0; i <= selectedItemPositions.size() - 1; i++) {

            Ayah ayah = adapter.getSelectedAyah(selectedItemPositions.get(i));

            StarredAyah starred = new StarredAyah(
                    ayah.getId() + "@" + ayah.getTableId(),
                    ayah.getSuraID(),
                    ayah.getVerseID(),
                    ayah.getAyahText().toString(),
                    ayah.getTableId());
            starredAyahs[i] = starred;
        }
        starredViewModel.insertStarredAyahs(starredAyahs);
        Snackbar.make(getView(), selectedItemPositions.size() + " ayə ulduzlandı.", Snackbar.LENGTH_SHORT).show();

    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

//    private class ActionModeCallback implements ActionMode.Callback {
//        @Override
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            mode.getMenuInflater().inflate(R.menu.menu_copy, menu);
//            return true;
//        }
//
//        @Override
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            return false;
//        }
//
//        @Override
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            int id = item.getItemId();
//            if (id == R.id.action_copy) {
//                //copy ayahs
//                copySelectedAyahs();
//                mode.finish();
//                return true;
//            }
//            return false;
//        }
//
//        @Override
//        public void onDestroyActionMode(ActionMode mode) {
//            adapter.clearSelections();
//            actionMode = null;
//        }
//    }

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
      //  setHasOptionsMenu(true);


        refreshTable(translation);

        View view = inflater.inflate(R.layout.ayahs_fragment, container, false);
        recycler = view.findViewById(R.id.recycler_ayahs);
        fastScroller = view.findViewById(R.id.fastscroll);
        adapter = new RecycleAdapter();


        adapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onItemClick(View view, Ayah obj, int pos) {
                if (adapter.getSelectedItemCount() > 0) {
                    enableActionMode(pos);
                } else {

                    //todo dialog
                }
            }

            @Override
            public void onItemLongClick(View view, Ayah obj, int pos) {
                enableActionMode(pos);
            }
        });
        grid = new GridLayoutManager(view.getContext(), 1);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(grid);

        recycler.setAdapter(adapter);
        //actionModeCallback = new ActionModeCallback();

        fastScroller.setRecyclerView(recycler);
        surahViewModel.getSurahs().observe(getViewLifecycleOwner(), new Observer<List<Surah>>() {
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

        viewModel.getAyahs(surahNumber, table).observe(getViewLifecycleOwner(), observer);


        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                refreshTable(-1);

                if (getView() != null)
                    viewModel.getAyahs(surahNumber, table).observe(getViewLifecycleOwner(), observer);
            }
        };
        pref.registerOnSharedPreferenceChangeListener(prefListener);
        return view;
    }

    public void refreshTable(int translation) {
        if (translation == -1) {
            table = tableNames[pref.getInt("main", 1)];
        } else {
            table = tableNames[translation];
        }
    }

    public void update(int translation) {
        refreshTable(translation);
        viewModel.getAyahs(surahNumber, table).observe(this, observer);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().post("empty");
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> implements SectionTitleProvider {
        List<Ayah> ayahs = new ArrayList<>();

        OnClickListener onClickListener = null;
        private SparseBooleanArray selected_items;
        private int current_selected_idx = -1;

        public RecycleAdapter() {
            selected_items = new SparseBooleanArray();
        }

//        private void displayImage(ViewHolder holder, Inbox inbox) {
//            if (inbox.image != null) {
//                holder.image.setImageResource(inbox.image);
//                holder.image.setColorFilter(null);
//                holder.image_letter.setVisibility(View.GONE);
//            } else {
//                holder.image.setImageResource(R.drawable.shape_circle);
//                holder.image.setColorFilter(inbox.color);
//                holder.image_letter.setVisibility(View.VISIBLE);
//            }
//        }

        private void toggleCheckedIcon(ViewHolder holder, int position) {
            if (selected_items.get(position, false)) {

                if (current_selected_idx == position) resetCurrentIndex();
            } else {

                if (current_selected_idx == position) resetCurrentIndex();
            }
        }

        public String getSelectedAyahText(int position) {

            Ayah a = ayahs.get(position);

            String text = a.getAyahText().toString();
            text += "\n\t" + "Quran (" + a.getSuraID() + ":" + a.getVerseID() + ")\n\n";
            resetCurrentIndex();

            return text;
        }

        public Ayah getSelectedAyah(int position) {

            return ayahs.get(position);

        }

        public void toggleSelection(int pos) {
            current_selected_idx = pos;
            if (selected_items.get(pos, false)) {
                selected_items.delete(pos);
            } else {
                selected_items.put(pos, true);
            }
            notifyItemChanged(pos);
        }

        public Ayah getItem(int position) {
            return ayahs.get(position);
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        public void clearSelections() {
            selected_items.clear();
            notifyDataSetChanged();
        }

        public int getSelectedItemCount() {
            return selected_items.size();
        }

        public List<Integer> getSelectedItems() {
            List<Integer> items = new ArrayList<>(selected_items.size());
            for (int i = 0; i < selected_items.size(); i++) {
                items.add(selected_items.keyAt(i));
            }
            return items;
        }

        private void resetCurrentIndex() {
            current_selected_idx = -1;
        }


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

            if (table == tableNames[0]) {
                holder.ayah.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                holder.ayah.setTypeface(Main.regular);
            } else {
                holder.ayah.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                holder.ayah.setTypeface(Main.bold);
            }

            String text = (position + 1) + ". " + ayah.getAyahText();
            if (ayah.getStar() == 1)
                text = "★" + text;
            holder.ayah.setText(text);


            final Ayah x = ayahs.get(position);


            holder.lyt_parent.setActivated(selected_items.get(position, false));

            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (actionMode == null) {
                        Ayah currentAyah = RecycleAdapter.this.ayahs.get(position);
                        final ArrayList<Ayah> ayahs = new ArrayList<>();
                        AyahDialog dialog = new AyahDialog(getContext(), currentSurah, currentAyah, viewModel, getChosenBooks(), getTranslations());
                        final RelativeLayout layout = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.ayah_long_press_dialog, null);
                        dialog.setCustomView(layout);
                        dialog.show();
                        return;
                    }

                    onClickListener.onItemClick(v, x, position);
                }
            });

            holder.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onClickListener == null) return false;
                    onClickListener.onItemLongClick(v, x, position);
                    return true;
                }
            });

            toggleCheckedIcon(holder, position);

        }

        @Override
        public int getItemCount() {
            return ayahs.size();
        }

        @Override
        public String getSectionTitle(int position) {
            return ayahs.get(position).getVerseID().toString();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView ayah;
            public View lyt_parent;

            ViewHolder(final View itemView) {
                super(itemView);
                ayah = itemView.findViewById(R.id.ayah_text);
                lyt_parent = (View) itemView.findViewById(R.id.card);
            }

//            @Override
//            public void onClick(View v) {
//                Ayah currentAyah = RecycleAdapter.this.ayahs.get(getAdapterPosition());
//                // final ArrayList<Ayah> ayahs = new ArrayList<>();
//                AyahDialog dialog = new AyahDialog(getContext(), currentSurah, currentAyah, viewModel, getChosenBooks(), getTranslations());
//                final RelativeLayout layout = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.ayah_long_press_dialog, null);
//                dialog.setCustomView(layout);
//                dialog.show();
//            }
        }
    }


    public interface OnClickListener {
        void onItemClick(View view, Ayah obj, int pos);

        void onItemLongClick(View view, Ayah obj, int pos);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(Just event) {

        if (actionMode != null)
            actionMode.finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


}

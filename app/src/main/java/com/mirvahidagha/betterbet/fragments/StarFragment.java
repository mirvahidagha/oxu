package com.mirvahidagha.betterbet.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;
import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.StarredAyah;
import com.mirvahidagha.betterbet.Entities.Surah;
import com.mirvahidagha.betterbet.Others.MyData;
import com.mirvahidagha.betterbet.Others.TabFragment;
import com.mirvahidagha.betterbet.R;
import com.mirvahidagha.betterbet.ViewModels.AyahViewModel;
import com.mirvahidagha.betterbet.ViewModels.StarredViewModel;
import com.mirvahidagha.betterbet.ViewModels.SurahViewModel;
import com.mirvahidagha.betterbet.dialog.AyahDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class StarFragment extends TabFragment {

    StarredViewModel starredViewModel;
    RecyclerView recyclerView;
    SharedPreferences pref;
    String[] translations;
    private ActionMode actionMode;
    RecycleAdapter adapter;

    public StarFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_star, container, false);
        //GridLayoutManager grid = new GridLayoutManager(getActivity(), 2);
        recyclerView = rootView.findViewById(R.id.recycler_ayahs);
       // setHasOptionsMenu(true);
        StaggeredGridLayoutManager grid = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(grid);
        recyclerView.setHasFixedSize(true);
        adapter = new RecycleAdapter();
        recyclerView.setAdapter(adapter);

        starredViewModel = ViewModelProviders.of(this).get(StarredViewModel.class);

        starredViewModel.getStarredAyahs().observe(getViewLifecycleOwner(), new Observer<List<StarredAyah>>() {
            @Override
            public void onChanged(List<StarredAyah> starredAyahs) {
                adapter.setAyahs(starredAyahs);
            }
        });


        adapter.setOnClickListener(new StarFragment.OnClickListener() {
            @Override
            public void onItemClick(View view, StarredAyah obj, int pos) {
                if (adapter.getSelectedItemCount() > 0) {
                    enableActionMode(pos);
                } else {

                    //todo dialog


                }
            }

            @Override
            public void onItemLongClick(View view, StarredAyah obj, int pos) {
                enableActionMode(pos);
            }
        });
        translations = getContext().getResources().getStringArray(R.array.translations);
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

    private void copySelectedAyahs() {
        StringBuilder copiedAyahs = new StringBuilder();
        List<Integer> selectedItemPositions = adapter.getSelectedItems();
        for (int i = 0; i <= selectedItemPositions.size() - 1; i++) {
            copiedAyahs.append(adapter.getSelectedAyahText(selectedItemPositions.get(i)));
        }

        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text label", copiedAyahs);
        clipboard.setPrimaryClip(clip);

        Snackbar.make(getView(), selectedItemPositions.size()+" ayə kopyalandı.", Snackbar.LENGTH_SHORT).show();
    }


    private void unstarSelected() {

        List<Integer> arr = adapter.getSelectedItems();
        List<StarredAyah> starred = adapter.getAyahs();

        StarredAyah[] starredAyahs = new StarredAyah[arr.size()];

        for (int i = 0; i <= arr.size() - 1; i++) {

            starredAyahs[i] = starred.get(arr.get(i));

        }


        starredViewModel.deleteStarredAyahs(starredAyahs);
        Snackbar.make(getView(), arr.size()+" ayə silindi.", Snackbar.LENGTH_SHORT).show();
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


    private void enableActionMode(int position) {




        if (actionMode == null) {

            actionMode = ((AppCompatActivity) Objects.requireNonNull(getActivity())).startSupportActionMode(new ActionMode.Callback() {


                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.menu_starred_action, menu);
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
                    } else if (id == R.id.action_delete) {
                        unstarSelected();
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


    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
        List<StarredAyah> ayahs = new ArrayList<>();


        StarFragment.OnClickListener onClickListener = null;
        private SparseBooleanArray selected_items;
        private int current_selected_idx = -1;

        public RecycleAdapter() {
            selected_items = new SparseBooleanArray();
        }

        @Override
        public RecycleAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.starred_ayah_item, parent, false);
            return new RecycleAdapter.ViewHolder(view);
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

        public void setAyahs(List<StarredAyah> ayahs) {
            this.ayahs = ayahs;
            notifyDataSetChanged();
        }

        public List<StarredAyah> getAyahs() {
            return ayahs;
        }

        public String getSelectedAyahText(int position) {

            StarredAyah a = ayahs.get(position);

            String text = a.getAyahText();
            text += "\n\t" + "Quran(" + a.getSuraID() + ":" + a.getVerseID() + ")\n\tTərcümə: "
                    +translations[a.getTableId()]+"\n\n";
            resetCurrentIndex();

            return text;
        }

        public StarredAyah getSelectedAyah(int position) {

            return ayahs.get(position);

        }

        private void toggleCheckedIcon(StarFragment.RecycleAdapter.ViewHolder holder, int position) {
            if (selected_items.get(position, false)) {

                if (current_selected_idx == position) resetCurrentIndex();
            } else {

                if (current_selected_idx == position) resetCurrentIndex();
            }
        }

        @Override
        public void onBindViewHolder(final RecycleAdapter.ViewHolder holder, final int position) {

            StarredAyah currentAyah = ayahs.get(position);
            String[] translations = getResources().getStringArray(R.array.translations);
            // int currentTableIndex = pref.getInt("main", 1);
            holder.ayah.setText(currentAyah.getAyahText());
            String headerText = currentAyah.getSuraID() + " - " + currentAyah.getVerseID();
            holder.header.setText(headerText);
            holder.translator.setText(translations[currentAyah.getTableId()]);

//
//            holder.cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    StarredAyah ayah = ayahs.get(position);
//                    //  EventBus.getDefault().post(new MyData(ayah.getSuraID(), ayah.getVerseID() - 1));
//                }
//            });
//

            final StarredAyah x = ayahs.get(position);
            holder.cardView.setActivated(selected_items.get(position, false));

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (actionMode == null) {

                        //todo ayənin yerləşdiyi yerə get
                        EventBus.getDefault().post(new MyData(x.getSuraID(), x.getVerseID() - 1, x.getTableId()));
                        return;
                    }

                    StarredAyah starredAyah = ayahs.get(position);
                    onClickListener.onItemClick(v, starredAyah, position);
                }
            });

            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
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

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

            TextView ayah, header, translator;
            CardView cardView;

            ViewHolder(final View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.card);
                ayah = itemView.findViewById(R.id.ayah_text);
                header = itemView.findViewById(R.id.ayah_header);
                translator = itemView.findViewById(R.id.translator);
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


        public void setOnClickListener(StarFragment.OnClickListener onClickListener) {
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


    }


    public interface OnClickListener {
        void onItemClick(View view, StarredAyah obj, int pos);

        void onItemLongClick(View view, StarredAyah obj, int pos);
    }


    @Override
    public void search(String text) {
        Toast.makeText(getActivity(), "Axtarış işləmir", Toast.LENGTH_SHORT).show();
    }

}

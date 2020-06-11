package com.mirvahidagha.betterbet.fragments;


import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mirvahidagha.betterbet.Activities.Main;
import com.mirvahidagha.betterbet.Others.AlphabetSurahOrder;
import com.mirvahidagha.betterbet.Others.DBHelper;
import com.mirvahidagha.betterbet.Entities.Surah;
import com.mirvahidagha.betterbet.Others.MyData;
import com.mirvahidagha.betterbet.Others.OriginalSurahOrder;
import com.mirvahidagha.betterbet.Others.SurahNazilOrder;
import com.mirvahidagha.betterbet.Others.TabFragment;
import com.mirvahidagha.betterbet.R;
import com.mirvahidagha.betterbet.ViewModels.SurahViewModel;
import com.mirvahidagha.betterbet.dialog.BetterDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SurahsFragment extends TabFragment {
    public RecyclerView recyclerView;

    SurahViewModel viewModel;
 //   SearchView searchView;
    RecycleAdapter adapter;
    short cycle = 2;
  //  MenuItem menuItem;
    ArrayList<Surah> surahs = new ArrayList<>();

    public SurahsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
      //  setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.quran, container, false);
        DBHelper.copyDatabase(view.getContext(), "quran.db");

        GridLayoutManager grid = new GridLayoutManager(getActivity(), 2);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(grid);
        adapter = new RecycleAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(SurahViewModel.class);
        viewModel.getSurahs().observe(getViewLifecycleOwner(), new Observer<List<Surah>>() {
            @Override
            public void onChanged(List<Surah> surahList) {
                surahs.clear();
                surahs.addAll(surahList);
                adapter.setSurahs(surahs);
            }
        });

        return view;
    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void customEventReceived(String event) {
        recyclerView.setVisibility(event.equals("empty") ? View.VISIBLE : View.INVISIBLE);
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> implements Filterable {

        List<Surah> surahs = new ArrayList<>();
        List<Surah> filteredSurahs = new ArrayList<>();

        @NotNull
        @Override
        public RecycleAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_surah, parent, false);
            return new RecycleAdapter.ViewHolder(view);
        }

        void setSurahs(List<Surah> surahs) {
            this.surahs = surahs;
            this.filteredSurahs = surahs;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(final RecycleAdapter.ViewHolder holder, final int position) {
            //  holder.positionRecyclerItem = holder.getAdapterPosition();
            final Surah surah = filteredSurahs.get(position);


            holder.arab.setTypeface(Main.bold);
            holder.azeri.setTypeface(Main.bold);
            holder.meaning.setTypeface(Main.regular);
            holder.count.setTypeface(Main.regular);
            holder.number.setTypeface(Main.bold);

            holder.arab.setText(surah.getArab());
            holder.azeri.setText(surah.getId() + ". " + surah.getAzeri());
            holder.meaning.setText(surah.getMeaning());

//            String order = String.valueOf("");
//          holder.order.setText("Sırası: " + order);
            String count = String.valueOf(surah.getCount());
            String place = String.valueOf(surah.getPlace());
            String placeCount = String.format("%sdə nazil olub, %s ayədir.", place, count);

            holder.count.setText(placeCount);
            holder.number.setText(String.valueOf(position + 1));

            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_scale_animation);
            animation.setInterpolator(new AccelerateInterpolator());

            holder.cardView.setAnimation(animation);


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EventBus.getDefault().post(new MyData(surah.getId(), 0));

                }
            });

            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.custom_dialog, null);
                    BetterDialog betterDialog = new BetterDialog(getContext(), surah);
                    betterDialog.setCustomView(layout).show();
                    return false;
                }
            });

        }

        @Override
        public int getItemCount() {
            return filteredSurahs.size();
        }

        @Override
        public Filter getFilter() {

            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    String Key = constraint.toString();
                    if (Key.isEmpty()) {

                        filteredSurahs = surahs;

                    } else {
                        List<Surah> lstFiltered = new ArrayList<>();
                        for (Surah row : surahs) {

                            if (row.getAzeri().toLowerCase().contains(Key.toLowerCase())) {
                                lstFiltered.add(row);
                            }

                        }

                        filteredSurahs = lstFiltered;

                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredSurahs;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredSurahs = (List<Surah>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView arab, number, count, azeri, meaning, order;
            View view;
            CardView cardView;

            ViewHolder(final View itemView) {
                super(itemView);

                cardView = itemView.findViewById(R.id.card);
                arab = itemView.findViewById(R.id.sura_ar);
                azeri = itemView.findViewById(R.id.sura_az);
                count = itemView.findViewById(R.id.count_place);
                meaning = itemView.findViewById(R.id.sura_meaning);

                //   order = itemView.findViewById(R.id.sura_order);
                number = itemView.findViewById(R.id.number);
                view = itemView;

            }

        }
    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//
//        menuItem = menu.findItem(R.id.menu_action_surahs);
//
//        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//
//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EventBus.getDefault().post(true);
//            }
//        });
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//
//                EventBus.getDefault().post(false);
//                return false;
//            }
//        });
//
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater = Objects.requireNonNull(getActivity()).getMenuInflater();
        inflater.inflate(R.menu.menu_surahs, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_surahs_order) {

            switch (++cycle % 3) {
                case 0:
                    Collections.sort(surahs, new SurahNazilOrder());
                    adapter.setSurahs(surahs);
                    item.setIcon(getResources().getDrawable(R.drawable.ic_nazilolma));
                    break;
                case 1:
                    Collections.sort(surahs, new AlphabetSurahOrder());
                    adapter.setSurahs(surahs);
                    item.setIcon(getResources().getDrawable(R.drawable.ic_sort_by_abc));

                    break;
                case 2:
                    Collections.sort(surahs, new OriginalSurahOrder());
                    adapter.setSurahs(surahs);
                    item.setIcon(getResources().getDrawable(R.drawable.quran_order));

                    break;
            }

        }

        return super.onOptionsItemSelected(item);

    }

    int cycle() {
        return ++cycle % 3;
    }


    @Override
    public void search(String text) {
        adapter.getFilter().filter(text);
    }
}

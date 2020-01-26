package com.mirvahidagha.betterbet.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

import com.mirvahidagha.betterbet.Others.DBHelper;
import com.mirvahidagha.betterbet.Entities.Surah;
import com.mirvahidagha.betterbet.Others.MyData;
import com.mirvahidagha.betterbet.R;
import com.mirvahidagha.betterbet.ViewModels.SurahViewModel;
import com.mirvahidagha.betterbet.dialog.BetterDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SurahsFragment extends Fragment {
    public RecyclerView recyclerView;

    SurahViewModel viewModel;
    SearchView searchView;
    RecycleAdapter adapter;
    boolean visible;
    MenuItem menuItem;

    public SurahsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.quran, container, false);
        DBHelper.copyDatabase(view.getContext(), "quran.db");

        GridLayoutManager grid = new GridLayoutManager(getActivity(), 2);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(grid);
        adapter = new RecycleAdapter();
        recyclerView.setAdapter(adapter);
        viewModel = ViewModelProviders.of(this).get(SurahViewModel.class);
        viewModel.getSurahs().observe(this, new Observer<List<Surah>>() {
            @Override
            public void onChanged(List<Surah> surahs) {
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
    public void customEventReceived(MyData data) {
        EventBus.getDefault().post(2);
        assert getFragmentManager() != null;
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        AyahsFragment fragment = (new AyahsFragment()).getInstance(data.getSurahId(), data.getScrollPosition());
        trans.replace(R.id.quran_layout, fragment);
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        trans.addToBackStack(null);
        recyclerView.setVisibility(View.INVISIBLE);
        trans.commit();
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
            final View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_list, parent, false);
            return new RecycleAdapter.ViewHolder(view);
        }

        public void setSurahs(List<Surah> surahs) {
            this.surahs = surahs;
            this.filteredSurahs = surahs;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(final RecycleAdapter.ViewHolder holder, final int position) {
            //  holder.positionRecyclerItem = holder.getAdapterPosition();
            final Surah surah = filteredSurahs.get(position);

            holder.arab.setText(surah.getArab());
            holder.azeri.setText(surah.getAzeri());
            holder.meaning.setText(surah.getMeaning());

//            String order = String.valueOf("");
//          holder.order.setText("Sırası: " + order);

            holder.place.setText(surah.getPlace());
            String count = String.valueOf(surah.getCount());
            holder.count.setText(count);
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

            TextView arab, number, place, count, azeri, meaning, order;
            View view;
            CardView cardView;

            ViewHolder(final View itemView) {
                super(itemView);

                cardView = itemView.findViewById(R.id.card);
                arab = itemView.findViewById(R.id.sura_ar);
                azeri = itemView.findViewById(R.id.sura_az);
                count = itemView.findViewById(R.id.sura_count);
                meaning = itemView.findViewById(R.id.sura_meaning);
                place = itemView.findViewById(R.id.sura_place);
                //   order = itemView.findViewById(R.id.sura_order);
                number = itemView.findViewById(R.id.number);
                view = itemView;

            }

        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

            menuItem = menu.findItem(R.id.menu_action_surahs);

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

            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(true);
                }
            });
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {

                    EventBus.getDefault().post(false);
                    return false;
                }
            });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_surahs, menu);
    }

}

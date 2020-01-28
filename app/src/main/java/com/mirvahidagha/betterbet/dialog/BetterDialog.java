package com.mirvahidagha.betterbet.dialog;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.mirvahidagha.betterbet.Entities.Surah;
import com.mirvahidagha.betterbet.Others.MyData;
import com.mirvahidagha.betterbet.Others.RecyclerAyah;
import com.mirvahidagha.betterbet.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by vahid on 12/29/17.
 */

public class BetterDialog extends SweetAlertDialog {

    Context context;
    Surah surah;

    public BetterDialog(Context context, Surah surah) {
        super(context, R.style.alert_dialog_light);
        super.setTitleText(surah.getAzeri());
        this.context = context;
        this.surah = surah;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        copy.setVisibility(View.GONE);
        star.setVisibility(View.GONE);
    }

    @Override
    public SweetAlertDialog setCustomView(View view) {

        RecyclerAyah recycler = view.findViewById(R.id.recycler_dialog);
        RecycleAdapter adapter = new RecycleAdapter(surah.getCount());
        recycler.setHasFixedSize(true);
        GridLayoutManager grid = new GridLayoutManager(view.getContext(), 5);
        recycler.setLayoutManager(grid);
        recycler.setAdapter(adapter);
        return super.setCustomView(view);
    }

    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

        int count;
        RecycleAdapter(int count) {
            this.count = count;
        }

        @Override
        public RecycleAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.item_ayah, parent, false);
            return new RecycleAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecycleAdapter.ViewHolder holder, final int position) {


            holder.number.setTypeface(bold);
            holder.number.setText(String.valueOf(position + 1));

            holder.number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EventBus.getDefault().post(new MyData(surah.getId(), position));
                    //   Toast.makeText(context, surah.getId()+" " + (position+1), Toast.LENGTH_SHORT).show();
                    cancel();
                }
            });

        }

        @Override
        public int getItemCount() {
            return count;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            MaterialButton number;

            ViewHolder(final View itemView) {
                super(itemView);
                number = itemView.findViewById(R.id.ayah_number);
            }

        }
    }

}

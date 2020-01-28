package com.mirvahidagha.betterbet.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.Surah;
import com.mirvahidagha.betterbet.Others.RecyclerAyah;
import com.mirvahidagha.betterbet.R;
import com.mirvahidagha.betterbet.ViewModels.AyahViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class AyahDialog extends SweetAlertDialog implements View.OnClickListener {
    //  MaterialButton copy, star;
    RecyclerAyah recyclerView;
    Context context;
    AyahViewModel viewModel;
    Ayah ayah;
    ArrayList<Ayah> otherAyahs;
    ArrayList<String> tables, translations;
    GridLayoutManager grid;
    Surah surah;
    SharedPreferences pref;

    public AyahDialog(Context context, Surah surah, Ayah ayah, AyahViewModel viewModel, ArrayList<String> tables, ArrayList<String> translations) {
        super(context, R.style.alert_dialog_light);
        this.context = context;
        this.ayah = ayah;
        this.viewModel = viewModel;
        this.tables = tables;
        this.surah = surah;
        this.translations = translations;
        pref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otherAyahs = new ArrayList<>();
        DialogAdapter adapter = new DialogAdapter();
        Observer<Ayah> observer = new Observer<Ayah>() {
            @Override
            public void onChanged(Ayah ayahContent) {
                otherAyahs.add(ayahContent);
                adapter.updateItems(otherAyahs);
            }
        };

        for (LiveData<Ayah> ayahContentLiveData :
                viewModel.getAyahContent(tables, ayah.getSuraID(), ayah.getVerseID())) {

            ayahContentLiveData.observe((LifecycleOwner) context, observer);
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(grid);
        recyclerView.setAdapter(adapter);
        setOnClick(this::onClick);

        if (ayah.getStar() == 1)
            star.setText("Ulduzu sil");

        String titleOfDialog = surah.getAzeri() + " " + ayah.getVerseID();
        setTitleText(titleOfDialog);


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public SweetAlertDialog setCustomView(View view) {

        //  copy = view.findViewById(R.id.copy);
        //  star = view.findViewById(R.id.star);

        grid = new GridLayoutManager(context, 1);
        recyclerView = view.findViewById(R.id.recycler_other);

        //   copy.setOnClickListener(this);
        //  star.setOnClickListener(this);

        return super.setCustomView(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.copy:
                copyText(ayah.getAyahText().toString());
                cancel();
                Toast.makeText(context, "Ayə kopyalandı.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.star:
                starAyah(ayah.getId());
                cancel();
                break;

        }
    }


    private void starAyah(int number) {
        if (ayah.getStar() == 0)
            ayah.setStar(1);
        else ayah.setStar(0);
        viewModel.update(ayah);
    }

    private void copyText(String text) {

        text += "\n\n\t" + "Quran (" + ayah.getSuraID() + ":" + ayah.getVerseID() + ")";
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text label", text);
        clipboard.setPrimaryClip(clip);
    }

    public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.ViewHolder> {
        ArrayList<Ayah> arrayList = new ArrayList<>();

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.other_ayahs_item, parent, false);
            return new DialogAdapter.ViewHolder(view);
        }


        String header(int i) {

            return null;
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.ayah.setText(arrayList.get(position).getAyahText());
            holder.ayah.setTypeface(bold);
            if (position == 0) {
                holder.ayah.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                holder.ayah.setTypeface(regular);

            }

            holder.header.setText(translations.get(position));
            holder.header.setTypeface(light);
            holder.item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    copyText(arrayList.get(position).getAyahText().toString());
                    Toast.makeText(context, "Ayə kopyalandı.", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });


        }

         void updateItems(ArrayList<Ayah> ayahs) {
            arrayList = ayahs;
            Collections.sort(arrayList);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

         class ViewHolder extends RecyclerView.ViewHolder {
            TextView ayah, header;
            ConstraintLayout item;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ayah = itemView.findViewById(R.id.other_ayah);
                header = itemView.findViewById(R.id.translation_header);
                item = itemView.findViewById(R.id.card);
            }
        }
    }
}

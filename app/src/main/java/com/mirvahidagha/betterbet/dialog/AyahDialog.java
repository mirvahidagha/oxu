package com.mirvahidagha.betterbet.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.AyahContent;
import com.mirvahidagha.betterbet.R;
import com.mirvahidagha.betterbet.ViewModels.AyahViewModel;
import com.mirvahidagha.betterbet.fragments.AyahsFragment;

import java.util.ArrayList;
import java.util.List;

public class AyahDialog extends SweetAlertDialog implements View.OnClickListener {
    MaterialButton copy, star;
    RecyclerView recyclerView;
    Context context;
    AyahViewModel viewModel;
    Ayah ayah;
    ArrayList<AyahContent> otherAyahs;
    ArrayList<String> tables;
    GridLayoutManager   grid;

    public AyahDialog(Context context, Ayah ayah, AyahViewModel viewModel, ArrayList<String> tables) {
        super(context, R.style.alert_dialog_light);
        setTitleText();
        this.context = context;
        this.ayah = ayah;
        this.viewModel = viewModel;
        this.tables = tables;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otherAyahs = new ArrayList<>();

        DialogAdapter adapter = new DialogAdapter();
        Observer<AyahContent> observer = new Observer<AyahContent>() {
            @Override
            public void onChanged(AyahContent ayahContent) {
                otherAyahs.add(ayahContent);
                adapter.updateItems(otherAyahs);
            }
        };

        for (LiveData<AyahContent> ayahContentLiveData :
                viewModel.getAyahContent(tables, ayah.getSura(), ayah.getNumber())) {

            ayahContentLiveData.observe((LifecycleOwner) context, observer);
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(grid);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public SweetAlertDialog setCustomView(View view) {

        copy = view.findViewById(R.id.copy);
        star = view.findViewById(R.id.star);

         grid = new GridLayoutManager(context, 1);
        recyclerView = view.findViewById(R.id.recycler_other);

        copy.setOnClickListener(this);
        star.setOnClickListener(this);

        if(ayah.getStarred()==1)
        star.setText("Ulduzu sil");


        return super.setCustomView(view);
    }

    private void setTitleText() {
        String titleOfDialog = "Ayə üzərində əməliyyatlar";
        super.setTitleText(titleOfDialog);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.copy:
                 //  copyText();
                cancel();
                Toast.makeText(context, "Ayə kopyalandı.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.star:
                cancel();

                  starAyah(ayah.getId());
                break;

        }
    }

    void copyText(String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text label", text);
        clipboard.setPrimaryClip(clip);
    }

    void starAyah(int number) {
        if (ayah.getStarred() == 0)
            ayah.setStarred(1);
        else ayah.setStarred(0);
        viewModel.update(ayah);
    }

    public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.ViewHolder> {
        ArrayList<AyahContent> arrayList = new ArrayList<>();

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.other_ayahs_item, parent, false);
            return new DialogAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.ayah.setText(arrayList.get(position).getAyahText());

        }

        public void updateItems(ArrayList<AyahContent> ayahContents) {
            arrayList = ayahContents;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView ayah;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ayah = itemView.findViewById(R.id.other_ayah);
            }
        }
    }
}

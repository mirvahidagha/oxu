package com.mirvahidagha.betterbet.Search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Others.MyData;
import com.mirvahidagha.betterbet.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Typeface.BOLD;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> implements Filterable {

    Context mContext;
    List<NewsItem> mData;
    List<NewsItem> mDataFiltered;

    public NewsAdapter(Context mContext, List<NewsItem> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.mDataFiltered = mData;
    }

    public void setAdapterData(List<NewsItem> items) {
        mData = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layout = LayoutInflater.from(mContext).inflate(R.layout.item_news, viewGroup, false);
        return new NewsViewHolder(layout);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder newsViewHolder, final int position) {

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_animation);
        animation.setInterpolator(new AccelerateInterpolator());

        newsViewHolder.card.setAnimation(animation);
        newsViewHolder.tv_title.setText(mDataFiltered.get(position).getTitle() +
                " - " + mDataFiltered.get(position).getAyah().getNumber());

        newsViewHolder.tv_content.setText(mDataFiltered.get(position).getAyahContent().getAyahText());

        newsViewHolder.card.setOnClickListener(v -> {
            Ayah ayah = mDataFiltered.get(position).getAyah();
            EventBus.getDefault().post(new MyData(ayah.getSura(), ayah.getNumber() - 1));
        });
    }

    @Override
    public int getItemCount() {
        return mDataFiltered.size();
    }

     private CharSequence highlight(String search, String originalText) {
        if (search != null && !search.equalsIgnoreCase("")) {

            int start = originalText.indexOf(search);
            if (start < 0) {
                return originalText;
            } else {
                Spannable highlighted = new SpannableString(originalText);
                while (start >= 0) {
                    int spanStart = Math.min(start, originalText.length());
                    int spanEnd = Math.min(start + search.length(), originalText.length());

                    ForegroundColorSpan foreground = new ForegroundColorSpan(Color.RED);

                    BackgroundColorSpan background = new BackgroundColorSpan(Color.RED);

                    highlighted.setSpan(foreground, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    highlighted.setSpan(new StyleSpan(BOLD), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    start = originalText.indexOf(search, spanEnd);
                }
                return highlighted;
            }
        }
        return originalText;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String Key = constraint.toString();
                if (Key.isEmpty()) {
                    mDataFiltered = new ArrayList<>();
                  //  mDataFiltered = mData;
                } else {
                    List<NewsItem> lstFiltered = new ArrayList<>();
                    for (NewsItem row : mData) {

                        if (row.getAyahContent().getAyahText().toString().contains(Key)) {

                            row.getAyahContent().setAyahText(highlight(Key, row.getAyahContent().getAyahText().toString()));
                            lstFiltered.add(row);
                        }
                    }
                    mDataFiltered = lstFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDataFiltered = (List<NewsItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title, tv_content;
        CardView card;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            tv_title = itemView.findViewById(R.id.ayah_header);
            tv_content = itemView.findViewById(R.id.ayah_text);

        }

    }
}

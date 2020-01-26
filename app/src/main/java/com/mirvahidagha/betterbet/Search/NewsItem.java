package com.mirvahidagha.betterbet.Search;

import com.mirvahidagha.betterbet.Entities.Ayah;

public class NewsItem {


    String Title;

    Ayah ayah;

    public Ayah getAyah() {
        return ayah;
    }

    public NewsItem(String title, Ayah ayah, Ayah ayahContent) {
        Title = title;
        this.ayah = ayah;
        this.ayah = ayahContent;
    }

    public NewsItem() {
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setAyah(Ayah ayah) {
        this.ayah = ayah;
    }

    public String getTitle() {
        return Title;
    }

}

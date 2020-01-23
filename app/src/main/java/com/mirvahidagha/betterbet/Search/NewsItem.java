package com.mirvahidagha.betterbet.Search;

import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.AyahContent;

public class NewsItem {


    String Title;

    Ayah ayah;

    public AyahContent getAyahContent() {
        return ayahContent;
    }

    public NewsItem(String title, Ayah ayah, AyahContent ayahContent) {
        Title = title;
        this.ayah = ayah;
        this.ayahContent = ayahContent;
    }

    public void setAyahContent(AyahContent ayahContent) {
        this.ayahContent = ayahContent;
    }

    AyahContent ayahContent;

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

    public Ayah getAyah() {
        return ayah;
    }

}

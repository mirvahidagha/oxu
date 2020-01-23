package com.mirvahidagha.betterbet.Others;

import com.mirvahidagha.betterbet.Entities.Ayah;

import java.util.ArrayList;

public class MyData {

    public MyData(int surahId, int scrollPosition) {
        this.surahId = surahId;
        this.scrollPosition = scrollPosition;
    }

    public int getSurahId() {
        return surahId;
    }

    public void setSurahId(int surahId) {
        this.surahId = surahId;
    }

    public int getScrollPosition() {
        return scrollPosition;
    }

    public void setScrollPosition(int scrollPosition) {
        this.scrollPosition = scrollPosition;
    }

    private int surahId;
    private int scrollPosition;
}

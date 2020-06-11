package com.mirvahidagha.betterbet.Others;

public class MyData {

    public MyData(int surahId, int scrollPosition) {
        this.surahId = surahId;
        this.scrollPosition = scrollPosition;
        this.transtalion = -1;
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

    public MyData(int surahId, int scrollPosition, int translation) {
        this.surahId = surahId;
        this.scrollPosition = scrollPosition;
        this.transtalion = translation;
    }

    public int getTranstalion() {
        return transtalion;
    }

    public void setTranstalion(int transtalion) {
        this.transtalion = transtalion;
    }

    private int transtalion;
}

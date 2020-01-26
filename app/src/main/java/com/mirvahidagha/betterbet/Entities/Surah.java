package com.mirvahidagha.betterbet.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "surahs")
public class Surah {

    public String getAzeri() {
        return azeri;
    }

    public void setAzeri(String azeri) {
        this.azeri = azeri;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getArab() {
        return arab;
    }

    public void setArab(String arab) {
        this.arab = arab;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "sura_id")
    int id;
    @NotNull
    @ColumnInfo(name = "sura_ar")
    String arab;
    @NotNull
    @ColumnInfo(name = "sura_az")
    String azeri;
    @NotNull
    @ColumnInfo(name = "sura_meaning")
    String meaning;
    @NotNull
    @ColumnInfo(name = "ayah_count")
    int count;
    @NotNull
    @ColumnInfo(name = "surah_order")
    int order;
    @NotNull
    @ColumnInfo(name = "place")
    String place;
}

package com.mirvahidagha.betterbet.Entities;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "verses")
public class Ayah {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getStarred() {
        return starred;
    }

    public void setStarred(int starred) {
        this.starred = starred;
    }

    public int getSura() {
        return sura;
    }

    public void setSura(int sura) {
        this.sura = sura;
    }

    @PrimaryKey()
    int id;

    @ColumnInfo(name = "verseID")
    int number;

    @ColumnInfo(name = "starred", defaultValue = "0")
    int starred;

    @ColumnInfo(name = "suraID")
    int sura;

}

package com.mirvahidagha.betterbet.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "abdulbaki")

public class AyahContent {
    public Integer getSuraID() {
        return suraID;
    }

    public void setSuraID(Integer suraID) {
        this.suraID = suraID;
    }

    public Integer getVerseID() {
        return verseID;
    }

    public void setVerseID(Integer verseID) {
        this.verseID = verseID;
    }

    public CharSequence getAyahText() {
        return ayahText;
    }

    public void setAyahText(CharSequence ayahText) {
        this.ayahText = ayahText;
    }

    @ColumnInfo(name = "SuraID")
    Integer suraID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @PrimaryKey
    @ColumnInfo(name = "id")
    int id;
    @ColumnInfo(name = "VerseID")
    Integer verseID;
    @ColumnInfo(name = "AyahText")
    CharSequence ayahText;
}

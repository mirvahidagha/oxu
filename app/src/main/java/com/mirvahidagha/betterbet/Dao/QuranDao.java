package com.mirvahidagha.betterbet.Dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.mirvahidagha.betterbet.Activities.Main;
import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.Surah;

@androidx.room.Dao
public interface QuranDao {

    @Transaction
    @Query("SELECT * FROM surahs")
    public LiveData<List<Surah>> getSurahs();

    @Query("SELECT * FROM surahs where sura_id=:number")
    public LiveData<Surah> getSurah(int number);


    @RawQuery(observedEntities = Ayah.class)
    LiveData<Ayah> getAyah(SupportSQLiteQuery query);

    @RawQuery(observedEntities = Ayah.class)
    LiveData<List<Ayah>> getAyahContent(SupportSQLiteQuery query);

    @RawQuery(observedEntities = Ayah.class)
    LiveData<List<Ayah>> getStarredAyahs(SupportSQLiteQuery query);

    @RawQuery(observedEntities = Ayah.class)
    LiveData<List<Ayah>> getAllAyahs(SupportSQLiteQuery query);

    @RawQuery(observedEntities = Ayah.class)
    int updateAyah(SupportSQLiteQuery query);



}

package com.mirvahidagha.betterbet.Dao;

import android.database.sqlite.SQLiteQuery;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.AyahContent;
import com.mirvahidagha.betterbet.Entities.Surah;

@androidx.room.Dao
public interface QuranDao {

    @Update
    void updateAyah(Ayah ayah);

    @Query("Select * from verses where suraID=:surahNumber ORDER BY id ASC")
    LiveData<List<Ayah>> getAllAyahs(int surahNumber);

    @Query("Select * from verses ORDER BY id ASC")
    LiveData<List<Ayah>> getAllAyahs();

    @Query("Select * from verses where starred=:starred ORDER BY id ASC")
    LiveData<List<Ayah>> getStarredAyahs(int starred);

    @Transaction
    @Query("SELECT * FROM surahs")
    public LiveData<List<Surah>> getSurahs();

    @RawQuery(observedEntities = AyahContent.class)
    LiveData<AyahContent> getAyah(SupportSQLiteQuery query);

    @RawQuery(observedEntities = AyahContent.class)
    LiveData<List<AyahContent>> getAyahContent(SupportSQLiteQuery query);
}

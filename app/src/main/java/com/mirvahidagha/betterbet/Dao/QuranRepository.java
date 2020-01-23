package com.mirvahidagha.betterbet.Dao;

import android.app.Application;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.AyahContent;
import com.mirvahidagha.betterbet.Entities.Surah;

public class QuranRepository {
    private QuranDao quranDao;
    QuranDatabase database;
    LiveData<List<Surah>> allSurah;

    public QuranRepository(Application application) {
        database = QuranDatabase.getInstance(application);
        quranDao = database.quranDao();
        allSurah = quranDao.getSurahs();
    }

    public void update(Ayah ayah) {
        new UpdateAsyncTask(quranDao).execute(ayah);
    }

    public LiveData<List<Ayah>> getAllAyahs(int surahNumber) {

        return quranDao.getAllAyahs(surahNumber);
    }

    public LiveData<List<Ayah>> getAllAyahs() {

        return quranDao.getAllAyahs();
    }

    public LiveData<List<Ayah>> getStarredAyahs() {

        return quranDao.getStarredAyahs(1);
    }

    public LiveData<List<Surah>> getSurahs() {

        return allSurah;
    }

    public LiveData<AyahContent> getAyahContent(String tableName, int surahNumber, int ayahNumber) {
        String queryString = "Select * from " + tableName + " where SuraID =" + surahNumber + " and VerseID=" + ayahNumber;
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        return quranDao.getAyah(query);
    }

    public LiveData<List<AyahContent>> getAllAyahContent(String tableName) {
        String queryString = "Select * from " + tableName+" ORDER BY id ASC";
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        return quranDao.getAyahContent(query);
    }

    private static class UpdateAsyncTask extends AsyncTask<Ayah, Void, Void> {

        private QuranDao quranDao;

        private UpdateAsyncTask(QuranDao quranDao) {
            this.quranDao = quranDao;
        }

        @Override
        protected Void doInBackground(Ayah... ayahs) {
            quranDao.updateAyah(ayahs[0]);
            return null;
        }
    }


//    public MediatorLiveData<ArrayList> getOtherAyahs(ArrayList<LiveData<AyahContent>> sources) {
//        MediatorLiveData mergedSources = new MediatorLiveData();
//
//        return mergedSources;
//    }

    public ArrayList<LiveData<AyahContent>> getOtherAyahs(ArrayList<String> tables, int surahNumber, int ayahNumber) {
        ArrayList<LiveData<AyahContent>> list = new ArrayList<>();

        for (String table :
                tables) {
            list.add(getAyahContent(table, surahNumber, ayahNumber));
        }

        return null;
    }

}

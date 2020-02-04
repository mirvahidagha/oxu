package com.mirvahidagha.betterbet.Dao;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.Surah;
import com.mirvahidagha.betterbet.R;

public class QuranRepository {
    private QuranDao quranDao;
    QuranDatabase database;
    LiveData<List<Surah>> allSurah;
    Context context;
    SharedPreferences pref;

    public QuranRepository(Application application) {
        database = QuranDatabase.getInstance(application);
        quranDao = database.quranDao();
        allSurah = quranDao.getSurahs();
        context = application.getApplicationContext();
        pref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public void update(Ayah ayah) {

        String queryString = "update " + table() + " set star=" + Math.abs(ayah.getStar() - 1) + " where id=" + ayah.getId();
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);

        new UpdateAsyncTask(quranDao).execute(query);
    }




    public LiveData<List<Ayah>> getAllAyahs(int surahNumber) {
        String queryString = "select * from " + table() + " where SuraID=" + surahNumber;
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        return quranDao.getAllAyahs(query);
    }

    private String table() {
        String tables[] = context.getResources().getStringArray(R.array.table_names);
        return tables[pref.getInt("main", 1)];
    }

    public LiveData<List<Ayah>> getStarredAyahs() {
        String queryString = "select * from " + table() + " where star='1'";
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        return quranDao.getStarredAyahs(query);
    }

    public LiveData<List<Surah>> getSurahs() {

        return allSurah;
    }



    public LiveData<Surah> getSurah(int i) {
        return quranDao.getSurah(i);
    }

    public LiveData<Ayah> getAyahContent(String tableName, int surahNumber, int ayahNumber) {
        String queryString = "Select * from " + tableName + " where SuraID =" + surahNumber + " and VerseID=" + ayahNumber;
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        return quranDao.getAyah(query);
    }

    public LiveData<List<Ayah>> getAllAyahContent() {
        String queryString = "Select * from " + table() + " ORDER BY id ASC";
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        return quranDao.getAyahContent(query);
    }

    private static class UpdateAsyncTask extends AsyncTask<SimpleSQLiteQuery, Void, Void> {

        private QuranDao quranDao;

        private UpdateAsyncTask(QuranDao quranDao) {
            this.quranDao = quranDao;
        }

        @Override
        protected Void doInBackground(SimpleSQLiteQuery... query) {
            quranDao.updateAyah(query[0]);
            return null;
        }

    }

    public ArrayList<LiveData<Ayah>> getOtherAyahs(ArrayList<String> tables, int surahNumber, int ayahNumber) {
        ArrayList<LiveData<Ayah>> list = new ArrayList<>();

        for (String table :
                tables) {
            list.add(getAyahContent(table, surahNumber, ayahNumber));
        }

        return null;
    }

}

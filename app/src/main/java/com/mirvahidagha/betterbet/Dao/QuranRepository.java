package com.mirvahidagha.betterbet.Dao;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.StarredAyah;
import com.mirvahidagha.betterbet.Entities.SubjectWithIndexes;
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

    public LiveData<List<SubjectWithIndexes>> getSubjects(){

        return quranDao.getSubjects();
    }


    public void update(Ayah ayah) {

        String queryString = "update " + table() + " set star=" + Math.abs(ayah.getStar() - 1) + " where id=" + ayah.getId();
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);

        new UpdateAsyncTask(quranDao).execute(query);
    }


    public LiveData<List<Ayah>> getAllAyahs(int surahNumber, String table) {
        String queryString = "select * from " + table + " where SuraID=" + surahNumber;
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString);
        return quranDao.getAllAyahs(query);
    }

    private String table() {
        String tables[] = context.getResources().getStringArray(R.array.table_names);
        return tables[pref.getInt("main", 1)];
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

    public void insertStarredAyahs(StarredAyah...starredAyahs) {
     new InsertAsyncTask(quranDao).execute(starredAyahs);
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

    public LiveData<List<StarredAyah>> getStarredAyahs() {
        return quranDao.getStarred();
    }

    public void deleteStarredAyahs(StarredAyah...starredAyahs) {
         new DeleteAsyncTask(quranDao).execute(starredAyahs);
    }



    private static class InsertAsyncTask extends AsyncTask<StarredAyah, Void, Void> {

        private QuranDao quranDao;

        private InsertAsyncTask(QuranDao quranDao) {
            this.quranDao = quranDao;
        }

        @Override
        protected Void doInBackground(StarredAyah... starredAyahs) {
            quranDao.insertStarredAyahs(starredAyahs);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<StarredAyah, Void, Void> {

        private QuranDao quranDao;

        private DeleteAsyncTask(QuranDao quranDao) {
            this.quranDao = quranDao;
        }

        @Override
        protected Void doInBackground(StarredAyah... starredAyahs) {
            quranDao.deleteStarred(starredAyahs);
            return null;
        }
    }

}

package com.mirvahidagha.betterbet.Dao;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mirvahidagha.betterbet.Entities.Ayah;
import com.mirvahidagha.betterbet.Entities.Index;
import com.mirvahidagha.betterbet.Entities.StarredAyah;
import com.mirvahidagha.betterbet.Entities.Subject;
import com.mirvahidagha.betterbet.Entities.Surah;

@Database(entities = {Surah.class, Ayah.class, StarredAyah.class, Subject.class, Index.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class QuranDatabase extends RoomDatabase {

    private static QuranDatabase instance;

    public abstract QuranDao quranDao();

    public static synchronized QuranDatabase getInstance(Context context) {

        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    QuranDatabase.class,
                    "quran.db")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }

        return instance;
    }

    private static Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new CopyDatabase().execute();
        }
    };

    private static class CopyDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }

}

package com.mirvahidagha.betterbet.Others;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {

    private final static String DB_PATH = "/data/data/com.mirvahidagha.betterbet/databases/";


    String dbName;
    Context context;

    File dbFile;

    public DBHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, dbName, factory, version);
        this.context = context;
        this.dbName = dbName;
        dbFile = new File(DB_PATH + dbName);
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {

        if (!dbFile.exists()) {
            SQLiteDatabase db = super.getWritableDatabase();
            // copyDataBase(db.getPath());
            DBHelper.copyDatabase(context, dbName);
        }
        return super.getWritableDatabase();
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        if (!dbFile.exists()) {
            SQLiteDatabase db = super.getReadableDatabase();
            //  copyDataBase(db.getPath());
            DBHelper.copyDatabase(context, dbName);
        }
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void copyDatabase(final Context ctx, String dbName) {
        if (ctx != null) {
            File f = ctx.getDatabasePath(dbName);
            if (!f.exists()) {

                // check databases exists
                if (!f.getParentFile().exists())
                    f.getParentFile().mkdir();

                try {
                    InputStream in = ctx.getAssets().open(dbName);
                    OutputStream out = new FileOutputStream(f.getAbsolutePath());

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                    in.close();
                    out.close();

                } catch (Exception ex) {

                }
            }
        }
    }

}
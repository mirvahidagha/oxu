package com.mirvahidagha.betterbet.Others;

import android.app.Application;
import android.content.Context;

/**
 * Created by agha on 11/29/17.
 */

public class MyApp extends Application {

    private Context context;

    public Context getContext() {
        return context;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }
}

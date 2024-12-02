package com.example.eventlotterysystem;

import android.app.Application;
import android.content.Context;


/**
 * Custom Application class for maintaining global application state.
 */
public class MyApplication extends Application {
    private static MyApplication instance;

    /**
     * Called when the application is starting, before any other application objects have been created.
     * This method sets the instance of this application.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * Returns the application context.
     *
     * @return The application context.
     */
    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}


package com.etna.mypictionis;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class FireApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (FirebaseApp.getApps(this).isEmpty()) {

        }
    }


}

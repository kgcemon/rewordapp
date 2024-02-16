package com.app.earningpoints;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.app.earningpoints.util.Session;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.startapp.sdk.adsbase.StartAppAd;


public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    @SuppressLint("StaticFieldLeak")
    public static MyApplication instance;
    Activity a;
    Session pref;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        try {
            FirebaseApp.initializeApp(getApplicationContext());
            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
            firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());
        } catch (Exception ignored) {
        }
        try {
            StartAppAd.disableSplash();
        }catch (Exception e){}
        nightmode();
    }

    private void nightmode() {
        pref=new Session(this);
        if(pref.isNightModeOn()!=null) {
            if (pref.isNightModeOn().equalsIgnoreCase("yes")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }



    public static synchronized MyApplication getInstance ()
    {
        return instance;
    }

    public static boolean hasNetwork ()
    {
        return instance.checkIfHasNetwork();
    }

    public boolean checkIfHasNetwork()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        a=activity;
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        a=activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        a=activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) { }
}

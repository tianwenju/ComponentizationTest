package com.delta.faultprocessing;

import android.app.Application;
import android.util.Log;

import com.delta.ameslibs.TTSManager;
import com.delta.commonlibs.ApplicationAsLibrary;

/**
 * @description :
 * @autHor :  V.Wenju.Tian
 * @date : 2017/4/24 16:16
 */


public class FaultApplicaiton extends Application implements ApplicationAsLibrary {
    private static final String TAG = "FaultApplicaiton";

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: ");
        TTSManager instance = TTSManager.getInstance();
        instance.speak("this is fault");
        super.onCreate();
    }

    @Override
    public void onCreateAsLibrary(Application application) {
        Log.e(TAG, "onCreateAsLibrary: " + application.getPackageName());
    }
}

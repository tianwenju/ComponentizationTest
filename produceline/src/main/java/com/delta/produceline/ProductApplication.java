package com.delta.produceline;

import android.app.Application;
import android.util.Log;

import com.delta.ameslibs.TTSManager;
import com.delta.commonlibs.ApplicationAsLibrary;

/**
 * @description :
 * @autHor :  V.Wenju.Tian
 * @date : 2017/4/24 16:49
 */


public class ProductApplication extends Application implements ApplicationAsLibrary {

    private static final String TAG = "ProductApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        TTSManager ttsManager =TTSManager.getInstance();
        ttsManager.speak("this is product");
        Log.e(TAG, "onCreate: ");
    }

    @Override
    public void onCreateAsLibrary(Application application) {
        Log.e(TAG, "onCreateAsLibrary: " + application.getPackageName());
    }
}

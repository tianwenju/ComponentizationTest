package com.delta.ameslibs;

/**
 * @description :
 * @autHor :  V.Wenju.Tian
 * @date : 2017/4/24 15:33
 */


public class TTSManager {

    private TTSManager() {

    }

    private static class SingleTonHolder {
        private static final TTSManager instance = new TTSManager();
    }

    public static final TTSManager getInstance() {
        return SingleTonHolder.instance;
    }
    public void speak(String message){
        System.out.println(message);
    }
}

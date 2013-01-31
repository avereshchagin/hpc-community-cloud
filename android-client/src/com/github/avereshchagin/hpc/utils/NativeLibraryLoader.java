package com.github.avereshchagin.hpc.utils;

import android.util.Log;

public class NativeLibraryLoader {
    
    private static final String TAG = "NativeLibraryLoader";

    private NativeLibraryLoader() {
    }

    public static NativeLibraryLoader fromPath(String path) {
        try {
            System.load(path);
            return new NativeLibraryLoader();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return null;
    }

    public native String getString();
}

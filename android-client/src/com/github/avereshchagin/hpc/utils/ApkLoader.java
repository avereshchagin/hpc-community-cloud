package com.github.avereshchagin.hpc.utils;

import android.content.Context;
import dalvik.system.DexClassLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public class ApkLoader {

    private static final String TAG = "ApkLoader";

    public static Method loadEntryPoint(String apkPath, Context context)
            throws IOException, ClassNotFoundException, NoSuchMethodException {
        File apkFile = new File(apkPath);
        if (!apkFile.exists() || !apkFile.isFile()) {
            throw new IOException("File " + apkPath + " does not exist");
        }

        String entryPointClassName = "com.github.avereshchagin.hpc.Application";

        File dexOutputDir = context.getDir("dex", 0);
        DexClassLoader classLoader = new DexClassLoader(apkPath, dexOutputDir.getPath(), null, ApkLoader.class.getClassLoader());
        Class<?> myClass = classLoader.loadClass(entryPointClassName);
        return myClass.getDeclaredMethod("main", String[].class);
    }
}

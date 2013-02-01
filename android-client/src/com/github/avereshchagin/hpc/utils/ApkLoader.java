package com.github.avereshchagin.hpc.utils;

import android.content.Context;
import android.util.Log;
import dalvik.system.DexClassLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

    public static String executeProgram(String path, Context context) {
        try {
            Method method = loadEntryPoint(path, context);
            Object[] array = {null};
            Object result = method.invoke(null, array);
            Log.d(TAG, method.toString());
            if (result instanceof String) {
                return (String) result;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return "";
    }
}

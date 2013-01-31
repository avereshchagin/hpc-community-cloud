package com.github.avereshchagin.hpc.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.github.avereshchagin.hpc.R;
import com.github.avereshchagin.hpc.utils.ApkLoader;
import com.github.avereshchagin.hpc.utils.NativeLibraryLoader;
import com.github.avereshchagin.hpc.utils.SystemParametersRetriever;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.show_button).setOnClickListener(this);
        findViewById(R.id.download_apk).setOnClickListener(this);
        findViewById(R.id.download_so).setOnClickListener(this);
    }

    private static void downloadFileTo(String source, String destination) {
        Log.d(TAG, "Download started");
        try {
            URL url = new URL(source);
            URLConnection connection = url.openConnection();
            connection.connect();
            int fileLength = connection.getContentLength();
            Log.d(TAG, "File length: " + fileLength);
            InputStream in = new BufferedInputStream(url.openStream());
            OutputStream out = new FileOutputStream(destination);
            byte[] data = new byte[1024];
            int count;
            while ((count = in.read(data)) != -1) {
                out.write(data, 0, count);
            }
            out.flush();
            out.close();
            in.close();
            Log.d(TAG, "Download successfully completed");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private static String runApkProgram(String path, Context context) {
        try {
            Method method = ApkLoader.loadEntryPoint(path, context);
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

    private String getHost() {
        View ipInput = findViewById(R.id.ip_input);
        String host = ((EditText) ipInput).getText().toString();
        if ("".equals(host)) {
            // using emulator host address
            host = "10.0.2.2";
        }
        return host;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.show_button:
                View parametersText = findViewById(R.id.parameters);
                ((TextView) parametersText).setText(
                        "CPU: " + Double.toString(SystemParametersRetriever.getCpuBogoMIPS()) +
                                "\n" + SystemParametersRetriever.getMemoryInfo());
                break;
            
            case R.id.download_apk:
                File storageDirectory = Environment.getExternalStorageDirectory();
                downloadFileTo("http://" + getHost() + "/sample-apk.apk", storageDirectory.getPath() + "/sample-apk.apk");
                String result = runApkProgram(storageDirectory.getPath() + "/sample-apk.apk", getApplicationContext());
                parametersText = findViewById(R.id.parameters);
                ((TextView) parametersText).setText(result);
                break;
            
            case R.id.download_so:
                String directory = "/data/data/com.github.avereshchagin.hpc";
                downloadFileTo("http://" + getHost() + "/library.so", directory + "/library.so");

                NativeLibraryLoader loader = NativeLibraryLoader.fromPath(directory + "/library.so");
                parametersText = findViewById(R.id.parameters);
                ((TextView) parametersText).setText(loader.getString());
                break;
        }
    }
}

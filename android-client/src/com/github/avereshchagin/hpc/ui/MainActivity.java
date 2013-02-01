package com.github.avereshchagin.hpc.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.github.avereshchagin.hpc.R;
import com.github.avereshchagin.hpc.utils.ApkLoader;
import com.github.avereshchagin.hpc.utils.NativeLibraryLoader;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final String APK_NAME = "sample-apk.apk";
    private static final String SO_NAME = "library.so";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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

    private String getHost() {
        View ipInput = findViewById(R.id.ip_input);
        String host = ((EditText) ipInput).getText().toString();
        if ("".equals(host)) {
            // using emulator host address
            host = "10.0.2.2";
        }
        return "http://" + host;
    }

    private void downloadApk() {
        File filesDir = getApplicationContext().getFilesDir();
        Log.d(TAG, "Files directory: " + filesDir.toString());
        if (!filesDir.exists() && !filesDir.mkdirs()) {
            Log.e(TAG, "Cannot create files directory: " + filesDir.toString());
            return;
        }
        String destinationPath = filesDir.getPath() + File.separator + APK_NAME;

        downloadFileTo(getHost() + File.separator + APK_NAME, destinationPath);
        String result = ApkLoader.executeProgram(destinationPath, getApplicationContext());
        TextView parametersText = (TextView) findViewById(R.id.parameters);
        parametersText.setText(result);
    }

    private void downloadSo() {
        File filesDir = getApplicationContext().getFilesDir();
        Log.d(TAG, "Files directory: " + filesDir.toString());
        if (!filesDir.exists() && !filesDir.mkdirs()) {
            Log.e(TAG, "Cannot create files directory: " + filesDir.toString());
            return;
        }
        String destinationPath = filesDir.getPath() + File.separator + SO_NAME;

        downloadFileTo(getHost() + File.separator + SO_NAME, destinationPath);
        NativeLibraryLoader loader = NativeLibraryLoader.fromPath(destinationPath);
        if (loader != null) {
            TextView parametersText = (TextView) findViewById(R.id.parameters);
            parametersText.setText(loader.getString());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.download_apk:
                downloadApk();
                break;

            case R.id.download_so:
                downloadSo();
                break;
        }
    }
}

package com.github.avereshchagin.hpc.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.github.avereshchagin.hpc.R;
import com.github.avereshchagin.hpc.utils.ApkLoader;
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

        View showButton = findViewById(R.id.show_button);
        showButton.setOnClickListener(this);
        View downloadButton = findViewById(R.id.download_button);
        downloadButton.setOnClickListener(this);
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
            case R.id.download_button:
                File storageDirectory = Environment.getExternalStorageDirectory();
                Log.d(TAG, storageDirectory.toString());
                try {
                    View ipInput = findViewById(R.id.ip_input);
                    String host = ((EditText) ipInput).getText().toString();
                    if ("".equals(host)) {
                        // using emulator host address
                        host = "10.0.2.2";
                    }
                    URL url = new URL("http://" + host + "/sample-apk.apk");
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    int fileLength = connection.getContentLength();
                    Log.d(TAG, "File length: " + fileLength);
                    InputStream in = new BufferedInputStream(url.openStream());
                    OutputStream out = new FileOutputStream(storageDirectory.getPath() + "/sample-apk.apk");
                    byte[] data = new byte[1024];
                    int count;
                    while ((count = in.read(data)) != -1) {
                        out.write(data, 0, count);
                    }
                    out.flush();
                    out.close();
                    in.close();
                    Log.d(TAG, "Download completed");
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }

                try {
                    Method method = ApkLoader.loadEntryPoint(storageDirectory.getPath() + "/sample-apk.apk",
                            getApplicationContext());
                    Object[] array = {null};
                    Object result = method.invoke(null, array);
                    parametersText = findViewById(R.id.parameters);
                    if (result instanceof String) {
                        ((TextView) parametersText).setText((String) result);
                    } else {
                        ((TextView) parametersText).setText("Epic fail!!1");
                    }
                    Log.d(TAG, method.toString());
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
        }
    }
}

package com.github.avereshchagin.hpc.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.github.avereshchagin.hpc.R;
import com.github.avereshchagin.hpc.client.Client;

import java.io.IOException;

public class AuthActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "AuthActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth);

        findViewById(R.id.button_login).setOnClickListener(this);
    }

    private void login() {
        EditText inputServer = (EditText) findViewById(R.id.input_server);
        EditText inputUsername = (EditText) findViewById(R.id.input_userneme);
        EditText inputPassword = (EditText) findViewById(R.id.input_password);
        Client client = null;
        try {
            String host = inputServer.getText().toString();
            if ("".equals(host)) {
                host = "10.5.22.103";
            }
            client = new Client(host, 2120);
            if (client.authorize(inputUsername.getText().toString(), inputPassword.getText().toString())) {
                Log.d(TAG, "Authorized");
                Intent intent = getIntent();
                intent.putExtra("result", true);
                setResult(RESULT_OK, intent);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Authorized with uid = " + client.getUid());
                builder.setPositiveButton("OK", null);
                builder.create().show();

                finish();
            } else {
                Log.d(TAG, "Authorization failed");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Authorization failed. Try again.");
                builder.setPositiveButton("OK", null);
                builder.create().show();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Unable to connect to the server.");
            builder.setPositiveButton("OK", null);
            builder.create().show();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                login();
                break;
        }
    }
}
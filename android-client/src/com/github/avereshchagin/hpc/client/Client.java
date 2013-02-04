package com.github.avereshchagin.hpc.client;

import android.util.Log;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Client {
    
    private static final String TAG = "Client";

    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private int uid = -1;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Log.d(TAG, "Connected to " + host + " : " + String.valueOf(port));
    }
    
    public boolean authorize(String username, String password) {
        Log.d(TAG, "Authorization...");
        Map<String, String> data = new HashMap<String, String>();
        data.put("Action", "Auth");
        data.put("UserName", username);
        data.put("UserPassword", password);
        try {
            writer.write(Protocol.encode(data));
            writer.flush();
            StringBuilder lines = new StringBuilder();
            String line;
            while (!"".equals(line = reader.readLine())) {
                lines.append(line).append('\n');    
            }
            lines.append('\n');
            Map<String, String> response = Protocol.decode(lines.toString());
            for (Map.Entry<String, String> entry : response.entrySet()) {
                Log.v(TAG, "Key: " + entry.getKey() + "; Value: " + entry.getValue());
            }
            String uidString = response.get("Uid");
            try {
                uid = Integer.parseInt(uidString);
                Log.d(TAG, "UID = " + uid);
            } catch (NumberFormatException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            if (uid >= 0) {
                return true;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    public int getUid() {
        return uid;
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}

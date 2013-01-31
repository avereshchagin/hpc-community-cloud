package com.github.avereshchagin.hpc.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemParametersRetriever {

    private static final String TAG = "SystemParametersRetriever";

    private static final Pattern MEM_TOTAL_PATTERN = Pattern.compile(".*MemTotal\\s*:\\s*([\\d\\.]+)\\s*kB");
    private static final Pattern MEM_FREE_PATTERN = Pattern.compile(".*MemFree\\s*:\\s*([\\d\\.]+)\\s*kB");
    private static final Pattern BOGOMIPS_PATTERN = Pattern.compile(".*BogoMIPS\\s*:\\s*([\\d\\.]+)");

    private static String executeCommand(String... command) {
        ProcessBuilder builder;
        try {
            builder = new ProcessBuilder(command);
            Process process = builder.start();
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                return sb.toString();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return "";
    }

    public static double getCpuBogoMIPS() {
        String text = executeCommand("/system/bin/cat", "/proc/cpuinfo");
        Matcher matcher = BOGOMIPS_PATTERN.matcher(text);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public static String getMemoryInfo() {
        String text = executeCommand("/system/bin/cat", "/proc/meminfo");

        int total = 0;
        int free = 0;
        Matcher matcher = MEM_TOTAL_PATTERN.matcher(text);
        if (matcher.find()) {
            try {
                total = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        matcher = MEM_FREE_PATTERN.matcher(text);
        if (matcher.find()) {
            try {
                free = Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return String.format("Total: %d\nFree: %d", total, free);
    }
}

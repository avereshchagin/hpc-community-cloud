package com.github.avereshchagin.hpc.client;

import java.util.HashMap;
import java.util.Map;

public class Protocol {
    
    public static Map<String, String> decode(String request) {
        Map<String, String> result = new HashMap<String, String>();
        String[] lines = request.split("\n");
        for (String line : lines) {
            if ("".equals(line)) {
                break;
            }
            int index = line.indexOf("=");
            if (index > 0) {
                result.put(line.substring(0, index), line.substring(index + 1));
            }
        }
        return result;
    }
    
    public static String encode(Map<String, String> data) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            result.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
        }
        result.append('\n');
        return result.toString();
    }

    public static void main(String[] args) {
        Map<String, String> result = new HashMap<String, String>();
        result.put("username", "root");
        result.put("password", "hash");
        String encoded = encode(result);
        System.out.println(encoded);
        
        Map<String, String> decoded = decode(encoded);
        for (Map.Entry<String, String> entry : decoded.entrySet()) {
            System.out.println("Key: " + entry.getKey() + " Value: " + entry.getValue());
        }
    }
}

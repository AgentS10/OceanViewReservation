package com.oceanview.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lightweight JSON builder and parser — no external library.
 * Used across all handlers for consistent JSON responses.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class JsonUtil {

    private JsonUtil() {}

    public static String success(String message) {
        return "{\"success\":true,\"message\":\"" + escape(message) + "\"}";
    }

    public static String success(String message, String dataKey, String dataValue) {
        return "{\"success\":true,\"message\":\"" + escape(message) + "\",\"" + dataKey + "\":" + dataValue + "}";
    }

    public static String error(String message) {
        return "{\"success\":false,\"message\":\"" + escape(message) + "\"}";
    }

    /** Parse a flat JSON object (string values only) into a Map. */
    public static Map<String, String> parseObject(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.isBlank()) return map;
        Pattern p = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
        Matcher m = p.matcher(json);
        while (m.find()) map.put(m.group(1), m.group(2));
        // Also capture numeric values
        Pattern pNum = Pattern.compile("\"([^\"]+)\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");
        Matcher mNum = pNum.matcher(json);
        while (mNum.find()) {
            if (!map.containsKey(mNum.group(1))) map.put(mNum.group(1), mNum.group(2));
        }
        return map;
    }

    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

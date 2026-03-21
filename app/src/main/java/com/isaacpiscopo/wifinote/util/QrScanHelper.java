package com.isaacpiscopo.wifinote.util;

import com.isaacpiscopo.wifinote.model.Network;

/**
 * Utility class for parsing standard WiFi QR code strings into {@link Network} objects.
 */
public final class QrScanHelper {

    /** Private constructor -- utility class, not instantiated. */
    private QrScanHelper() {}

    /**
     * Parses a standard WiFi QR string into a {@link Network} object.
     * Supports the format: {@code WIFI:S:<ssid>;T:<security>;P:<password>;;}
     *
     * @param wifiString the raw scanned string.
     * @return a populated {@link Network}, or {@code null} if the string is not a valid WiFi QR.
     */
    public static Network parseWifiQrString(String wifiString) {
        if (wifiString == null || !wifiString.startsWith("WIFI:")) {
            return null;
        }

        String ssid = extractField(wifiString, "S:");
        String password = extractField(wifiString, "P:");
        String security = extractField(wifiString, "T:");

        if (ssid == null || ssid.isEmpty()) {
            return null;
        }

        if (password == null) {
            password = "";
        }

        // Normalise security type to match app values
        if (security == null || security.equalsIgnoreCase("nopass")) {
            security = "OPEN";
        } else if (security.equalsIgnoreCase("WPA") || security.equalsIgnoreCase("WPA2")) {
            security = "WPA2";
        } else if (security.equalsIgnoreCase("WPA3")) {
            security = "WPA3";
        } else {
            security = "WPA2";
        }

        return new Network(ssid, password, security);
    }

    /**
     * Extracts a field value from a WiFi QR string by its key prefix.
     *
     * @param wifiString the full WiFi QR string.
     * @param key        the field key to search for (e.g. {@code "S:"}).
     * @return the extracted value, or {@code null} if the key is not present.
     */
    private static String extractField(String wifiString, String key) {
        int start = wifiString.indexOf(key);
        if (start == -1) {
            return null;
        }
        start += key.length();
        int end = wifiString.indexOf(';', start);
        if (end == -1) {
            end = wifiString.length();
        }
        return wifiString.substring(start, end).replace("\\;", ";")
                .replace("\\,", ",")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}

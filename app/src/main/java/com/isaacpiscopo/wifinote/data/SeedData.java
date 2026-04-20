package com.isaacpiscopo.wifinote.data;

import com.isaacpiscopo.wifinote.model.Network;

import java.util.List;

/**
 * Inserts example network records into the database on first launch.
 * Call {@link #seedIfEmpty(DbHelper)} from {@code MainActivity.onCreate}.
 */
public class SeedData {

    /** Private constructor — utility class, not instantiated. */
    private SeedData() {}

    /**
     * Inserts five sample networks if the database is empty.
     * Subsequent calls are no-ops.
     *
     * @param dbHelper an open {@link DbHelper} instance.
     */
    public static void seedIfEmpty(DbHelper dbHelper) {
        List<Network> existing = dbHelper.getAllNetworks();
        if (!existing.isEmpty()) {
            return;
        }

        dbHelper.insertNetwork(new Network("HomeNetwork",    "password123", "WPA2"));
        dbHelper.insertNetwork(new Network("OfficeWifi",     "office2024!", "WPA2"));
        dbHelper.insertNetwork(new Network("GuestNetwork",   "guest1234",   "WPA2"));
        dbHelper.insertNetwork(new Network("CafeHotspot",    "",            "OPEN"));
        dbHelper.insertNetwork(new Network("SecureNet5G",    "ultra$ecure", "WPA3"));
    }
}

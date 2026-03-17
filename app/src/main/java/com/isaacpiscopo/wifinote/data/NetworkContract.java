package com.isaacpiscopo.wifinote.data;

import android.provider.BaseColumns;

/**
 * Defines the schema constants for the networks table.
 * Follows the Contract pattern recommended in the Android developer documentation.
 */
public final class NetworkContract {

    /** Private constructor — this class is not instantiated. */
    private NetworkContract() {}

    /**
     * Defines column names for the networks table.
     * Extends {@link BaseColumns} to inherit the {@code _ID} primary key column.
     */
    public static final class NetworkEntry implements BaseColumns {

        /** The name of the networks table. */
        public static final String TABLE_NAME = "networks";

        /** Column for the WiFi network name (SSID). */
        public static final String COLUMN_SSID = "ssid";

        /** Column for the network password. */
        public static final String COLUMN_PASSWORD = "password";

        /**
         * Column for the security type.
         * Stored as a string: {@code "WPA2"}, {@code "WPA3"}, or {@code "OPEN"}.
         */
        public static final String COLUMN_SECURITY = "security";

        /** Column for the Unix timestamp (ms) when the record was created. */
        public static final String COLUMN_CREATED_AT = "created_at";
    }
}

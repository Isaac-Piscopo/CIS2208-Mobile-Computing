package com.isaacpiscopo.wifinote.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.isaacpiscopo.wifinote.data.NetworkContract.NetworkEntry;
import com.isaacpiscopo.wifinote.model.Network;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the local SQLite database for WifiNote.
 * Provides CRUD operations for {@link Network} records.
 */
public class DbHelper extends SQLiteOpenHelper {

    /** Current schema version. Increment when the schema changes. */
    public static final int DATABASE_VERSION = 1;

    /** File name for the on-device database. */
    public static final String DATABASE_NAME = "wifinote.db";

    private static final String SQL_CREATE_NETWORKS =
            "CREATE TABLE " + NetworkEntry.TABLE_NAME + " (" +
            NetworkEntry._ID          + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NetworkEntry.COLUMN_SSID  + " TEXT NOT NULL, " +
            NetworkEntry.COLUMN_PASSWORD   + " TEXT NOT NULL, " +
            NetworkEntry.COLUMN_SECURITY   + " TEXT NOT NULL, " +
            NetworkEntry.COLUMN_CREATED_AT + " INTEGER NOT NULL" +
            ")";

    private static final String SQL_DELETE_NETWORKS =
            "DROP TABLE IF EXISTS " + NetworkEntry.TABLE_NAME;

    /** Constructs the helper and opens or creates the database. */
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /** Creates the database schema on first run. */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_NETWORKS);
    }

    /** Drops and recreates all tables on a schema version upgrade. */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_NETWORKS);
        onCreate(db);
    }

    /**
     * Inserts a new network record into the database.
     *
     * @param network the {@link Network} to insert; its {@code id} field is ignored.
     * @return the row id of the newly inserted row, or {@code -1} on failure.
     */
    public long insertNetwork(Network network) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NetworkEntry.COLUMN_SSID,       network.getSsid());
        values.put(NetworkEntry.COLUMN_PASSWORD,   network.getPassword());
        values.put(NetworkEntry.COLUMN_SECURITY,   network.getSecurity());
        values.put(NetworkEntry.COLUMN_CREATED_AT, network.getCreatedAt());

        return db.insert(NetworkEntry.TABLE_NAME, null, values);
    }

    /**
     * Returns all network records ordered by creation date, newest first.
     *
     * @return a {@link List} of {@link Network} objects; empty if none exist.
     */
    public List<Network> getAllNetworks() {
        List<Network> networks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        // Query all columns, ordered newest first.
        Cursor cursor = db.query(
                NetworkEntry.TABLE_NAME,
                null,   // all columns
                null,   // no WHERE clause
                null,
                null,   // no GROUP BY
                null,   // no HAVING
                NetworkEntry.COLUMN_CREATED_AT + " DESC"
        );

        while (cursor.moveToNext()) {
            networks.add(networkFromCursor(cursor));
        }
        cursor.close();
        return networks;
    }

    /**
     * Returns a single network by its row id.
     *
     * @param id the database row id.
     * @return the matching {@link Network}, or {@code null} if not found.
     */
    public Network getNetworkById(long id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                NetworkEntry.TABLE_NAME,
                null,
                NetworkEntry._ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        Network network = null;
        if (cursor.moveToFirst()) {
            network = networkFromCursor(cursor);
        }
        cursor.close();
        return network;
    }

    /**
     * Updates an existing network record.
     *
     * @param network the {@link Network} to update; must have a valid {@code id}.
     * @return the number of rows affected (1 on success, 0 if not found).
     */
    public int updateNetwork(Network network) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NetworkEntry.COLUMN_SSID,     network.getSsid());
        values.put(NetworkEntry.COLUMN_PASSWORD, network.getPassword());
        values.put(NetworkEntry.COLUMN_SECURITY, network.getSecurity());

        return db.update(
                NetworkEntry.TABLE_NAME,
                values,
                NetworkEntry._ID + " = ?",
                new String[]{String.valueOf(network.getId())}
        );
    }

    /**
     * Deletes a single network record by row id.
     *
     * @param id the database row id to delete.
     * @return the number of rows deleted (1 on success, 0 if not found).
     */
    public int deleteNetwork(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(
                NetworkEntry.TABLE_NAME,
                NetworkEntry._ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    /**
     * Deletes all network records from the database.
     *
     * @return the number of rows deleted.
     */
    public int deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(NetworkEntry.TABLE_NAME, null, null);
    }

    /**
     * Constructs a {@link Network} from the current position of the given cursor.
     *
     * @param cursor an open cursor positioned at a valid row.
     * @return the populated {@link Network}.
     */
    private Network networkFromCursor(Cursor cursor) {
        long id       = cursor.getLong(cursor.getColumnIndexOrThrow(NetworkEntry._ID));
        String ssid   = cursor.getString(cursor.getColumnIndexOrThrow(NetworkEntry.COLUMN_SSID));
        String pass   = cursor.getString(cursor.getColumnIndexOrThrow(NetworkEntry.COLUMN_PASSWORD));
        String sec    = cursor.getString(cursor.getColumnIndexOrThrow(NetworkEntry.COLUMN_SECURITY));
        long created  = cursor.getLong(cursor.getColumnIndexOrThrow(NetworkEntry.COLUMN_CREATED_AT));
        return new Network(id, ssid, pass, sec, created);
    }
}

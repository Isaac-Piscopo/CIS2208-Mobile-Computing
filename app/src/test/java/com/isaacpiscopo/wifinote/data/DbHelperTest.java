package com.isaacpiscopo.wifinote.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.isaacpiscopo.wifinote.model.Network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Unit tests for {@link DbHelper} using Robolectric to provide an Android context.
 */
@RunWith(RobolectricTestRunner.class)
public class DbHelperTest {

    private DbHelper dbHelper;

    /** Opens a fresh in-memory database before each test. */
    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = new DbHelper(context);
        dbHelper.deleteAll();
    }

    /** Closes the database after each test. */
    @After
    public void tearDown() {
        dbHelper.close();
    }

    /** Verifies that a network inserted into the database can be retrieved by id. */
    @Test
    public void insertAndGetById_roundTrip() {
        Network network = new Network("TestNet", "pass1234", "WPA2");
        long id = dbHelper.insertNetwork(network);

        Network retrieved = dbHelper.getNetworkById(id);

        assertNotNull(retrieved);
        assertEquals("TestNet", retrieved.getSsid());
        assertEquals("pass1234", retrieved.getPassword());
        assertEquals("WPA2", retrieved.getSecurity());
    }

    /** Verifies that getAllNetworks returns one record after a single insert. */
    @Test
    public void getAllNetworks_returnsInsertedRecord() {
        dbHelper.insertNetwork(new Network("Net1", "abc12345", "WPA2"));

        assertEquals(1, dbHelper.getAllNetworks().size());
    }

    /** Verifies that getNetworkById returns null for a non-existent id. */
    @Test
    public void getNetworkById_nonExistentId_returnsNull() {
        Network result = dbHelper.getNetworkById(9999L);
        assertNull(result);
    }
}

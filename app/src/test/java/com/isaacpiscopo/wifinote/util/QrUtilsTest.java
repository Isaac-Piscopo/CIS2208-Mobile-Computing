package com.isaacpiscopo.wifinote.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.graphics.Bitmap;

import com.isaacpiscopo.wifinote.model.Network;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * Unit tests for {@link QrUtils} verifying ZXing QR encoder integration.
 * Runs on the JVM via Robolectric (Bitmap support requires Android context stubs).
 */
@RunWith(RobolectricTestRunner.class)
public class QrUtilsTest {

    /**
     * Verifies that encoding a valid network produces a non-null 512x512 bitmap.
     */
    @Test
    public void encodeWifiQr_validNetwork_returnsCorrectDimensions() {
        Network network = new Network("HomeNet", "pass1234", "WPA2");

        Bitmap qr = QrUtils.encodeWifiQr(network);

        assertNotNull("QR bitmap must not be null for a valid network", qr);
        assertEquals(512, qr.getWidth());
        assertEquals(512, qr.getHeight());
    }

    /**
     * Verifies that encoding a network with an empty SSID still produces a valid 512x512 bitmap.
     * ZXing encodes the resulting WIFI string without error even when the SSID field is empty,
     * so the method returns a bitmap rather than null.
     */
    @Test
    public void encodeWifiQr_emptySsid_returnsValidBitmap() {
        Network network = new Network("", "", "WPA2");

        Bitmap qr = QrUtils.encodeWifiQr(network);

        assertNotNull("QR bitmap must not be null even for an empty SSID", qr);
        assertEquals(512, qr.getWidth());
        assertEquals(512, qr.getHeight());
    }
}

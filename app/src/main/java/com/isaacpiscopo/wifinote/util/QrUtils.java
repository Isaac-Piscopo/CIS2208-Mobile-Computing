package com.isaacpiscopo.wifinote.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.isaacpiscopo.wifinote.model.Network;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for encoding WiFi network credentials as QR code bitmaps.
 */
public final class QrUtils {

    /** Standard WiFi QR code size in pixels. */
    private static final int QR_SIZE = 512;

    /** Private constructor -- utility class, not instantiated. */
    private QrUtils() {}

    /**
     * Encodes the given network's credentials as a 512x512 WiFi QR code bitmap.
     * The encoded string follows the standard {@code WIFI:S:<ssid>;T:<security>;P:<password>;;} format.
     *
     * @param network the {@link Network} to encode.
     * @return a {@link Bitmap} containing the QR code, or {@code null} on encoding failure.
     */
    public static Bitmap encodeWifiQr(Network network) {
        String wifiString = buildWifiString(network);

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);

        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(wifiString, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hints);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixels[y * width + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;

        } catch (WriterException e) {
            return null;
        }
    }

    /**
     * Builds the standard WiFi QR string for the given network.
     *
     * @param network the {@link Network} to encode.
     * @return the formatted WiFi QR string.
     */
    private static String buildWifiString(Network network) {
        String ssid = escapeSpecialChars(network.getSsid());
        String password = escapeSpecialChars(network.getPassword());
        String security = network.getSecurity().equals("OPEN") ? "nopass" : network.getSecurity();

        return "WIFI:S:" + ssid + ";T:" + security + ";P:" + password + ";;";
    }

    /**
     * Escapes characters that have special meaning in the WiFi QR string format.
     *
     * @param value the raw string to escape.
     * @return the escaped string.
     */
    private static String escapeSpecialChars(String value) {
        return value
                .replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace("\"", "\\\"");
    }
}

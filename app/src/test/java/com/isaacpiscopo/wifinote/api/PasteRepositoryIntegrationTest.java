package com.isaacpiscopo.wifinote.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import org.junit.Test;

import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Integration test for {@link PasteRepository} against the live paste.rs endpoint.
 *
 * <p>This test requires network access and is automatically skipped when offline --
 * it uses {@code Assume.assumeTrue} so that {@code ./gradlew testDebugUnitTest}
 * always passes in CI or offline environments.
 */
public class PasteRepositoryIntegrationTest {

    /**
     * Returns true if the paste.rs host is reachable within two seconds.
     */
    private boolean isNetworkAvailable() {
        try {
            InetAddress addr = InetAddress.getByName("paste.rs");
            return !addr.equals(InetAddress.getByName("0.0.0.0")) && addr.isReachable(2000);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Posts a short text payload to the live paste.rs API and verifies the
     * response URL begins with {@code https://paste.rs/}.
     *
     * <p>Skipped automatically when paste.rs is unreachable.
     */
    @Test
    public void livePost_returns201AndPasteUrl() throws InterruptedException {
        assumeTrue("paste.rs not reachable -- skipping live integration test",
                isNetworkAvailable());

        PasteRepository repository = new PasteRepository("https://paste.rs/");
        CountDownLatch latch = new CountDownLatch(1);
        String[] result = {null};

        repository.share("WiFiNote integration test " + System.currentTimeMillis(),
                new PasteRepository.ShareCallback() {
                    @Override
                    public void onSuccess(String url) {
                        result[0] = url;
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(String message) {
                        result[0] = "FAILURE: " + message;
                        latch.countDown();
                    }
                });

        latch.await(10, TimeUnit.SECONDS);
        assertTrue("Expected paste URL, got: " + result[0],
                result[0] != null && result[0].startsWith("https://paste.rs/"));
    }
}

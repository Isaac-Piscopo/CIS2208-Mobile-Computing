package com.isaacpiscopo.wifinote.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

/**
 * Unit tests for {@link PasteRepository} using {@link MockWebServer}.
 */
public class PasteRepositoryTest {

    private MockWebServer server;
    private PasteRepository repository;

    /** Starts the mock server and creates a repository pointed at it. */
    @Before
    public void setUp() throws Exception {
        server     = new MockWebServer();
        server.start();
        repository = new PasteRepository(server.url("/").toString());
    }

    /** Shuts down the mock server after each test. */
    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    /**
     * Verifies that a 201 response body is delivered to {@link PasteRepository.ShareCallback#onSuccess}.
     */
    @Test
    public void testShareSuccess() throws InterruptedException {
        server.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody("https://paste.rs/abc123"));

        CountDownLatch latch = new CountDownLatch(1);
        String[] result = {null};

        repository.share("test payload", new PasteRepository.ShareCallback() {
            @Override
            public void onSuccess(String url) {
                result[0] = url;
                latch.countDown();
            }

            @Override
            public void onFailure(String message) {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);
        assertEquals("https://paste.rs/abc123", result[0]);
    }

    /**
     * Verifies that a 500 server error is delivered to {@link PasteRepository.ShareCallback#onFailure}.
     */
    @Test
    public void testShareFailure() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(500));

        CountDownLatch latch = new CountDownLatch(1);
        String[] failureMessage = {null};
        String[] successUrl = {null};

        repository.share("test payload", new PasteRepository.ShareCallback() {
            @Override
            public void onSuccess(String url) {
                successUrl[0] = url;
                latch.countDown();
            }

            @Override
            public void onFailure(String message) {
                failureMessage[0] = message;
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);
        assertNull("onSuccess must not fire on a 500 response", successUrl[0]);
        assertEquals("Server error: 500", failureMessage[0]);
    }
}

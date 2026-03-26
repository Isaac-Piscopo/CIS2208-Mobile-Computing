package com.isaacpiscopo.wifinote.api;

import com.isaacpiscopo.wifinote.BuildConfig;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Singleton repository for posting plain-text content to paste.rs.
 * Wraps a Retrofit instance and exposes a single {@link #share} method.
 */
public class PasteRepository {

    /** Production base URL for the paste.rs service. */
    private static final String BASE_URL = "https://paste.rs/";

    private static PasteRepository instance;

    private final PasteApi api;

    /** Callback interface for share results. */
    public interface ShareCallback {
        /**
         * Called on the Retrofit callback thread when the paste succeeds.
         *
         * @param url the URL of the created paste.
         */
        void onSuccess(String url);

        /**
         * Called on the Retrofit callback thread when the paste fails.
         *
         * @param message a human-readable error description.
         */
        void onFailure(String message);
    }

    /**
     * Private production constructor using {@link #BASE_URL}.
     * Use {@link #getInstance()} for production access.
     */
    private PasteRepository() {
        this(BASE_URL);
    }

    /**
     * Package-private constructor accepting an explicit base URL.
     * Intended for unit tests only (e.g. MockWebServer).
     *
     * @param baseUrl the base URL for the Retrofit instance.
     */
    PasteRepository(String baseUrl) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .build();

        api = retrofit.create(PasteApi.class);
    }

    /**
     * Returns the singleton {@link PasteRepository} instance, creating it if necessary.
     *
     * @return the singleton instance.
     */
    public static synchronized PasteRepository getInstance() {
        if (instance == null) {
            instance = new PasteRepository();
        }
        return instance;
    }

    /**
     * POSTs {@code text} to paste.rs asynchronously.
     * Results are delivered on the Retrofit callback thread -- switch to the main thread
     * before updating UI.
     *
     * @param text     the plain-text content to share.
     * @param callback receives the paste URL on success, or an error message on failure.
     */
    public void share(String text, ShareCallback callback) {
        RequestBody body = RequestBody.create(text,
                MediaType.parse("text/plain; charset=utf-8"));

        api.paste(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        callback.onSuccess(response.body().string().trim());
                    } catch (IOException e) {
                        callback.onFailure("Failed to read response");
                    }
                } else {
                    callback.onFailure("Server error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }
}

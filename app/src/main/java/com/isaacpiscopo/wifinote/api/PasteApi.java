package com.isaacpiscopo.wifinote.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Retrofit interface for the paste.rs plain-text paste service.
 * A POST to "/" with a plain-text body returns the paste URL as a plain-text response.
 */
public interface PasteApi {

    /**
     * Uploads {@code body} to paste.rs and returns the resulting paste URL.
     *
     * @param body the plain-text content to paste.
     * @return a {@link Call} whose response body is the paste URL string.
     */
    @POST("/")
    Call<ResponseBody> paste(@Body RequestBody body);
}

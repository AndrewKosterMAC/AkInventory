package com.webfactional.andrewk.akinventory;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class ItemUploadIntentService extends IntentService
{
    public static final String ACTION_POST_TO_ANDREWK = "com.webfactional.andrewk.akinventory.ItemUploadIntentService.ACTION_POST_TO_ANDREWK";

    public static final String PARAM_URL_PATH = "com.webfactional.andrewk.akinventory.ItemUploadIntentService.PARAM_URL_PATH";

    public static final String PARAM_ITEM = "com.webfactional.andrewk.akinventory.ItemUploadIntentService.PARAM_ITEM";

    public ItemUploadIntentService() {
        super("ItemUploadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null)
        {
            final String action = intent.getAction();

            if (ACTION_POST_TO_ANDREWK.equals(action))
            {
                final String paramUrlPath = intent.getStringExtra(PARAM_URL_PATH);
                final Serializable paramItem = intent.getSerializableExtra(PARAM_ITEM);
                handleActionPostToAndrewk(paramUrlPath, paramItem);
            }
        }
    }

    public static final MediaType JSON
        = MediaType.parse("application/json; charset=utf-8");

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPostToAndrewk(String urlPath, Serializable item)
    {
        OkHttpClient client = new OkHttpClient();

        String itemJson = new Gson().toJson(item);

        RequestBody body = RequestBody.create(JSON, itemJson);
        Request request = new Request.Builder()
                .url("http://andrewk.webfactional.com" + urlPath)
                .post(body)
                .build();

        try
        {
            Response response = client.newCall(request).execute();

            Log.i("AK", "handleActionPostToAndrewk response: " + response.body().string());
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }
}

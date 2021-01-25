package com.example.bluedawnproject;

import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectionSpeaker implements Speaker<JSONObject> {
    ArrayList<Listener> listeners;
    JSONObject data;
    MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client  = new OkHttpClient();

    public ConnectionSpeaker() {
        listeners = new ArrayList<Listener>();
        data = new JSONObject();
    }

    @Override
    public void speakToAll() {
        for (Listener listener : listeners) {
            listener.listen(this.data);
        }
    }

    @Override
    public void update(JSONObject json) {
        this.data = json;
        speakToAll();
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    Callback callback = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            try {
                JSONObject data = new JSONObject("{error: 'Something went wrong'}");
                update(data);
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }
        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            final String body = response.body().string();
            try {
                JSONObject data = new JSONObject(body);
                update(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    void post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    void delete(String url, String id) throws IOException {
        Request request = new Request.Builder()
                .url(url+"/"+id)
                .delete()
                .build();
        client.newCall(request).enqueue(callback);
    }

    void get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }
}

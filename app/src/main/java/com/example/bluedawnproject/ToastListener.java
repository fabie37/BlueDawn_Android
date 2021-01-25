package com.example.bluedawnproject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class ToastListener implements Listener<JSONObject> {

    private Activity activity;

    public ToastListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void listen(JSONObject data) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Iterator<String> keys = data.keys();
                String res = new String();
                for (Iterator<String> it = keys; it.hasNext(); ) {
                    String key = it.next();
                    try {
                        if (key.compareTo("data") == 0) {
                            res += "data: ...";
                        } else {
                            res += key + ":" + data.get(key) + " \n ";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(activity, res, Toast.LENGTH_SHORT).show();

            }
        });
    }
}

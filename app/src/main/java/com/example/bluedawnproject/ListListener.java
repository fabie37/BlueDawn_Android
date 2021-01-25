package com.example.bluedawnproject;

import android.app.Activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONObject;

public class ListListener implements Listener<JSONObject> {

    private Activity activity;
    private RecyclerView view;
    private Boolean listened_before;
    private ConnectionSpeaker cs;

    public ListListener(Activity activity, RecyclerView view, ConnectionSpeaker connectionSpeaker) {
        this.activity = activity;
        this.view = view;
        this.listened_before = false;
        this.cs = connectionSpeaker;
    }

    @Override
    public void listen(JSONObject data) {
        cs.removeListener(this);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view = (RecyclerView) activity.findViewById(R.id.purchasesView);
                MyAdapter myAdapter = new MyAdapter(activity, data, cs);
                view.setAdapter(myAdapter);
                view.setLayoutManager(new LinearLayoutManager(activity));
            }
        });
    }
}

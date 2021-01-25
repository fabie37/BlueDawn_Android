package com.example.bluedawnproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity2 extends AppCompatActivity {

    Button createButton;
    RecyclerView view;

    ConnectionSpeaker connectionSpeaker;
    ToastListener toastListener;
    ListListener listListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        this.connectionSpeaker = new ConnectionSpeaker();
        this.toastListener = new ToastListener(this);
        this.listListener = new ListListener(this, this.view, this.connectionSpeaker);
        connectionSpeaker.addListener(toastListener);
        connectionSpeaker.addListener(listListener);


        // Everything with create button
        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try {
            connectionSpeaker.get("http://local.bluedawn:5000/api/v1/purchases");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
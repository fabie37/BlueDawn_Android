package com.example.bluedawnproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context ct;
    JSONArray data;
    ConnectionSpeaker connectionSpeaker;

    public MyAdapter(Context ct, JSONObject json, ConnectionSpeaker connectionSpeaker)  {
        this.ct = ct;
        this.connectionSpeaker = connectionSpeaker;
        try {
            data = json.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void remove(String id) throws JSONException, IOException {
        int i;
        for (i=0; i<data.length(); i++) {
            JSONObject purchase= this.data.getJSONObject(i);
            if (purchase.getString("_id").compareTo(id) == 0) {
                break;
            }
        }
        this.data.remove(i);
        notifyItemChanged(i);
        notifyItemRangeRemoved(i, 1);
        this.connectionSpeaker.delete("http://local.bluedawn:5000/api/v1/purchases", id);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ct);
        View view = inflater.inflate(R.layout.my_row, parent, false);
    return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            JSONObject purchase = data.getJSONObject(position);
            Float total = Float.valueOf(purchase.getString("total"));
            total /= 100;
            String formated = "£"+ String.valueOf(total);
            holder.store.setText(purchase.getString("store"));
            holder.date.setText(purchase.getString("date").substring(0,10));
            holder.total.setText(formated);
            holder.category.setText(purchase.getString("category"));
            holder.setId(purchase.getString("_id"));
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        remove(holder.getId());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView store, category, total, date;
        Button delete;
        String id;

        void setId(String id) {
            this.id = id;
        }

        String getId() {
            return this.id;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            store = (TextView)  itemView.findViewById(R.id.rowShopText);
            category = (TextView)  itemView.findViewById(R.id.rowCategoryText);
            total = (TextView)  itemView.findViewById(R.id.rowTotalText);
            date = (TextView)  itemView.findViewById(R.id.rowDateText);
            delete = (Button)  itemView.findViewById(R.id.deleteButton);
        }
    }
}

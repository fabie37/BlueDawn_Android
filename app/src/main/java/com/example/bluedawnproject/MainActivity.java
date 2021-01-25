package com.example.bluedawnproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;


import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Button submitButton;
    Button purchasesButton;
    TextView shopText;
    AutoCompleteTextView categoryAutoComplete;
    TextView totalText;
    TextView dateText;
    DatePickerDialog picker;
    ConnectionSpeaker connectionSpeaker;
    ToastListener toastListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up listeners and connections
        connectionSpeaker = new ConnectionSpeaker();
        toastListener = new ToastListener(this);
        connectionSpeaker.addListener(toastListener);

        // Everything to do with what kind of shop
        shopText = (TextView) findViewById(R.id.shopText);

        // Everything to do with categories
        categoryAutoComplete = (AutoCompleteTextView) findViewById(R.id.categoryAutoComplete);
        String[] categories = {"Groceries","Shopping","Health","Services","Restaurants","Transport","General","Entertainment","Utilities","Other"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, categories);
        categoryAutoComplete.setAdapter(categoryAdapter);

        // Everything to do with total
        totalText = (TextView) findViewById(R.id.totalText);
        totalText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});

        // Everything to date with the date
        dateText = (TextView) findViewById(R.id.dateText);
        dateText.setShowSoftInputOnFocus(false);
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateText.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        // Finally everything to do with submitting
        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String shop = shopText.getText().toString();
                String category = categoryAutoComplete.getText().toString();
                String total = totalText.getText().toString() ;
                if (total.contains(".")) {
                    total = total.replace(".","");
                } else {
                    total = total+"00";
                }
                String date = dateText.getText().toString();

                if (isNull(shop, "Shop") || isNull(category, "Category") || isNull(total, "Total") || isNull(date, "Date")) {
                    return;
                }

                if (!isNumber(total)) {
                    return;
                }
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("store",shop);
                    payload.put("category",category);
                    payload.put("total",total);
                    payload.put("date",date);

                    // Connect to server
                    String url = "http://local.bluedawn:5000/api/v1/purchases";
                    connectionSpeaker.post(url, payload.toString());

                } catch (Exception err) {
                    Toast.makeText(MainActivity.this, "Something went wrong " + err, Toast.LENGTH_SHORT).show();
                } finally {
                    shopText.setText("");
                    categoryAutoComplete.setText("");
                    totalText.setText("");
                    dateText.setText("");
                }

            }
        });

        // Everything to do with next purhcases button
        purchasesButton = (Button) findViewById(R.id.purchasesButton);
        purchasesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        MainActivity2.class);
                startActivity(intent);
            }
        });
    }

    protected boolean isNull(String text, String ViewName) {
        if (text.isEmpty()) {
            Toast.makeText(MainActivity.this, ViewName + " is empty", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    protected boolean isNumber(String text) {
        if (!text.matches("\\d+")) {
            Toast.makeText(MainActivity.this, text + " is not a number", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}

class DecimalDigitsInputFilter implements InputFilter {
    private Pattern mPattern;
    DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Matcher matcher = mPattern.matcher(dest);
        if (!matcher.matches())
            return "";
        return null;
    }
}


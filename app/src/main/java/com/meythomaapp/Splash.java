package com.meythomaapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import pl.droidsonroids.gif.GifImageView;

public class Splash extends AppCompatActivity {

    private static final String TAG = "11";
    private int REQUEST_CODE_ASK_PERMISSIONS;
    TextView textView;
    GifImageView gifImageView;
    SQLiteDatabase db;
    EditText usered, passed;
    Button submit;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        textView = (TextView) findViewById(R.id.handle);
        gifImageView = (GifImageView) findViewById(R.id.gif);
        usered = (EditText) findViewById(R.id.usered);
        passed = (EditText) findViewById(R.id.passed);
        submit = (Button) findViewById(R.id.submit_btn);
        createdDB();

        if (isNetworkAvailable() == true) {


            textView.performClick();
            gifImageView.setVisibility(View.INVISIBLE);
            Toast.makeText(Splash.this, "Enter the username and password to login...", Toast.LENGTH_SHORT).show();


        } else {
            Toast.makeText(this, "plzz check the internet connection...", Toast.LENGTH_SHORT).show();
            finishAffinity();
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usered.getText().toString().isEmpty() && passed.getText().toString().isEmpty()) {
                    Toast.makeText(Splash.this, "plz enter valid username and password", Toast.LENGTH_SHORT).show();
                } else if (usered.getText().toString().isEmpty()) {
                    usered.setError("Enter valid username");
                } else if (passed.getText().toString().isEmpty()) {
                    usered.setError("Enter valid password");
                } else {
                    progressDialog = new ProgressDialog(Splash.this);
                    progressDialog.setMessage("Log in...");
                    progressDialog.show();
                    loginattempt(usered.getText().toString(), passed.getText().toString());
                }
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    void loginattempt(String username, String password) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfigClass.loginURL + "?user=" + username + "&pass=" + password, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if (response.equals("1")) {
                    Intent intent = new Intent(Splash.this, HomeActivity.class);
                    startActivity(intent);
                    Toast.makeText(Splash.this, "Login Success...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Splash.this, "Incorrect Details", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(Splash.this);
        requestQueue.add(stringRequest);


    }

    void createdDB() {
        db = openOrCreateDatabase("loginStatus", MODE_PRIVATE, null);
        db.execSQL("create table if not exists Tables(status TEXT);");
        Toast.makeText(getApplicationContext(), "Database Created Successfully", Toast.LENGTH_SHORT).show();
        db.close();
    }
}

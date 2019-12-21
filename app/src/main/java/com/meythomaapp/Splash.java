package com.meythomaapp;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
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
import org.json.JSONArray;
import org.json.JSONObject;
import pl.droidsonroids.gif.GifImageView;
public class Splash extends AppCompatActivity {
    TextView textView;
    GifImageView gifImageView;
    SQLiteDatabase db;
    EditText usered, passed;
    Button submit, reset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        textView = (TextView) findViewById(R.id.handle);
        gifImageView = (GifImageView) findViewById(R.id.gif);
        usered = (EditText) findViewById(R.id.usered);
        passed = (EditText) findViewById(R.id.passed);
        submit = (Button) findViewById(R.id.submit_btn);
        reset = (Button) findViewById(R.id.reset);
        createdDB();
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usered.setText("");
                passed.setText("");
            }
        });
        db = openOrCreateDatabase("loginStatus", MODE_PRIVATE, null);
        Cursor c = db.rawQuery("SELECT * FROM Tables", null);
        if (c.getCount() == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    textView.performClick();
                    gifImageView.setVisibility(View.INVISIBLE);
                }
            }, 2000);
        } else {
            while (c.moveToNext()) {
                if (c.getString(0).equals("1")) {
                    getArea();
                    customerlist();
                }
            }
        }
        if (isNetworkAvailable() == false) {
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
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Verify Account...");
        progressDialog.show();
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, AppConfigClass.loginURL + "?user=" + password + "&pass=" + username, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("1")) {
                    db = openOrCreateDatabase("loginStatus", MODE_PRIVATE, null);
                    db.execSQL("insert into Tables values('" + response + "');");
                    db.close();
                    getArea();
                    customerlist();
                    progressDialog.dismiss();
                    Toast.makeText(Splash.this, "Login Successfull...", Toast.LENGTH_SHORT).show();
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
        requestQueue.add(stringRequest1);
    }
    void createdDB() {
        db = openOrCreateDatabase("loginStatus", MODE_PRIVATE, null);
        db.execSQL("create table if not exists Tables(status TEXT);");
        db.close();
    }
    void getArea() {
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, AppConfigClass.getAreaListURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Constants.areasListAl.clear();
                    Constants.areasIdListAl.clear();
                    Constants.areasListAlMap.clear();
                    JSONObject jsonObject1 = new JSONObject(response);
                    JSONArray jsonArray1 = jsonObject1.getJSONArray("project_details");
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject jsObj = jsonArray1.getJSONObject(i);
                        String itemType = jsObj.getString("type");

                        if (itemType.equalsIgnoreCase("Area")) {
                            String cId = jsObj.getString("id");
                            String name = jsObj.getString("name");
                            Constants.areasIdListAl.add(cId);
                            Constants.areasListAlMap.put(cId, name);
                            Constants.areasListAl.add(name);
                        }
                    }
                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        RequestQueue requestQueue2 = Volley.newRequestQueue(Splash.this);
        requestQueue2.add(stringRequest2);
    }
    void customerlist() {
        StringRequest stringRequest3 = new StringRequest(Request.Method.GET, AppConfigClass.getCustomerURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Constants.customerListAl.clear();
                    Constants.customerIdListAl.clear();
                    Constants.customerJsonObjListMap.clear();
                    JSONObject jsonObject1 = new JSONObject(response);
                    JSONArray jsonArray1 = jsonObject1.getJSONArray("project_details");
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject jsObj = jsonArray1.getJSONObject(i);
                        String cId = jsObj.getString("customer_id");
                        String name = jsObj.getString("company_name");
                        Constants.customerJsonObjListMap.put(name, jsObj);
                        Constants.customerIdListAl.add(cId);
                        Constants.customerListAl.add(name);
                    }
                    Intent intent = new Intent(Splash.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        RequestQueue requestQueue3 = Volley.newRequestQueue(Splash.this);
        requestQueue3.add(stringRequest3);
    }
}

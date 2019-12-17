package com.meythomaapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private GetAreaTask mGetAreaTask = null;
    private GetUserTask mGetUserTask = null;
    private GetCustomerTask mGetCustomerTask = null;

    // UI references.
    private AutoCompleteTextView mUsername;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    Context conx;
    AppPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        conx = this;


        // Set up the login form.
        mUsername = (AutoCompleteTextView) findViewById(R.id.username);
//        populateAutoComplete();
        preferences = new AppPreferences(this);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mGetCustomerTask = new GetCustomerTask();
        startMyTask(mGetCustomerTask, "");

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsername.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsername.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsername.setError(getString(R.string.error_field_required));
            focusView = mUsername;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(username, password);
//            startMyTask(mAuthTask, "");
            mAuthTask.execute();
            /*String smid = "0";
            if(username.equalsIgnoreCase("hussain")){
                smid = "1";
            }else {
                smid = "2";
            }
            preferences.SavePreferences(Constants.PREFERENCES_LOGIN_STATUS, "true", Constants.PRIVATE_PREFERENCE);
            preferences.SavePreferences(Constants.PREFERENCES_SALESPERSON_ID, smid, Constants.PRIVATE_PREFERENCE);
            preferences.SavePreferences(Constants.PREFERENCES_SALESPERSON_NAME, username, Constants.PRIVATE_PREFERENCE);
            Intent intent = new Intent(conx,HomeActivity.class);
            startActivity(intent);
            finish();*/
        }
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() == 10;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.INVISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.INVISIBLE : View.GONE);
                }
            });
        }
        else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.INVISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.INVISIBLE);
        }
    }







    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<String, Void, String> {

        private final String username;
        private final String monumber;

        UserLoginTask(String usrname, String mobnumber) {
            username = usrname;
            monumber = mobnumber;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
        String resp="";
        String URL = AppConfigClass.loginURL+"?user="+monumber+"&pass="+username;
            try {
                HttpEntity resEntity;
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(URL);
                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("username", new StringBody(username));
                reqEntity.addPart("password", new StringBody(monumber));
                post.setEntity(reqEntity);
                HttpResponse response = client.execute(post);
                resEntity = response.getEntity();
                resp = EntityUtils.toString(resEntity).trim();
               // for GET Method
               /* String usernam = URLEncoder.encode(username, "UTF-8");
                String password = URLEncoder.encode(monumber, "UTF-8");
                String URL = AppConfigClass.loginURL+"?username="+usernam+"&password="+password;

                HttpClient Client = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                resp = Client.execute(httpget, responseHandler).trim();*/


            } catch (Exception e) {
                resp = e.toString();
            }

            // TODO: register the new account here.
            return resp;
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            showProgress(false);
            if(!result.equalsIgnoreCase("incorrect")){

                String smid = result.trim();
                preferences.SavePreferences(Constants.PREFERENCES_LOGIN_STATUS, "true", Constants.PRIVATE_PREFERENCE);
                preferences.SavePreferences(Constants.PREFERENCES_SALESPERSON_ID, smid, Constants.PRIVATE_PREFERENCE);
                preferences.SavePreferences(Constants.PREFERENCES_SALESPERSON_NAME, username, Constants.PRIVATE_PREFERENCE);
                Intent intent = new Intent(conx,HomeActivity.class);
                startActivity(intent);
                finish();


            }else{

                mUsername.setError(getString(R.string.error_invalid_login));

            }
          //  Toast.makeText(conx,""+result,Toast.LENGTH_LONG).show();

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class GetAreaTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {

            //showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String resp="";
            try {

                // for GET Method

                String URL = AppConfigClass.getAreaListURL;

                HttpClient Client = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                resp = Client.execute(httpget, responseHandler).trim();

            } catch (Exception e) {
                resp = e.toString();
            }

            // TODO: register the new account here.
            return resp;
        }

        @Override
        protected void onPostExecute(final String result) {

            try{
                JSONArray jsArray = new JSONArray(result);
                JSONObject jsObj;
                int len = jsArray.length();

                Constants.areasListAl.clear();
                Constants.areasIdListAl.clear();
                Constants.areasListAlMap.clear();
                for (int i = 0; i < len; i++) {
                    jsObj = jsArray.getJSONObject(i);

                    String itemType = jsObj.getString("type");

                    if(itemType.equalsIgnoreCase("Area")) {
                        String cId = jsObj.getString("id");
                        String name = jsObj.getString("name");
                        Constants.areasIdListAl.add(cId);
                        Constants.areasListAlMap.put(cId,name);
                        Constants.areasListAl.add(name);
                    }

                }


                String loginStatus = preferences.getStringPreference(conx, Constants.PREFERENCES_LOGIN_STATUS);
                if (loginStatus.equalsIgnoreCase("true")) {

                    Intent intent = new Intent(conx,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    mPasswordView = (EditText) findViewById(R.id.password);
                    mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                                attemptLogin();
                                return true;
                            }
                            return false;
                        }
                    });

                    Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
                    mEmailSignInButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            attemptLogin();
                        }
                    });


                }

               // showProgress(false);
            }catch (Exception e){
                Log.e("Get Area","Error is "+e.toString());
            }

            showProgress(false);

        }

        @Override
        protected void onCancelled() {
            mGetAreaTask = null;
            //showProgress(false);
        }
    }
    public class GetCustomerTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {

            showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String resp="";
            try {

                // for GET Method

                String URL = AppConfigClass.getCustomerURL;

                HttpClient Client = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                resp = Client.execute(httpget, responseHandler).trim();

            } catch (Exception e) {
                resp = e.toString();
            }

            // TODO: register the new account here.
            return resp;
        }

        @Override
        protected void onPostExecute(final String result) {

            try{
                JSONArray jsArray = new JSONArray(result);
                JSONObject jsObj;
                int len = jsArray.length();

                Constants.customerListAl.clear();
                Constants.customerIdListAl.clear();
                Constants.customerJsonObjListMap.clear();
                for (int i = 0; i < len; i++) {
                    jsObj = jsArray.getJSONObject(i);
                    String cId = jsObj.getString("customer_id");
                    String name = jsObj.getString("company_name");
                    Constants.customerJsonObjListMap.put(name,jsObj);
                    Constants.customerIdListAl.add(cId);
                    Constants.customerListAl.add(name);
                }

                mGetUserTask = new GetUserTask();
                startMyTask(mGetUserTask, "");


               // showProgress(false);
            }catch (Exception e){
                Log.e("GetCustomerTask","Error is "+e.toString());
            }

           // showProgress(false);

        }

        @Override
        protected void onCancelled() {
            mGetCustomerTask = null;
            //showProgress(false);
        }
    }

    public class GetUserTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {

//            showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String resp="";
            try {

                // for GET Method

                String URL = AppConfigClass.getUserURL;

                HttpClient Client = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                resp = Client.execute(httpget, responseHandler).trim();

            } catch (Exception e) {
                resp = e.toString();
            }

            // TODO: register the new account here.
            return resp;
        }

        @Override
        protected void onPostExecute(final String result) {

            try{
                JSONArray jsArray = new JSONArray(result);
                JSONObject jsObj;
                int len = jsArray.length();

                Constants.userListAl.clear();
                Constants.userIdListAl.clear();
                Constants.userJsonObjListMap.clear();
                for (int i = 0; i < len; i++) {
                    jsObj = jsArray.getJSONObject(i);
                    String cId = jsObj.getString("person_id");
                    Constants.userJsonObjListMap.put(cId,jsObj);
                    Constants.userIdListAl.add(cId);
                    String name = jsObj.getString("person_name");
                    Constants.userListAl.add(name);

                }
                mGetAreaTask = new GetAreaTask();
                startMyTask(mGetAreaTask, "");

                // showProgress(false);
            }catch (Exception e){
                Log.e("GetCustomerTask","Error is "+e.toString());
            }

            // showProgress(false);

        }

        @Override
        protected void onCancelled() {
            mGetCustomerTask = null;
            //showProgress(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        // API 11
    void startMyTask(AsyncTask asyncTask, String... params) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[])params);
            else
                asyncTask.execute((Object[])params);
        } catch (Exception e) {
            if(Constants.DEBUGGING) System.out.print(e);
        }
    }
}


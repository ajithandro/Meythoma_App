package com.meythomaapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class AddCategory extends AppCompatActivity {

    ArrayList<GetCategoryBean> beanList=new ArrayList<GetCategoryBean>();
    private AddNewCategoryTask mAddNewCategoryTask = null;
    private GetCategoryTask mGetCategoryTask = null;
    EditText cn,ct;
    Button cadd;
    Context conx;
    private View mAddCat_progress;
    AppPreferences preferences;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_category);
        conx = this;
        preferences = new AppPreferences(this);
        mAddCat_progress  = findViewById(R.id.add_cat_progress);
        mGetCategoryTask = new GetCategoryTask();
        mGetCategoryTask.execute();


        cn = (EditText) findViewById(R.id.cn);
        ct = (EditText) findViewById(R.id.ct);
        cadd = (Button) findViewById(R.id.cadd);
        cadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scn = cn.getText().toString().trim();
                String sct = ct.getText().toString().trim();
                if(scn.length()==0 || sct.length()==0){
                    Toast.makeText(conx,"Please fill all the details",Toast.LENGTH_LONG).show();
                }else{
                    mAddNewCategoryTask = new AddNewCategoryTask(scn, sct, "insert", "0");
                    mAddNewCategoryTask.execute();
                }
            }
        });

        listView=(ListView)findViewById(R.id.listv);
//        String[] items={"Apple","Banana","Coconut","Grape","Peach","Pear"};
//        arrayList=new ArrayList<>(Arrays.asList(items));
        /*arrayAdapter=new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtitem,arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show input box
                showInputBox(arrayList.get(position),position);
            }
        });*/
    }

    public void showInputBox(String oldItem, final int index){
        final Dialog dialog=new Dialog(AddCategory.this);
        dialog.setTitle("Input Box");
        dialog.setContentView(R.layout.input_box);
        TextView txtMessage=(TextView)dialog.findViewById(R.id.txtmessage);
        txtMessage.setText("Update item");
        txtMessage.setTextColor(Color.parseColor("#ff2222"));
        final EditText editText=(EditText)dialog.findViewById(R.id.txtinput);
        editText.setText(oldItem);
        Button bt=(Button)dialog.findViewById(R.id.btdone);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cnamee = editText.getText().toString();
                arrayList.set(index,cnamee);
                String  indxx = beanList.get(index).getCat_id();
                mAddNewCategoryTask = new AddNewCategoryTask(cnamee, "c", "update", indxx);
                mAddNewCategoryTask.execute();
                arrayAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public class AddNewCategoryTask extends AsyncTask<String, Void, String> {

        private String catName;
        private String catType;
        private String quryType;
        private String indexId;


        AddNewCategoryTask(String cname,String ctype,String qrytype,String inxid) {
            try {
                catName = URLEncoder.encode(cname, "UTF-8");
                catType = URLEncoder.encode(ctype, "UTF-8");
                quryType = URLEncoder.encode(qrytype, "UTF-8");
                indexId = URLEncoder.encode(inxid, "UTF-8");
            }catch(Exception e){

            }
        }
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
             //   String sPersonId = preferences.getStringPreference(conx, Constants.PREFERENCES_SALESPERSON_ID);
                String sPersonName = preferences.getStringPreference(conx, Constants.PREFERENCES_SALESPERSON_NAME);
                String URL = AppConfigClass.addnewcategoryURL+"?task=add&sprsnName="+sPersonName
                        +"&cname="+catName
                        +"&ctype="+catType
                        +"&indxid="+indexId
                        +"&qtype="+quryType;
//                String URL = AppConfigClass.addnewcategoryURL+"?task=add&category_name="+catName;

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
            mAddNewCategoryTask = null;
            showProgress(false);
            if(result.equalsIgnoreCase("Success")){

                Toast.makeText(conx,"Added Successfully",Toast.LENGTH_LONG).show();
                cn.setText("");
                ct.setText("");
                if(quryType.equalsIgnoreCase("insert")) {
                    arrayList.add(catName);
                    arrayAdapter.notifyDataSetChanged();
                }
            }else{
                Toast.makeText(conx,"Failed",Toast.LENGTH_LONG).show();
            }
            //  Toast.makeText(conx,""+result,Toast.LENGTH_LONG).show();


        }

        @Override
        protected void onCancelled() {
            mAddNewCategoryTask = null;
            showProgress(false);
        }
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            /*mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });*/

            mAddCat_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            mAddCat_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAddCat_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mAddCat_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            // mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    public class GetCategoryTask extends AsyncTask<String, Void, String> {


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

                String URL = AppConfigClass.getCategoryListURL;

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
            mGetCategoryTask = null;
            try{
                JSONArray jsArray = new JSONArray(result);
                JSONObject jsObj;
                int len = jsArray.length();
                GetCategoryBean gcb;
                for (int i = 0; i < len; i++) {
                    jsObj = jsArray.getJSONObject(i);
                    gcb = new GetCategoryBean();
                    String cId = jsObj.getString("Id");
                    String name = jsObj.getString("name");
                    gcb.setCat_id(cId);
                    gcb.setCat_name(name);
                    beanList.add(gcb);
                    arrayList.add(name);
                }
                arrayAdapter=new ArrayAdapter<String>(conx, R.layout.list_item, R.id.txtitem,arrayList);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Show input box
                        showInputBox(arrayList.get(position),position);
                    }
                });

            }catch (Exception e){
                Log.e("ADD CATEGORY","Error is "+e.toString());
            }

            showProgress(false);

        }

        @Override
        protected void onCancelled() {
            mGetCategoryTask = null;
            showProgress(false);
        }
    }
}

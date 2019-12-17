package com.meythomaapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class AddCustomer extends Fragment {

    private ListView lv_customers;
    private ArrayAdapter<String> customers_adapter;
    private ArrayList<String> customersArraylist;
    private ArrayList<String> areasArraylist;
    private LinearLayout cust_listview;
    private AddCustomer conx;
    private String TAG = "AddCustomer";
//    private GetCustomersTask mGetCustomersTask = null;

    private DeleteAddCustomersTask mDeleteAddCustomersTask = null;
    private String nQuery = "";
    private String nCust = "";
    private String comName;
    private String custname;
    private String addrsS;
    private String areaS;
    private String pincodeS;
    private String connumber;
    private String gstnumber;
    private String salesPersionId;
    private double latitude = 0.0;
    private double longitude = 0.0;
    boolean isMockLocatiion = true;


    EditText com_name, cust_name, addrs,  pincode, con_number, gst_number;
    Spinner added_by_spinner,area_spinner;
    Button save_customer_btn;

    private View mHome_progress;
    private String addedBy = "";
    private String createDT = "";
    private String created_by = "";
    GPSTracker gps;
    AppPreferences preferences;
View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.activity_add_customer,container,false);
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle("Add New Customer");
        conx = this;
        cust_listview = (LinearLayout) view.findViewById(R.id.cust_listview);
        customersArraylist = new ArrayList<>();
        customersArraylist.add("-Added By-");
        for(String cs : Constants.userListAl){

            customersArraylist.add(cs);

        }
        areasArraylist = new ArrayList<>();
        areasArraylist.add("-Area-");

        for(String cs : Constants.areasListAl){
            areasArraylist.add(cs);
        }
        Collections.sort(Constants.customerListAl);
        customers_adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_activated_1,Constants.customerListAl);
        lv_customers = (ListView) view.findViewById(R.id.lv_customers);
        preferences = new AppPreferences(getActivity());
        salesPersionId = preferences.getStringPreference(getActivity(),Constants.PREFERENCES_SALESPERSON_ID);
        if (gpsDialog != null) {
            if (gpsDialog.isShowing()) {
                gpsDialog.dismiss();
            }
        }
        gps = new GPSTracker(getActivity());
        mHome_progress = view.findViewById(R.id.home_progress);

        init();
        lv_customers.setAdapter(customers_adapter);

        initcall();
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lv_customers,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                /*String dArea = String.valueOf(lv_area.getItemAtPosition(position));
                                String msg = "Are you sure you want to Delete \""+dArea+"\" Area";
                                boolean isReturn = showConfirmDialog(msg);
                                return isReturn;*/
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    delete_position = position;
                                    String dcust = String.valueOf(lv_customers.getItemAtPosition(position));
                                    nCust =dcust;
                                    String msg = "Are you sure you want to Delete \""+nCust+"\" Customer";
                                    showConfirmDialog(msg);
                                    /*areasarraylist.remove(position);
                                    area_adapter.notifyDataSetChanged();*/

                                }

                            }
                        });
        lv_customers.setOnTouchListener(touchListener);
        lv_customers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = String.valueOf(adapterView.getItemAtPosition(i));
                nCust = name;
                nQuery = "Update";
                save_customer_btn.setText(nQuery);
                gst_number.setFocusable(false);
                //Toast.makeText(conx,name,Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsObj = Constants.customerJsonObjListMap.get(nCust);
                    String jcom_name = jsObj.getString("company_name");
                    String jcust_name = jsObj.getString("customer_name");
                    String jsaddrs = jsObj.getString("company_address");
                    String jsarea_spinner = jsObj.getString("company_area");
                    String jspincode = jsObj.getString("area_pincode");
                    String jscon_number = jsObj.getString("contact_number");
                    String jsgst_number = jsObj.getString("customer_number");
                    String jsadded_by_spinner = jsObj.getString("created_by");
                    createDT = jsObj.getString("created_datetime");
                    created_by = jsObj.getString("created_by");
                    int added_id = (Integer.parseInt(jsadded_by_spinner));
                    JSONObject added_user_jsObj =  Constants.userJsonObjListMap.get(jsadded_by_spinner);
                    String added_user = added_user_jsObj.getString("person_name");
                    String areaId = Constants.areasListAlMap.get(jsarea_spinner);
                    int areaIndx = (Constants.areasListAl.indexOf(areaId))+1;
                    int addedByIndx = (Constants.userListAl.indexOf(added_user))+1;
                    com_name.setText(jcom_name);
                    cust_name.setText(jcust_name);
                    addrs.setText(jsaddrs);
                    area_spinner.setSelection(areaIndx);
                    pincode.setText(jspincode);
                    con_number.setText(jscon_number);
                    gst_number.setText(jsgst_number);
                    added_by_spinner.setSelection(addedByIndx);
                }catch (Exception e){
                    Log.e(TAG,e.toString());
                }

                //  et_area.setText(name);
            }
        });




        final View activityRootView = view.findViewById(R.id.rootlayout);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                activityRootView.getWindowVisibleDisplayFrame(r);

                int screenHeight = activityRootView.getRootView().getHeight();
                Log.e("screenHeight", String.valueOf(screenHeight));
                int heightDiff = screenHeight - (r.bottom - r.top);
                Log.e("heightDiff", String.valueOf(heightDiff));
                boolean visible = heightDiff > screenHeight / 3;
                Log.e("visible", String.valueOf(visible));
                if (visible) {
                    // Toast.makeText(conx, "I am here 1", Toast.LENGTH_SHORT).show();
                    cust_listview.setVisibility(View.GONE);
                } else {
//                    Toast.makeText(conx, "I am here 2", Toast.LENGTH_SHORT).show();
                    cust_listview.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }





    void initcall() {

        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {

                buildAlertMessageNoGps();

            } catch (Exception e) {
                if (Constants.DEBUGGING) System.out.println(e);
            }
        }
        com_name = (EditText) view.findViewById(R.id.com_name);
        cust_name = (EditText) view.findViewById(R.id.cust_name);
        addrs = (EditText) view.findViewById(R.id.addrs);
        area_spinner = (Spinner) view.findViewById(R.id.spinner_area);
        pincode = (EditText) view.findViewById(R.id.pincode);
        con_number = (EditText) view.findViewById(R.id.con_number);
        gst_number = (EditText) view.findViewById(R.id.gst_number);
        added_by_spinner = (Spinner) view.findViewById(R.id.spinner_addedby);
        ArrayAdapter<String> areaDataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areasArraylist);
        areaDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        area_spinner.setAdapter(areaDataAdapter);
        ArrayAdapter<String> added_by_DataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, customersArraylist);
        added_by_DataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        added_by_spinner.setAdapter(added_by_DataAdapter);

//        added_by_spinner = (Spinner) findViewById(R.id.added_by_spinner);
        save_customer_btn = (Button)view. findViewById(R.id.save_customer_btn);

        save_customer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);


                comName = com_name.getText().toString().trim();
                custname = cust_name.getText().toString().trim();
                gstnumber = gst_number.getText().toString().trim(); //GST
                addrsS = addrs.getText().toString().trim();
                String isareaS = area_spinner.getSelectedItem().toString().trim();
                int areaSIndex = Constants.areasListAl.indexOf(isareaS);
                areaS = Constants.areasIdListAl.get(areaSIndex);
                String isaddedBy = added_by_spinner.getSelectedItem().toString().trim();
                int addedbyIndex = Constants.userListAl.indexOf(isaddedBy);
                addedBy = Constants.userIdListAl.get(addedbyIndex);
                /*area_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        areaS = adapterView.getItemAtPosition(i).toString();
                        Toast.makeText(conx,""+areaS,Toast.LENGTH_LONG);
                    }
                });*/
                pincodeS = pincode.getText().toString().trim();
                connumber = con_number.getText().toString().trim();


                /*added_by_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        addedBy = adapterView.getItemAtPosition(i).toString();
                        Toast.makeText(conx,""+addedBy,Toast.LENGTH_LONG);
                    }
                });*/

                try {
                    gps = new GPSTracker(getActivity());
                    if (gps != null) {
                        isMockLocatiion = gps.isMockLocationOn(getActivity());
                    }
                }catch (Exception e){
                    System.out.println(e);
                }

                // String comName = com_name.getText().toString();

                if (comName.length() == 0 || custname.length() == 0 || addrsS.length() == 0 || areaS.length() == 0 || pincodeS.length() == 0 || connumber.length() == 0 || gstnumber.length() == 0) {
                    showProgress(false);
                    Toast.makeText(getActivity(), "Please fill all the details", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        nQuery = "Add";
                        mDeleteAddCustomersTask = new DeleteAddCustomersTask();
                        mDeleteAddCustomersTask.execute();
                    }catch (Exception e){
                        System.out.println(e);
                    }

                }
            }
        });

    }

    Dialog gpsDialog;

    private void buildAlertMessageNoGps() {

        //**
        gpsDialog = new Dialog(getActivity());
        gpsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        gpsDialog.setCancelable(false);
        gpsDialog.setContentView(R.layout.create_gps_dialog);
        String msg = "This app requires locations services to be enabled. \n" +
                "Note: due to contractual agreements, not all content may be available in all areas.";
        TextView txt_dia = (TextView) gpsDialog.findViewById(R.id.txt_dia);
        txt_dia.setText(msg);

        Button oKButton = (Button) gpsDialog.findViewById(R.id.pin_ok_btn);
        //Button noButton = (Button) gpsDialog.findViewById(R.id.pin_no_btn);

        oKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsDialog.dismiss();
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        gpsDialog.show();


    }

    private void init() {

        customers_adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_activated_1,Constants.customerListAl);
        Toast.makeText(getActivity(), customersArraylist.toString(), Toast.LENGTH_SHORT).show();
        lv_customers = (ListView) view.findViewById(R.id.lv_customers);
    }
    /*public class GetCustomersTask extends AsyncTask<String, Void, String> {


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
                GetCategoryBean gcb;
                customersarraylist.clear();
                for (int i = 0; i < len; i++) {
                    jsObj = jsArray.getJSONObject(i);
                    gcb = new GetCategoryBean();

                    String cId = jsObj.getString("id");
                    String name = jsObj.getString("name");
                    gcb.setCat_id(cId);
                    gcb.setCat_name(name);
                    customersarraylist.add(name);


                }

                showProgress(false);
            }catch (Exception e){
                Log.e("Get Area","Error is "+e.toString());
            }

            showProgress(false);

        }

        @Override
        protected void onCancelled() {
            mGetCustomersTask = null;
            showProgress(false);
        }
    }*/

    public class DeleteAddCustomersTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {

            showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String resp="";
            try {
                try {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                }catch (Exception e){
                    System.out.println(e);
                    latitude = 0.0;
                    longitude = 0.0;
                }
                // for GET Method
                String URL = "";
                if(nQuery.equalsIgnoreCase("Update")) {
                    URL = AppConfigClass.deleteAddCustomerURL + "?query=Update&salesPersionId=" + salesPersionId+"&comName="+comName+"&custname="+custname+"&customer_number="+gstnumber
                            +"&addrsS="+addrsS+"&area="+areaS+"&pincodeS="+pincodeS+"&connumber="+connumber+"&latitude="+latitude+"&longitude="+longitude+"&addedBy="+created_by+"&updatedBy="+addedBy+"&createDateTime="+createDT;
                }else if(nQuery.equalsIgnoreCase("Add")) {
                    URL = AppConfigClass.deleteAddCustomerURL + "?query=Add&salesPersionId=" + salesPersionId+"&comName="+comName+"&custname="+custname+"&customer_number="+gstnumber
                            +"&addrsS="+addrsS+"&area="+areaS+"&pincodeS="+pincodeS+"&connumber="+connumber+"&latitude="+latitude+"&longitude="+longitude+"&addedBy="+addedBy;
                }else{
//                    URL = AppConfigClass.deleteAddCustomerURL + "?query=Delete&comName="+comName+"&custname="+custname+"&connumber="+connumber+"&pincodeS="+pincodeS;
                    URL = AppConfigClass.deleteAddCustomerURL + "?query=Delete&comName="+comName+"&custname="+custname+"&connumber="+connumber+"&pincodeS="+pincodeS+"&customer_number="+gstnumber;
                }
                URL = URL.replace(" ","%20");
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
                if(result.startsWith("Error")){
                    Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();
                }else {
                    boolean isJsArr = false;
                    try{
                        JSONArray jsArray1 = new JSONArray(result);
                        isJsArr = true;
                    }catch (Exception e){
                        isJsArr = false;
                    }

                    if(isJsArr) {
                        JSONArray jsArray = new JSONArray(result);
                        JSONObject jsObj;
                        int len = jsArray.length();

                        Constants.customerListAl.clear();
                        Constants.customerJsonObjListMap.clear();
                        for (int i = 0; i < len; i++) {
                            jsObj = jsArray.getJSONObject(i);
                            String cId = jsObj.getString("customer_id");
                            String name = jsObj.getString("company_name");
                            Constants.customerJsonObjListMap.put(name,jsObj);
                            Constants.customerListAl.add(name);
                            Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
                            Log.d("Data",name);
                        }
                        if(nQuery.equalsIgnoreCase("Add")) {
                            Toast.makeText(getActivity(), "Customer Added Successfully", Toast.LENGTH_LONG).show();
                            com_name.setText("");
                            cust_name.setText("");
                            addrs.setText("");
                            pincode.setText("");
                            con_number.setText("");
                            gst_number.setText("");
                            added_by_spinner.setSelection(0);
                            area_spinner.setSelection(0);
                        }else if(nQuery.equalsIgnoreCase("Update")) {
                            Toast.makeText(getActivity(), "Customer Updated Successfully", Toast.LENGTH_LONG).show();
                            com_name.setText("");
                            cust_name.setText("");
                            addrs.setText("");
                            pincode.setText("");
                            con_number.setText("");
                            gst_number.setText("");
                            added_by_spinner.setSelection(0);
                            area_spinner.setSelection(0);
                        }else {
                            Toast.makeText(getActivity(), "Customer Deleted Successfully", Toast.LENGTH_LONG).show();
                        }
//                        et_area.setText("");
                        init();
                        lv_customers.setAdapter(customers_adapter);
                    }else{
                        Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                    }
                }
            }catch (Exception e){
                Log.e("Get Area","Error is "+e.toString());
            }

            showProgress(false);

        }

        @Override
        protected void onCancelled() {
            mDeleteAddCustomersTask = null;
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

            mHome_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            mHome_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mHome_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mHome_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            // mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public TextView conf_tv;
    public Dialog conf_dialog;
    private int delete_position = 0;
    public void showConfirmDialog(String msg) {


        conf_dialog = new Dialog(getActivity());
        conf_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        conf_dialog.setCancelable(false);
        conf_dialog.setContentView(R.layout.custom_dialog);
        conf_tv = (TextView) conf_dialog.findViewById(R.id.txt_dia);
        conf_tv.setText(msg);
        final Button yesButton = (Button) conf_dialog.findViewById(R.id.btn_yes);
        final Button noButton = (Button) conf_dialog.findViewById(R.id.btn_no);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conf_dialog.dismiss();
                try {
                    JSONObject jsObj = Constants.customerJsonObjListMap.get(nCust);
                    comName = jsObj.getString("company_name");
                    custname = jsObj.getString("customer_name");
                    pincodeS = jsObj.getString("area_pincode");
                    connumber = jsObj.getString("contact_number");
                    gstnumber = jsObj.getString("customer_number");

                    //&customer_number="+gstnumber

                }catch (Exception e){

                }
                String deleteCust = Constants.customerListAl.get(delete_position);
                Constants.customerListAl.remove(delete_position);
                JSONObject remJsOb = Constants.customerJsonObjListMap.get(deleteCust);
                Constants.customerJsonObjListMap.remove(remJsOb);
                customers_adapter.notifyDataSetChanged();
                nQuery = "Delete";
                mDeleteAddCustomersTask = new DeleteAddCustomersTask();
                mDeleteAddCustomersTask.execute();

            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                conf_dialog.dismiss();
                customers_adapter.notifyDataSetChanged();
            }
        });
        if (conf_dialog != null) {
            conf_dialog.show();
        }

    }


    }



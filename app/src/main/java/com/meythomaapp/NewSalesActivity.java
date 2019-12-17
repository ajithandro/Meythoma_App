package com.meythomaapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class NewSalesActivity extends AppCompatActivity {

    private GetCategoryTask mGetCategoryTask = null;
    private PostNewSalesTask mPostNewSalesTask = null;
    EditText quantity, price_kg, amountEt, amountGst, amountTotal, in_date, et_comments, bill;
    Spinner company_name_spinner, product_type_spinner, ordertaken;
    Button add_cart_btn, order_submit_btn, back_to_sale;
    ImageView view_cart;
    TextView cart_count, gtotal;
    static NewSalesActivity conx;
    List<String> CNlist;
    ArrayList<String> jsObjArray = null;

    ArrayList<String> catArrayList = new ArrayList<>();
    ArrayList<String> catIdArrayList = new ArrayList<>();
    ArrayAdapter<String> CNSpinnerAdapter;
    ArrayAdapter<String> CASpinnerAdapter;
    private View mnew_progress;
    static String addedBy = "";
    GPSTracker gps;
    AppPreferences preferences;
    RadioGroup radioGst;
    RadioButton selectedRadioBtn, radios, radion;
    SharedPreferences sharedPreferences;
    String cmName, gstYn, proTyp, quan, pric, amount, sgst, samountTotal,billno,ordby;
    //    int totalAMT = 0;
//    int totalGST = 0;
    int cartCount = 0;
    public static int gTotalAmt = 0;

    private ListView lv_cart;
    //    private ArrayAdapter<String> cart_adapter;
//    private CartAdapter cart_adapter;
    private CartAdapterNew cart_adapter_new;
    private LinearLayout new_sale_form, cart_view_form;
    private int mYear, mMonth, mDay;

    String username;
    String customer_id;
    String delivery_date;
    String salesperson;
    String gst;
    String comments;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sales);

        sharedPreferences = getSharedPreferences("prefer", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        conx = this;
        Toast.makeText(this, sharedPreferences.getString("k", ""), Toast.LENGTH_SHORT).show();
        preferences = new AppPreferences(this);

        mnew_progress = findViewById(R.id.new_progress);
        CNlist = new ArrayList<String>();
        view_cart = (ImageView) findViewById(R.id.view_cart);
        cart_count = (TextView) findViewById(R.id.cart_count);
        back_to_sale = (Button) findViewById(R.id.back_to_sale);
        radios = (RadioButton) findViewById(R.id.radioSgst);
        radion = (RadioButton) findViewById(R.id.radioNgst);
        company_name_spinner = (Spinner) findViewById(R.id.company_name_spinner);
        product_type_spinner = (Spinner) findViewById(R.id.product_type_spinner);
        order_submit_btn = (Button) findViewById(R.id.order_submit_btn);
        bill = (EditText) findViewById(R.id.billno);
        ordertaken = (Spinner) findViewById(R.id.ordertaken);
        billno=bill.getText().toString();

        String[] ordtak = {"-Order By-", "Hussian", "SivaPrkash", "Gowtham", "Vaisul", "Ganesh", "Seenu"};
        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ordtak) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.parseColor("#000000"));

                return v;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView,
                        parent);
                v.setBackgroundColor(Color.parseColor("#8BC34A"));
                ((TextView) v).setTextColor(Color.parseColor("#000000"));

                return v;
            }
        };
        stringArrayAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        ordertaken.setAdapter(stringArrayAdapter);

        gtotal = (TextView) findViewById(R.id.gtotal);
        gTotalAmt = 0;
        back_to_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_sale_form.setVisibility(View.VISIBLE);
                cart_view_form.setVisibility(View.GONE);
            }
        });


        if (sharedPreferences.getString("k", "").equals("YES")) {
            radion.setEnabled(false);
        } else if (sharedPreferences.getString("k", "").equals("NO")) {
            radios.setEnabled(false);
        } else {
            radion.setEnabled(true);
            radion.setEnabled(true);
        }
        new_sale_form = (LinearLayout) findViewById(R.id.new_sale_form);
        cart_view_form = (LinearLayout) findViewById(R.id.cart_view_form);
        lv_cart = (ListView) findViewById(R.id.lv_cart);

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lv_cart,
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


                                    //String dcust = String.valueOf(lv_cart.getItemAtPosition(position));

                                    String msg = "Are you sure you want to Delete Item from Cart";
                                    showConfirmDialog(msg, position);
                                    /*areasarraylist.remove(position);
                                    area_adapter.notifyDataSetChanged();*/

                                }

                            }
                        });

        lv_cart.setOnTouchListener(touchListener);
        lv_cart.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());

        order_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    showProgress(true);
                    editor.putString("k", "Fine");
                    editor.commit();
                    Toast.makeText(NewSalesActivity.this, "Updated To Server", Toast.LENGTH_SHORT).show();
                    jsObjArray = new ArrayList<>();
                    jsObjArray.clear();
                    ObjectMapper mapperObj;//= new ObjectMapper();
                    for (CartBean cbObj : AppConfigClass.cardBeanAl) {
                        mapperObj = new ObjectMapper();
                        String jsonStr = mapperObj.writeValueAsString(cbObj);
                        jsObjArray.add(jsonStr);
                    }

                    salesperson = preferences.getStringPreference(conx, Constants.PREFERENCES_SALESPERSON_ID);
                    username = preferences.getStringPreference(conx, Constants.PREFERENCES_SALESPERSON_NAME);

                    String visitedCompany = company_name_spinner.getSelectedItem().toString().trim();
                    ordby= ordertaken.getSelectedItem().toString().trim();

                    int companyIndex = Constants.customerListAl.indexOf(visitedCompany);
                    customer_id = Constants.customerIdListAl.get(companyIndex);
                    comments = et_comments.getText().toString();
                    delivery_date = in_date.getText().toString();

                    gst = gstYn;

                    Log.e("***********************", jsObjArray.toString());
                    mPostNewSalesTask = new PostNewSalesTask();
                    mPostNewSalesTask.execute();
                    in_date.setText("");
                    bill.setText("");
                    et_comments.setText("");
                    Toast.makeText(NewSalesActivity.this,"Updated to Server...", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            back_to_sale.performClick();
                        }
                    },2000);

                } catch (Exception e) {

                }
            }
        });

        radioGst = (RadioGroup) findViewById(R.id.radioGst);
        quantity = (EditText) findViewById(R.id.quantity);
        price_kg = (EditText) findViewById(R.id.price_kg);
        amountEt = (EditText) findViewById(R.id.amountEt);
        amountGst = (EditText) findViewById(R.id.amountGst);
        amountTotal = (EditText) findViewById(R.id.amountTotal);
        et_comments = (EditText) findViewById(R.id.et_comments);


        amountEt.setEnabled(false);
        amountGst.setEnabled(false);
        amountTotal.setEnabled(false);
        quantity.setText("0");
        price_kg.setText("0");
        amountEt.setText("0");
        amountGst.setText("0");
        amountTotal.setText("0");

        in_date = (EditText) findViewById(R.id.in_date);
        in_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(conx,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

//                                in_date.setText( (monthOfYear + 1) + "/" + dayOfMonth + "/" +  year); //mm/dd/yyy
                                in_date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        price_kg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String qus = quantity.getText().toString().trim();
                String prs = price_kg.getText().toString().trim();

                int selectedId = radioGst.getCheckedRadioButtonId();
                selectedRadioBtn = (RadioButton) findViewById(selectedId);
                gstYn = selectedRadioBtn.getText().toString().trim();
                if (gstYn.equals("YES")) {
                    radion.setEnabled(false);
                    editor.putString("k", "YES");
                    editor.commit();
                } else {
                    radios.setEnabled(false);
                    editor.putString("k", "NO");
                    editor.commit();
                }
                if (qus.length() > 0 && prs.length() > 0) {
                    int qu = Integer.parseInt(qus);
                    int pr = Integer.parseInt(prs);
                    int amt = qu * pr;
                    int gstamt = 0;
                    gstamt = (int) (amt * .05);
                    amountEt.setText("" + amt);
                    if (gstYn.equalsIgnoreCase("Yes")) {
                        amountGst.setText("" + gstamt);
                    } else {
                        gstamt = 0;
                        amountGst.setText("" + gstamt);
                    }
                    int total = amt + gstamt;
                    amountTotal.setText("" + total);


                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        add_cart_btn = (Button) findViewById(R.id.add_cart_btn);
        add_cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGst.getCheckedRadioButtonId();
                selectedRadioBtn = (RadioButton) findViewById(selectedId);
                gstYn = selectedRadioBtn.getText().toString().trim();
                // find the radiobutton by returned id
                CartBean cartBean = new CartBean();
                cart_count = (TextView) findViewById(R.id.cart_count);
                cmName = company_name_spinner.getSelectedItem().toString().trim();
                String proTypN = product_type_spinner.getSelectedItem().toString().trim();
                int indx = catArrayList.indexOf(proTypN);
                proTyp = catIdArrayList.get(indx);
                quan = quantity.getText().toString().trim();
                pric = price_kg.getText().toString().trim();
                amount = amountEt.getText().toString().trim();
                sgst = amountGst.getText().toString().trim();
                samountTotal = amountTotal.getText().toString().trim();
//                int amt = Integer.parseInt(amount);
//                int gst = Integer.parseInt(sgst);
//                totalAMT = totalAMT + amt;
//                totalGST = totalGST + gst;

                cartBean.setCmpnyName(cmName);
                cartBean.setGstYesNo(gstYn);
                cartBean.setProduType(proTyp);
                cartBean.setQualityKg(quan);
                cartBean.setPriceprKg(pric);
                cartBean.setAmounttot(amount);
                cartBean.setGstamt(sgst);
                cartBean.setTotalamt(samountTotal);
                AppConfigClass.cardBeanAl.add(cartBean);
                showToast("Item added into cart successfully");
                quantity.setText("");
                price_kg.setText("");
                amountEt.setText("");
                amountGst.setText("");
                amountTotal.setText("");
                cartCount++;
                cart_count.setText("" + cartCount);

            }
        });

        view_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                cart_adapter= new CartAdapter(AppConfigClass.cardBeanAl,getApplicationContext());
                if (cart_count.getText().toString().equals("")) {

                    Toast.makeText(conx, "Cart is Empty", Toast.LENGTH_SHORT).show();

                } else {
                    setGtotalAmt();
                    cart_adapter_new = new CartAdapterNew(conx, AppConfigClass.cardBeanAl);
                    lv_cart.setAdapter(cart_adapter_new);
                    new_sale_form.setVisibility(View.GONE);
                    cart_view_form.setVisibility(View.VISIBLE);
                }

            }
        });

        mGetCategoryTask = new GetCategoryTask();
        mGetCategoryTask.execute();


        CNlist.clear();
        CNlist.add("-Select-");
        Collections.sort(Constants.customerListAl);
        for (String cn : Constants.customerListAl) {
            CNlist.add(cn);
        }
        CNSpinnerAdapter = new ArrayAdapter<String>
                (conx, android.R.layout.simple_spinner_item, CNlist) {
            //By using this method we will define how
            //the text appears before clicking a spinner
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //   ((TextView) v).setTextColor(Color.parseColor("#E30D81"));
                return v;
            }

            //By using this method we will define
            //how the listview appears after clicking a spinner
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView,
                        parent);
                v.setBackgroundColor(Color.parseColor("#8BC34A"));
                ((TextView) v).setTextColor(Color.parseColor("#000000"));
                return v;
            }
        };
        CNSpinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        company_name_spinner.setAdapter(CNSpinnerAdapter);


    }









    private void setGtotalAmt() {
        int tot = 0;
        for (CartBean cb : AppConfigClass.cardBeanAl) {
            tot = tot + Integer.parseInt(cb.getTotalamt());
        }
        gtotal.setText("" + tot);
    }

    private void init() {

//        cart_adapter_new= new CartAdapter(AppConfigClass.cardBeanAl,getApplicationContext());
        cart_adapter_new = new CartAdapterNew(conx, AppConfigClass.cardBeanAl);
        lv_cart = (ListView) findViewById(R.id.lv_cart);
        lv_cart.setAdapter(cart_adapter_new);

    }


    public class GetCategoryTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {

            showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String resp = "";
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
            try {
                JSONArray jsArray = new JSONArray(result);
                JSONObject jsObj;
                int len = jsArray.length();
                catArrayList.clear();
                catIdArrayList.clear();
                catArrayList.add("-Select-");
                for (int i = 0; i < len; i++) {
                    jsObj = jsArray.getJSONObject(i);

                    String name = jsObj.getString("name");
                    String cid = jsObj.getString("id");

                    catIdArrayList.add(cid);
                    catArrayList.add(name);
                }

                Collections.sort(catArrayList);
                CASpinnerAdapter = new ArrayAdapter<String>
                        (conx, android.R.layout.simple_spinner_item, catArrayList) {
                    //By using this method we will define how
                    //the text appears before clicking a spinner
                    public View getView(int position, View convertView,
                                        ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);
                        ((TextView) v).setTextColor(Color.parseColor("#000000"));
                        return v;
                    }

                    //By using this method we will define
                    //how the listview appears after clicking a spinner
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View v = super.getDropDownView(position, convertView,
                                parent);
                        v.setBackgroundColor(Color.parseColor("#8BC34A"));
                        ((TextView) v).setTextColor(Color.parseColor("#000000"));
                        return v;
                    }
                };
                CASpinnerAdapter.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item);
                product_type_spinner.setAdapter(CASpinnerAdapter);

            } catch (Exception e) {
                Log.e("ADD CATEGORY", "Error is " + e.toString());
            }

            showProgress(false);

        }

        @Override
        protected void onCancelled() {
            mGetCategoryTask = null;
            showProgress(false);
        }
    }


    public class PostNewSalesTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {

            // showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            String resp = "";
            try {
                String URL = AppConfigClass.newSalesEntryURL;


                HttpClient httpclient = new DefaultHttpClient();
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                // put the values of id and name in that variable
                nameValuePairs.add(new BasicNameValuePair("all_arraylist", jsObjArray.toString()));
                nameValuePairs.add(new BasicNameValuePair("username",ordby));
                nameValuePairs.add(new BasicNameValuePair("customer_id", customer_id));
                nameValuePairs.add(new BasicNameValuePair("delivery_date", delivery_date));
                nameValuePairs.add(new BasicNameValuePair("salesperson", salesperson));
                nameValuePairs.add(new BasicNameValuePair("gst", gst));
                nameValuePairs.add(new BasicNameValuePair("bno",billno));
                nameValuePairs.add(new BasicNameValuePair("orby",ordby));
                nameValuePairs.add(new BasicNameValuePair("comments", comments));


                HttpPost httppost = new HttpPost(URL);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                InputStream is = entity.getContent();
                BufferedReader reader = new BufferedReader
                        (new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();
                resp = sb.toString();

            } catch (Exception e) {
                resp = e.toString();
            }

            // TODO: register the new account here.
            return resp;
        }

        @Override
        protected void onPostExecute(final String result) {
            mPostNewSalesTask = null;
            try {
                Log.e("ADD CATEGORY", "result is " + result);

            } catch (Exception e) {
                Log.e("ADD CATEGORY", "Error is " + e.toString());
            }

            showProgress(false);

        }

        @Override
        protected void onCancelled() {
            mPostNewSalesTask = null;
            showProgress(false);
        }
    }


    public String converResponseToString(InputStream InputStream) {
        String mResult = "";
        StringBuilder mStringBuilder;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(InputStream, "UTF-8"), 8);
            mStringBuilder = new StringBuilder();
            mStringBuilder.append(reader.readLine() + "\n");
            String line = "0";
            while ((line = reader.readLine()) != null) {
                mStringBuilder.append(line + "\n");
            }
            InputStream.close();
            mResult = mStringBuilder.toString();
            return mResult;
        } catch (Exception e) {
            return mResult;
        }
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


            mnew_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            mnew_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mnew_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mnew_progress.setVisibility(show ? View.VISIBLE : View.GONE);

        }
    }

    private void showToast(String msg) {
        Toast.makeText(conx, msg, Toast.LENGTH_LONG).show();
    }

    public TextView conf_tv;
    public Dialog conf_dialog;
    private int delete_position = 0;

    public void showConfirmDialog(String msg, final int position) {


        conf_dialog = new Dialog(conx);
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
                AppConfigClass.cardBeanAl.remove(position);
                cart_adapter_new.notifyDataSetChanged();
                cartCount--;
                setGtotalAmt();
//                init();


            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                conf_dialog.dismiss();
                cart_adapter_new.notifyDataSetChanged();
                setGtotalAmt();
            }
        });
        if (conf_dialog != null) {
            conf_dialog.show();
        }

    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            if (cart_view_form.getVisibility() == View.VISIBLE) {
                new_sale_form.setVisibility(View.VISIBLE);
                cart_view_form.setVisibility(View.GONE);
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}









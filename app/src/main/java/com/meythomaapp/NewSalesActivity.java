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
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewSalesActivity extends Fragment {
    private GetCategoryTask mGetCategoryTask = null;
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
    String cmName, gstYn, proTyp, quan, pric, amount, sgst, samountTotal, billno, ordby;
    int cartCount = 0;
    public static int gTotalAmt = 0;
    private ListView lv_cart;
    private CartAdapterNew cart_adapter_new;
    private LinearLayout new_sale_form, cart_view_form;
    private int mYear, mMonth, mDay;
    String username;
    String visitedCompany;
    String customer_id;
    String delivery_date;
    String salesperson;
    String gst;
    String comments;
    View view;
    LinearLayout getNew_sale_form;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_new_sales, container, false);
        conx = this;
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("New Sales");
        getNew_sale_form=(LinearLayout)view.findViewById(R.id.new_sale_form);
        mnew_progress = view.findViewById(R.id.new_progress);
        CNlist = new ArrayList<String>();
        getNew_sale_form.setEnabled(true);
        view_cart = (ImageView)view. findViewById(R.id.view_cart);
        cart_count = (TextView) view.findViewById(R.id.cart_count);
        radios = (RadioButton) view.findViewById(R.id.radioSgst);
        radion = (RadioButton) view.findViewById(R.id.radioNgst);
        company_name_spinner = (Spinner) view.findViewById(R.id.company_name_spinner);
        product_type_spinner = (Spinner)view. findViewById(R.id.product_type_spinner);
        order_submit_btn = (Button)view. findViewById(R.id.order_submit_btn);
        bill = (EditText)view. findViewById(R.id.billno);
        ordertaken = (Spinner)view. findViewById(R.id.ordertaken);
        billno = bill.getText().toString();
        String[] ordtak = {"-Order By-", "Hussian", "SivaPrakash", "Gowtham", "Vaisul", "Ganesh", "Seenu"};
        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, ordtak) {
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
                v.setBackgroundColor(Color.parseColor("#239B56"));
                ((TextView) v).setTextColor(Color.parseColor("#FFFFFF"));
                return v;
            }
        };

        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ordertaken.setAdapter(stringArrayAdapter);
        gtotal = (TextView)view. findViewById(R.id.gtotal);
        gTotalAmt = 0;
        new_sale_form = (LinearLayout) view.findViewById(R.id.new_sale_form);
        cart_view_form = (LinearLayout) view.findViewById(R.id.cart_view_form);
        lv_cart = (ListView)view. findViewById(R.id.lv_cart);
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lv_cart,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    String msg = "Are you sure you want to Delete Item from Cart";
                                    showConfirmDialog(msg, position);
                                }
                            }
                        });
        lv_cart.setOnTouchListener(touchListener);
        lv_cart.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());
        order_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    jsObjArray = new ArrayList<>();
                    jsObjArray.clear();
                    ObjectMapper mapperObj;//= new ObjectMapper();
                    for (CartBean cbObj : AppConfigClass.cardBeanAl) {
                        mapperObj = new ObjectMapper();
                        String jsonStr = mapperObj.writeValueAsString(cbObj);
                        jsObjArray.add(jsonStr);
                    }
                    salesperson = "1";
                    username = "hussain";
                    Log.d("jsonStringresponse",salesperson+username);
                    String visitedCompany = company_name_spinner.getSelectedItem().toString().trim();
                    ordby = ordertaken.getSelectedItem().toString().trim();
                    int companyIndex = Constants.customerListAl.indexOf(visitedCompany);
                    customer_id = Constants.customerIdListAl.get(companyIndex);
                    comments = et_comments.getText().toString();
                    delivery_date = in_date.getText().toString();
                    gst = gstYn;
                    Toast.makeText(getActivity(), customer_id, Toast.LENGTH_SHORT).show();
                    Log.e("***********************", jsObjArray.toString());
                    postnewsaletoserver();
                    in_date.setText("");
                    bill.setText("");
                    et_comments.setText("");

                } catch (Exception e) {
                }
            }
        });
        radioGst = (RadioGroup) view.findViewById(R.id.radioGst);
        quantity = (EditText)view. findViewById(R.id.quantity);
        price_kg = (EditText) view.findViewById(R.id.price_kg);
        amountEt = (EditText) view.findViewById(R.id.amountEt);
        amountGst = (EditText) view.findViewById(R.id.amountGst);
        amountTotal = (EditText) view.findViewById(R.id.amountTotal);
        et_comments = (EditText)view. findViewById(R.id.et_comments);
        amountEt.setEnabled(false);
        amountGst.setEnabled(false);
        amountTotal.setEnabled(false);
        amountEt.setText("0");
        amountGst.setText("0");
        amountTotal.setText("0");
        in_date = (EditText) view.findViewById(R.id.in_date);
        in_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
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
                selectedRadioBtn = (RadioButton) view.findViewById(selectedId);
                gstYn = selectedRadioBtn.getText().toString().trim();
                Toast.makeText(getActivity(), gstYn, Toast.LENGTH_SHORT).show();
                if (gstYn.equals("YES")) {
                    radion.setEnabled(false);

                } else {
                    radios.setEnabled(false);


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
        add_cart_btn = (Button) view.findViewById(R.id.add_cart_btn);
        add_cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGst.getCheckedRadioButtonId();
                selectedRadioBtn = (RadioButton) view.findViewById(selectedId);
                CartBean cartBean = new CartBean();
                cart_count = (TextView) view.findViewById(R.id.cart_count);
                cmName = company_name_spinner.getSelectedItem().toString().trim();
                String proTypN = product_type_spinner.getSelectedItem().toString().trim();
                int indx = catArrayList.indexOf(proTypN);
                proTyp = catIdArrayList.get(indx);
                quan = quantity.getText().toString().trim();
                pric = price_kg.getText().toString().trim();
                amount = amountEt.getText().toString().trim();
                sgst = amountGst.getText().toString().trim();
                samountTotal = amountTotal.getText().toString().trim();
                cartBean.setCmpnyName(cmName);
                cartBean.setGstYesNo(gstYn);
                cartBean.setProduType(proTyp);
                cartBean.setQualityKg(quan);
                cartBean.setPriceprKg(pric);
                cartBean.setAmounttot(amount);
                cartBean.setGstamt(sgst);
                cartBean.setTotalamt(samountTotal);
                Log.d("cartbeanproducts",proTyp);
                AppConfigClass.cardBeanAl.add(cartBean);
                quantity.setText("");
                price_kg.setText("");
                amountEt.setText("");
                amountGst.setText("");
                amountTotal.setText("");
                setGtotalAmt();
                cart_adapter_new = new CartAdapterNew(getActivity(), AppConfigClass.cardBeanAl);
                lv_cart.setAdapter(cart_adapter_new);
            }
        });
        mGetCategoryTask = new GetCategoryTask();
        mGetCategoryTask.execute();
        CNlist.clear();
        CNlist.add("-Select-");
        for (String cn : Constants.customerListAl) {
            CNlist.add(cn);
        }
        CNSpinnerAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, CNlist) {
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                return v;
            }

            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView,
                        parent);
                v.setBackgroundColor(Color.parseColor("#239B56"));
                ((TextView) v).setTextColor(Color.parseColor("#FFFFFF"));
                return v;
            }
        };
        CNSpinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        company_name_spinner.setAdapter(CNSpinnerAdapter);
        return view;
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
        cart_adapter_new = new CartAdapterNew(getActivity(), AppConfigClass.cardBeanAl);
        lv_cart = (ListView) view.findViewById(R.id.lv_cart);
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


                CASpinnerAdapter = new ArrayAdapter<String>
                        (getActivity(), android.R.layout.simple_spinner_item, catArrayList) {
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
                        v.setBackgroundColor(Color.parseColor("#239B56"));
                        ((TextView) v).setTextColor(Color.parseColor("#FFFFFF"));
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
            getNew_sale_form.setEnabled(true);

        }

        @Override
        protected void onCancelled() {
            mGetCategoryTask = null;
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
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    public TextView conf_tv;
    public Dialog conf_dialog;
    private int delete_position = 0;

    public void showConfirmDialog(String msg, final int position) {


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
                getActivity().finish();
            }
            return true;
        }

        return super.getActivity().onKeyDown(keyCode, event);
    }

    void postnewsaletoserver() {
        showProgress(true);
        Toast.makeText(getActivity(), "working fine", Toast.LENGTH_SHORT).show();
//        String path = "all_arraylist=" + jsObjArray.toString() + "&username=" + ordby + "&customer_id=" + customer_id + "&delivery_date=" + delivery_date + "&salesperson=" + salesperson + "&gst=" + gst + "&bno=" + billno + "&orby=" + ordby + "&comments=" + comments;
//        Log.d("retrivedatafromserver", path);
        StringRequest request = new StringRequest(Request.Method.POST, AppConfigClass.newSalesEntryURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                showProgress(false);
                Toast.makeText(getActivity(), s.toString(), Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("all_arraylist", jsObjArray.toString());
                params.put("username", ordby);
                params.put("customer_id", customer_id);
                params.put("delivery_date", delivery_date);
                params.put("salesperson", salesperson);
                params.put("gst", gst);
                params.put("bno", billno);
                params.put("orby", ordby);
                params.put("comments", comments);
                Log.d("paramsdata",params.toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);


    }


}









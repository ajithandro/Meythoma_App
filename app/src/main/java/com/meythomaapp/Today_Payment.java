package com.meythomaapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Today_Payment extends Fragment {
    View view;
    SwipeRefreshLayout swipeRefreshLayout;
    private int mYear, mMonth, mDay;
    ProgressDialog progressDialog;
    Button paygst, paynongst;
    ArrayList paytotal, order_id, buyerad, orderDate, deliveryDate, billno, companyName, productDetails, totalAmount, gstamt, ordertakenby, totalamt, kgdetails, orderstatus, paymentstatus, paybalance, paysno, paydate, payamount;
    StringBuilder stringproduct, stringvol, stringrate, totalstring, strsno, strdate, stramt;
    float sumproduct, gstval, sumtotal ;
    RecyclerView recyclerViewpay;
    LinearLayout paylin;
    EditText orderdateinput;
    String strDate, ordsts;
    String[] splitproduct;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_today__payment, container, false);
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Today Payments");
        paylin = (LinearLayout) view.findViewById(R.id.paylin);
        paygst = (Button) view.findViewById(R.id.paygstbtn);
        paynongst = (Button) view.findViewById(R.id.paynongstbtn);
        paylin.setVisibility(View.INVISIBLE);
        recyclerViewpay = (RecyclerView) view.findViewById(R.id.payorder);
        orderdateinput = (EditText) view.findViewById(R.id.orderdateinput);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipepay);
        orderdateinput.requestFocus();
        orderdateinput.setShowSoftInputOnFocus(false);
        declarearrays();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd ");
        strDate = mdformat.format(calendar.getTime());
        ordsts = "nongst";
        getpayments(ordsts, strDate);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getpayments(ordsts, strDate);
            }
        });
        orderdateinput.setOnClickListener(new View.OnClickListener() {
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
                                orderdateinput.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                getpayments(ordsts, orderdateinput.getText().toString());
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        paygst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ordsts = "gst";
                getpayments(ordsts, strDate);
            }
        });
        paynongst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ordsts = "nongst";
                getpayments(ordsts, strDate);
            }
        });
        return view;
    }

    void getpayments(final String type, final String strDate) {
        showprogress();
        cleararrays();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfigClass.payments + "?ty=" + type + "&date=" + strDate + " 00:00:00", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    JSONArray jsonArray1 = jsonObject1.getJSONArray("project_details");
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject obj = jsonArray1.getJSONObject(i);
                        orderDate.add(obj.getString("order_date"));
                        deliveryDate.add(obj.getString("delivery_date"));
                        companyName.add(obj.getString("company_name"));
                        ordertakenby.add(obj.getString("created_by"));
                        orderstatus.add(obj.getString("order_status"));
                        buyerad.add(obj.getString("company_address"));
                        totalamt.add(obj.getString("paidAmount"));
                        order_id.add(obj.getString("order_id"));
                        if (obj.getString("status").equals("10")) {
                            paymentstatus.add("Completed");
                        } else {
                            paymentstatus.add("Pending");
                        }
                        paybalance.add(obj.getString("balanceAmount"));
                        billno.add(obj.getString("billNumber"));
                        String productarray = obj.getString("products");
                        if (productarray.contains(",")) {
                            stringproduct = new StringBuilder();
                            stringvol = new StringBuilder();
                            stringrate = new StringBuilder();
                            String[] prod = productarray.split(",");
                            for (int k = 0; k < prod.length; k++) {
                                String[] splitproduct = prod[k].split(":");
                                stringproduct.append("\n" + splitproduct[0].trim());
                                stringrate.append("\n" + splitproduct[1].trim());
                                stringvol.append("\n" + splitproduct[2].trim());
                                sumproduct = Float.parseFloat(splitproduct[1]) * Float.parseFloat(splitproduct[2]);
                                sumtotal = sumtotal + sumproduct;
                            }

                            if (obj.getString("is_tax").equals("1")) {
                                gstval = (sumtotal * 5) / 100;
                                gstamt.add(String.valueOf(gstval) + " (yes)");
                                float sum = sumtotal + gstval;
                                paytotal.add(String.valueOf(sum));
                            } else {
                                gstamt.add("0 (No)");
                                paytotal.add(String.valueOf(sumtotal));

                            }
                            sumtotal=0;
                            productDetails.add(stringproduct.toString());
                            kgdetails.add(stringvol.toString());
                            totalAmount.add(stringrate.toString());
                        } else {
                            stringproduct = new StringBuilder();
                            stringvol = new StringBuilder();
                            stringrate = new StringBuilder();
                            totalstring = new StringBuilder();
                            String[] splitproduct = productarray.split(":");
                            stringproduct.append("\n" + splitproduct[0].trim());
                            stringrate.append("\n" + splitproduct[1].trim());
                            stringvol.append("\n" + splitproduct[2].trim());
                            sumproduct = Float.parseFloat(splitproduct[1]) * Float.parseFloat(splitproduct[2]);
                            if (obj.getString("is_tax").equals("1")) {
                                gstval = (sumproduct * 5) / 100;
                                gstamt.add(String.valueOf(gstval) + " (yes)");
                                float sum = sumproduct + gstval;
                                paytotal.add(String.valueOf(sum));
                            } else { //totalamt.add(obj.getString("paidAmount"));
                                // totalamt.add(String.valueOf(sum));
                                gstamt.add("0 (No)");
                                paytotal.add(String.valueOf(sumproduct));

                            }
                            productDetails.add(stringproduct.toString());
                            kgdetails.add(stringvol.toString());
                            totalAmount.add(stringrate.toString());
                        }

                    }


                    progressDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);

                    Toast.makeText(getActivity(), "Total Record's : " + billno.size(), Toast.LENGTH_SHORT).show();
                    if (billno.size() == 0) {
                        paylin.setVisibility(View.VISIBLE);
                        recyclerViewpay.setVisibility(View.INVISIBLE);
                    } else {
                        paylin.setVisibility(View.INVISIBLE);
                        recyclerViewpay.setVisibility(View.VISIBLE);
                    }


                } catch (Exception e) {

                }
                try {
                    JSONObject jsonObject2 = new JSONObject(response);
                    JSONArray jsonArray2 = jsonObject2.getJSONArray("project_details1");
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject obj1 = jsonArray2.getJSONObject(i);


                        Log.d("Kalil", obj1.getString("GROUP_CONCAT(DISTINCT DATE_FORMAT(chequeDate, '%y-%m-%d'), ': ', paidAmount SEPARATOR ', ')"));
                        String productarray = obj1.getString("GROUP_CONCAT(DISTINCT DATE_FORMAT(chequeDate, '%y-%m-%d'), ': ', paidAmount SEPARATOR ', ')");

                        if (productarray.contains(",")) {
                            strsno = new StringBuilder();
                            strdate = new StringBuilder();
                            stramt = new StringBuilder();
                            String[] prod = productarray.split(",");
                            for (int k = 0; k < prod.length; k++) {
                                splitproduct = prod[k].split(":");

                                        strdate.append("\n" + splitproduct[0].trim());
                                stramt.append("\n" + splitproduct[1].trim());

                            }
                            for (int o=1;o<=splitproduct.length;o++) {
                                strsno.append("\n" + String.valueOf(o));
                            }
                            paysno.add(strsno.toString());
                            paydate.add(strdate.toString());
                            payamount.add(stramt.toString());


                        } else {
                            strsno = new StringBuilder();
                            strdate = new StringBuilder();
                            stramt = new StringBuilder();
                           splitproduct = productarray.split(":");

                            strdate.append("\n" + splitproduct[0].trim());
                            stramt.append("\n" + splitproduct[1].trim());
                            for (int o=1;o<splitproduct.length;o++) {
                                strsno.append("\n" + String.valueOf(o));
                            }
                            paysno.add(strsno.toString());
                            paydate.add(strdate.toString());
                            payamount.add(stramt.toString());
                        }


                    }
                } catch (Exception e) {

                }
                displaydata();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);


    }

    void declarearrays() {
        order_id = new ArrayList();
        buyerad = new ArrayList();
        orderDate = new ArrayList();
        deliveryDate = new ArrayList();
        billno = new ArrayList();
        companyName = new ArrayList();
        productDetails = new ArrayList();
        totalAmount = new ArrayList();
        gstamt = new ArrayList();
        ordertakenby = new ArrayList();
        totalamt = new ArrayList();
        kgdetails = new ArrayList();
        orderstatus = new ArrayList();
        paymentstatus = new ArrayList();
        paybalance = new ArrayList();
        paysno = new ArrayList();
        paydate = new ArrayList();
        payamount = new ArrayList();
        paytotal = new ArrayList();
    }

    void showprogress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching All Order's List..");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    void cleararrays() {
        orderDate.clear();
        order_id.clear();
        deliveryDate.clear();
        billno.clear();
        productDetails.clear();
        gstamt.clear();
        companyName.clear();
        ordertakenby.clear();
        totalamt.clear();
        buyerad.clear();
        totalAmount.clear();
        orderstatus.clear();
        kgdetails.clear();
        paymentstatus.clear();
        paybalance.clear();
        paysno.clear();
        paydate.clear();
        payamount.clear();
        paytotal.clear();
    }

    void displaydata() {
        LinearLayoutManager linearLayoutManagertwo = new LinearLayoutManager(getActivity());
        recyclerViewpay.setLayoutManager(linearLayoutManagertwo);
        PaymentAdapter paymentAdapter = new PaymentAdapter(getActivity(), order_id, buyerad, orderDate, deliveryDate, billno, companyName, productDetails, totalAmount, gstamt, ordertakenby, totalamt, kgdetails, orderstatus, paymentstatus, paybalance, paysno, paydate, payamount, paytotal);
        paymentAdapter.notifyDataSetChanged();
        recyclerViewpay.setAdapter(paymentAdapter);
    }


}

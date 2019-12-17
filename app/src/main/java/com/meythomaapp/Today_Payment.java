package com.meythomaapp;

import android.app.ProgressDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

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
    ProgressDialog progressDialog;
    Button paygst,paynongst;
    ArrayList buyerad,orderDate, deliveryDate, billno, companyName, productDetails, totalAmount, gstamt, ordertakenby, totalamt, kgdetails, orderstatus,paymentstatus, paybalance;
    StringBuilder stringproduct, stringvol, stringrate, totalstring;
    float sumproduct, gstval, sumtotal = 0;
    RecyclerView recyclerViewpay;
    LinearLayout paylin;
    String strDate;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_today__payment, container, false);
        paylin=(LinearLayout)view.findViewById(R.id.paylin);
        paygst=(Button)view.findViewById(R.id.paygstbtn);
        paynongst=(Button)view.findViewById(R.id.paynongstbtn);
        paylin.setVisibility(View.INVISIBLE);
        recyclerViewpay=(RecyclerView)view.findViewById(R.id.payorder);

        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Invoice Details");
        progressDialog = new ProgressDialog(getActivity());
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
        paymentstatus=new ArrayList();
        paybalance=new ArrayList();
        progressDialog.setMessage("Fetching Today payments...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd ");
        strDate = mdformat.format(calendar.getTime());

        getpayments("nongst");

        paygst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Fetching Today payments...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                getpayments("gst");
            }
        });
        paynongst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Fetching Today payments...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                getpayments("nongst");
            }
        });


        return view;
    }

    void getpayments(final String type) {
        orderDate.clear();
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST,AppConfigClass.payments+"?ty="+type+"&date="+strDate+" 00:00:00", new Response.Listener<String>() {
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
                            if (obj.getString("status").equals("10")){
                                paymentstatus.add("Completed");
                            }else{
                                paymentstatus.add("Pending");
                            }

                            paybalance.add(obj.getString("balanceAmount"));
                            billno.add(obj.getString("billNumber"));
                            String productarray = obj.getString("GROUP_CONCAT(DISTINCT pc.name, ': ',opd.amount, ': ', opd.product_volume_kg SEPARATOR ', ')");
                            if (productarray.contains(",")) {

                                stringproduct = new StringBuilder();
                                stringvol = new StringBuilder();
                                stringrate = new StringBuilder();
                                String[] prod = productarray.split(",");
                                for (int k = 0; k < prod.length; k++) {

                                    String[] splitproduct = prod[k].split(":");
                                    stringproduct.append("\n" + splitproduct[0]);
                                    stringrate.append("\n"+ splitproduct[1]);
                                    stringvol.append("\n" + splitproduct[2] );
                                    sumproduct = Float.parseFloat(splitproduct[1]) * Float.parseFloat(splitproduct[2]);
                                    sumtotal = sumtotal + sumproduct;

                                }
                                if (obj.getString("is_tax").equals("1")) {
                                    gstval = (sumtotal * 5) / 100;
                                    gstamt.add(String.valueOf(gstval) + "(yes)");
                                    float sum = sumtotal + gstval;
                                    totalamt.add(String.valueOf(sum));

                                } else {
                                    gstamt.add("0(No)");
                                    totalamt.add(String.valueOf(sumtotal));

                                }

                                sumtotal = 0;
                                productDetails.add(stringproduct.toString());
                                kgdetails.add(stringvol.toString());
                                totalAmount.add(stringrate.toString());


                            } else {

                                stringproduct = new StringBuilder();
                                stringvol = new StringBuilder();
                                stringrate = new StringBuilder();
                                totalstring = new StringBuilder();
                                String[] splitproduct = productarray.split(":");

                                stringproduct.append("\n" + splitproduct[0]);
                                stringrate.append("\n"+splitproduct[1]);
                                stringvol.append("\n" + splitproduct[2] );
                                sumproduct = Float.parseFloat(splitproduct[1]) * Float.parseFloat(splitproduct[2]);


                                if (obj.getString("is_tax").equals("1")) {
                                    gstval = (sumproduct * 5) / 100;
                                    gstamt.add(String.valueOf(gstval) + "(yes)");
                                    float sum = sumproduct + gstval;
                                    totalamt.add(String.valueOf(sum));
                                } else {
                                    gstamt.add("0(No)");
                                    totalamt.add(String.valueOf(sumproduct));
                                }

                                productDetails.add(stringproduct.toString());
                                kgdetails.add(stringvol.toString());
                                totalAmount.add(stringrate.toString());
                            }


                            LinearLayoutManager linearLayoutManagertwo = new LinearLayoutManager(getActivity());
                            recyclerViewpay.setLayoutManager(linearLayoutManagertwo);
                            OrdersAdapter ordersAdapter = new OrdersAdapter(getActivity(), buyerad,orderDate, deliveryDate, billno, companyName, productDetails, totalAmount, gstamt, ordertakenby, totalamt, kgdetails, orderstatus,paymentstatus,paybalance);
                            recyclerViewpay.setAdapter(ordersAdapter);
                        }
                        progressDialog.dismiss();

                        Toast.makeText(getActivity(), "Total Record's : " + billno.size(), Toast.LENGTH_SHORT).show();
                        if (billno.size()==0){
                            paylin.setVisibility(View.VISIBLE);
                            recyclerViewpay.setVisibility(View.INVISIBLE);
                        }else{
                            paylin.setVisibility(View.INVISIBLE);
                            recyclerViewpay.setVisibility(View.VISIBLE);
                        }



                } catch (Exception e) {

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);


    }


}

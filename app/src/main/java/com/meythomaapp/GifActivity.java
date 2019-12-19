package com.meythomaapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
public class GifActivity extends Fragment {
    View view;
    RecyclerView listorder;
    ProgressDialog progressDialog;
    ArrayList order_id, buyerad, orderDate, deliveryDate, billno, companyName, productDetails, totalAmount, gstamt, ordertakenby, totalamt, kgdetails, orderstatus, paymentstatus, paybalance;
    StringBuilder stringproduct, stringvol, stringrate, totalstring;
    float sumproduct, gstval, sumtotal = 0;
    LinearLayout giflin;
    Button upgst, upnongst;
    private int REQUEST_CODE_ASK_PERMISSIONS;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_gif, container, false);
        listorder = (RecyclerView) view.findViewById(R.id.orderlist);
        giflin = (LinearLayout) view.findViewById(R.id.giflin);
        upgst = (Button) view.findViewById(R.id.upgst);
        upnongst = (Button) view.findViewById(R.id.upnongst);
        giflin.setVisibility(View.INVISIBLE);
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Upcoming Orders");
        progressDialog = new ProgressDialog(getActivity());
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
        showprogress();
        getvalues("nongst");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                if (Build.VERSION.SDK_INT >= 24) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                }
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        upgst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getvalues("gst");
            }
        });
        upnongst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getvalues("nongst");
            }
        });
        return view;
    }
    void getvalues(final String status) {
        showprogress();
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfigClass.retryurl + "?ty=" + status, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    JSONArray jsonArray1 = jsonObject1.getJSONArray("project_details");
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject obj = jsonArray1.getJSONObject(i);
                        orderDate.add(obj.getString("order_date"));
                        deliveryDate.add(obj.getString("delivery_date"));
                        if (obj.getString("is_tax").equals("1")) {
                            billno.add(obj.getString("invoiceId"));
                        } else {
                            billno.add(obj.getString("nonInvoiceId"));
                        }
                        companyName.add(obj.getString("company_name"));
                        buyerad.add(obj.getString("company_address"));
                        order_id.add(obj.getString("order_id"));
                        ordertakenby.add(obj.getString("created_by"));
                        orderstatus.add(obj.getString("order_status"));
                        paymentstatus.add("Waiting");
                        paybalance.add(" - ");
                        String productarray = obj.getString("GROUP_CONCAT(DISTINCT pc.name, ': ',opd.amount, ': ', opd.product_volume_kg SEPARATOR ', ')");
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
                            if (obj.getString("is_tax").equals(1)) {
                                gstval = (sumtotal * 5) / 100;
                                gstamt.add(String.valueOf(gstval) + " (yes)");
                                float sum = sumtotal + gstval;
                                totalamt.add(String.valueOf(sum));
                            } else {
                                gstamt.add("0 (No)");
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
                            stringproduct.append("\n" + splitproduct[0].trim());
                            stringrate.append("\n" + splitproduct[1].trim());
                            stringvol.append("\n" + splitproduct[2].trim());
                            sumproduct = Float.parseFloat(splitproduct[1]) * Float.parseFloat(splitproduct[2]);
                            if (obj.getString("is_tax").equals(1)) {
                                gstval = (sumproduct * 5) / 100;
                                gstamt.add(String.valueOf(gstval) + " (yes)");
                                float sum = sumproduct + gstval;
                                totalamt.add(String.valueOf(sum));
                            } else {
                                gstamt.add("0 (No)");
                                totalamt.add(String.valueOf(sumproduct));
                            }
                            productDetails.add(stringproduct.toString());
                            kgdetails.add(stringvol.toString());
                            totalAmount.add(stringrate.toString());
                        }
                        LinearLayoutManager linearLayoutManagertwo = new LinearLayoutManager(getActivity());
                        listorder.setLayoutManager(linearLayoutManagertwo);
                        OrdersAdapter ordersAdapter = new OrdersAdapter(getActivity(),order_id,buyerad, orderDate, deliveryDate, billno, companyName, productDetails, totalAmount, gstamt, ordertakenby, totalamt, kgdetails, orderstatus, paymentstatus, paybalance);
                        ordersAdapter.notifyDataSetChanged();
                        listorder.setAdapter(ordersAdapter);
                    }
                    Toast.makeText(getActivity(), "Total Record's : " + billno.size(), Toast.LENGTH_SHORT).show();
                    if (billno.size() == 0) {
                        giflin.setVisibility(View.VISIBLE);
                        listorder.setVisibility(View.INVISIBLE);
                    } else {
                        giflin.setVisibility(View.INVISIBLE);
                        listorder.setVisibility(View.VISIBLE);
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
    void showprogress() {
        progressDialog.setMessage("Fetching Upcoming Order's List..");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
}
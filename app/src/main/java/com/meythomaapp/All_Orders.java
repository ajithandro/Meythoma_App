package com.meythomaapp;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
public class All_Orders extends Fragment {
    View view;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList order_id, buyerad, orderDate, deliveryDate, billno, companyName, totalAmount, gstamt, ordertakenby, totalamt, kgdetails, orderstatus, paymentstatus, paybalance;
    ArrayList productDetails, product;
    StringBuilder stringproduct, stringvol, stringrate, totalstring;
    float sumproduct, gstval, sumtotal = 0;
    RecyclerView listorder;
    AutoCompleteTextView autoname;
    Button allgstbtn, allnongstbtn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_all__orders, container, false);
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("All Orders");
        autoname = (AutoCompleteTextView) view.findViewById(R.id.sn);
        allgstbtn = (Button) view.findViewById(R.id.allgstbtn);
        allnongstbtn = (Button) view.findViewById(R.id.allnongstbtn);
        listorder = (RecyclerView) view.findViewById(R.id.allorderlist);
        declarearray();

        getvalues("nongst");
        sharedPreferences = getActivity().getSharedPreferences("dbdata", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("ty", "nongst");
        editor.commit();
        autoname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getcompany(adapterView.getItemAtPosition(i).toString(), sharedPreferences.getString("ty", null));
                autoname.setText("");
            }
        });
        allgstbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getvalues("gst");
                editor.putString("ty", "gst");
                editor.commit();
            }
        });
        allnongstbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getvalues("nongst");
                editor.putString("ty", "nongst");
                editor.commit();
            }
        });
        final ArrayAdapter<String> shopAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,companyName);
        autoname.setThreshold(2);
        autoname.setAdapter(shopAdapter);
        return view;
    }
    void getvalues(final String type) {
        showprogress();
        cleararrays();
        companyName.clear();
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfigClass.retryallorders + "?ty=" + type, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                        ordertakenby.add(obj.getString("created_by"));
                        orderstatus.add(obj.getString("order_status"));
                        order_id.add(obj.getString("order_id"));
                        buyerad.add(obj.getString("company_address"));
                        if (obj.getString("status").equals("10")) {
                            paymentstatus.add("Completed");
                        } else {
                            paymentstatus.add("Pending");
                        }
                        paybalance.add(obj.getString("balanceAmount"));
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
                            if (obj.getString("is_tax").equals("1")) {
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
                            if (obj.getString("is_tax").equals("1")) {
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
                        displaydata();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), "Total Record's : " + billno.size(), 10000 * 60).show();
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    void getcompany(final String statusdata, final String type) {
        showprogress();
        cleararrays();
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, AppConfigClass.shoporders + "?s=" + statusdata + "&ty=" + type, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
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
                        ordertakenby.add(obj.getString("created_by"));
                        order_id.add(obj.getString("order_id"));
                        orderstatus.add(obj.getString("order_status"));
                        buyerad.add(obj.getString("company_address"));
                        if (obj.getString("status").equals("10")) {
                            paymentstatus.add("Completed");
                        } else {
                            paymentstatus.add("Pending");
                        }
                        paybalance.add(obj.getString("balanceAmount"));
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
                            if (obj.getString("is_tax").equals("1")) {
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
                            if (obj.getString("is_tax").equals("1")) {
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
                        displaydata();
                    }
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Total Record's : " + billno.size(), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });
        RequestQueue requestQueue1 = Volley.newRequestQueue(getActivity());
        requestQueue1.add(stringRequest1);
    }
    private void showprogress() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching All Order's List..");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void displaydata() {
        LinearLayoutManager linearLayoutManagertwo = new LinearLayoutManager(getActivity());
        listorder.setLayoutManager(linearLayoutManagertwo);
        OrdersAdapter ordersAdapter = new OrdersAdapter(getActivity(), order_id, buyerad, orderDate, deliveryDate, billno, companyName, productDetails, totalAmount, gstamt, ordertakenby, totalamt, kgdetails, orderstatus, paymentstatus, paybalance);
        ordersAdapter.notifyDataSetChanged();
        listorder.setAdapter(ordersAdapter);
    }
    private void cleararrays() {
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
    }
    void declarearray() {
        order_id = new ArrayList();
        buyerad = new ArrayList();
        orderDate = new ArrayList();
        deliveryDate = new ArrayList();
        billno = new ArrayList();
        companyName = new ArrayList();
        productDetails = new ArrayList();
        product = new ArrayList<String>();
        totalAmount = new ArrayList();
        gstamt = new ArrayList();
        ordertakenby = new ArrayList();
        totalamt = new ArrayList();
        kgdetails = new ArrayList();
        orderstatus = new ArrayList();
        paymentstatus = new ArrayList();
        paybalance = new ArrayList();
    }

}

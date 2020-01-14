package com.meythomaapp;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Today_Payment extends Fragment {
    View view;
    SwipeRefreshLayout swipeRefreshLayout;
    private int mYear, mMonth, mDay;
    ProgressDialog progressDialog;
    Button paygst, paynongst;
    ArrayList s_no, paytotal, order_id, buyerad, orderDate, deliveryDate, billno, companyName, productDetails, totalAmount, gstamt, ordertakenby, totalamt, kgdetails, orderstatus, paymentstatus, paybalance, paysno, paydate, payamount;
    StringBuilder stringproduct, stringvol, stringrate, totalstring, strsno, strdate, stramt;
    float sumproduct, gstval, sumtotal = 0;
    RecyclerView recyclerViewpay;
    LinearLayout paylin;
    EditText orderdateinput;
    String strDate, ordsts;
    String[] splitproduct;
    ImageView todaypdf;
    private File pdfFile;
    double todaypaid=0;

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
        todaypdf = (ImageView) view.findViewById(R.id.todayreport);
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
                orderdateinput.setText(" ");
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
        todaypdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Share PDF Report to whatsapp");
                builder.setMessage("You can share today payment Report directly on whatsapp...");
                builder.setCancelable(true);
                builder.setPositiveButton("Make Report", new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        todaypaymentdetails();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
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
                if (response.equals("No data Found")) {
                    paylin.setVisibility(View.VISIBLE);
                    recyclerViewpay.setVisibility(View.INVISIBLE);
                    progressDialog.dismiss();
                } else {
                    try {
                        paylin.setVisibility(View.INVISIBLE);
                        recyclerViewpay.setVisibility(View.VISIBLE);
                        JSONObject jsonObject1 = new JSONObject(response);
                        Log.d("projectarray2", "onResponse: " + response);
                        JSONArray jsonArray1 = jsonObject1.getJSONArray("project_details");
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            JSONObject obj = jsonArray1.getJSONObject(i);
                            s_no.add(String.valueOf(i + 1));
                            Log.d("projectarray2", "onResponse: " + jsonArray1);
                            orderDate.add(obj.getString("order_date"));
                            deliveryDate.add(obj.getString("delivery_date"));
                            companyName.add(obj.getString("company_name"));
                            ordertakenby.add(obj.getString("created_by"));
                            orderstatus.add(obj.getString("order_status"));
                            buyerad.add(obj.getString("company_address"));
                            totalamt.add(obj.getString("paidAmount"));
                            order_id.add(obj.getString("order_id"));
                            Toast.makeText(getActivity(), obj.getString("products"), Toast.LENGTH_SHORT).show();
                            if (obj.getString("status").equals("10")) {
                                paymentstatus.add("Completed");
                            } else {
                                paymentstatus.add("Pending");
                            }
                            paybalance.add(obj.getString("balanceAmount"));
                            billno.add(obj.getString("billNumber"));
                            if (obj.getString("products").contains(",")) {
                                stringproduct = new StringBuilder();
                                stringvol = new StringBuilder();
                                stringrate = new StringBuilder();
                                String[] prod = obj.getString("products").split(",");
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
                                sumtotal = 0;
                                productDetails.add(stringproduct.toString());
                                kgdetails.add(stringvol.toString());
                                totalAmount.add(stringrate.toString());
                            } else {

                                stringproduct = new StringBuilder();
                                stringvol = new StringBuilder();
                                stringrate = new StringBuilder();
                                totalstring = new StringBuilder();
                                String[] splitproduct = obj.getString("products").split(":");
                                stringproduct.append("\n" + splitproduct[0].trim());
                                stringrate.append("\n" + splitproduct[1].trim());
                                stringvol.append("\n" + splitproduct[2].trim());
                                sumproduct = Float.parseFloat(splitproduct[1]) * Float.parseFloat(splitproduct[2]);
                                productDetails.add(stringproduct.toString());
                                kgdetails.add(stringvol.toString());
                                totalAmount.add(stringrate.toString());
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
                            }
                        }
                        progressDialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Total Record's : " + billno.size(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                    }
                    try {
                        JSONObject jsonObject2 = new JSONObject(response);
                        JSONArray jsonArray2 = jsonObject2.getJSONArray("project_details1");
                        Log.d("projectarray1", "onResponse: " + jsonArray2);
                        for (int i = 0; i < jsonArray2.length(); i++) {
                            JSONObject obj1 = jsonArray2.getJSONObject(i);
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
                                for (int o = 1; o <= prod.length; o++) {
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
                                strsno.append("\n" + "1");
                                paysno.add(strsno.toString());
                                paydate.add(strdate.toString());
                                payamount.add(stramt.toString());
                            }
                        }
                    } catch (Exception e) {
                    }
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
        s_no = new ArrayList();
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

    void todaypaymentdetails() {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/MeythomaDailyReports");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i("PDF", "Created a new directory for PDF");
        }

        pdfFile = new File(docsFolder.getAbsolutePath(), strDate + " PayReports.pdf");
        try {
            OutputStream output = new FileOutputStream(pdfFile);
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, output);
            document.open();
            Paragraph paragraph = new Paragraph("ESTIMATE", FontFactory.getFont(FontFactory.TIMES_BOLD, 14, BaseColor.BLACK));
            paragraph.setAlignment(paragraph.ALIGN_CENTER);
            document.add(paragraph);
            document.add(new Paragraph(""));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            Paragraph paragraph1 = new Paragraph("DATE : " + strDate, FontFactory.getFont(FontFactory.TIMES_BOLD, 14, BaseColor.BLACK));
            paragraph.setAlignment(paragraph.ALIGN_LEFT);
            document.add(paragraph1);
            document.add(new Paragraph(""));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(""));
            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);
            table.setTotalWidth(575);
            PdfPCell p1 = new PdfPCell(new Phrase("S.NO", FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK)));
            p1.setColspan(1);
            p1.setFixedHeight(20f);
            p1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(p1);
            PdfPCell p2 = new PdfPCell(new Phrase("Shop Name", FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK)));
            p2.setColspan(5);
            p2.setFixedHeight(20f);
            p2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(p2);
            PdfPCell p3 = new PdfPCell(new Phrase("Total", FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK)));
            p3.setColspan(2);
            p3.setHorizontalAlignment(Element.ALIGN_CENTER);
            p3.setFixedHeight(20f);
            table.addCell(p3);
            PdfPCell p4 = new PdfPCell(new Phrase("Paid", FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK)));
            p4.setColspan(2);
            p4.setHorizontalAlignment(Element.ALIGN_CENTER);
            p4.setFixedHeight(20f);
            table.addCell(p4);
            for (int i = 0, j = 0, k = 0, L = 0; i < s_no.size() && j <companyName.size() && k < totalamt.size() && L < paytotal.size(); i++, j++, k++, L++) {
                PdfPCell p5 = new PdfPCell(new Phrase(String.valueOf(s_no.get(i)), FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK)));
                p5.setColspan(1);
                p5.setFixedHeight(20f);
                p5.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(p5);
                PdfPCell p6 = new PdfPCell(new Phrase(String.valueOf(companyName.get(j)), FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK)));
                p6.setColspan(5);
                p6.setFixedHeight(20f);
                table.addCell(p6);
                PdfPCell p8 = new PdfPCell(new Phrase(String.valueOf(paytotal.get(k)), FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
                p8.setColspan(2);
                p8.setFixedHeight(20f);
                p8.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(p8);
                PdfPCell p7 = new PdfPCell(new Phrase(String.valueOf(totalamt.get(L)), FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
                p7.setColspan(2);
                p7.setHorizontalAlignment(Element.ALIGN_CENTER);
                p7.setFixedHeight(20f);
                table.addCell(p7);
                double payt=Double.parseDouble(totalamt.get(L).toString());
                todaypaid = todaypaid + payt;
            }
            PdfPCell p9 = new PdfPCell(new Phrase("", FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK)));
            p9.setColspan(5);
            p9.setFixedHeight(20f);
            p9.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(p9);
            PdfPCell p91 = new PdfPCell(new Phrase("Total paid ", FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK)));
            p91.setColspan(3);
            p91.setFixedHeight(20f);
            p91.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(p91);
            PdfPCell p10 = new PdfPCell(new Phrase(String.valueOf(todaypaid),FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            p10.setColspan(2);
            p10.setHorizontalAlignment(Element.ALIGN_CENTER);
            p10.setFixedHeight(20f);
            table.addCell(p10);
            document.add(table);
            document.add(new Paragraph(""));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", FontFactory.getFont(FontFactory.TIMES_BOLD, 14, BaseColor.BLACK)));

            document.close();
            preview();
        } catch (FileNotFoundException e) {
        } catch (DocumentException e) {
        }

    }

    void preview() {
        Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", pdfFile);
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        getActivity().startActivity(share);
    }

}

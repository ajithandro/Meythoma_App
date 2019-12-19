package com.meythomaapp;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    Context context;
    ProgressDialog progressDialog;
    OrdersAdapter ordersAdapter;
    ArrayList order_id, buyerad, orderDate, deliveryDate, billno, companyName, productDetails, totalAmount, gstamt, ordertakenby, totalamt, kgdetails, orderstatus, paymentstatus, paybalance;
    Paragraph paragraph1;
    String company_name, buyer_address, order_date, invoive_no, totalcoast, outpath;
    private File pdfFile;
    String[] productdata, kgdata, pricedata;
    int totalcost;
    private static final String[] tensNames = {"", " ten", " twenty", " thirty", " forty",
            " fifty", " sixty", " seventy", " eighty", " ninety"};

    private static final String[] numNames = {"", " one", " two", " three", " four", " five",
            " six", " seven", " eight", " nine", " ten", " eleven", " twelve", " thirteen",
            " fourteen", " fifteen", " sixteen", " seventeen", " eighteen", " nineteen"};

    public OrdersAdapter(Context context, ArrayList order_id, ArrayList buyerad, ArrayList orderDate, ArrayList deliveryDate, ArrayList billno, ArrayList companyName, ArrayList productDetails, ArrayList totalAmount, ArrayList gstamt, ArrayList ordertakenby, ArrayList totalamt, ArrayList kgdetails, ArrayList orderstatus, ArrayList paymentstatus, ArrayList paybalance) {
        this.context = context;
        this.order_id = order_id;
        this.buyerad = buyerad;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
        this.billno = billno;
        this.companyName = companyName;
        this.productDetails = productDetails;
        this.totalAmount = totalAmount;
        this.gstamt = gstamt;
        this.ordertakenby = ordertakenby;
        this.totalamt = totalamt;
        this.kgdetails = kgdetails;
        this.orderstatus = orderstatus;
        this.paymentstatus = paymentstatus;
        this.paybalance = paybalance;
    }

    @Override
    public OrdersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.maindesign, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final OrdersAdapter.MyViewHolder holder, final int position) {

        holder.ordertext.setText("Order : " + orderDate.get(position).toString().trim());
        holder.deliverytext.setText("Delivery : " + deliveryDate.get(position).toString().trim());
        holder.billtext.setText("InVoice No : " + billno.get(position).toString().trim());
        holder.cnametext.setText("Shop : " + companyName.get(position).toString().trim());
        holder.producttext.setText(productDetails.get(position).toString() + "\n".trim());
        holder.totaltext.setText(totalAmount.get(position).toString() + "\n".trim());
        holder.ordertakentext.setText("Order by : " + ordertakenby.get(position).toString().trim());
        holder.totalamt.setText("₹ " + totalamt.get(position).toString().trim());
        holder.kg.setText(kgdetails.get(position).toString() + "\n".trim());
        holder.ostatus.setText("Order Status :  " + orderstatus.get(position).toString().trim());
        holder.gsttext.setText("GST : ₹ " + gstamt.get(position).toString().trim());
        holder.paystatus.setText("Payment Status : " + paymentstatus.get(position).toString().trim());
        holder.paybal.setText("Balance : ₹ " + paybalance.get(position).toString().trim());
        if (orderstatus.get(position).toString().trim().equals("Delivered")) {
           holder.layout.setVisibility(View.GONE);
        }
        holder.shareicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Title you wants to share
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Share order status...");
                builder.setMessage("You can share the order status to any application...");
                builder.setCancelable(true);
                builder.setPositiveButton("Share status", new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        String sharebody = "Meythoma International" + "\n" + "Order Date : " + orderDate.get(position).toString() + "\n" +
                                "Delivery : " + deliveryDate.get(position).toString() + "\n" +
                                "Order NO : " + billno.get(position).toString() + "\n" +
                                "Shop : " + companyName.get(position).toString() + "\n" +
                                "Product Details : " + productDetails.get(position).toString() + "/" + kgdetails.get(position).toString() + "/" + totalAmount.get(position).toString() + "/" + "\n" + "GST : " + gstamt.get(position).toString() + "\n" +
                                "Total Amount : " + totalamt.get(position).toString() + "\n" +
                                "Order by : " + ordertakenby.get(position).toString();


                        String sharesub = "Welcome to Easy shopping";
                        intent.putExtra(Intent.EXTRA_SUBJECT, sharesub);
                        intent.putExtra(Intent.EXTRA_TEXT, sharebody);
                        context.startActivity(Intent.createChooser(intent, "Share using"));

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
        holder.updateorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Updated Order Status");
                builder.setMessage("update this order status to Delivered");
                builder.setCancelable(true);
                builder.setPositiveButton("Delivered", new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Status Updating...");
                        progressDialog.show();
                        updateorderstatus("Delivered", order_id.get(position).toString());


                    }
                });
                builder.setNegativeButton("not Delivered", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });


        holder.pdfreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Share PDF Report to whatsapp");
                builder.setMessage("You can share invoice Report directly on whatsapp...");
                builder.setCancelable(true);
                builder.setPositiveButton("Make Report", new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//
                        company_name = companyName.get(position).toString();
                        buyer_address = buyerad.get(position).toString();
                        order_date = orderDate.get(position).toString();
                        invoive_no = billno.get(position).toString();
                        totalcoast = totalamt.get(position).toString();
                        productdata = productDetails.get(position).toString().split("\\n");
                        kgdata = kgdetails.get(position).toString().split("\\n");
                        pricedata = totalAmount.get(position).toString().split("\\n");
                        generatePdf();
                        preview();
//


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

    }

    @Override
    public int getItemCount() {

        return billno.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ordertext, deliverytext, billtext, cnametext, producttext, totaltext, gsttext, ordertakentext, totalamt, kg, ostatus, paystatus, paybal;
        ImageView shareicon, pdfreport, updateorder;
        LinearLayout layout;

        public MyViewHolder(View itemView) {
            super(itemView);
            shareicon = (ImageView) itemView.findViewById(R.id.shareicon);
            ordertext = (TextView) itemView.findViewById(R.id.odate);
            deliverytext = (TextView) itemView.findViewById(R.id.ddate);
            billtext = (TextView) itemView.findViewById(R.id.nobill);
            cnametext = (TextView) itemView.findViewById(R.id.cname);
            ostatus = (TextView) itemView.findViewById(R.id.status);
            producttext = (TextView) itemView.findViewById(R.id.productdetails);
            totaltext = (TextView) itemView.findViewById(R.id.tamou);
            gsttext = (TextView) itemView.findViewById(R.id.gststatus);
            ordertakentext = (TextView) itemView.findViewById(R.id.byorder);
            totalamt = (TextView) itemView.findViewById(R.id.totalamount);
            kg = (TextView) itemView.findViewById(R.id.productvalume);
            paystatus = (TextView) itemView.findViewById(R.id.paystatus);
            paybal = (TextView) itemView.findViewById(R.id.paybal);
            pdfreport = (ImageView) itemView.findViewById(R.id.pdfreport);
            updateorder = (ImageView) itemView.findViewById(R.id.dele);
            layout = (LinearLayout)itemView.findViewById(R.id.linview);

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void generatePdf() {


        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/MeythomaBills");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i("PDF", "Created a new directory for PDF");
        }

        outpath = invoive_no + "_" + company_name + ".pdf";
        pdfFile = new File(docsFolder.getAbsolutePath(), outpath);
        try {
            OutputStream output = new FileOutputStream(pdfFile);
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, output);
            document.open();
            Paragraph paragraph = new Paragraph("ESTIMATE", FontFactory.getFont(FontFactory.TIMES_BOLD, 14, BaseColor.BLACK));
            paragraph.setAlignment(paragraph.ALIGN_CENTER);
            document.add(paragraph);
            document.add(new Paragraph("Customer Copy", FontFactory.getFont(FontFactory.TIMES_BOLD, 14, BaseColor.BLACK)));
            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);
            table.setTotalWidth(575);

            PdfPCell c1 = new PdfPCell(new Phrase(" Buyer : " + company_name + "\n" + " " + buyer_address, FontFactory.getFont(FontFactory.TIMES_BOLD, 14, BaseColor.BLACK)));
            c1.setColspan(7);
            c1.setFixedHeight(50f);
            table.addCell(c1);
            PdfPCell c2 = new PdfPCell(new Phrase(" Date : " +
                    order_date + "\n" + "\n" + " Invoice No : # " + invoive_no, FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, BaseColor.BLACK)));
            c2.setColspan(3);
            c2.setFixedHeight(50f);
            table.addCell(c2);
            PdfPCell c3 = new PdfPCell(new Phrase("S.No", FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            c3.setColspan(1);
            c3.setFixedHeight(20f);
            c3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c3);
            PdfPCell c4 = new PdfPCell(new Phrase("Description", FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            c4.setColspan(4);
            c4.setFixedHeight(20f);
            c4.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c4);
            PdfPCell c5 = new PdfPCell(new Phrase("Quantity", FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            c5.setColspan(1);
            c5.setFixedHeight(20f);
            c5.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c5);
            PdfPCell c6 = new PdfPCell(new Phrase("Rate per Kg", FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            c6.setColspan(2);
            c6.setFixedHeight(20f);
            c6.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c6);
            PdfPCell c7 = new PdfPCell(new Phrase("Amount", FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            c7.setColspan(2);
            c7.setFixedHeight(20f);
            c7.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c7);

            for (int i = 1, j = 1, k = 1; i < productdata.length && j < kgdata.length && k < pricedata.length; i++, j++, k++) {

                PdfPCell c8 = new PdfPCell(new Phrase(String.valueOf(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK)));
                c8.setColspan(1);
                c8.setFixedHeight(20f);
                c8.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c8);
                PdfPCell c9 = new PdfPCell(new Phrase("   " + productdata[i].trim(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK)));
                c9.setColspan(4);
                c9.setFixedHeight(20f);
                table.addCell(c9);
                PdfPCell c10 = new PdfPCell(new Phrase(kgdata[j].trim(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK)));
                c10.setColspan(1);
                c10.setHorizontalAlignment(Element.ALIGN_CENTER);
                c10.setFixedHeight(20f);
                table.addCell(c10);
                PdfPCell c11 = new PdfPCell(new Phrase(pricedata[k].trim(), FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
                c11.setColspan(2);
                c11.setFixedHeight(20f);
                c11.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c11);
                totalcost = Integer.parseInt(kgdata[j].trim()) * Integer.parseInt(pricedata[k].trim());
                PdfPCell c12 = new PdfPCell(new Phrase("Rs. " + String.valueOf(totalcost), FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
                c12.setColspan(2);
                c12.setHorizontalAlignment(Element.ALIGN_CENTER);
                c12.setFixedHeight(20f);
                table.addCell(c12);


            }
            PdfPCell c13 = new PdfPCell(new Phrase(" Amount Chargable (in words)" + "\n" + "\n" + " " + Currency.convertToIndianCurrency(totalcoast), FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            c13.setColspan(6);
            c13.setFixedHeight(40f);
            table.addCell(c13);
            PdfPCell c14 = new PdfPCell(new Phrase("Total", FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            c14.setColspan(2);
            c14.setFixedHeight(40f);
            c14.setHorizontalAlignment(Element.ALIGN_CENTER);
            c14.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(c14);
            PdfPCell c15 = new PdfPCell(new Phrase("Rs. " + totalcoast, FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            c15.setColspan(2);
            c15.setFixedHeight(40f);
            c15.setHorizontalAlignment(Element.ALIGN_CENTER);
            c15.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(c15);
            PdfPCell c16 = new PdfPCell(new Phrase("Customer Signatory", FontFactory.getFont(FontFactory.TIMES_BOLD, 14, BaseColor.BLACK)));
            c16.setColspan(5);
            c16.setFixedHeight(70f);
            c16.setHorizontalAlignment(Element.ALIGN_CENTER);
            c16.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(c16);
            PdfPCell c17 = new PdfPCell(new Phrase("Authorized Signatory", FontFactory.getFont(FontFactory.TIMES_BOLD, 14, BaseColor.BLACK)));
            c17.setColspan(5);
            c17.setFixedHeight(70f);
            c17.setHorizontalAlignment(Element.ALIGN_CENTER);
            c17.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table.addCell(c17);
            document.add(table);
            document.add(new Paragraph(" "));
            paragraph1 = new Paragraph("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", FontFactory.getFont(FontFactory.TIMES_BOLD, 14, BaseColor.BLACK));
            paragraph1.setAlignment(paragraph.ALIGN_CENTER);
            document.add(paragraph1);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("  "));
            PdfPTable table2 = new PdfPTable(10);
            table2.setWidthPercentage(100);
            table2.setTotalWidth(575);

            PdfPCell d1 = new PdfPCell(new Phrase(" Buyer : " + company_name + "\n" + " " + buyer_address, FontFactory.getFont(FontFactory.TIMES_BOLD, 14, BaseColor.BLACK)));
            d1.setColspan(07);
            d1.setFixedHeight(50f);
            table2.addCell(d1);
            PdfPCell d2 = new PdfPCell(new Phrase(" Date : " + order_date + "\n" + "\n" + " Invoice No : # " + invoive_no, FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, BaseColor.BLACK)));
            d2.setColspan(03);
            d2.setFixedHeight(50f);
            table2.addCell(d2);
            PdfPCell d3 = new PdfPCell(new Phrase("S.No", FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            d3.setColspan(1);
            d3.setFixedHeight(20f);
            d3.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.addCell(d3);
            PdfPCell d4 = new PdfPCell(new Phrase("Description", FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            d4.setColspan(4);
            d4.setFixedHeight(20f);
            d4.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.addCell(d4);
            PdfPCell d5 = new PdfPCell(new Phrase("Quantity", FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            d5.setColspan(1);
            d5.setFixedHeight(20f);
            d5.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.addCell(d5);
            PdfPCell d6 = new PdfPCell(new Phrase("Rate per Kg", FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            d6.setColspan(2);
            d6.setFixedHeight(20f);
            d6.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.addCell(d6);
            PdfPCell d7 = new PdfPCell(new Phrase("Amount", FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            d7.setColspan(2);
            d7.setFixedHeight(20f);
            d7.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.addCell(d7);
            for (int i = 1, j = 1, k = 1; i < productdata.length && j < kgdata.length && k < pricedata.length; i++, j++, k++) {
                PdfPCell d8 = new PdfPCell(new Phrase(String.valueOf(i), FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK)));
                d8.setColspan(1);
                d8.setFixedHeight(20f);
                d8.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(d8);
                PdfPCell d9 = new PdfPCell(new Phrase("   " + productdata[i].trim(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK)));
                d9.setColspan(4);
                d9.setFixedHeight(20f);
                table2.addCell(d9);
                PdfPCell d10 = new PdfPCell(new Phrase(kgdata[j].trim(), FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, BaseColor.BLACK)));
                d10.setColspan(1);
                d10.setHorizontalAlignment(Element.ALIGN_CENTER);
                d10.setFixedHeight(20f);
                table2.addCell(d10);
                PdfPCell d11 = new PdfPCell(new Phrase(pricedata[k].trim(), FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
                d11.setColspan(2);
                d11.setFixedHeight(20f);
                d11.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(d11);
                totalcost = Integer.parseInt(kgdata[j].trim()) * Integer.parseInt(pricedata[k].trim());
                PdfPCell d12 = new PdfPCell(new Phrase("Rs. " + String.valueOf(totalcost), FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
                d12.setColspan(2);
                d12.setHorizontalAlignment(Element.ALIGN_CENTER);
                d12.setVerticalAlignment(Element.ALIGN_MIDDLE);
                d12.setFixedHeight(20f);
                table2.addCell(d12);

            }

            PdfPCell d13 = new PdfPCell(new Phrase(" Amount Chargable (in words)" + "\n" + "\n" + " " + Currency.convertToIndianCurrency(totalcoast), FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            d13.setColspan(6);
            d13.setFixedHeight(40f);
            table2.addCell(d13);
            PdfPCell d14 = new PdfPCell(new Phrase("Total", FontFactory.getFont(FontFactory.TIMES_BOLD, 15, BaseColor.BLACK)));
            d14.setColspan(2);
            d14.setFixedHeight(40f);
            d14.setHorizontalAlignment(Element.ALIGN_CENTER);
            d14.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table2.addCell(d14);
            PdfPCell d15 = new PdfPCell(new Phrase("Rs. " + totalcoast, FontFactory.getFont(FontFactory.TIMES_BOLD, 12, BaseColor.BLACK)));
            d15.setColspan(2);
            d15.setFixedHeight(40f);
            d15.setHorizontalAlignment(Element.ALIGN_CENTER);
            d15.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table2.addCell(d15);
            PdfPCell d16 = new PdfPCell(new Phrase("Customer Signatory", FontFactory.getFont(FontFactory.TIMES_BOLD, 14, BaseColor.BLACK)));
            d16.setColspan(5);
            d16.setFixedHeight(70f);
            d16.setHorizontalAlignment(Element.ALIGN_CENTER);
            d16.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table2.addCell(d16);
            PdfPCell d17 = new PdfPCell(new Phrase("Authorized Signatory", FontFactory.getFont(FontFactory.TIMES_BOLD, 14, BaseColor.BLACK)));
            d17.setColspan(5);
            d17.setFixedHeight(70f);
            d17.setHorizontalAlignment(Element.ALIGN_CENTER);
            d17.setVerticalAlignment(Element.ALIGN_BOTTOM);
            table2.addCell(d17);
            document.add(table2);
            document.close();
            Toast.makeText(context, "Pdf Generated", Toast.LENGTH_SHORT).show();


        } catch (FileNotFoundException e) {

        } catch (DocumentException e) {

        }


    }

    void preview() {

        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", pdfFile);

        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(share);
    }

    void updateorderstatus(final String status, final String orderid) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfigClass.updatestatusurl + "?status=" + status + "&oid=" + orderid, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                progressDialog.dismiss();
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }
}

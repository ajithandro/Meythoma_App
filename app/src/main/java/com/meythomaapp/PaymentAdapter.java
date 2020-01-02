package com.meythomaapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.MyViewHolder> {
    Context context;
    ProgressDialog progressDialog;
    ArrayList order_id, buyerad, orderDate, deliveryDate, billno, companyName, productDetails, totalAmount, gstamt, ordertakenby, totalamt, kgdetails, orderstatus, paymentstatus, paybalance, paysno, paydate, payamount,paytotal;

    public PaymentAdapter(Context context, ArrayList order_id, ArrayList buyerad, ArrayList orderDate, ArrayList deliveryDate, ArrayList billno, ArrayList companyName, ArrayList productDetails, ArrayList totalAmount, ArrayList gstamt, ArrayList ordertakenby, ArrayList totalamt, ArrayList kgdetails, ArrayList orderstatus, ArrayList paymentstatus, ArrayList paybalance,ArrayList paysno,ArrayList paydate,ArrayList payamount,ArrayList paytotal) {
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
        this.paysno = paysno;
        this.paydate = paydate;
        this.payamount = payamount;
        this.paytotal=paytotal;

    }

    @NonNull
    @Override
    public PaymentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paymentdesign, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentAdapter.MyViewHolder holder, int position) {
        holder.ordertext.setText("Order : " + orderDate.get(position).toString().trim());
        holder.deliverytext.setText("Delivery : " + deliveryDate.get(position).toString().trim());
        holder.billtext.setText("InVoice No : " + billno.get(position).toString().trim());
        holder.cnametext.setText("Shop : " + companyName.get(position).toString().trim());
        holder.producttext.setText(productDetails.get(position).toString().trim());
        holder.totaltext.setText(totalAmount.get(position).toString().trim());
        holder.ordertakentext.setText("Order by : " + ordertakenby.get(position).toString().trim());
        holder.totalamt.setText("₹ " + totalamt.get(position).toString().trim());
        holder.kg.setText(kgdetails.get(position).toString().trim());
        holder.ostatus.setText("Order Status :  " + orderstatus.get(position).toString().trim());
        holder.gsttext.setText("GST : ₹ " + gstamt.get(position).toString().trim());
        holder.paystatus.setText("Payment Status : " + paymentstatus.get(position).toString().trim());
        holder.paybal.setText("Balance : ₹ " + paybalance.get(position).toString().trim());
        holder.snotxt.setText(paysno.get(position).toString().trim());
        holder.paydatetxt.setText(paydate.get(position).toString().trim());
        holder.payamttxt.setText(payamount.get(position).toString().trim());
        holder.paytotal.setText("Total Amount : "+paytotal.get(position).toString().trim());
    }

    @Override
    public int getItemCount() {
        return billno.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView paytotal,updatepay,ordertext, deliverytext, billtext, cnametext, producttext, totaltext, gsttext, ordertakentext, totalamt, kg, ostatus, paystatus, paybal,snotxt,paydatetxt,payamttxt;
        ImageView shareicon, pdfreport;
        LinearLayout layout;
        ViewGroup viewGroup;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            shareicon = (ImageView) itemView.findViewById(R.id.payshareicon);
            ordertext = (TextView) itemView.findViewById(R.id.payorderdate);
            deliverytext = (TextView) itemView.findViewById(R.id.deliveryorderdate);
            billtext = (TextView) itemView.findViewById(R.id.payvoiceno);
            cnametext = (TextView) itemView.findViewById(R.id.paycompanyname);
            ostatus = (TextView) itemView.findViewById(R.id.payorderstatus);
            producttext = (TextView) itemView.findViewById(R.id.payproduct);
            totaltext = (TextView) itemView.findViewById(R.id.payamount);
            gsttext = (TextView) itemView.findViewById(R.id.paygststatus);
            ordertakentext = (TextView) itemView.findViewById(R.id.payorderby);
            totalamt = (TextView) itemView.findViewById(R.id.paytotalamt);
            kg = (TextView) itemView.findViewById(R.id.payquantity);
            paystatus = (TextView) itemView.findViewById(R.id.payamtstatus);
            paybal = (TextView) itemView.findViewById(R.id.payorderbal);
            pdfreport = (ImageView) itemView.findViewById(R.id.paypdfreport);
            layout = (LinearLayout) itemView.findViewById(R.id.linview1);
            updatepay=(TextView)itemView.findViewById(R.id.payupdate);
            snotxt=(TextView)itemView.findViewById(R.id.paysno);
            paydatetxt=(TextView)itemView.findViewById(R.id.paydatepartial);
            payamttxt=(TextView)itemView.findViewById(R.id.payamtpartial);
            paytotal=(TextView)itemView.findViewById(R.id.paytotal);
            viewGroup = (ViewGroup) itemView.findViewById(android.R.id.content);
        }
    }
}

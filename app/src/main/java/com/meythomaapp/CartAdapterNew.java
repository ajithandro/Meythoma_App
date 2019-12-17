package com.meythomaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import java.util.ArrayList;

/**
 * Created by Murali on 19-May-18.
 */

public class CartAdapterNew extends BaseAdapter {

    private Context context;
    private ArrayList<CartBean> ModelArrayList;

    public CartAdapterNew(Context context, ArrayList<CartBean> ModelArrayList) {

        this.context = context;
        this.ModelArrayList = ModelArrayList;
    }

    public void remove(int position) {
        ModelArrayList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return ModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return ModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        CartBean dataModel = ModelArrayList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cart_row_item, null, true);

            holder.itmType = (TextView) convertView.findViewById(R.id.itm_type);
            holder.itmQuan = (TextView) convertView.findViewById(R.id.itm_quan);
            holder.itmPric = (TextView) convertView.findViewById(R.id.itm_price_pk);
            holder.itmAmnt = (TextView) convertView.findViewById(R.id.itm_amt);
            holder.itmGstAmt = (TextView) convertView.findViewById(R.id.itm_gst_amt);
            holder.itmTotAmt = (TextView) convertView.findViewById(R.id.itm_total_amt);


            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.itmType.setText(dataModel.getProduType());
        holder.itmQuan.setText(dataModel.getQualityKg());
        holder.itmPric.setText(dataModel.getPriceprKg());
        holder.itmAmnt.setText(dataModel.getAmounttot());
        holder.itmGstAmt.setText(dataModel.getGstamt());
        holder.itmTotAmt.setText(dataModel.getTotalamt());

        if (position % 2 == 1) {
            convertView.setBackgroundResource(R.color.blue_gray);
        } else {
            convertView.setBackgroundResource(R.color.corn_yellow);
        }

        return convertView;
    }

    private class ViewHolder {

        protected TextView itmType;
        protected TextView itmQuan;
        protected TextView itmPric;
        protected TextView itmAmnt;
        protected TextView itmGstAmt;
        protected TextView itmTotAmt;


    }

}
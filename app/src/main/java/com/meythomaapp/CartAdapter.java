package com.meythomaapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Murali on 19-May-18.
 */

public class CartAdapter extends ArrayAdapter<CartBean> implements View.OnClickListener{

    private ArrayList<CartBean> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView itmType;
        TextView itmQuan;
        TextView itmPric;
        TextView itmAmnt;
        TextView itmGstAmt;
        TextView itmTotAmt;
    }

    public CartAdapter(ArrayList<CartBean> data, Context context) {
        super(context, R.layout.cart_row_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        CartBean dataModel=(CartBean)object;


    }


    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CartBean dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.cart_row_item, parent, false);
            TextView itmType;
            TextView itmQuan;
            TextView itmPric;
            TextView itmAmnt;
            TextView itmGstAmt;
            TextView itmTotAmt;

            viewHolder.itmType = (TextView) convertView.findViewById(R.id.itm_type);
            viewHolder.itmQuan = (TextView) convertView.findViewById(R.id.itm_quan);
            viewHolder.itmPric = (TextView) convertView.findViewById(R.id.itm_price_pk);
            viewHolder.itmAmnt = (TextView) convertView.findViewById(R.id.itm_amt);
            viewHolder.itmGstAmt = (TextView) convertView.findViewById(R.id.itm_gst_amt);
            viewHolder.itmTotAmt = (TextView) convertView.findViewById(R.id.itm_total_amt);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.itmType.setText(dataModel.getProduType());
        viewHolder.itmQuan.setText(dataModel.getQualityKg());
        viewHolder.itmPric.setText(dataModel.getPriceprKg());
        viewHolder.itmAmnt.setText(dataModel.getAmounttot());
        viewHolder.itmGstAmt.setText(dataModel.getGstamt());
        viewHolder.itmTotAmt.setText(dataModel.getTotalamt());

        if (position % 2 == 1) {
            convertView.setBackgroundResource(R.color.blue_gray);
        } else {
            convertView.setBackgroundResource(R.color.corn_yellow);
        }
        // Return the completed view to render on screen
        return convertView;
    }
}

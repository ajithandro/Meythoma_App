package com.meythomaapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.DatePickerDialog;


import java.util.Calendar;

public class SalesList extends Activity {

    EditText txtDate;
    private int mYear, mMonth, mDay;
    private static CustomAdapter adapter;
    ListView listView;
    TextView cmpnyName,gstis,total,gstamt,gTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_sales_list);

            String compName = getIntent().getExtras().getString("cmname");
            String gstYN = getIntent().getExtras().getString("gstyn");
            int totamt = getIntent().getExtras().getInt("totAmt");
            int totgst = getIntent().getExtras().getInt("totGst");
            cmpnyName = (TextView) findViewById(R.id.cmpnyName);
            gstis = (TextView) findViewById(R.id.gstis);
            total = (TextView) findViewById(R.id.total);
            gstamt = (TextView) findViewById(R.id.gstamt);
            gTotal = (TextView) findViewById(R.id.gTotal);
            cmpnyName.setText(compName);
            gstis.setText(gstYN);
            total.setText(""+totamt);
            gstamt.setText(""+totgst);
            int grTotal = totamt+totgst;
            gTotal.setText(""+grTotal);
            listView=(ListView)findViewById(R.id.sales_listview);
            adapter= new CustomAdapter(AppConfigClass.cardBeanAl,getApplicationContext());
            listView.setAdapter(adapter);

            txtDate = (EditText) findViewById(R.id.in_date);

            txtDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get Current Date
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);


                    DatePickerDialog datePickerDialog = new DatePickerDialog(SalesList.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            });
        }catch (Exception e){
             System.out.println(e);
        }
    }
}

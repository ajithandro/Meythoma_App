package com.meythomaapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        FragmentManager fragmentManager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final TextView textView=(TextView)findViewById(R.id.handle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame,new GifActivity()).commit();




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    Dialog gpsDialog;

    private void buildAlertMessageNoGps() {

        //**
        gpsDialog = new Dialog(HomeActivity.this);
        gpsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        gpsDialog.setCancelable(false);
        gpsDialog.setContentView(R.layout.create_gps_dialog);
        String msg = "This app requires locations services to be enabled. \n" +
                "Note: due to contractual agreements, not all content may be available in all areas.";
        TextView txt_dia = (TextView) gpsDialog.findViewById(R.id.txt_dia);
        txt_dia.setText(msg);

        Button oKButton = (Button) gpsDialog.findViewById(R.id.pin_ok_btn);
        //Button noButton = (Button) gpsDialog.findViewById(R.id.pin_no_btn);

        oKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsDialog.dismiss();
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        gpsDialog.show();


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.homepage){
            fragmentManager.beginTransaction().replace(R.id.frame,new GifActivity()).commit();

        }
   else if (id == R.id.alloders){
            fragmentManager.beginTransaction().replace(R.id.frame,new All_Orders()).commit();

        }
        else if (id == R.id.nav_payment) {
            // Handle the camera action
            fragmentManager.beginTransaction().replace(R.id.frame,new Today_Payment()).commit();
        }
        else if (id == R.id.nav_add_new_user) {
            // Handle the camera action
            fragmentManager.beginTransaction().replace(R.id.frame,new AddCustomer()).commit();
        } else if (id == R.id.nav_add_area) {
            // Handle the camera action
            fragmentManager.beginTransaction().replace(R.id.frame,new AddAreaActivity()).commit();
        } else if (id == R.id.nav_add_category) {
            Intent _mintent = new Intent(HomeActivity.this,AddCategory.class);
            startActivity(_mintent);
        } else if (id == R.id.nav_new_sale) {
            Intent _mintent = new Intent(HomeActivity.this,NewSalesActivity.class);
            startActivity(_mintent);
        } else if (id == R.id.nav_visited_entry) {
            fragmentManager.beginTransaction().replace(R.id.frame,new VisitedEntryActivity()).commit();
        } else if (id == R.id.nav_update_delivery_details) {
            fragmentManager.beginTransaction().replace(R.id.frame,new Soon()).commit();
        }  else if (id == R.id.nav_logout) {

           finishAffinity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;


    }




}
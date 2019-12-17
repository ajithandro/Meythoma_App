package com.meythomaapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;

public class VisitedEntryActivity extends Fragment {

    private Spinner salce_person_spinner,area_spinner,company_name_spinner;
    private UpdateVisitedEntryTask mUpdateVisitedEntryTask = null;
    private EditText visit_comments;
    private ArrayList<String> salesPersionArrayList = new ArrayList<>();
    private ArrayList<String> companyNameArrayList = new ArrayList<>();
    private ArrayList<String> areaArrayList = new ArrayList<>();
    private ArrayAdapter<String> salesPersionSpinnerAdapter;
    private ArrayAdapter<String> companyNameSpinnerAdapter;
    private ArrayAdapter<String> areaSpinnerAdapter;
    private Button visited_entry_btn;
    private View visit_progress;
    private VisitedEntryActivity conx;
    private AppPreferences preferences;

    private String visitedCompanyId = "";
    private String visitedCompany = "";
    private String visitedComments = "";
View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view=inflater.inflate(R.layout.visited_entry,container,false);
        conx = this;
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle("Visited Entries");
        preferences = new AppPreferences(getActivity());
        visit_progress = view.findViewById(R.id.vist_progress);
        salce_person_spinner = (Spinner) view.findViewById(R.id.spinner_sales_person);
        area_spinner = (Spinner) view.findViewById(R.id.spinner_area);
        company_name_spinner = (Spinner)view. findViewById(R.id.company_name_spinner);
        visited_entry_btn = (Button) view.findViewById(R.id.visited_entry_btn);
        visit_comments = (EditText) view.findViewById(R.id.visit_comments);
        visited_entry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                visitedCompany = company_name_spinner.getSelectedItem().toString().trim();
                int companyIndex = Constants.customerListAl.indexOf(visitedCompany);
                visitedCompanyId = Constants.customerIdListAl.get(companyIndex);
                visitedComments = visit_comments.getText().toString();

                mUpdateVisitedEntryTask = new UpdateVisitedEntryTask();
                startMyTask(mUpdateVisitedEntryTask, "");
            }
        });
        loadAreaSpinner();
        loadCompanyNameSpinner();
        loadSalespersionSpinner();
        return  view;
    }
    private void loadSalespersionSpinner(){

        salesPersionArrayList.clear();
        salesPersionArrayList.add("-Select-");
        for(String cn: Constants.userListAl){
            salesPersionArrayList.add(cn);
        }


        salesPersionSpinnerAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, salesPersionArrayList) {
            //By using this method we will define how
            //the text appears before clicking a spinner
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //   ((TextView) v).setTextColor(Color.parseColor("#E30D81"));
                return v;
            }

            //By using this method we will define
            //how the listview appears after clicking a spinner
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView,
                        parent);
                v.setBackgroundColor(Color.parseColor("#E30D81"));
                ((TextView) v).setTextColor(Color.parseColor("#ffffff"));
                return v;
            }
        };
        salesPersionSpinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        salce_person_spinner.setAdapter(salesPersionSpinnerAdapter);
    }

    private void loadCompanyNameSpinner(){

        companyNameArrayList.clear();
        companyNameArrayList.add("-Select-");
        for(String cn: Constants.customerListAl){
            companyNameArrayList.add(cn);
        }


        companyNameSpinnerAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, companyNameArrayList) {
            //By using this method we will define how
            //the text appears before clicking a spinner
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //   ((TextView) v).setTextColor(Color.parseColor("#E30D81"));
                return v;
            }

            //By using this method we will define
            //how the listview appears after clicking a spinner
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView,
                        parent);
                v.setBackgroundColor(Color.parseColor("#E30D81"));
                ((TextView) v).setTextColor(Color.parseColor("#ffffff"));
                return v;
            }
        };
        companyNameSpinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        company_name_spinner.setAdapter(companyNameSpinnerAdapter);
    }
    private void loadAreaSpinner(){

        areaArrayList.clear();
        areaArrayList.add("-Select-");
        for(String cn: Constants.areasListAl){
            areaArrayList.add(cn);
        }


        areaSpinnerAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, areaArrayList) {
            //By using this method we will define how
            //the text appears before clicking a spinner
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //   ((TextView) v).setTextColor(Color.parseColor("#E30D81"));
                return v;
            }

            //By using this method we will define
            //how the listview appears after clicking a spinner
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView,
                        parent);
                v.setBackgroundColor(Color.parseColor("#E30D81"));
                ((TextView) v).setTextColor(Color.parseColor("#ffffff"));
                return v;
            }
        };
        areaSpinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        area_spinner.setAdapter(areaSpinnerAdapter);
    }

    public class UpdateVisitedEntryTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {

            showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String resp="";
            try {

                // for GET Method
                String salesPersionId = preferences.getStringPreference(getActivity(),Constants.PREFERENCES_SALESPERSON_ID);
                String username = preferences.getStringPreference(getActivity(),Constants.PREFERENCES_SALESPERSON_NAME);
                String URL = AppConfigClass.companyVisitedEntryURL+"?username="+username+"&visitedCompanyId="+visitedCompanyId
                        +"&visitedComments="+visitedComments+"&visitedPersonId="+salesPersionId;
                URL = URL.replace(" ","%20");
                HttpClient Client = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(URL);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                resp = Client.execute(httpget, responseHandler).trim();

            } catch (Exception e) {
                resp = e.toString();
            }

            // TODO: register the new account here.
            return resp;
        }

        @Override
        protected void onPostExecute(final String result) {
            mUpdateVisitedEntryTask = null;
            try{
                Log.e("VISIT ENTRY ",result);
            }catch (Exception e){
                Log.e("VISIT ENTRY","Error is "+e.toString());
            }
            showProgress(false);
            showToast(result);
            getActivity().finish();

        }

        @Override
        protected void onCancelled() {
            mUpdateVisitedEntryTask = null;
            showProgress(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            visit_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            visit_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    visit_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            visit_progress.setVisibility(show ? View.VISIBLE : View.GONE);

        }
    }

    private void showToast(String msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        // API 11
    void startMyTask(AsyncTask asyncTask, String... params) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[])params);
            else
                asyncTask.execute((Object[])params);
        } catch (Exception e) {
            if(Constants.DEBUGGING) System.out.print(e);
        }
    }

    }




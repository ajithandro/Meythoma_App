package com.meythomaapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddAreaActivity extends Fragment {
    private ListView lv_area;
    private ArrayAdapter<String> area_adapter;
    private EditText et_area;
    private Button btn_add_area;
    private AddAreaActivity conx;
    private DeleteAddAreaTask mDeleteAddAreaTask = null;
    private String itemType;
    private String nArea = "";
    private String nAreaQuery = "";
    private View mArea_progress;
    private ArrayList<String> areasListAl = new ArrayList<>();
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_add_area, container, false);
        conx = this;
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle("Add Areas");
        area_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, Constants.areasListAl);
        lv_area = (ListView) view.findViewById(R.id.lv_area);
        et_area = (EditText) view.findViewById(R.id.et_area);
        btn_add_area = (Button) view.findViewById(R.id.btn_add_area);
        mArea_progress = view.findViewById(R.id.area_progress);
        btn_add_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nArea = et_area.getText().toString();
                nAreaQuery = "Add";
                mDeleteAddAreaTask = new DeleteAddAreaTask();
                mDeleteAddAreaTask.execute();
            }
        });
        init();
        lv_area.setAdapter(area_adapter);
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        lv_area,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    delete_position = position;
                                    String dArea = String.valueOf(lv_area.getItemAtPosition(position));
                                    nArea = dArea;
                                    String msg = "Are you sure you want to Delete \"" + dArea + "\" Area";
                                    showConfirmDialog(msg);
                                }
                            }
                        });
        lv_area.setOnTouchListener(touchListener);
        lv_area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = String.valueOf(adapterView.getItemAtPosition(i));
                nArea = name;
                et_area.setText(name);
            }
        });
        return view;
    }

    private void init() {
        area_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, Constants.areasListAl);
        lv_area = (ListView) view.findViewById(R.id.lv_area);
    }

    public class DeleteAddAreaTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            String resp = "";
            try {
                String inputStr = nArea.replace(" ", "%20");
                String URL = AppConfigClass.deleteAddAreaURL + "?query=" + nAreaQuery + "&area=" + inputStr;
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
            try {
                if (result.startsWith("Error")) {
                    Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                } else {
                    boolean isJsArr = false;
                    try {
                        JSONArray jsArray1 = new JSONArray(result);
                        isJsArr = true;
                    } catch (Exception e) {
                        isJsArr = false;
                    }
                    if (isJsArr) {
                        JSONArray jsArray = new JSONArray(result);
                        JSONObject jsObj;
                        int len = jsArray.length();
                        GetCategoryBean gcb;
                        Constants.areasListAl.clear();
                        for (int i = 0; i < len; i++) {
                            jsObj = jsArray.getJSONObject(i);
                            gcb = new GetCategoryBean();
                            itemType = jsObj.getString("type");
                            if (itemType.equalsIgnoreCase("Area")) {
                                String cId = jsObj.getString("id");
                                String name = jsObj.getString("name");
                                gcb.setCat_id(cId);
                                gcb.setCat_name(name);
                                Constants.areasListAl.add(name);
                            }
                        }
                        Toast.makeText(getActivity(), "Area Added Successfully", Toast.LENGTH_LONG).show();
                        et_area.setText("");
                        init();
                        lv_area.setAdapter(area_adapter);
                    } else {
                        Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Log.e("Get Area", "Error is " + e.toString());
            }
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mDeleteAddAreaTask = null;
            showProgress(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            /*mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });*/
            mArea_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            mArea_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mArea_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mArea_progress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public TextView conf_tv;
    public Dialog conf_dialog;
    private int delete_position = 0;

    public void showConfirmDialog(String msg) {
        conf_dialog = new Dialog(getActivity());
        conf_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        conf_dialog.setCancelable(false);
        conf_dialog.setContentView(R.layout.custom_dialog);
        conf_tv = (TextView) conf_dialog.findViewById(R.id.txt_dia);
        conf_tv.setText(msg);
        final Button yesButton = (Button) conf_dialog.findViewById(R.id.btn_yes);
        final Button noButton = (Button) conf_dialog.findViewById(R.id.btn_no);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conf_dialog.dismiss();
                Constants.areasListAl.remove(delete_position);
                area_adapter.notifyDataSetChanged();
                nAreaQuery = "Delete";
                mDeleteAddAreaTask = new DeleteAddAreaTask();
                mDeleteAddAreaTask.execute();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conf_dialog.dismiss();
                area_adapter.notifyDataSetChanged();
            }
        });
        if (conf_dialog != null) {
            conf_dialog.show();
        }
    }
}



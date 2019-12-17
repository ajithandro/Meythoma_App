package com.meythomaapp;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by murali on 13-04-2018.
 */

public class Constants {

    public static List<String> customerListAl = new ArrayList<String>();
    public static List<String> customerIdListAl = new ArrayList<String>();
    public static List<String> areasListAl = new ArrayList<String>();
    public static List<String> areasIdListAl = new ArrayList<String>();
    public static List<String> userListAl = new ArrayList<String>();
    public static List<String> userIdListAl = new ArrayList<String>();
    public static Map<String, JSONObject> customerJsonObjListMap = new HashMap<String, JSONObject>();
    public static Map<String, JSONObject> userJsonObjListMap = new HashMap<String, JSONObject>();
    public static Map<String, String> areasListAlMap = new HashMap<String, String>();

    public  static boolean DEBUGGING=true;
    public  static final  String PUBLIC_PREFERENCE="public";
    public  static final  String PRIVATE_PREFERENCE="private";
    public  static final  String PUBLIC_PREFERENCE_NAME="PUBmEYTHOMAappData";
    public  static final  String PRIVATE_PREFERENCE_NAME="PVTmEYTHOMAappData";

    public  static final  String PREFERENCES_SALESPERSON_ID = "SalsePersonId";
    public  static final  String PREFERENCES_SALESPERSON_NAME = "SalsePersonName";
    public  static final  String PREFERENCES_LOGIN_STATUS = "LoginStatus";


}

package com.meythomaapp;

import java.util.ArrayList;

/**
 * Created by murali on 06-04-2018.
 */

public class AppConfigClass {

    public static ArrayList<CartBean> cardBeanAl = new ArrayList<CartBean>();

    //http://192.168.1.107:8084/MethomaWebApi/
//    private static String serverName = "http://192.168.1.29:8084/";
//    private static String serverName = "http://env-8044220.j.layershift.co.uk/";//methoma/";

    private static String serverName = "http://meythoma.com/";
    private static String AppName = "API";
    public static String loginURL = serverName+AppName+"/login.php";
    public static String addnewcustomerURL = serverName+AppName+"/AddNewCustomer.php";
    public static String addnewcategoryURL = serverName+AppName+"/AddCategory.php";
    public static String getCategoryListURL = serverName+AppName+"/getcategory.php";
    public static String getCompanysURL = serverName+AppName+"/getcompanys.php";
    public static String getAreaListURL = serverName+AppName+"/getarea.php";
    public static String deleteAddAreaURL = serverName+AppName+"/deleteaddarea.php";
    public static String getCustomerURL = serverName+AppName+"/getcustomer.php";

    public static String getUserURL = serverName+AppName+"/getuser.php";
    public static String deleteAddCustomerURL = serverName+AppName+"/deleteaddcustomer.php";
    public static String companyVisitedEntryURL = serverName+AppName+"/companyvisitedentry.php";
    public static String newSalesEntryURL = serverName+AppName+"/newsalesentry.php";
    public static String displaydataURL = serverName+AppName+"/displaydata.php";
    public static String retryurl = serverName+AppName+"/upcomingorderdetails.php";
    public static String retryallorders = serverName+AppName+"/retryallorders.php";
    public static String shoporders = serverName+AppName+"/shoporders.php";
    public static String payments = serverName+AppName+"/paymentDetails.php";
    public static String updatestatusurl = serverName+AppName+"/updateorder.php";
    public static String todaydelivered = serverName+AppName+"/todaydelivered.php";


}

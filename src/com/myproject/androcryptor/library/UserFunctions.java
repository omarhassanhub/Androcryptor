package com.myproject.androcryptor.library;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.content.Context;

//List of the functions. 
public class UserFunctions {

    private JSONParser jsonParser;

    //URL of the PHP API. Database hosted at www.000webhost.com/
    //For local use: http://127.0.0.10/androcryptor/
    private static String dAccLoginURL = "http://androcryptor.comeze.com/";
    private static String dAccRegisterURL = "http://androcryptor.comeze.com/";
    private static String dAccForPassURL = "http://androcryptor.comeze.com/";
    private static String dAccChangePassURL = "http://androcryptor.comeze.com/";

    private static String dAccLoginTag = "login";
    private static String dAccRegisterTag = "register";
    private static String dAccForPassTag = "forpass";
    private static String dAccChangegPassTag = "chgpass";

 
    public UserFunctions(){
        jsonParser = new JSONParser();
    }

    //User login.
    public JSONObject dAccLoginUser(String email, String password){

        List<NameValuePair> dAccParams = new ArrayList<NameValuePair>();
        dAccParams.add(new BasicNameValuePair("tag", dAccLoginTag));
        dAccParams.add(new BasicNameValuePair("email", email));
        dAccParams.add(new BasicNameValuePair("password", password));
        JSONObject dAccJson = jsonParser.dAccGetJSONFromUrl(dAccLoginURL, dAccParams);
        return dAccJson;
    }

    //Change user password.
    public JSONObject dAccChangePassword(String newpas, String email){
        List<NameValuePair> dAccParams = new ArrayList<NameValuePair>();
        dAccParams.add(new BasicNameValuePair("tag", dAccChangegPassTag));

        dAccParams.add(new BasicNameValuePair("newpas", newpas));
        dAccParams.add(new BasicNameValuePair("email", email));
        JSONObject json = jsonParser.dAccGetJSONFromUrl(dAccChangePassURL, dAccParams);
        return json;
    }

    //Reset user password.
    public JSONObject dAccForPassword(String forgotpassword){
        List<NameValuePair> dAccParams = new ArrayList<NameValuePair>();
        dAccParams.add(new BasicNameValuePair("tag", dAccForPassTag));
        dAccParams.add(new BasicNameValuePair("forgotpassword", forgotpassword));
        JSONObject dAccJson = jsonParser.dAccGetJSONFromUrl(dAccForPassURL, dAccParams);
        return dAccJson;
    }

    //Register user.
    public JSONObject dAccRegisterUser(String fname, String lname, String email, String uname, String password){
     
        List<NameValuePair> dAccParams = new ArrayList<NameValuePair>();
        dAccParams.add(new BasicNameValuePair("tag", dAccRegisterTag));
        dAccParams.add(new BasicNameValuePair("fname", fname));
        dAccParams.add(new BasicNameValuePair("lname", lname));
        dAccParams.add(new BasicNameValuePair("email", email));
        dAccParams.add(new BasicNameValuePair("uname", uname));
        dAccParams.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.dAccGetJSONFromUrl(dAccRegisterURL,dAccParams);
        return json;
    }

    //User logout.
    public boolean dAccLogoutUser(Context context){
        DatabaseHandler dAccDatabase = new DatabaseHandler(context);
        dAccDatabase.dAccResetTables();
        return true;
    }

}


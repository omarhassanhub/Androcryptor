package com.myproject.androcryptor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.myproject.androcryptor.library.UserFunctions;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AccountPassResetFragment extends Activity {

private static String dAccKeySucc  = "success";
private static String dAccKeyError = "error";

  EditText dAccEmail;
  TextView dAccAlert;
  Button dAccResetPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  
        setContentView(R.layout.fragment_passreset);

        Button dAccLogin = (Button) findViewById(R.id.buttonBack);
        dAccLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent dAccIntent = new Intent(view.getContext(), AccountLoginFragment.class);
                startActivityForResult(dAccIntent, 0);
                finish();
            }

        });


        dAccEmail = (EditText) findViewById(R.id.textPass);
        dAccAlert = (TextView) findViewById(R.id.textAlert);
        dAccResetPassword = (Button) findViewById(R.id.buttonPasswordReset);
        dAccResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	
                dAccNetworkAsync(view);

            }

        });}
    //Check network 
    private class dAccNetworkVerify extends AsyncTask<String,String,Boolean>

    {
        private ProgressDialog dAccDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dAccDialog = new ProgressDialog(AccountPassResetFragment.this);
            dAccDialog.setMessage("Loading..");
            dAccDialog.setTitle("Verifying Network");
            dAccDialog.setIndeterminate(false);
            dAccDialog.setCancelable(true);
            dAccDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args){

            ConnectivityManager dAccConnectivityM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo dAccNetworkI = dAccConnectivityM.getActiveNetworkInfo();
            if (dAccNetworkI != null && dAccNetworkI.isConnected()) {
                try {
                    URL dAccUrl = new URL("http://www.google.com");
                    HttpURLConnection dAccUrlc = (HttpURLConnection) dAccUrl.openConnection();
                    dAccUrlc.setConnectTimeout(3000);
                    dAccUrlc.connect();
                    if (dAccUrlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
     
                    e1.printStackTrace();
                } catch (IOException e) {
             
                    e.printStackTrace();
                }
            }
            return false;

        }
        @Override
        protected void onPostExecute(Boolean th){

            if(th == true){
                dAccDialog.dismiss();
                new dAccProcessRegister().execute();
            }
            else{
                dAccDialog.dismiss();
                dAccAlert.setText("Error in Network Connection");
            }
        }
    }
    //Communicate with the server. 
    private class dAccProcessRegister extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog dAccDialog;

        String dAccForgotPassword;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dAccForgotPassword = dAccEmail.getText().toString();

            dAccDialog = new ProgressDialog(AccountPassResetFragment.this);
            dAccDialog.setTitle("Communicating Servers");
            dAccDialog.setMessage("Receiving Data ...");
            dAccDialog.setIndeterminate(false);
            dAccDialog.setCancelable(true);
            dAccDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions dAccUserFunction = new UserFunctions();
            JSONObject json = dAccUserFunction.dAccForPassword(dAccForgotPassword);
            return json;

        }
        
        //Alert user
        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.getString(dAccKeySucc) != null) {
                    dAccAlert.setText("");
                    String dAccrRes = json.getString(dAccKeySucc);
                    String dAccrRed = json.getString(dAccKeyError);


                    if(Integer.parseInt(dAccrRes) == 1){
                       dAccDialog.dismiss();
                        dAccAlert.setText("A reset email is sent to you.");

                    }
                    else if (Integer.parseInt(dAccrRed) == 2)
                    {    dAccDialog.dismiss();
                        dAccAlert.setText("Your email does not exist in Androcryptor database.");
                    }
                    else {
                        dAccDialog.dismiss();
                        dAccAlert.setText("Error happened in modifying password");
                    }

                }}
            catch (JSONException e) {
                e.printStackTrace();


            }
        }}
	public void dAccNetworkAsync(View view){
	    new dAccNetworkVerify().execute();
	}}


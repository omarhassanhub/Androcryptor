package com.myproject.androcryptor;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import com.myproject.androcryptor.R;
import com.myproject.androcryptor.library.DatabaseHandler;
import com.myproject.androcryptor.library.UserFunctions;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AccountRegisterFragment extends Activity {

    private static String dAccKeySuc  = "success";
    private static String dAccKeyUid  = "uid";
    private static String dAccKeyFirstName  = "fname";
    private static String dAccKeyLastName  = "lname";
    private static String dAccKeyUsername  = "uname";
    private static String dAccKeyEmail  = "email";
    private static String dAccKeyCreatedAt  = "created_at";
    private static String dAccKeyError  = "error";

    EditText dAccInputFN;
    EditText dAccInputLN;
    EditText dAccInputUN;
    EditText dAccInputEM;
    EditText dAccInputPa;
    Button dAccButtonRegister;
    TextView dAccRegisterErrorMassage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_accountregister);

        dAccInputFN = (EditText) findViewById(R.id.fname);
        dAccInputLN = (EditText) findViewById(R.id.lname);
        dAccInputUN = (EditText) findViewById(R.id.uname);
        dAccInputEM = (EditText) findViewById(R.id.email);
        dAccInputPa = (EditText) findViewById(R.id.pword);
        dAccButtonRegister = (Button) findViewById(R.id.buttonReg);
        dAccRegisterErrorMassage = (TextView) findViewById(R.id.register_error);

        Button dAccLogin = (Button) findViewById(R.id.buttonBack);
        dAccLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent dAccIntent = new Intent(view.getContext(), AccountLoginFragment.class);
                startActivityForResult(dAccIntent, 0);
                finish();
            }

        });
        
        //Validate user inputs. 
        dAccButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (  ( !dAccInputUN.getText().toString().equals("")) && ( !dAccInputPa.getText().toString().equals("")) && ( !dAccInputFN.getText().toString().equals("")) && ( !dAccInputLN.getText().toString().equals("")) && ( !dAccInputEM.getText().toString().equals("")) )
                {
                    if ( dAccInputUN.getText().toString().length() > 4 ){
                    	dAccNetAsync(view);

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),
                                "Username must be min 5 characters", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "One or more fields are blank", Toast.LENGTH_SHORT).show();
                }
            }
        });
       }
    
    //Check network.
    private class dAccNetworkVerify extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            nDialog = new ProgressDialog(AccountRegisterFragment.this);
            nDialog.setMessage("Loading..");
            nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
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
                nDialog.dismiss();
                new dAccProcessRegister().execute();
            }
            else{
                nDialog.dismiss();
                dAccRegisterErrorMassage.setText("Error in Network Connection");
            }
        }
    }
    //Communicate with the server.
    private class dAccProcessRegister extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog dAccProgressDialog;

        String email,password,fname,lname,uname;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dAccInputUN = (EditText) findViewById(R.id.uname);
            dAccInputPa = (EditText) findViewById(R.id.pword);
               fname = dAccInputFN.getText().toString();
               lname = dAccInputLN.getText().toString();
                email = dAccInputEM.getText().toString();
                uname= dAccInputUN.getText().toString();
                password = dAccInputPa.getText().toString();
            dAccProgressDialog = new ProgressDialog(AccountRegisterFragment.this);
            dAccProgressDialog.setTitle("Communicating Servers");
            dAccProgressDialog.setMessage("Registering ...");
            dAccProgressDialog.setIndeterminate(false);
            dAccProgressDialog.setCancelable(true);
            dAccProgressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {


        UserFunctions userFunction = new UserFunctions();
        JSONObject dAccJson = userFunction.dAccRegisterUser(fname, lname, email, uname, password);

            return dAccJson;


        }
        
        //Alert user.
       @Override
        protected void onPostExecute(JSONObject json) {

                try {
                    if (json.getString(dAccKeySuc) != null) {
                        dAccRegisterErrorMassage.setText("");
                        String dAccRes = json.getString(dAccKeySuc);

                        String dAccRed = json.getString(dAccKeyError);

                        if(Integer.parseInt(dAccRes) == 1){
                            dAccProgressDialog.setTitle("Receiving Data");
                            dAccProgressDialog.setMessage("Loading Data");

                            dAccRegisterErrorMassage.setText("Successfully Registered");


                            DatabaseHandler dAccDatabase = new DatabaseHandler(getApplicationContext());
                            JSONObject dAccJsonUser = json.getJSONObject("user");

                            UserFunctions dAccLogout = new UserFunctions();
                            dAccLogout.dAccLogoutUser(getApplicationContext());
                            dAccDatabase.dAccAddUser(dAccJsonUser.getString(dAccKeyFirstName),dAccJsonUser.getString(dAccKeyLastName),dAccJsonUser.getString(dAccKeyEmail),dAccJsonUser.getString(dAccKeyUsername),dAccJsonUser.getString(dAccKeyUid),dAccJsonUser.getString(dAccKeyCreatedAt));
      
                            Intent dAccRegistered = new Intent(getApplicationContext(), AccountLoginFragment.class);
            
                            dAccRegistered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            dAccProgressDialog.dismiss();
                            startActivity(dAccRegistered);

                              finish();
                        }

                        else if (Integer.parseInt(dAccRed) ==2){
                            dAccProgressDialog.dismiss();
                            dAccRegisterErrorMassage.setText("This user already exists");
                        }
                        else if (Integer.parseInt(dAccRed) ==3){
                            dAccProgressDialog.dismiss();
                            dAccRegisterErrorMassage.setText("Incorrect email id");
                        }

                    }

                        else{
                        dAccProgressDialog.dismiss();

                        dAccRegisterErrorMassage.setText("Error happened in registration");
                        }

                } catch (JSONException e) {
                    e.printStackTrace();


                }
            }}
        public void dAccNetAsync(View view){
            new dAccNetworkVerify().execute();
        }}

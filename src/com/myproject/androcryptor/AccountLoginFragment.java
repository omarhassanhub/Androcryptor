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
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import com.myproject.androcryptor.R;
import com.myproject.androcryptor.library.DatabaseHandler;
import com.myproject.androcryptor.library.UserFunctions;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
/*
public class AccountLoginFragment extends Activity{
	   
    @Override
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_accountlogin);
        
        final Button orderButton = (Button) findViewById(R.id.buttonLogin);
        final Button orderButton2 = (Button) findViewById(R.id.buttonReg);
        OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View v) {
            	if (v.equals(orderButton)) {
                   
            		Intent intent = new Intent(AccountLoginFragment.this,MenuDrawerFragment.class);
                startActivity(intent);
            	}else if (v.equals(orderButton2)) {
                	Intent intent1 = new Intent(AccountLoginFragment.this,AccountRegisterFragment.class);
	                startActivity(intent1);
                }else{}
                
            }
        };
        orderButton.setOnClickListener(listener);
        orderButton2.setOnClickListener(listener);
        
        
      }
       
     }

*/
public class AccountLoginFragment extends Activity {

    Button dAccButtonLogin;
    Button dAccButtonRegister;
    Button dAccButtonPasswordReset;
    EditText dAccInputEmail;
    EditText dAccInputPassword;
    
    private TextView dAccLoginErrorMassage;
    private static String dAccKeySucc  = "success";
    private static String dAccKeyUid  = "uid";
    private static String dAccKeyUsername  = "uname";
    private static String dAccKeyFirstName  = "fname";
    private static String dAccKeyLastName  = "lname";
    private static String dAccKeyEmail  = "email";
    private static String dAccKeyCreatedAt  = "created_at";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_accountlogin);

        dAccInputEmail = (EditText) findViewById(R.id.email);
        dAccInputPassword = (EditText) findViewById(R.id.pword);
        dAccButtonRegister = (Button) findViewById(R.id.buttonReg);
        dAccButtonLogin = (Button) findViewById(R.id.buttonLogin);
        dAccButtonPasswordReset = (Button)findViewById(R.id.buttonReset);
        dAccLoginErrorMassage = (TextView) findViewById(R.id.loginErrorMsg);

        dAccButtonPasswordReset.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
        Intent dAccIntent = new Intent(view.getContext(), AccountPassResetFragment.class);
        startActivityForResult(dAccIntent, 0);
        finish();
        }});


        dAccButtonRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent dAccIntent = new Intent(view.getContext(), AccountRegisterFragment.class);
                startActivityForResult(dAccIntent, 0);
                finish();
             }});

        dAccButtonLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if (  ( !dAccInputEmail.getText().toString().equals("")) && ( !dAccInputPassword.getText().toString().equals("")) )
                {
                    dAccNetworkAsync(view);
                }
                else if ( ( !dAccInputEmail.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "The password field is blank", Toast.LENGTH_SHORT).show();
                }
                else if ( ( !dAccInputPassword.getText().toString().equals("")) )
                {
                    Toast.makeText(getApplicationContext(),
                            "The email field blank", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "The email and password fields are blank", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class dAccNetworkVerify extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog dAccDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dAccDialog = new ProgressDialog(AccountLoginFragment.this);
            dAccDialog.setTitle("Checking Network");
            dAccDialog.setMessage("Loading..");
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
                new dAccProcessLogin().execute();
            }
            else{
                dAccDialog.dismiss();
                dAccLoginErrorMassage.setText("Error in Network Connection");
            }
        }
    }

    private class dAccProcessLogin extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog dAccDialog;

        String email,password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dAccInputEmail = (EditText) findViewById(R.id.email);
            dAccInputPassword = (EditText) findViewById(R.id.pword);
            email = dAccInputEmail.getText().toString();
            password = dAccInputPassword.getText().toString();
            dAccDialog = new ProgressDialog(AccountLoginFragment.this);
            dAccDialog.setTitle("Communicating Servers");
            dAccDialog.setMessage("Logging in ...");
            dAccDialog.setIndeterminate(false);
            dAccDialog.setCancelable(true);
            dAccDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions dAccUserFunction = new UserFunctions();
            JSONObject dAccJson = dAccUserFunction.dAccLoginUser(email, password);
            return dAccJson;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
               if (json.getString(dAccKeySucc) != null) {

                    String dAccRes = json.getString(dAccKeySucc);

                    if(Integer.parseInt(dAccRes) == 1){
                        dAccDialog.setMessage("Loading User Profile");
                        dAccDialog.setTitle("Receiving Data");
                        DatabaseHandler dAccDatabase = new DatabaseHandler(getApplicationContext());
                        JSONObject dAccJsonUser = json.getJSONObject("user");
             
                        UserFunctions logout = new UserFunctions();
                        logout.dAccLogoutUser(getApplicationContext());
                        dAccDatabase.dAccAddUser(dAccJsonUser.getString(dAccKeyFirstName),dAccJsonUser.getString(dAccKeyLastName),dAccJsonUser.getString(dAccKeyEmail),dAccJsonUser.getString(dAccKeyUsername),dAccJsonUser.getString(dAccKeyUid),dAccJsonUser.getString(dAccKeyCreatedAt));
             
                        Intent upanel = new Intent(getApplicationContext(), MenuDrawerFragment.class);
                        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        dAccDialog.dismiss();
                        startActivity(upanel);
               
                        finish();
                    }else{

                        dAccDialog.dismiss();
                        dAccLoginErrorMassage.setText("Invalid username/password");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
       }
    }
    public void dAccNetworkAsync(View view){
        new dAccNetworkVerify().execute();
    }
}
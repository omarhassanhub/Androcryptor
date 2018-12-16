package com.myproject.androcryptor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import com.myproject.androcryptor.R;
import com.myproject.androcryptor.AccountPassChangeFragment.dAccNetworkVerify;
import com.myproject.androcryptor.AccountPassChangeFragment.dAccProcessRegister;
import com.myproject.androcryptor.library.DatabaseHandler;
import com.myproject.androcryptor.library.UserFunctions;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public  class AccountPassChangeFragment extends Activity implements OnItemClickListener {
	
	private static String dAccKeySucc = "success";
    private static String dAccKeyError = "error";
    
    EditText dAccNewPassword;
    TextView dAccAlert;
    Button dAccChangePassword;
    Button dAccCancel;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getActionBar().setTitle("Edit Details");
        setContentView(R.layout.fragment_passchange);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        dAccCancel = (Button) findViewById(R.id.buttonCancel);
        dAccCancel.setOnClickListener(new View.OnClickListener(){
        public void onClick(View arg0){
                
                finish();
            }

        });

        dAccNewPassword = (EditText) findViewById(R.id.newPassword);
        dAccAlert = (TextView) findViewById(R.id.alertPassword);
        dAccChangePassword = (Button) findViewById(R.id.buttonChange);

        dAccChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dAccNetworkAsync(view);
            }
        });}
    
    //Check network
    public class dAccNetworkVerify extends AsyncTask<String,String,Boolean>
    {
        private ProgressDialog dAccDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dAccDialog = new ProgressDialog(AccountPassChangeFragment.this);
            dAccDialog.setMessage("Loading..");
            dAccDialog.setTitle("Checking Network");
            dAccDialog.setIndeterminate(false);
            dAccDialog.setCancelable(true);
            dAccDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args){
            ConnectivityManager dAccConnectivityM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo dAccNetInfo = dAccConnectivityM.getActiveNetworkInfo();
            if (dAccNetInfo != null && dAccNetInfo.isConnected()) {
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
    public class dAccProcessRegister extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog dAccDialog;

        String newpas,email;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DatabaseHandler dAccDatabase = new DatabaseHandler(getApplicationContext());

            HashMap<String,String> dAccUser = new HashMap<String, String>();
            dAccUser = dAccDatabase.dAccGetUserDetails();

            newpas = dAccNewPassword.getText().toString();
            email = dAccUser.get("email");

            dAccDialog = new ProgressDialog(AccountPassChangeFragment.this);
            dAccDialog.setTitle("Communicating Servers");
            dAccDialog.setMessage("Receiving Data ...");
            dAccDialog.setIndeterminate(false);
            dAccDialog.setCancelable(true);
            dAccDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions dAccUserFunction = new UserFunctions();
            JSONObject dAccJson = dAccUserFunction.dAccChangePassword(newpas, email);
            Log.d("Button", "Register");
            return dAccJson;
        }
        
        //Alert user
        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                if (json.getString(dAccKeySucc) != null) {
                    dAccAlert.setText("");
                    String dAccRes = json.getString(dAccKeySucc);
                    String dAccRed = json.getString(dAccKeyError);


                    if (Integer.parseInt(dAccRes) == 1) {

                        dAccDialog.dismiss();
                        dAccAlert.setText("Password is successfully changed.");


                    } else if (Integer.parseInt(dAccRed) == 2) {
                        dAccDialog.dismiss();
                        dAccAlert.setText("Incorrect old Password.");
                    } else {
                        dAccDialog.dismiss();
                        dAccAlert.setText("Error happened in changing Password.");
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();

            }

        }}
    public void dAccNetworkAsync(View view){
        new dAccNetworkVerify().execute();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {

	    MenuInflater dAccInflater = getMenuInflater();
	    dAccInflater.inflate(R.menu.second_activity_bar, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {

	    case android.R.id.home:

	    	Intent dAccIntent = new Intent(this, MenuSettingFragment.class);
	    	this.finish();
	    	
	    	return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		
	}
    
    
}

package com.myproject.androcryptor;

import java.io.File;
import java.io.IOException;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.microsoft.onedrivesdk.saver.ISaver;
import com.microsoft.onedrivesdk.saver.Saver;
import com.microsoft.onedrivesdk.saver.SaverException;


public class OnedriveSaveFileFragment extends Activity implements OnItemClickListener {

    public static final int PICK_FROM_GALLERY_REQUEST_CODE = 4;
    private static final String ONEDRIVE_APP_ID = "4813EF88";
    private static Uri 				gBoxUrF;
    static final int 				RESULT_STORE_FILE = 4;
    
    private final OnClickListener oBoxBeginUpload = new OnClickListener() {
        @Override
        public void onClick(final View v) {

            final Intent gBoxImageInt = new Intent(Intent.ACTION_PICK);
        	gBoxImageInt.setType("*/*");
            startActivityForResult(gBoxImageInt, RESULT_STORE_FILE);
        }
    };

    private ISaver oBoxUploader;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Upload File");
        setContentView(R.layout.fragment_onedrivesavefile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        setContentView(R.layout.fragment_onedrivesavefile);

        oBoxUploader = Saver.createSaver(ONEDRIVE_APP_ID);


        findViewById(R.id.buttonSave).setOnClickListener(oBoxBeginUpload);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        	
        	
        	switch (requestCode) 
    		{
    			
    			case RESULT_STORE_FILE:
    				gBoxUrF = data.getData();
    				oBoxUpload( this);
    				break;
    		
    		}
            try {
            	oBoxUploader.handleSave(requestCode, resultCode, data);

            } catch (final SaverException e) {

            }

    }
    

    public void gBoxDisplayT(final String toast) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
			}
		});
	}


    private void oBoxUpload(final Activity activity) {
        
            	
            	String password = "password";
				String gBoxPa;
				gBoxPa = getgBoxPaUri(gBoxUrF);
				gBoxUrF = Uri.fromFile(new File(gBoxPa));
				
				File oBoxFileCont = new File(gBoxUrF.getPath());
				
				File oBoxCovertedFile = DropboxEncryptionFragment.dBoxConFile(oBoxFileCont, password);

                oBoxUploader.startSaving(activity, gBoxPa, Uri.fromFile(oBoxCovertedFile));
                gBoxDisplayT("File Encrypted");
                
    }

     
  //Get file path from Uri.
  	public String getgBoxPaUri(Uri uri) 
  	{
          String[] gBoxProj = { MediaStore.Images.Media.DATA };
          Cursor gBoxCursor = this.getContentResolver().query(uri, gBoxProj, null, null, null);
          int gBoxColumnIndex = gBoxCursor
                  .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
          gBoxCursor.moveToFirst();
          return gBoxCursor.getString(gBoxColumnIndex);
  	}


	 @Override
		public boolean onCreateOptionsMenu(Menu menu) {
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.second_activity_bar, menu);
		    return super.onCreateOptionsMenu(menu);
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		    
		    case android.R.id.home:
		    	
		    	Intent oBoxIntent = new Intent(this, OnedriveSaveFileFragment.class);
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
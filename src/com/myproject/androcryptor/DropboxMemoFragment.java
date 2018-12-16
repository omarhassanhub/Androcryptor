package com.myproject.androcryptor;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.dropbox.chooser.android.DbxChooser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DropboxMemoFragment extends Activity implements OnItemClickListener {

	   private MediaRecorder dBoxAudRec;
	   private String dBoxOutFile = null;
	   private Button dBoxRec;
	   private Button dBoxHalt;
	   private Button dBoxUpAud;
	   private Button dBoxUpDow;
	   DbxChooser dBoxSelector;
	   static final int DBX_CHOOSER_REQUEST = 2;
	   
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      getActionBar().setTitle("Voice Memo");
	      setContentView(R.layout.fragment_dropboxmemo);
	      getActionBar().setDisplayHomeAsUpEnabled(true);
	      
	      dBoxRec = (Button)findViewById(R.id.btnStart);
	      dBoxHalt = (Button)findViewById(R.id.btnStop);
	      dBoxUpAud = (Button)findViewById(R.id.btnUpload);
	      dBoxUpDow = (Button)findViewById(R.id.btnDownload);
	      dBoxHalt.setEnabled(false);
	      dBoxUpAud.setEnabled(false);
	
	      //onClickListeners for buttons in this class.
	      dBoxRec.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try {
					
					  Date dBoxDate = new Date();
			          DateFormat dBoxFormat = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss", Locale.UK);
			          
				      dBoxOutFile = Environment.getExternalStorageDirectory().
				      getAbsolutePath() + "/" +dBoxFormat.format(dBoxDate)+".3gp";
				      
				      //Create a MediaRecorder.
				      dBoxAudRec = new MediaRecorder();
				      dBoxAudRec.setAudioSource(MediaRecorder.AudioSource.MIC);
				      dBoxAudRec.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				      dBoxAudRec.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
				      dBoxAudRec.setOutputFile(dBoxOutFile);
				      
			          dBoxAudRec.prepare();
			          dBoxAudRec.start();
			      } catch (IllegalStateException e) {
			         e.printStackTrace();
			      } catch (IOException e) {
			         e.printStackTrace();
			      }
			      dBoxRec.setEnabled(false);
			      dBoxHalt.setEnabled(true);
			      Toast.makeText(getApplicationContext(), "Recording began", Toast.LENGTH_LONG).show();
				
			}
		});
	      
	      dBoxHalt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				  dBoxAudRec.stop();
			      dBoxAudRec.release();
			      dBoxAudRec  = null;
			      dBoxHalt.setEnabled(false);
			      dBoxUpAud.setEnabled(true);
			      Toast.makeText(getApplicationContext(), "Recording done successfully",
			      Toast.LENGTH_LONG).show();
				
			}
		});
	      
	      // Upload file to Dropbox.
	      dBoxUpAud.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try{
					String password = "password";
					File dBoxFile = new File(dBoxOutFile);
					File dBoxCovertedFile = DropboxEncryptionFragment.dBoxConFile(dBoxFile, password);
					
					if (dBoxCovertedFile == null){
						String dBoxString = 
								new String(getString(R.string.errorEnc));
						Toast.makeText(getApplicationContext(),
								dBoxString,
								Toast.LENGTH_SHORT).show();
					}else{
						
						String dBoxString = 
								new String(getString(R.string.fileEncrypted));
						Toast.makeText(getApplicationContext(),
								dBoxString,
								Toast.LENGTH_SHORT).show();
						
					DropboxFileUploadFragment dBoxUpload = new DropboxFileUploadFragment(DropboxMemoFragment.this, 
							DropboxFragment.dBoxMi, "Androcryptor/Audio/", dBoxCovertedFile);
					dBoxUpload.execute();
			        dBoxRec.setEnabled(true);
					}
							   
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
	 
			}
		});
	      
	      
	      
	   // Download file from Dropbox.
	      dBoxUpDow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				dBoxSelector = new DbxChooser(DropboxFragment.CHOOSER_APP_KEY);
				
				dBoxSelector.forResultType(DbxChooser.ResultType.FILE_CONTENT).launch(DropboxMemoFragment.this, DBX_CHOOSER_REQUEST);
	 
			}
		});
	      
	      
	   }
	   
	   
	   @Override
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    	if(resultCode == Activity.RESULT_OK) {
	    		

				String password = "password";
				switch(requestCode) {
				
				//Decrypt file.
				case DBX_CHOOSER_REQUEST:
					if (resultCode == Activity.RESULT_OK) {
		                DbxChooser.Result dBoxRes = new DbxChooser.Result(data);
		                Log.d("main", "Link to chosen file: " + dBoxRes.getLink());

		                File dBoxFile2 = new File(dBoxRes.getLink().getEncodedPath());
		                
						int encryptRes = DropboxEncryptionFragment.dBoxDecrFile(dBoxFile2, password);
						if (encryptRes == 0){
							String dBoxString = 
									new String(getString(
											R.string.fileDecryptedDownloaded));
							Toast.makeText(this.getApplicationContext(),
									dBoxString,
									Toast.LENGTH_SHORT).show();
							
							String dBoxFpath = Environment.
									getExternalStorageDirectory().getPath() +
									File.separator + "Download" + File.separator +
				                    dBoxRes.getName();
							dBoxRetrieveDoc(dBoxFpath);
						} else {
							String dBoxString = 
									new String(getString(R.string.errorDec));
							Toast.makeText(this.getApplicationContext(),
									dBoxString,
									Toast.LENGTH_SHORT).show();
						}
		            } else {

		            	Log.e("ERROR", "Unsuccessful or rejected by the end user");
		            }
					break;
				default:
					super.onActivityResult(requestCode, resultCode, data);
					break;
				}
			}
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
		    	Intent dBoxIntent = new Intent(this, DropboxMemoFragment.class);
		    	this.finish();
		    	
		    	return true;
		    }
		    return super.onOptionsItemSelected(item);
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			
		}
		
		
		 public void dBoxRetrieveDoc(String documentName) {
		        Intent dBoxInt = new Intent(android.content.Intent.ACTION_VIEW);
		        File dBoxFile = new File(documentName);
		        String dBoxExt = android.webkit.MimeTypeMap.getFileExtensionFromUrl
		        		(Uri.fromFile(dBoxFile).toString());
		        String dBoxMt = android.webkit.MimeTypeMap.getSingleton().
		        		getMimeTypeFromExtension(dBoxExt);
		        if (dBoxExt.equalsIgnoreCase("") || dBoxMt == null)
		        {
		        	
		        	dBoxInt.setDataAndType(Uri.fromFile(dBoxFile), "text/*");
		        }
		        else
		        {
		        	dBoxInt.setDataAndType(Uri.fromFile(dBoxFile), dBoxMt);            
		        }

		        startActivity(Intent.createChooser(dBoxInt, "Select an App:"));
		    }
		 
}
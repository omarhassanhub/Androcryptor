package com.myproject.androcryptor;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.app.Activity;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import java.util.ArrayList;
import java.util.List;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.chooser.android.DbxChooser;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.myproject.androcryptor.GoogledriveFragment;

public class GoogledriveFragment extends Fragment implements OnClickListener{
	static final int 				REQUEST_ACCOUNT_PICKER = 1;
	static final int 				REQUEST_AUTHORIZATION = 2;
	static final int 				RESULT_STORE_FILE = 4;
	static final int 				RESOLVE_CONNECTION_REQUEST_CODE = 0;
	private static Uri 				gBoxUrF;
	private String[] 				gBoxFarr;
	private String 					gBoxVa;
	private ArrayAdapter 			gBoxAdap;
	private GoogleAccountCredential gBoxCert;
	private Context 				gBoxPerspective;
	private List<File> 				gBoxLiR;
	private static Drive 			gBoxSer;
	private ListView 				gBoxLiV;
	
	private GoogleApiClient gBoxGoogleApiClient;
	
	public GoogledriveFragment(){
		
	}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_googledrive, container, false);
         
        gBoxCert = GoogleAccountCredential.usingOAuth2(this.getActivity(), DriveScopes.DRIVE_FILE);
        
        gBoxPerspective = getActivity().getApplicationContext();
		
        getActivity().setContentView(R.layout.fragment_googledrive);
		gBoxLiV = (ListView) getActivity().findViewById(R.id.listView1);
		
		OnItemClickListener gBoxManager = new OnItemClickListener() 
		{
		    public void onItemClick(AdapterView parent, View v, int position, long id) 
		    {
		    	gBoxLoadLi(position);
		    }
		};

		gBoxLiV.setOnItemClickListener(gBoxManager); 
		
		//Start another activity by pressing a button.
		
		final Button btnLin = (Button) getActivity().findViewById(R.id.buttonLinkAcc);
		btnLin.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {
            	startActivityForResult(gBoxCert.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            	
            }
        });
        
        final Button btnUnLink = (Button) getActivity().findViewById(R.id.buttonUnLinkAcc);
        btnUnLink.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {
            	 if (gBoxGoogleApiClient != null) {
                     gBoxGoogleApiClient.disconnect();
                 }
            }
        });
        
        
		final Button btnUpload = (Button) getActivity().findViewById(R.id.buttonUploadAcc);
		btnUpload.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {
            	if (gBoxCert.getSelectedAccountName() == null) {
					gBoxDisplayT("Please link with Google Drive");
            	}else{
            	
            	final Intent gBoxImageInt = new Intent(Intent.ACTION_PICK);
            	gBoxImageInt.setType("*/*");
                startActivityForResult(gBoxImageInt, RESULT_STORE_FILE);
            	}
            	}
        });
        
        final Button btnDownload = (Button) getActivity().findViewById(R.id.buttonDownloadAcc);
        btnDownload.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) 
            {
            	if (gBoxCert.getSelectedAccountName() == null) {
					gBoxDisplayT("Please link with Google Drive");
            	}else{
            	getGBoxFiles();
            }
            	
            }
        });
        
        return rootView;
        
        
    }
	//Get google drive contents.
	private void getGBoxFiles()
	{
		Thread gBoxT = new Thread(new Runnable() 
    	{
    		@Override
    		public void run() 
    		{
                gBoxLiR = new ArrayList<File>();
				com.google.api.services.drive.Drive.Files f1 = gBoxSer.files();
				com.google.api.services.drive.Drive.Files.List request = null;
				
				do 
				{
					try 
					{ 
						request = f1.list();
						request.setQ("trashed=false");
						com.google.api.services.drive.model.FileList fileList = request.execute();
						
						gBoxLiR.addAll(fileList.getItems());
						request.setPageToken(fileList.getNextPageToken());
					} catch (UserRecoverableAuthIOException e) {
						startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
					} catch (IOException e) {
						e.printStackTrace();
						if (request != null)
						{
							request.setPageToken(null);
						}
					}
				} while (request.getPageToken() !=null && request.getPageToken().length() > 0);
				
				gBoxFillLi();
    		}
    	});
		gBoxT.start();
	}
	//Download file from list.
	private void gBoxLoadLi(int position)
	{
		gBoxVa = (String) gBoxLiV.getItemAtPosition(position);
    	
    	Thread gBoxT = new Thread(new Runnable() 
    	{
    		@Override
    		public void run() 
    		{
		    	for(File tmp : gBoxLiR)
				{
					if (tmp.getTitle().equalsIgnoreCase(gBoxVa))
					{
						if (tmp.getDownloadUrl() != null && tmp.getDownloadUrl().length() >0)
						{
							try
							{
								com.google.api.client.http.HttpResponse resp = 
										gBoxSer.getRequestFactory()
										.buildGetRequest(new GenericUrl(tmp.getDownloadUrl()))
										.execute();
								InputStream gBoxInput = resp.getContent();
								try 
								{
									final java.io.File gBoxF = new java.io.File(Environment
											.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), 
											tmp.getTitle());
									
									

									
									gBoxHoldFile(gBoxF, gBoxInput);
									
									
									String password = "password";
									int encryptRes = DropboxEncryptionFragment.dBoxDecrFile(gBoxF, password);
									
									if (encryptRes == 0){
										String gBoxString = 
												new String(getString(
														R.string.fileDecryptedDownloaded));

										
										gBoxDisplayT(gBoxString);
										
										String gBoxFpath = Environment.
												getExternalStorageDirectory().getPath() +
												java.io.File.separator + "Download" + java.io.File.separator +
							                    gBoxF.getName();
										dBoxRetrieveDoc(gBoxFpath);
									} else {
										String gBoxString = 
												new String(getString(R.string.errorDec));

										
										gBoxDisplayT(gBoxString);
									}
					            
									
								} finally {
									gBoxInput.close();
								}
								
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
    		}
    	});
    	gBoxT.start();
	}
	//Populate list.
	private void gBoxFillLi()
	{
		getActivity().runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				gBoxFarr = new String[gBoxLiR.size()];
				int i = 0;
				for(File tmp : gBoxLiR)
				{
					gBoxFarr[i] = tmp.getTitle();
					i++;
				}
				gBoxAdap = new ArrayAdapter<String>(gBoxPerspective, android.R.layout.simple_list_item_1, gBoxFarr);
				gBoxLiV.setAdapter(gBoxAdap);
			}
		});
	}
	//Store file.
	private void gBoxHoldFile(java.io.File file, InputStream iStream)
	{
		try 
		{
			final OutputStream gBoxOput = new FileOutputStream(file);
			try
			{
				try
				{
					final byte[] gBoxBuff = new byte[1024];
					int read;
					while ((read = iStream.read(gBoxBuff)) != -1)
					{
						gBoxOput.write(gBoxBuff, 0, read);
					}
					gBoxOput.flush();
				} finally {
					gBoxOput.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Start the picker, authorise user, and get file data. 
	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) 
	{
		switch (requestCode) 
		{
			case REQUEST_ACCOUNT_PICKER:
				if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) 
				{
					
					String gBoxAccName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
					if (gBoxAccName != null) {
						gBoxCert.setSelectedAccountName(gBoxAccName);
						gBoxSer = getgBoxDriveService(gBoxCert);
						
					}
				}
				break;
			case REQUEST_AUTHORIZATION:
				if (resultCode == Activity.RESULT_OK) {
					gBoxGoogleApiClient.connect();

				} else {
					startActivityForResult(gBoxCert.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
				}
				break;
			case RESULT_STORE_FILE:
				gBoxUrF = data.getData();
				gBoxSaveToGDrive();
				break;
		
		}
	}
    
    private Drive getgBoxDriveService(GoogleAccountCredential credential) {
        return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
            .build();
      }

    //Save file to google drive.
    private void gBoxSaveToGDrive() 
    {
    	Thread gBoxT = new Thread(new Runnable() 
    	{
    		@Override
    		public void run() 
    		{
				try 
				{String password = "password";
					String gBoxPa;
					gBoxPa = getgBoxPaUri(gBoxUrF);
					gBoxUrF = Uri.fromFile(new java.io.File(gBoxPa));
					
					ContentResolver gBoxCr = GoogledriveFragment.this.getActivity().getContentResolver();
					
					java.io.File gBoxFileCont = new java.io.File(gBoxUrF.getPath());
					
					java.io.File gBoxCovertedFile = DropboxEncryptionFragment.dBoxConFile(gBoxFileCont, password);
					
					FileContent gBoxMediaCont = new FileContent(gBoxCr.getType(gBoxUrF), gBoxCovertedFile);

					File gBoxBody = new File();
					gBoxBody.setTitle(gBoxFileCont.getName());
					gBoxBody.setMimeType(gBoxCr.getType(gBoxUrF));
					
					

					com.google.api.services.drive.Drive.Files f1 = gBoxSer.files();
					com.google.api.services.drive.Drive.Files.Insert i1 = f1.insert(gBoxBody, gBoxMediaCont);
					File gBoxfile = i1.execute();
					
					if (gBoxfile != null) 
					{
						gBoxDisplayT("File Encrypted and Uploaded");
					}
				} catch (UserRecoverableAuthIOException e) {
					startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
				} catch (IOException e) {
					e.printStackTrace();
					gBoxDisplayT("Transfer Fault: " + e.toString());
				}
    		}
    	});
    	gBoxT.start();
	}

	public void gBoxDisplayT(final String toast) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getActivity().getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
			}
		});
	}
	//Get file path from Uri.
	public String getgBoxPaUri(Uri uri) 
	{
        String[] gBoxProj = { MediaStore.Images.Media.DATA };
        Cursor gBoxCursor = getActivity().getContentResolver().query(uri, gBoxProj, null, null, null);
        int gBoxColumnIndex = gBoxCursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        gBoxCursor.moveToFirst();
        return gBoxCursor.getString(gBoxColumnIndex);
	}


	@Override
	public void onClick(View v) {
		
		
	}

	public void dBoxRetrieveDoc(String documentName) {
        Intent dBoxInt = new Intent(android.content.Intent.ACTION_VIEW);
        java.io.File dBoxFile = new java.io.File(documentName);
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

package com.myproject.androcryptor;


import com.microsoft.onedrivesdk.picker.*;
import android.app.*;
import android.app.DownloadManager.Request;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class OnedrivePickFileFragment extends Activity implements OnItemClickListener {

    private static final String ONEDRIVE_APP_ID = "4414AD98";
    
    //Check radio button is selected. 
    private final OnClickListener oBoxBeginChooseListener = new OnClickListener() {
        @Override
        public void onClick(final View v) {

            oBoxClear();

            LinkType oBoxLinkType;
            
            oBoxLinkType = LinkType.DownloadLink;


            oBoxPicker.startPicking((Activity)v.getContext(), oBoxLinkType);
        }
    };

   //Save file locally.
    private final OnClickListener oBoxSave = new OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (oBoxDownloadUrl == null) {
                return;
            }

            final DownloadManager oBoxDownloadManager = (DownloadManager)v.getContext().getSystemService(DOWNLOAD_SERVICE);
            final Request oBoxRequest = new Request(oBoxDownloadUrl);
            oBoxRequest.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            oBoxDownloadManager.enqueue(oBoxRequest);
        }
    };

   
    private IPicker oBoxPicker;

    private Uri oBoxDownloadUrl;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getActionBar().setTitle("Download File");
        setContentView(R.layout.fragment_onedrivepickfile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        

        oBoxPicker = Picker.createPicker(ONEDRIVE_APP_ID);

        ((Button)findViewById(R.id.buttonDownload)).setOnClickListener(oBoxBeginChooseListener);

        ((Button)findViewById(R.id.buttonSave)).setOnClickListener(oBoxSave);
    }
    
    //Get file from the picker.
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        final IPickerResult oBoxResult = oBoxPicker.getPickerResult(requestCode, resultCode, data);

        if (oBoxResult == null) {
            Toast.makeText(this, "Cannot get file from OneDrive.", Toast.LENGTH_LONG).show();
            return;
        }

            String password = "password";
            File oBoxFile2 = new File(oBoxResult.getLink().getEncodedPath());
            
			int encryptRes = DropboxEncryptionFragment.dBoxDecrFile(oBoxFile2, password);
			if (encryptRes == 0){
				String oBoxString = 
						new String(getString(
								R.string.fileDecryptedDownloaded));
				Toast.makeText(this.getApplicationContext(),
						oBoxString,
						Toast.LENGTH_SHORT).show();
				
				String oBoxFpath = Environment.
						getExternalStorageDirectory().getPath() +
						File.separator + "Download" + File.separator;
				dBoxRetrieveDoc(oBoxFpath);
			} else {
				String oBoxString = 
						new String(getString(R.string.errorDec));
				Toast.makeText(this.getApplicationContext(),
						oBoxString,
						Toast.LENGTH_SHORT).show();
			}
  

        oBoxUpdateResTable(oBoxResult);
    }

   
    private void oBoxUpdateResTable(final IPickerResult result) {

            oBoxDownloadUrl = result.getLink();

    }

   
    private void oBoxClear() {

        oBoxDownloadUrl = null;
    }

    //Loading large Bitmaps.
    private Bitmap getoBoxBitmap(final Uri uri) {
        try {
            if (uri == null) {
                return null;
            }

            final URL oBoxUrl = new URL(uri.toString());
            final InputStream oBoxInputStream = oBoxUrl.openConnection().getInputStream();
            final Bitmap oBoxBitMap = BitmapFactory.decodeStream(oBoxInputStream);
            oBoxInputStream.close();
            return oBoxBitMap;
        } catch (final Exception e) {
            return null;
        }
    }

    //UI thread.
    private AsyncTask<Void, Void, Bitmap> createUpdateThumbnail(final ImageView imageView, final Uri imageSource) {
        return new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(final Void... params) {
                return getoBoxBitmap(imageSource);
            }

            @Override
            protected void onPostExecute(final Bitmap result) {
                imageView.setImageBitmap(result);
            }
        };
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
	    	
	    	Intent oBoxIntent = new Intent(this, OnedrivePickFileFragment.class);
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
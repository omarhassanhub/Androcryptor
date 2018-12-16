package com.myproject.androcryptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

@SuppressLint("SimpleDateFormat")
public class DropboxFileUploadFragment extends AsyncTask<Void, Long, Boolean> {

    private DropboxAPI<?> dBoxMi;
    private final ProgressDialog dBoxDia;
    private String dBoxPa;
    private File dBoxFile;
    private Context dBoxContext;
    private String dBoxErrMasage;private long dBoxFLength;
    private UploadRequest dBoxReq;
    

    @SuppressWarnings("deprecation")
	public DropboxFileUploadFragment(Context context, DropboxAPI<?> api, String
			dropboxPath, File file) {
    	
        dBoxContext = context.getApplicationContext();
        dBoxFLength = file.length();
        dBoxMi = api;
        dBoxPa = dropboxPath;
        dBoxFile = file;

        String dBoxString = dBoxContext.getResources().getString(R.string.uploading);
        String dBoxString2 = dBoxContext.getResources().getString(R.string.cancel);
        
        dBoxDia = new ProgressDialog(context);
        dBoxDia.setMax(100);
        dBoxDia.setMessage(dBoxString + file.getName());
        dBoxDia.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dBoxDia.setProgress(0);
        dBoxDia.setButton(dBoxString2, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dBoxReq.abort();
            }
        });
        dBoxDia.show();
    }
    
    //Extract file details and upload it to Dropbox drive.

    @Override
    protected Boolean doInBackground(Void... params) {
        try {

            FileInputStream dBoxFileInput = new FileInputStream(dBoxFile);
            String dBoxP2 = dBoxPa + dBoxFile.getName();
            dBoxReq = dBoxMi.putFileOverwriteRequest(dBoxP2, dBoxFileInput, dBoxFile.length(),
                    new ProgressListener() {
                @Override
                public long progressInterval() {
 
                    return 500;
                }

                @Override
                public void onProgress(long bytes, long total) {
                    publishProgress(bytes);
                }
            });

            if (dBoxReq != null) {
                dBoxReq.upload();
                return true;
            }

        } catch (DropboxUnlinkedException e) {

            dBoxErrMasage = "Application was not verified.";
        } catch (DropboxFileSizeException e) {

            dBoxErrMasage = "File too large to transfer";
        } catch (DropboxPartialFileException e) {
 
            dBoxErrMasage = "Transfer rejected";
        } catch (DropboxServerException e) {
    
            if (e.error == DropboxServerException._401_UNAUTHORIZED) {
            
            	Log.e("ERROR", "User is unauthorized");
            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
            
            	Log.e("ERROR", "Access denied");
            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
            
            	Log.e("ERROR", "File is not reachable");
            } else if (e.error == 
            		DropboxServerException._507_INSUFFICIENT_STORAGE) {
            	
            	Log.e("ERROR", "Not enough storage");
            } else {
            
                Log.e("ERROR", "All failed");
            }
       
            dBoxErrMasage = e.body.userError;
            if (dBoxErrMasage == null) {
                dBoxErrMasage = e.body.error;
            }
        } catch (DropboxIOException e) {

            dBoxErrMasage = "Net issue.  Please Re-try.";
        } catch (DropboxParseException e) {
       
            dBoxErrMasage = "Provider error.  Please Re-try.";
        } catch (DropboxException e) {
      
            dBoxErrMasage = "Unidentified error.  Please Re-try.";
        } catch (FileNotFoundException e) {
       
            Log.e("ERROR", "File is not accessible");
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Long... progress) {
        int dBoxPer = (int)(100.0*(double)progress[0]/dBoxFLength + 0.5);
        dBoxDia.setProgress(dBoxPer);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        dBoxDia.dismiss();
        String dBoxString = dBoxContext.getResources().getString(R.string.fileUpload);
        if (result) {
        	dBoxDisplayT(dBoxString);
            dBoxFile.delete();
        } else {
        	dBoxDisplayT(dBoxErrMasage);
        }
    }

    private void dBoxDisplayT(String msg) {
        Toast dBoxErr = Toast.makeText(dBoxContext, msg, Toast.LENGTH_LONG);
        dBoxErr.show();
    }
}

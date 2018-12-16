package com.myproject.androcryptor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.DropboxAPI.ThumbFormat;
import com.dropbox.client2.DropboxAPI.ThumbSize;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

public class DropboxFileDownloadFragment extends AsyncTask<Void, Long, Boolean> {

    private Long dBoxFlength;
    private String dBoxErrMssage;
    private Context dBoxPerspective;
    private String dBoxP;
    private ImageView dBoxImg;
    private final ProgressDialog dBoxDia;
    private DropboxAPI<?> dBoxMi;
    private FileOutputStream dBoxOutput;
    private Drawable dBoxLayout;
    private boolean dBoxRejected;
    
    private final static String IMAGE_FILE_NAME = "Androcryptor.png";

    @SuppressWarnings("deprecation")
	public DropboxFileDownloadFragment(Context context, DropboxAPI<?> api,
            String dropboxPath, ImageView view) {

        dBoxPerspective = context.getApplicationContext();

        dBoxMi = api;
        dBoxP = dropboxPath;
        dBoxImg = view;

        String dBoxString = 
        		new String(dBoxPerspective.getResources().getString(R.string.cancel));
        String dBoxString2 = 
        		new String(dBoxPerspective.getResources().
        				getString(R.string.fileDownload));
        
        dBoxDia = new ProgressDialog(context);
        dBoxDia.setMessage(dBoxString2);
        dBoxDia.setButton(dBoxString, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dBoxRejected = true;
                dBoxErrMssage = 
                		dBoxPerspective.getResources().getString(R.string.cancelled);

                if (dBoxOutput != null) {
                    try {
                    	dBoxOutput.close();
                    } catch (IOException e) {
        
    	            	Log.e("ERROR", "Task rejected");
                    }
                }
            }
        });

        dBoxDia.show();
    }
    //Extract file details and download it to the SD card. 
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (dBoxRejected) {
                return false;
            }

            Entry dBoxEntDir = dBoxMi.metadata(dBoxP, 1000, null, true, null);

            if (!dBoxEntDir.isDir || dBoxEntDir.contents == null) {

                dBoxErrMssage = 
                		dBoxPerspective.getResources().getString(R.string.emptyFile);
                return false;
            }


            ArrayList<Entry> dBoxTh = new ArrayList<Entry>();
            for (Entry ent: dBoxEntDir.contents) {
                if (ent.thumbExists) {
     
                	dBoxTh.add(ent);
                }
            }

            if (dBoxRejected) {
                return false;
            }

            if (dBoxTh.size() == 0) {
    
            	dBoxErrMssage = 
                		dBoxPerspective.getResources().getString(R.string.noImage);
                return false;
            }

  
            int dBoxInd = (int)(Math.random() * dBoxTh.size());
            Entry dBoxEntDir2 = dBoxTh.get(dBoxInd);
            String dBoxP = dBoxEntDir2.path;
            dBoxFlength = dBoxEntDir2.bytes;


            String dBoxCdir = dBoxPerspective.getCacheDir().getAbsolutePath() + "/" +
            		IMAGE_FILE_NAME;
            try {
            	dBoxOutput = new FileOutputStream(dBoxCdir);
            } catch (FileNotFoundException e) {
            	dBoxErrMssage = 
                		dBoxPerspective.getResources().getString(R.string.storeError);
                return false;
            }

            dBoxMi.getThumbnail(dBoxP, dBoxOutput, ThumbSize.BESTFIT_960x640,
                    ThumbFormat.JPEG, null);
            if (dBoxRejected) {
                return false;
            }

            dBoxLayout = Drawable.createFromPath(dBoxCdir);
     
            return true;

        } catch (DropboxUnlinkedException e) {
    
        	dBoxErrMssage = dBoxPerspective.getResources().getString(R.string.authError);
        } catch (DropboxPartialFileException e) {

            dBoxErrMssage = dBoxPerspective.getResources().getString(R.string.cancelled);
        } catch (DropboxServerException e) {
    
            if (e.error == DropboxServerException._304_NOT_MODIFIED) {
   
            	Log.e("ERROR", "did not alter file");
            } else if (e.error == DropboxServerException._401_UNAUTHORIZED) {
       
            	Log.e("ERROR", "Unapproved end user");
            } else if (e.error == DropboxServerException._403_FORBIDDEN) {
           
            	Log.e("ERROR", "Access denied");
            } else if (e.error == DropboxServerException._404_NOT_FOUND) {
     
            	Log.e("ERROR", "File not reachable");
            } else if (e.error == DropboxServerException._406_NOT_ACCEPTABLE) {
     
            	Log.e("ERROR", "Excessive entry");
            } else if (e.error == 
            		DropboxServerException._415_UNSUPPORTED_MEDIA) {
       
            	Log.e("ERROR", "Unable to thumbnail file");
            } else if (e.error == 
            		DropboxServerException._507_INSUFFICIENT_STORAGE) {
       
            	Log.e("ERROR", "Not adequate storage");
            } else {
            
                Log.e("ERROR", "All failed");
            }
        
            dBoxErrMssage = e.body.userError;
            if (dBoxErrMssage == null) {
                dBoxErrMssage = e.body.error;
            }
        } catch (DropboxIOException e) {
   
        	dBoxErrMssage = dBoxPerspective.getResources().getString(R.string.networkError);
        } catch (DropboxParseException e) {
     
        	dBoxErrMssage = dBoxPerspective.getResources().getString(R.string.dropboxError);
        } catch (DropboxException e) {
          
        	dBoxErrMssage = dBoxPerspective.getResources().getString(R.string.unknownError);
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(Long... progress) {
        int dBoxPer = (int)(100.0*(double)progress[0]/dBoxFlength + 0.5);
        dBoxDia.setProgress(dBoxPer);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        dBoxDia.dismiss();
        if (result) {
           
            dBoxImg.setImageDrawable(dBoxLayout);
        } else {
        
        	dBoxDisplayT(dBoxErrMssage);
        }
    }

    private void dBoxDisplayT(String msg) {
        Toast dBoxErr = Toast.makeText(dBoxPerspective, msg, Toast.LENGTH_LONG);
        dBoxErr.show();
    }


}

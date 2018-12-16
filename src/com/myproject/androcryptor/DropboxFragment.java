package com.myproject.androcryptor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.view.MenuInflater;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.dropbox.chooser.android.DbxChooser;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import com.myproject.androcryptor.DropboxFragment;
import com.myproject.androcryptor.DropboxFileBrowseFragment;
import com.myproject.androcryptor.DropboxFragment;
import com.myproject.androcryptor.DropboxEncryptionFragment;
import com.myproject.androcryptor.R;
import com.myproject.androcryptor.DropboxFileUploadFragment;

@SuppressLint("SimpleDateFormat")

public class DropboxFragment extends Fragment implements OnClickListener {
    private static final String TAG = "Androcryptor";
    private static final int PICKFILE_RESULT_CODE = 1;
    static final int DBX_CHOOSER_REQUEST = 2;
    final static private String APP_KEY = "4c9rnxcxsxwzc2e";
    final static private String APP_SECRET = "zxhbqjkmvmav5so";
    final static  String CHOOSER_APP_KEY = "ugv9pn66hkvh5oj";
    final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    static DropboxAPI<AndroidAuthSession> dBoxMi;

    private boolean dBoxSignIn;
    DbxChooser dBoxSelector;
    private Button dBoxBtnSubmit;
    private RelativeLayout dBoxDisplay;
    private Button dBoxTransfer;
    private Button dBoxLoad;
    private Button dBoxTeam;
    private Button dBoxInvite;
    private Button dBoxMemoDrop;
    private Button dBoxComment;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

    	AndroidAuthSession session = buildSession();
        dBoxMi = new DropboxAPI<AndroidAuthSession>(session);
        dBoxVerifyKey();

    	View rootView = inflater.inflate(R.layout.fragment_dropbox, container, false);

        dBoxBtnSubmit = (Button) rootView.findViewById(R.id.buttonLinkAcc);
		dBoxBtnSubmit.setOnClickListener(this);
		
		dBoxTransfer = (Button) rootView.findViewById(R.id.buttonUploadAcc);
		dBoxTransfer.setOnClickListener(this);
		
		dBoxLoad = (Button) rootView.findViewById(R.id.buttonDownloadAcc);
		dBoxLoad.setOnClickListener(this);
		
		dBoxTeam = (Button) rootView.findViewById(R.id.team);
		dBoxTeam.setOnClickListener(this);
		
		dBoxInvite = (Button) rootView.findViewById(R.id.invite);
		dBoxInvite.setOnClickListener(this);
		
		dBoxMemoDrop = (Button) rootView.findViewById(R.id.memo);
		dBoxMemoDrop.setOnClickListener(this);
		
		dBoxComment = (Button) rootView.findViewById(R.id.comment);
		dBoxComment.setOnClickListener(this);

		dBoxSetSignedIn(false);

		dBoxSetSignedIn(dBoxMi.getSession().isLinked());

        return rootView; 
    }
    
    //Start another activity by pressing a button.
    @Override
	public void onClick(View v) {
    	if(v == dBoxBtnSubmit){
		if (dBoxSignIn) {
			dBoxSignOut();
        } else {

            dBoxMi.getSession().startAuthentication(DropboxFragment.this.getActivity());
        }
		
    }
    else if (v == dBoxTransfer) {
			  
			Intent dBoxIntent = new Intent(dBoxTransfer.getContext(), 
    				DropboxFileBrowseFragment.class);
    		startActivityForResult(dBoxIntent, PICKFILE_RESULT_CODE);

	}
       
    else if (v == dBoxLoad) {

			 dBoxSelector = new DbxChooser(CHOOSER_APP_KEY);
			
			dBoxSelector.forResultType(DbxChooser.ResultType.FILE_CONTENT).launch(DropboxFragment.this, DBX_CHOOSER_REQUEST);

	}
    	
    else if (v == dBoxTeam) {

    	Intent dBoxIntent = new Intent(getActivity(), DropboxTeamFragment.class);
        startActivity(dBoxIntent);

	}
    	
    else if (v == dBoxInvite) {

    	Intent dBoxIntent = new Intent(getActivity(), DropboxInviteFragment.class);
        startActivity(dBoxIntent);

	}
    	
    else if (v == dBoxComment) {

    	Intent dBoxIntent = new Intent(getActivity(), DropboxCommentFragment.class);
        startActivity(dBoxIntent);

	}
    	
    else if (v == dBoxMemoDrop) {

    	Intent dBoxIntent = new Intent(getActivity(), DropboxMemoFragment.class);
        startActivity(dBoxIntent);

	}

	}

    //Verify authentication
    @Override
    public void onResume() {
        super.onResume();
        AndroidAuthSession dBoxSes = dBoxMi.getSession();

        if (dBoxSes.authenticationSuccessful()) {
            try {

            	dBoxSes.finishAuthentication();

                TokenPair dBoxTok = dBoxSes.getAccessTokenPair();
                dBoxHoldKeys(dBoxTok.key, dBoxTok.secret);
                dBoxSetSignedIn(true);
            } catch (IllegalStateException e) {
            	String dBoxString = 
						new String(getString(R.string.errorAuth));
            	dBoxDisplay(dBoxString + 
                		e.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }

    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(resultCode == Activity.RESULT_OK) {
    		
    		//Encrypt file.
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(this.getActivity());

			String password = "password";
			switch(requestCode) {
			case PICKFILE_RESULT_CODE:
				if(data.hasExtra(DropboxFileBrowseFragment.EXTRA_FILE_PATH)) {

					File dBoxFile = new File(data.getStringExtra(
							DropboxFileBrowseFragment.EXTRA_FILE_PATH));

					File dBoxCovertedFile = DropboxEncryptionFragment.dBoxConFile(dBoxFile, password);
					if (dBoxCovertedFile == null){
						String dBoxString = 
								new String(getString(R.string.errorEnc));
						Toast.makeText(getActivity().getApplicationContext(),
								dBoxString,
								Toast.LENGTH_SHORT).show();
					} else {
						String dBoxString = 
								new String(getString(R.string.fileEncrypted));
						Toast.makeText(getActivity().getApplicationContext(),
								dBoxString,
								Toast.LENGTH_SHORT).show();
						

						String dBoxFpath = "/Androcryptor/";
						
					    DropboxFileUploadFragment dBoxUpload = new DropboxFileUploadFragment(this.getActivity(), 
					    		dBoxMi, dBoxFpath, dBoxCovertedFile);
					    dBoxUpload.execute();
					}
				}
				break;
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
						Toast.makeText(getActivity().getApplicationContext(),
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
						Toast.makeText(getActivity().getApplicationContext(),
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
    //Logout user from the system.
    private void dBoxSignOut() {

        dBoxMi.getSession().unlink();
        dBoxEraseKeys();
        dBoxSetSignedIn(false);
    }

    //Change button status.
    private void dBoxSetSignedIn(boolean loggedIn) {
    	dBoxSignIn = loggedIn;
    	if (loggedIn) {
    		dBoxBtnSubmit.setText("Unlink from Dropbox");

    	} else {
    		dBoxBtnSubmit.setText("Link with Dropbox");

    	}
    }
    //Verify app keys. 
    private void dBoxVerifyKey() {

        if (APP_KEY.startsWith("CHANGE") ||
                APP_SECRET.startsWith("CHANGE")) {
            Log.w("error", "You have to request for an app key and secret key at Dropbox");
            getActivity().finish();
            return;
        }

        Intent dBoxIntCheck = new Intent(Intent.ACTION_VIEW);
        String dBoxSch = "db-" + APP_KEY;
        String uri = dBoxSch + "://" + AuthActivity.AUTH_VERSION + "/test";
        dBoxIntCheck.setData(Uri.parse(uri));
        PackageManager pm = getActivity().getPackageManager();
        if (0 == pm.queryIntentActivities(dBoxIntCheck, 0).size()) {
            Log.w("error", "URL is not set correctly." + dBoxSch);
            getActivity().finish();
        }
    }

    private void dBoxDisplay(String msg) {
        Toast dBoxErr = Toast.makeText(this.getActivity(), msg, Toast.LENGTH_LONG);
        dBoxErr.show();
    }

    private String[] dBoxObtainKeys() {
        SharedPreferences dBoxPre = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String dBoxKey = dBoxPre.getString(ACCESS_KEY_NAME, null);
        String dBoxSec = dBoxPre.getString(ACCESS_SECRET_NAME, null);
        if (dBoxKey != null && dBoxSec != null) {
        	String[] dBoxR = new String[2];
        	dBoxR[0] = dBoxKey;
        	dBoxR[1] = dBoxSec;
        	return dBoxR;
        } else {
        	return null;
        }
    }

    private void dBoxHoldKeys(String key, String secret) {

        SharedPreferences dBoxPre = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor dBoxEd = dBoxPre.edit();
        dBoxEd.putString(ACCESS_KEY_NAME, key);
        dBoxEd.putString(ACCESS_SECRET_NAME, secret);
        dBoxEd.commit();
    }

    private void dBoxEraseKeys() {
        SharedPreferences dBoxPre = getActivity().getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor dBoxEdit = dBoxPre.edit();
        dBoxEdit.clear();
        dBoxEdit.commit();
    }
    //Authenticate app keys.
    private AndroidAuthSession buildSession() {
        AppKeyPair dBoxAppKp = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession dBoxSes;

        String[] dBoxHeld = dBoxObtainKeys();
        if (dBoxHeld != null) {
            AccessTokenPair dBoxTokAcc = new AccessTokenPair(dBoxHeld[0],
            		dBoxHeld[1]);
            dBoxSes = new AndroidAuthSession(dBoxAppKp, ACCESS_TYPE,
            		dBoxTokAcc);
        } else {
        	dBoxSes = new AndroidAuthSession(dBoxAppKp, ACCESS_TYPE);
        }

        return dBoxSes;
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

package com.myproject.androcryptor;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DropboxFileBrowseFragment extends ListActivity {
	
	public final static String EXTRA_FILE_PATH = "file_path";
	public final static String EXTRA_SHOW_HIDDEN_FILES = "show_hidden_files";
	public final static String EXTRA_ACCEPTED_FILE_EXTENSIONS = 
			"accepted_file_extensions";
	private final static String DEFAULT_INITIAL_DIRECTORY = 
			Environment.getExternalStorageDirectory().getPath();
	
	protected boolean dBoxDisplayHidFiles = false;
	protected String[] dBoxFileExt;
	protected ArrayList<File> dBoxFiles;
	protected dBoxFgetListAdap dBoxAdap;
	protected File dBoxDirectory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LayoutInflater dBoxInflator = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View emptyView = dBoxInflator.inflate(R.layout.fragment_pickfileemptyview, null);
		((ViewGroup)getListView().getParent()).addView(emptyView);
		getListView().setEmptyView(emptyView);

		dBoxDirectory = new File(DEFAULT_INITIAL_DIRECTORY);

		dBoxFiles = new ArrayList<File>();

		dBoxAdap = new dBoxFgetListAdap(this, dBoxFiles);
		setListAdapter(dBoxAdap);

		dBoxFileExt = new String[] {};
		
		if(getIntent().hasExtra(EXTRA_FILE_PATH)) {
			dBoxDirectory = new File(getIntent().getStringExtra(EXTRA_FILE_PATH));
		}
		if(getIntent().hasExtra(EXTRA_SHOW_HIDDEN_FILES)) {
			dBoxDisplayHidFiles = getIntent().getBooleanExtra(EXTRA_SHOW_HIDDEN_FILES, false);
		}
		if(getIntent().hasExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS)) {
			ArrayList<String> dBoxColl = getIntent().getStringArrayListExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS);
			dBoxFileExt = (String[]) dBoxColl.toArray(new String[dBoxColl.size()]);
		}
	}
	
	@Override
	protected void onResume() {
		dBoxRefFList();
		super.onResume();
	}
	//Update file list.
	protected void dBoxRefFList() {
	
		dBoxFiles.clear();
		
		dBoxExtFfil dBoxFil = new dBoxExtFfil(dBoxFileExt);
		
		File[] dBoxFiles2 = dBoxDirectory.listFiles(dBoxFil);
		if(dBoxFiles2 != null && dBoxFiles2.length > 0) {
			for(File f : dBoxFiles2) {
				if(f.isHidden() && !dBoxDisplayHidFiles) {
				
					continue;
				}
	
				dBoxFiles.add(f);
			}
			
			Collections.sort(dBoxFiles, new dBoxFComp());
		}
		dBoxAdap.notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed() {
		if(dBoxDirectory.getParentFile() != null) {
		
			dBoxDirectory = dBoxDirectory.getParentFile();
			dBoxRefFList();
			return;
		}
		
		super.onBackPressed();
	}
	
	//Get file path.
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File dBoxNFile = (File)l.getItemAtPosition(position);
		
		if(dBoxNFile.isFile()) {
	
			Intent dBoxExtra = new Intent();
			dBoxExtra.putExtra(EXTRA_FILE_PATH, dBoxNFile.getAbsolutePath());
			setResult(RESULT_OK, dBoxExtra);
		
			finish();
		} else {
			dBoxDirectory = dBoxNFile;
	
			dBoxRefFList();
		}
		
		super.onListItemClick(l, v, position, id);
	}
	
	//Select the file.
	private class dBoxFgetListAdap extends ArrayAdapter<File> {
		
		private List<File> dBoxObj;
		
		public dBoxFgetListAdap(Context context, List<File> objects) {
			super(context, R.layout.fragment_pickfilelistitem, android.R.id.text1, objects);
			dBoxObj = objects;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View dBoxTr = null;
			
			if(convertView == null) { 
				LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				dBoxTr = inflater.inflate(R.layout.fragment_pickfilelistitem, parent, false);
			} else {
				dBoxTr = convertView;
			}

			File dBoxObj2 = dBoxObj.get(position);

			ImageView dBoxImgV = (ImageView)dBoxTr.findViewById(R.id.pickerImage);
			TextView dBoxTxtV = (TextView)dBoxTr.findViewById(R.id.pickerText);

			dBoxTxtV.setSingleLine(true);
			
			dBoxTxtV.setText(dBoxObj2.getName());
			if(dBoxObj2.isFile()) {
		
				dBoxImgV.setImageResource(R.drawable.file);
			} else {
		
				dBoxImgV.setImageResource(R.drawable.folder);
			}
			
			return dBoxTr;
		}

	}
	
	private class dBoxFComp implements Comparator<File> {
	    @Override
	    public int compare(File f1, File f2) {
	    	if(f1 == f2) {
	    		return 0;
	    	}
	    	if(f1.isDirectory() && f2.isFile()) {
	    
	        	return -1;
	        }
	    	if(f1.isFile() && f2.isDirectory()) {
	 
	        	return 1;
	        }
	
	        return f1.getName().compareToIgnoreCase(f2.getName());
	    }
	}
	
	private class dBoxExtFfil implements FilenameFilter {
		private String[] dBoxExt;
		
		public dBoxExtFfil(String[] extensions) {
			super();
			dBoxExt = extensions;
		}
		
		@Override
		public boolean accept(File dir, String filename) {
			if(new File(dir, filename).isDirectory()) {
		
				return true;
			}
			if(dBoxExt != null && dBoxExt.length > 0) {
				for(int i = 0; i < dBoxExt.length; i++) {
					if(filename.endsWith(dBoxExt[i])) {
		
						return true;
					}
				}
	
				return false;
			}
	
			return true;
		}
	}
}

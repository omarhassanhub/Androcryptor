package com.myproject.androcryptor;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.HashMap;

import com.microsoft.onedrivesdk.picker.IPicker;
import com.microsoft.onedrivesdk.picker.Picker;
import com.myproject.androcryptor.R;
import com.myproject.androcryptor.library.DatabaseHandler;
import com.myproject.androcryptor.library.UserFunctions;

public class OnedriveFragment extends Fragment implements OnClickListener{

	public OnedriveFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_onedrive, container, false);
         
        Button a = (Button) rootView.findViewById(R.id.buttonUploadAcc);
        a.setOnClickListener(this);
        
        Button b = (Button) rootView.findViewById(R.id.buttonDownloadAcc);
        b.setOnClickListener(this);
        
        return rootView;
        
        
    }
	
	public void onClick(View v) {
        
        switch (v.getId()) {

        case R.id.buttonUploadAcc:

        	Intent intent = new Intent(getActivity(), OnedriveSaveFileFragment.class);
            startActivity(intent);

            break;
            
        case R.id.buttonDownloadAcc:

        	Intent intent2 = new Intent(getActivity(), OnedrivePickFileFragment.class);
            startActivity(intent2);

            break;
        }
    }
}

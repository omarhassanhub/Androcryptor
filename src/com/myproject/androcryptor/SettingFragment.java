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
import com.myproject.androcryptor.R;
import com.myproject.androcryptor.library.DatabaseHandler;
import com.myproject.androcryptor.library.UserFunctions;

public class SettingFragment extends Fragment implements OnClickListener{
	private Button button;
	
	public SettingFragment(){
		
	}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
         
        Button b = (Button) rootView.findViewById(R.id.button1);
        b.setOnClickListener(this);
        
        return rootView;
        
        
    }
	
	public void onClick(View v) {
        
        switch (v.getId()) {
        case R.id.button1:

        	Intent intent = new Intent(getActivity(), AccountPassChangeFragment.class);
            startActivity(intent);
            
            break;
        }
    }
}

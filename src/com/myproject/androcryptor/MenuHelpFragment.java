package com.myproject.androcryptor;

import com.myproject.androcryptor.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MenuHelpFragment extends Fragment {
	
	public MenuHelpFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
         
        return rootView;
    }
}

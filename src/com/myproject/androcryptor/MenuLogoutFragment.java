package com.myproject.androcryptor;

import com.myproject.androcryptor.R;
import com.myproject.androcryptor.library.DatabaseHandler;
import com.myproject.androcryptor.library.UserFunctions;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuLogoutFragment extends Fragment implements OnClickListener{
	
	public MenuLogoutFragment(){
		
	}
	Button btnLogout;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_logout, container, false);
        Button b = (Button) rootView.findViewById(R.id.button1);
        b.setOnClickListener(this);
        return rootView;
	
	
	}

	
	 public void onClick(View v) {
	        
	        switch (v.getId()) {
	        case R.id.button1:

	            UserFunctions logout = new UserFunctions();
	            logout.dAccLogoutUser(getActivity().getApplicationContext());
	            Intent login = new Intent(getActivity().getApplicationContext(), AccountLoginFragment.class);
	            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(login);

	        }
	    }
	 
}



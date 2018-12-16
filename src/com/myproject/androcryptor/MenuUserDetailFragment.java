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
import com.myproject.androcryptor.DropboxFileBrowseFragment;

public class MenuUserDetailFragment extends Fragment implements OnClickListener{
    private Button button;
    public MenuUserDetailFragment(){
    	
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_userdetail, container, false);

        Button b = (Button) rootView.findViewById(R.id.signout);
        b.setOnClickListener(this);

        DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
        
         HashMap<String,String> user = new HashMap<String, String>();
         user = db.dAccGetUserDetails();
         
         final TextView fName = (TextView) rootView.findViewById(R.id.textFirstName);
         fName.setText(user.get("fname"));
         final TextView lName = (TextView) rootView.findViewById(R.id.textLastName);
         lName.setText(user.get("lname"));
         final TextView Email = (TextView) rootView.findViewById(R.id.textEmail);
         Email.setText(user.get("email"));
         final TextView uName = (TextView) rootView.findViewById(R.id.textUsername);
         uName.setText(user.get("uname"));
         final TextView dName = (TextView) rootView.findViewById(R.id.textDeviceName);
         dName.setText(android.os.Build.MODEL);

        return rootView;
    }

    public void onClick(View v) {
       
        switch (v.getId()) {
        case R.id.signout:
         
        	UserFunctions logout = new UserFunctions();
            logout.dAccLogoutUser(getActivity().getApplicationContext());
            Intent login = new Intent(getActivity().getApplicationContext(), AccountLoginFragment.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            getActivity().finish();

            break;
    
        }
       
    }

}



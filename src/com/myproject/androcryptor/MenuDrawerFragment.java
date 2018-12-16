package com.myproject.androcryptor;

import java.util.ArrayList;

import com.myproject.androcryptor.R;
import com.myproject.androcryptor.adapter.NavDrawerListAdapter;
import com.myproject.androcryptor.library.UserFunctions;
import com.myproject.androcryptor.model.drawerNavItem;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MenuDrawerFragment extends Activity {
	
    private DrawerLayout aDrawLayout;
    private ListView aDrawList;
    private ActionBarDrawerToggle aDrawToggle;
    private CharSequence aDrawTitle1;
    private CharSequence aDrawTitle2;
    private String[] aDrawNavMenuTitles;
    private TypedArray aDrawNavMenuIcons;
    private ArrayList<drawerNavItem> aNavDrawItems;
    private NavDrawerListAdapter aDrawAdapter;

    @SuppressLint("a")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        aDrawTitle2 = aDrawTitle1 = getTitle();

        aDrawNavMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        aDrawNavMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        aDrawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        aDrawList = (ListView) findViewById(R.id.list_slidermenu);

        aNavDrawItems = new ArrayList<drawerNavItem>();
        //Navigation buttons.
        aNavDrawItems.add(new drawerNavItem(aDrawNavMenuTitles[0], aDrawNavMenuIcons.getResourceId(0, -1)));
        
        aNavDrawItems.add(new drawerNavItem(aDrawNavMenuTitles[1], aDrawNavMenuIcons.getResourceId(1, -1)));
        
        aNavDrawItems.add(new drawerNavItem(aDrawNavMenuTitles[2], aDrawNavMenuIcons.getResourceId(2, -1)));
        
        aNavDrawItems.add(new drawerNavItem(aDrawNavMenuTitles[3], aDrawNavMenuIcons.getResourceId(3, -1)));
        
        aNavDrawItems.add(new drawerNavItem(aDrawNavMenuTitles[4], aDrawNavMenuIcons.getResourceId(4, -1)));
        
        aNavDrawItems.add(new drawerNavItem(aDrawNavMenuTitles[5], aDrawNavMenuIcons.getResourceId(5, -1)));
        
        aNavDrawItems.add(new drawerNavItem(aDrawNavMenuTitles[6], aDrawNavMenuIcons.getResourceId(6, -1)));
        
        aNavDrawItems.add(new drawerNavItem(aDrawNavMenuTitles[7], aDrawNavMenuIcons.getResourceId(7, -1)));
        
        aDrawNavMenuIcons.recycle();

        aDrawList.setOnItemClickListener(new SlideMenuClickListener());

        aDrawAdapter = new NavDrawerListAdapter(getApplicationContext(),
                aNavDrawItems);
        aDrawList.setAdapter(aDrawAdapter);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        aDrawToggle = new ActionBarDrawerToggle(this, aDrawLayout,
                R.drawable.ic_drawer,
                R.string.app_name, 
                R.string.app_name 
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(aDrawTitle2);
       
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(aDrawTitle1);
     
                invalidateOptionsMenu();
            }
        };
        aDrawLayout.setDrawerListener(aDrawToggle);

        if (savedInstanceState == null) {
      
            displayView(0);
        }
    }
    //Configure slide menu.
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {

            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //Select an option.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (aDrawToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
        case R.id.action_settings:
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        boolean drawerOpen = aDrawLayout.isDrawerOpen(aDrawList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    //Display view upon button press.
    private void displayView(int position) {
 
        Fragment fragment = null;
        
        switch (position) {
        case 0:
        	fragment = new MenuUserDetailFragment();
            break;
        case 1:
            fragment = new DropboxFragment();
            break;
            
        case 2:
            fragment = new OnedriveFragment();
            break;
            
        case 3:
            fragment = new GoogledriveFragment();
            break;
            
        case 4:
            fragment = new MenuSettingFragment();
            break;
            
        case 5:
            fragment = new MenuHelpFragment();
            break;
            
        case 6:
            fragment = new MenuAboutFragment();
            break;
            
        case 7:
            
        	UserFunctions logout = new UserFunctions();
            logout.dAccLogoutUser(getApplicationContext());
            Intent login = new Intent(getApplicationContext(), AccountLoginFragment.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            finish();
            
        	break;

        default:
            break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            aDrawList.setItemChecked(position, true);
            aDrawList.setSelection(position);
            setTitle(aDrawNavMenuTitles[position]);
            aDrawLayout.closeDrawer(aDrawList);
        } else {

            Log.e("MainActivity", "Error when trying to create a fragment");
        }
    }
    //Set navigation title.
    @Override
    public void setTitle(CharSequence title) {
    	aDrawTitle2 = title;
        getActionBar().setTitle(aDrawTitle2);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
 
        aDrawToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        aDrawToggle.onConfigurationChanged(newConfig);
    }
    //Back button.
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
               .setMessage("Are you sure you want to logout?")
               .setCancelable(false)
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   UserFunctions logout = new UserFunctions();
       	            logout.dAccLogoutUser(getApplicationContext());
       	            Intent login = new Intent(getApplicationContext(), AccountLoginFragment.class);
       	            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       	            startActivity(login);
                   }
               })
               .setNegativeButton("No", null)
               .show();
    }

}
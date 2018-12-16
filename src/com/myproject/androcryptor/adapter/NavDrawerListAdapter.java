package com.myproject.androcryptor.adapter;

import java.util.ArrayList;

import com.myproject.androcryptor.R;
import com.myproject.androcryptor.model.drawerNavItem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//Using drawer base adapter. 
public class NavDrawerListAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<drawerNavItem> drawerNavItems;
	
	public NavDrawerListAdapter(Context context, ArrayList<drawerNavItem> drawerNavItems){
		this.context = context;
		this.drawerNavItems = drawerNavItems;
	}

	@Override
	public int getCount() {
		return drawerNavItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return drawerNavItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.fragment_drawerlistitem, null);
        }
         
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        TextView txtCount = (TextView) convertView.findViewById(R.id.counter);
         
        imgIcon.setImageResource(drawerNavItems.get(position).getDrawerIcon());        
        txtTitle.setText(drawerNavItems.get(position).getDrawerTitle());

        if(drawerNavItems.get(position).getDrawerCounterVisibility()){
        	txtCount.setText(drawerNavItems.get(position).getDrawerCount());
        }else{
        	txtCount.setVisibility(View.GONE);
        }
        
        return convertView;
	}

}

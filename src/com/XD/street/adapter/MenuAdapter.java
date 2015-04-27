package com.XD.street.adapter;

import java.util.List;
import java.util.Map;

import com.XD.street.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter{

	public List<Map<String, String>> menuList;
	private LayoutInflater mInflater;

	public MenuAdapter(Context context)
	{
		mInflater = LayoutInflater.from(context);//²¼¾ÖÌî³äÆ÷
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (menuList == null) {
			return 0;
		}
		else return menuList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return menuList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.menu_item, null);
		}
		Map<String, String> board = menuList.get(position);
		TextView menu_title = (TextView)convertView.findViewById(R.id.menu_item_title);
		
		menu_title.setText(board.get("name"));
		return convertView;
	}
}

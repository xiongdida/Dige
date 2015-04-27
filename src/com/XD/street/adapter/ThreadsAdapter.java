package com.XD.street.adapter;

import java.util.ArrayList;

import com.XD.street.R;
import com.XD.street.data.ThreadData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ThreadsAdapter extends BaseAdapter{
	public ArrayList<ThreadData> array;
	private LayoutInflater mInflater;

	public ThreadsAdapter(Context mContext) {
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(mContext);//²¼¾ÖÌî³äÆ÷
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return array.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return array.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		try 
		{			
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.thread_item, null);
			}
			ThreadData data = array.get(position);
			TextView title = (TextView)convertView.findViewById(R.id.thread_title);
			TextView date = (TextView)convertView.findViewById(R.id.thread_date);
			TextView author = (TextView)convertView.findViewById(R.id.thread_author);
			TextView replies = (TextView)convertView.findViewById(R.id.thread_replies);
			TextView lights = (TextView)convertView.findViewById(R.id.thread_light);
			ImageView lightpic = (ImageView)convertView.findViewById(R.id.light_pic);

			title.setText(data.subject);
			date.setText(data.postdate);
			author.setText(data.author);
			
			int random = (int)(Math.random()*5);
			int color = colors[random];
			replies.setBackgroundResource(color);
			replies.setText(data.replies);
			
			if( Integer.parseInt(data.lights) > 0) {
				lights.setText(data.lights);
				lights.setVisibility(View.VISIBLE);
				lightpic.setVisibility(View.VISIBLE);
			}
			else {
				lights.setVisibility(View.INVISIBLE);
				lightpic.setVisibility(View.INVISIBLE);
			}
			
			return convertView;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private int colors [] = {
			R.drawable.orange,
			R.drawable.navy,
			R.drawable.real_blue,
			R.drawable.purple,
			R.drawable.green
	};

}

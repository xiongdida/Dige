package com.XD.street.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.XD.street.data.ThreadData;
import com.XD.street.utils.URLConn;
import com.XD.street.utils.URLConn.OnResponseListener;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ThreadsModel {

	public static final String FailedLoad = "ThreadsDidFailedLoad";
	public static final String FinishedLoad = "ThreadsDidFinishedLoad";
	private static final String ThreadsURL = "http://mobileapi.hupu.com/1/1.1.1/bbs/getboardthreads?order=1&fid=%s&page=%d&num=20&boardpw=&token=null";
	private Context mContext;
	private String mFid;	
	private boolean clearFirst;
	private boolean isCancelled;
	private int mPage;
	
	private URLConn mConnection;
	
	public ArrayList<ThreadData> threads;

	public ThreadsModel(Context context, String fid) {
		// TODO Auto-generated constructor stub
	    mContext = context;
		mFid = fid;
		threads = new ArrayList<ThreadData>();
	}

	private void load(int page) {
		// TODO Auto-generated method stub
		isCancelled = false;
		if(mConnection != null){
			mConnection.cancel();
		}
		String url = String.format(ThreadsModel.ThreadsURL, mFid, page);
		mConnection = new URLConn(url);
		mConnection.setOnResponseListener(new OnResponseListener() {	
			@Override
			public void OnResponse(String response) {
					parser(response, clearFirst);
			}
			
			@Override
			public void OnError(String msg) {
				Log.d("XD", msg);
				Intent intent = new Intent(ThreadsModel.FailedLoad);
				mContext.sendBroadcast(intent);
			}
		});
		mConnection.startConnect();
	}

	protected void parser(String response, boolean clearFlag) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(response);
			JSONArray Jarray = object.getJSONArray("result");
			
			if(clearFlag) {
				threads.clear();
			}
			
			for (int i = 0; i < Jarray.length(); i++) {
				ThreadData thread = new ThreadData();
				JSONObject json = Jarray.getJSONObject(i);
				thread.author = json.getString("username");
				thread.tid = json.getString("tid");
				thread.fid = json.getString("fid");
				thread.subject = json.getString("subject");
				thread.replies = json.getString("replies");
				thread.lights = json.getString("lights");
				String postdate = json.getString("postdate");
				thread.postdate = dateFormat(Long.parseLong(postdate));
				
				if(!contains(thread))
				{
					threads.add(thread);
				}				
			}
			Intent intent = new Intent(ThreadsModel.FinishedLoad);
			mContext.sendBroadcast(intent);
			return;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (mContext != null) {
				Intent intent = new Intent(ThreadsModel.FailedLoad);
				mContext.sendBroadcast(intent);
			}
		}
	}

	private String dateFormat(long timeIntivalSince1970) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		return sdf.format(new Date(timeIntivalSince1970 * 1000));
	}
	
	private boolean contains(ThreadData thread) {
		// TODO Auto-generated method stub
		for(ThreadData t : threads)
		{
			if(thread.tid.equals(t.tid))
				return true;
		}
		return false;
	}

	public void cancel() {
		if(mConnection != null)
		{			
			mConnection.cancel();
		}
		isCancelled = true;
	}
	
	public void refresh() {
		// TODO Auto-generated method stub
		clearFirst = true;
	    mPage = 1;
	    load(mPage);
	}
	
	public void loadMore() {
		// TODO Auto-generated method stub
		clearFirst = false;
	    mPage++;
	    load(mPage);
	}
	

}

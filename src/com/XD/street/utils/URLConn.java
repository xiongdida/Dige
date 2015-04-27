package com.XD.street.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.XD.street.model.ThreadsModel;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class URLConn {
	
	private HttpURLConnection hpConnection;
	private String mURL;
	private String mHttpMethod;
	private connectTask mTask;
	private byte[] mHttpBody;
	private String mReadCharsetName;
	private String mWriteCharsetName;
	private OnResponseListener mListener;
	private Map<String, String> mRequestHeaders;

	public URLConn(String url) {
		// TODO Auto-generated constructor stub
		mURL = url;
		mHttpMethod = "GET";
		mRequestHeaders = new HashMap<String, String>();
		mReadCharsetName = "UTF-8";
		mWriteCharsetName = "UTF-8";
	}

	public void setHttpMethod(String method) {
		mHttpMethod = method;
	}
	
	public void addRequestHeader(String key, String value){
		mRequestHeaders.put(key, value);
	}
	
	public void setHttpBody(String body) {
		// TODO Auto-generated method stub
		try {
			mHttpBody = body.getBytes(mWriteCharsetName);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cancel() {
		// TODO Auto-generated method stub
		if(mTask != null) {
			mListener = null;
			mTask.cancel(true);
		}
		else {
			hpConnection.disconnect();
		}
	}

	

	public void startConnect() {
		// TODO Auto-generated method stub
		mTask = new connectTask();
		mTask.execute();
	}
	
	private class connectTask extends AsyncTask<String, Integer, String> 
	{		
		@Override  
        protected String doInBackground(String... params) {
			String response = connectProcess();
			return response;
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			if(hpConnection != null) {
				hpConnection.disconnect();
			}
		}
		
		@Override  
        protected void onPostExecute(String result) {
			if(mListener != null) {
				if (result == null) {
					mListener.OnError("postExecuteError");
					return;
				}
				else
				{
					mListener.OnResponse(result);				
					mListener = null;
				}
			}
		}
	}
	
	private HttpURLConnection connection() throws IOException {
		// TODO Auto-generated method stub
		URL url = new URL(mURL);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setReadTimeout(20000); // milliseconds
		conn.setConnectTimeout(20000); // milliseconds
		conn.setRequestMethod(mHttpMethod);
		conn.setDoInput(true);
		if(mHttpMethod.equalsIgnoreCase("POST")) {
			conn.setDoOutput(true);
			conn.setUseCaches(false);
		}
		for(String key : mRequestHeaders.keySet()) {
			String value = mRequestHeaders.get(key);
			conn.setRequestProperty(key, value);
			System.out.println("header: key: " + key + " value: " + value);
		}		
		return conn;
	}

	public String connectProcess() {
		// TODO Auto-generated method stub
		try {
			hpConnection = connection();
			hpConnection.connect();
			
			if(hpConnection.getRequestMethod().equalsIgnoreCase("POST")){
				OutputStream os = hpConnection.getOutputStream();
				if(mHttpBody != null) {
					os.write(mHttpBody);
				}
			}
			InputStream in = hpConnection.getInputStream();
			String response = readData(in, mReadCharsetName);
			hpConnection.disconnect();
			return response;
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	        if (mListener != null)
	        	mListener.OnError("connectError");
	        if (hpConnection != null)
	            hpConnection.disconnect();
	          return null;
		}		
	}

	private String readData(InputStream is, String charsetName) throws IOException {
		// TODO Auto-generated method stub
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while( (len = is.read(buffer)) != -1 ){
            baos.write(buffer, 0, len);
        }
        byte[] data = baos.toByteArray();
        baos.close();
        is.close();
        return new String(data, charsetName);
	}

	public interface OnResponseListener
	{
		public void OnResponse(String response);
		public void OnError(String msg);
	}

	public void setOnResponseListener(OnResponseListener listener) {
		// TODO Auto-generated method stub
		mListener = listener;
	}

}

package com.XD.street.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.XD.street.utils.URLConn;
import com.XD.street.utils.URLConn.OnResponseListener;
import com.XD.street.utils.dataUtils;


public class MyBoardsModel {

	private static MyBoardsModel instance;
	private static Context mContext;
	private URLConn mConnection;
	
	public List<Map<String, String>> list;
	
	public static final String Finish = "menuFinishedLoad";
	private static final String MY_BOARDS_URL = "http://mobileapi.hupu.com/1/1.1.1/bbs/getusercollectedboards?token=";
	public static final String MY_BOARDS_JSON = "myboards.json";
	
	
	public static MyBoardsModel getInstance(Context context) {
		// TODO Auto-generated method stub
		if (instance == null) {
			mContext = context;
			instance = new MyBoardsModel();			
		}
		return instance;
	}

	public void load() {
		// TODO Auto-generated method stub
		try 
		{
			String s = LoginModel.getInstance(mContext).getToken();
			if (s != null) {
			if (mConnection != null) {
				mConnection.cancel();
			}
			String url;
			url = MY_BOARDS_URL + URLEncoder.encode(s, "utf-8");
			mConnection = new URLConn(url);
			mConnection.setOnResponseListener(mOnResponseListener);
			mConnection.startConnect();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//
//	public void loadMyBoards() {
//		
//		if (fid.equals(MY_BOARDS_FID)) {
//			reloadMyBoards();
//			continue;
//		}		
//	}
	
	public void reloadMyBoards() {
		// TODO Auto-generated method stub
		try {
			String json = ReadJSONFromFile(MY_BOARDS_JSON);
			if (json != null) {
				JSONArray array = new JSONArray(json);
				list = new ArrayList<Map<String, String>>();
				
				for(int i = 0; i < array.length(); i++) {
					JSONObject board = array.getJSONObject(i);
					String fid = board.getString("fid");
					String name = board.getString("name");
					Map<String, String> map = new HashMap<String, String>();
					map.put("fid", fid);
					map.put("name", name);
					list.add(map);
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeToFile(String fileName, String json) {
		try {
			OutputStream output = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
			output.write(json.getBytes("utf-8"));
			output.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String ReadJSONFromFile(String fileName) {
		try {
			InputStream input = mContext.openFileInput(fileName);
			byte[] data = new byte[input.available()];
			input.read(data);
			input.close();
			return new String(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private OnResponseListener mOnResponseListener = new OnResponseListener() {

		@Override
		public void OnResponse(String response) {
			// TODO Auto-generated method stub
			System.out.println(response);
			try 
			{
				if (response != null) {
					JSONObject json = new JSONObject(response);
					if (json.has("result")) {
						JSONArray Jarray = json.getJSONArray("result");
						String jsonString = Jarray.toString();
						writeToFile(MY_BOARDS_JSON, jsonString);
						reloadMyBoards();
						Intent intent = new Intent(MyBoardsModel.Finish);
						mContext.sendBroadcast(intent);
					}
				}
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void OnError(String msg) {
			// TODO Auto-generated method stub
			System.out.println("XD: load my boards failed.");
		}		
	};


}

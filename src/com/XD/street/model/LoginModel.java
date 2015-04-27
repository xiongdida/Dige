package com.XD.street.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.XD.street.application.inApplication;
import com.XD.street.data.UserInfo;
import com.XD.street.utils.URLConn;
import com.XD.street.utils.URLConn.OnResponseListener;
import com.XD.street.utils.dataUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class LoginModel {

	// login broadcast msgs
	public static final String LOGIN_FINISHED = "login_finished";
	public static final String LOGIN_FAILED = "login_failed";
	public static final String WRONG_PASSWORD = "wrong_password";
		
	public boolean isLogedin;
	public UserInfo userInfo;
	
	private static final String LOGIN_URL = "http://mobileapi.hupu.com/1/1.1.1/passport/login";
	private static final String ZEN_MSG_TOKEN_URL = "http://my.hupu.com/mymsg.php?action=send";
	private static final String DEF_VALUE = "null";
	private static LoginModel instance;
	
	private URLConn connection;
	private SharedPreferences.Editor editor;
	private static Context mContext;
	
	public static synchronized LoginModel getInstance(Context context) {
		// TODO Auto-generated method stub
		if(instance == null)  
		{
			instance = new LoginModel();
			mContext = context;
			instance.load();
		}
        return instance;
	}
		
	private void load() {
		isLogedin = false;
		userInfo = new UserInfo();
		SharedPreferences sPreferences = mContext.getSharedPreferences("user_info", 0);
		editor = sPreferences.edit();
		
		// get preferences from the sharedPreferences
		if (sPreferences.contains("userName") && 
				sPreferences.contains("token") && 
				sPreferences.contains("uid")) {
			isLogedin = true;
			userInfo.userName = sPreferences.getString("userName", DEF_VALUE);
			userInfo.token = sPreferences.getString("token", DEF_VALUE);
			userInfo.uid = sPreferences.getString("uid", DEF_VALUE);
			userInfo.msgToken = sPreferences.getString("msgToken", DEF_VALUE);
			// verify values
			if(userInfo.userName.equals(DEF_VALUE) ||
					userInfo.token.equals(DEF_VALUE)||
					userInfo.uid.equals(DEF_VALUE)) {
				isLogedin = false;
			}
		}
	}
	
	public void login(String userName, String password) {
		if (userName == null || password == null) {
			mContext.sendBroadcast(new Intent(LOGIN_FAILED));
			return;
		}
		if (connection != null) {
			connection.cancel();
			connection = null;
		}
		connection = new URLConn(LOGIN_URL);
		connection.setHttpMethod("POST");
		connection.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		connection.setHttpBody("username=" + userName + "&password=" + dataUtils.md5(password));
		connection.setOnResponseListener(mOnResponseListener);
		connection.startConnect();
	}
	
	private OnResponseListener mOnResponseListener = new OnResponseListener() {

		@Override
		public void OnResponse(String response) {
			// TODO Auto-generated method stub
			try {
				if (response != null)
				{
			        System.out.println("response: " + response);
					JSONObject json = new JSONObject(response);
					if(json.has("error"))
					{
						JSONObject eJson = json.getJSONObject("error");
						String s = eJson.getString("text");
				        System.out.println(s);
				        mContext.sendBroadcast(new Intent(WRONG_PASSWORD));
					}
					else {
						JSONObject result = json.getJSONObject("result");
						JSONObject user = result.getJSONObject("user");
						userInfo.userName = user.getString("username");
						userInfo.uid = user.getString("uid");
						userInfo.token = user.getString("token");
						isLogedin = true;
						saveInfo();
//						fetchMSGToken();
						mContext.sendBroadcast(new Intent(LOGIN_FINISHED));
					}
				}
				return;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		        System.out.println("login failed!");
		        mContext.sendBroadcast(new Intent(LOGIN_FAILED));
			}
		}

		@Override
		public void OnError(String msg) {
			// TODO Auto-generated method stub
			mContext.sendBroadcast(new Intent(LOGIN_FAILED));
		}
		
	};
	
	private void saveInfo() {
		if (editor != null) {
			System.out.println("User Info Saved");
			editor.putString("userName", userInfo.userName);
			editor.putString("uid", userInfo.uid);
			editor.putString("token", userInfo.token);
			editor.commit();
		}
	}

	public void logout() {
		userInfo = new UserInfo();
		isLogedin = false;
		editor.clear();
		editor.commit();
	}
	
	public String getToken() {
		if (isLogedin) {
			return userInfo.token;
		}
		return null;
	}

}

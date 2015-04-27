package com.XD.street.model;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.XD.street.data.ThreadData;
import com.XD.street.data.ThreadReply;
import com.XD.street.utils.URLConn;
import com.XD.street.utils.URLConn.OnResponseListener;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;

public class contentModel {

	public static String ThreadData_FailedLoad = "ThreadData_Failed";
	public static String ThreadData_FinishedLoad = "ThreadData_FinishedLoad";
	public static String Replies_FailedLoad = "Replies_Failed";
	public static String Replies_FinishedLoad = "Replies_FinishedLoad";
	public static final String RepliesEmpty = "RepliesEmpty";
	public ThreadData threadData;
	public ArrayList<ThreadReply> replies;
	public int currentPage;
	public int floor;
	public int pageCount;
	
	private Context mContext;
	private String mTid;

	private static final String ThreadDataURL = "http://mobileapi.hupu.com/1/1.1.1/bbs/getthreaddata?type=2&tid=%s&boardpw=&token=";
	private static final String ThreadRepliesURL = "http://mobileapi.hupu.com/1/1.1.1/bbs/getthreadreplies?sort=1&tid=%s&page=%d&pagecount=20&boardpw=&token=";
	
	public contentModel(Context context, String tid) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mTid = tid;
		threadData = new ThreadData();
		replies = new ArrayList<ThreadReply>();
	}

	public void refresh() {
		// TODO Auto-generated method stub
		loadReplies(currentPage);
	}

	public void next() {
		// TODO Auto-generated method stub
		currentPage++;
		loadReplies(currentPage);
	}

	public void prev() {
		// TODO Auto-generated method stub
		currentPage--;
		loadReplies(currentPage);
	}
	
	public void loadThreadData() {
		// TODO Auto-generated method stub
		try{
			currentPage = 1;
			String url = String.format(ThreadDataURL, mTid);  //"4903564"
			url = url + "null";
			
			System.out.println("loadThreadData: " + url);
			URLConn mConnection = new URLConn(url);
			mConnection.setOnResponseListener(new OnResponseListener() {	
				@Override
				public void OnResponse(String response) {
					parserThreadTopic(response);
				}
				
				@Override
				public void OnError(String msg) {
					Log.d("XD", msg);
					Intent errorIntent = new Intent(contentModel.ThreadData_FailedLoad );
					mContext.sendBroadcast(errorIntent);
				}
			});
			mConnection.startConnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parserThreadTopic(String response) {
		// TODO Auto-generated method stub
		try {
			JSONObject object = new JSONObject(response);
			JSONObject Tjson = object.getJSONObject("result");
			
			threadData.author = Tjson.getString("username");
			threadData.tid = Tjson.getString("tid");
			threadData.fid = Tjson.getString("fid");
			threadData.subject = Tjson.getString("subject");
			threadData.replies = Tjson.getString("replies");
			threadData.lights = Tjson.getString("lights");
			String postdate = Tjson.getString("postdate");
			threadData.postdate = dateFormat(Long.parseLong(postdate));
			String content = Tjson.getString("content");
			threadData.content = content;
			
			Intent successIntent = new Intent(contentModel.ThreadData_FinishedLoad);
			mContext.sendBroadcast(successIntent);
			return;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (mContext != null) {
				Intent intent = new Intent(contentModel.ThreadData_FailedLoad);
				mContext.sendBroadcast(intent);
			}
		}
	}
	
	private String dateFormat(long timeIntivalSince1970) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		return sdf.format(new Date(timeIntivalSince1970 * 1000));
	}

	public void loadReplies(int page) {
		// TODO Auto-generated method stub
		try {
			String url = String.format(ThreadRepliesURL, mTid, page);
			url = url + "null";
			URLConn conn = new URLConn(url);
			conn.setOnResponseListener(new OnResponseListener() {
				
				@Override
				public void OnResponse(String response) {
					parserRepliesResponse(response);
				}
				
				@Override
				public void OnError(String msg) {
					Intent errorIntent = new Intent(contentModel.Replies_FailedLoad);
					mContext.sendBroadcast(errorIntent);
				}
			});
			conn.startConnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parserRepliesResponse(String response) {
		// TODO Auto-generated method stub
		try {
			JSONObject json = new JSONObject(response);
			JSONObject dict = json.getJSONObject("result");
			pageCount = dict.getInt("pagecount");
			currentPage = dict.getInt("page");
			floor = ((currentPage - 1) * 20 + 1);
			
			JSONObject data = dict.getJSONObject("data");
			replies.clear();
			if (data.has("replies")) {
				JSONArray repliesJSON = data.getJSONArray("replies");				
				for (int i = 0; i < repliesJSON.length(); i++) {
					JSONObject object = repliesJSON.getJSONObject(i);
					ThreadReply reply = ReplyOfJSON(object);
					reply.floor = String.valueOf(i + floor);
					if (reply != null) {
						replies.add(reply);
					}
				}
				Intent successIntent = new Intent(contentModel.Replies_FinishedLoad);
				mContext.sendBroadcast(successIntent);
			}
			
//			lightReplies.clear();
//			if (data.has("lightReplies")) {
//				JSONArray lightRepliesJSON = data.getJSONArray("lightReplies");
//				
//				for (int i = 0; i < lightRepliesJSON.length(); i++) {
//					JSONObject object = lightRepliesJSON.getJSONObject(i);
//					ZenThreadReply reply = zenReplyDataWithJSON(object);
//					if (reply != null) {
//						lightReplies.add(reply);
//					}
//				}
//			}
			else
			{
				Intent intent = new Intent(contentModel.RepliesEmpty);
				mContext.sendBroadcast(intent);
			}
			return;
		} catch (Exception e) {
			Log.d("XD", "加载不出来啊");
			e.printStackTrace();
		}
	}

	private ThreadReply ReplyOfJSON(JSONObject json) throws Exception {
		// TODO Auto-generated method stub
		ThreadReply data = new ThreadReply();
		data.author = json.getString("author");
		data.authorId = json.getString("authorid");
		String content = json.getString("content");
		data.content = content;
		data.postDate = json.getString("postdate");
		if (json.has("light")) {
			data.lights = json.getString("light");
		}
		else {
			data.lights = "0";
		}
		data.pid = json.getString("pid");
		return data;
	}


}

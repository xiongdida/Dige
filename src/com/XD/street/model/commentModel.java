package com.XD.street.model;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import com.XD.street.utils.URLConn;
import com.XD.street.utils.URLConn.OnResponseListener;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class commentModel {

	public static final String COMMENT_FINISHED = "comment_finished";
	public static final String COMMENT_FAILED = "comment_failed";
	public static final String RECOMMEND_FINISHED = "recommend_finished";
	public static final String RECOMMEND_FAILED = "recommend_failed";
	
	private static final String COMMENT_URL = "http://bbs.hupu.com/post.php?";
	private static final String RECOMMEND_URL = "http://bbs.hupu.com/indexinfo/buddys.php";
	
	private Context mContext;
	private String mFid;
	private String mTid;
	private URLConn mConnection;
		
	public commentModel(Context context, String fid, String tid) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mFid = fid;
		mTid = tid;
	}

/********推荐模块**********/
/*************************/
	
	
	public void recommend(String title, String content, String token) {
		// TODO Auto-generated method stub
		try {
			if (mConnection != null) {
				mConnection.cancel();
				mConnection = null;
			}

			String tokenEncoded = URLEncoder.encode(token, "utf-8");
			mConnection = new URLConn(RECOMMEND_URL);
			mConnection.setOnResponseListener(mRecommendListener);
			mConnection.setHttpMethod("POST");
			mConnection.addRequestHeader("Content-Type",
					"application/x-www-form-urlencoded ; charset=UTF-8");
			mConnection.addRequestHeader("Cookie", "u=" + tokenEncoded + ";");
			mConnection.addRequestHeader("X-Requested-With", "XMLHttpRequest");
			mConnection.addRequestHeader("Referer", "http://bbs.hupu.com/"
					+ mTid + ".html");

			String httpBody = "fid=" + mFid + "&act=rc" + "&cid=" + mTid
					+ "&title=" + URLEncoder.encode(title, "utf-8") + "&rmmsg="
					+ URLEncoder.encode(content, "utf-8") + "&type=1";
			mConnection.setHttpBody(httpBody);
			mConnection.startConnect();
		} catch (Exception e) {
			e.printStackTrace();
			Intent intent = new Intent(RECOMMEND_FAILED);
			intent.putExtra("msg", "推荐失败...");
			mContext.sendBroadcast(intent);
		}
	}
	
	private OnResponseListener mRecommendListener = new OnResponseListener() {

		@Override
		public void OnResponse(String response) {
			try {
				if (response != null) {
					String result = response.toLowerCase(Locale.getDefault());
					if (result.equals("1\n")) {
						Intent intent = new Intent(RECOMMEND_FINISHED);
						intent.putExtra("msg", "推荐成功");
						mContext.sendBroadcast(intent);
					} else if (result.equals("level")) {
						Intent intent = new Intent(RECOMMEND_FAILED);
						intent.putExtra("msg", "你的等级不够...");
						mContext.sendBroadcast(intent);
					}
					return;
				}

			} catch (Exception e) {
				e.printStackTrace();
				Intent intent = new Intent(RECOMMEND_FAILED);
				intent.putExtra("msg", "推荐失败...");
				mContext.sendBroadcast(intent);
			}
		}

		@Override
		public void OnError(String msg) {
			Intent intent = new Intent(RECOMMEND_FAILED);
			intent.putExtra("msg", "推荐失败...");
			mContext.sendBroadcast(intent);
		}
	};
	
/********评论模块**********/
/*************************/
	
	public void comment(String content, String title) {
		CommentTask task = new CommentTask();
		task.execute(content, title);
	}
	
	private String newcomment(String content, String title) {
		try {
			if (mConnection != null) {
				mConnection.cancel();
				mConnection = null;
			}
			String token = LoginModel.getInstance(mContext).getToken();
			String tokenEncoded = URLEncoder.encode(token, "utf-8");
			StringBuffer buf = new StringBuffer();
			StringBuffer contentBuf = new StringBuffer();
			contentBuf.append(content);

			buf.append("atc_content="
					+ URLEncoder.encode(contentBuf.toString(), "GBK"));
			
			buf.append("&usevote=0&douid=1&votetype=bbs&votetitle=&votename[]=&votename[]=&votename[]=&editnum=3&nowitnum=3&voteclass=&postfast=2&atc_title=Re:" + title + "&atc_usesign=1&atc_convert=1&atc_autourl=1&step=2&");
			buf.append("action=reply");
			buf.append("&fid=" + mFid);
			buf.append("&tid=" + mTid);
			buf.append("&subject=" + title + "&editor=0&atc_attachment=none&replayofpage=&replaymeta=1");

			String body = buf.toString();

			System.out.println("body: " + body);
			mConnection = new URLConn(COMMENT_URL);
			mConnection.setHttpMethod("POST");
			mConnection.addRequestHeader("Content-Type",
					"application/x-www-form-urlencoded");
			mConnection
					.addRequestHeader(
							"User-Agent",
							"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:26.0) Gecko/20100101 Firefox/26.0");
			mConnection.addRequestHeader("Referer", "	http://bbs.hupu.com/"
					+ mTid + ".html");
			mConnection.addRequestHeader("Cookie", "u=" + tokenEncoded + ";");
			mConnection.setHttpBody(body);
			String response = mConnection.connectProcess();
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			Intent intent = new Intent(COMMENT_FAILED);
			mContext.sendBroadcast(intent);
			
		}
		return null;
	}

	private class CommentTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				String content = params[0];
				String title = params[1];

				String response = newcomment(content, title);
				if (response != null) {
					return "sendfinish";
				}
			} catch (Exception e) {
				Log.d("XD" , "commentError");
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				if (result.indexOf("sendfinish") != -1) {
					mContext.sendBroadcast(new Intent(COMMENT_FINISHED));
				}
			} else {
				Intent intent = new Intent(COMMENT_FAILED);
				mContext.sendBroadcast(intent);
			}
		}
	}

}

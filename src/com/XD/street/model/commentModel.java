package com.XD.street.model;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;

import com.XD.street.utils.URLConn;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class commentModel {

	public static final String COMMENT_FINISHED = "comment_finished";
	public static final String COMMENT_FAILED = "comment_failed";
	
	private static final String COMMENT_URL = "http://bbs.hupu.com/post.php?";
	
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
//			buf.append("&subject=" + title + "&editor=0&atc_attachment=none&replayofpage=&replaymeta=1");

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

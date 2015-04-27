package com.XD.street.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;

import com.XD.street.R;
import com.XD.street.model.commentModel;
import com.XD.street.view.LoadingView;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class PostActivity extends SherlockActivity {

	public static final int TYPE_COMMENT = 0xFF01;
	
	private EditText mPostTitle;
	private EditText mPostContent;
	
	private String mFid;
	private String mTid;
	private String mSubject;
	
	private LoadingView mLoading;	
	private commentModel comModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.post_frame);
		
		mPostTitle = (EditText) findViewById(R.id.post_title);
		mPostContent = (EditText) findViewById(R.id.post_content);
		
		mLoading = new LoadingView(this);
		
		setTitle("评论");
		Intent intent = getIntent();
		mFid = intent.getStringExtra("fid");
		mTid = intent.getStringExtra("tid");
		mSubject = intent.getStringExtra("subject");
		
		mPostTitle.setFocusable(false);
		mPostTitle.setFocusableInTouchMode(false);
		mPostTitle.setText("Reply:" + mSubject);
		comModel = new commentModel(this, mFid, mTid);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.post_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
			case R.id.post_send:
				send();
				break;
		}
		return true;
	}
	
	@Override
	protected void onResume() {
		try {
			IntentFilter filter = new IntentFilter();
			filter.addAction(commentModel.COMMENT_FINISHED);
			filter.addAction(commentModel.COMMENT_FAILED);

			registerReceiver(mCommentReceiver, filter);

		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onResume();
	}
	
	private void send() {
		mLoading.show("正在发送");
		String content = mPostContent.getText().toString();
		comModel.comment(content, mSubject);
	}
	
	private BroadcastReceiver mCommentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(commentModel.COMMENT_FINISHED)) {
				mLoading.hide();
				mLoading.show("评论成功");
				dismiss();

			} else if (action.equals(commentModel.COMMENT_FAILED)) {
				mLoading.hide();
				mLoading.show("评论失败");
				mLoading.hide();
			}
		}
	};
	
	private void dismiss() {
		Handler handler = new Handler(getMainLooper());
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
			}
		}, 1000);
	}
}

package com.XD.street.activity;

import com.XD.street.R;
import com.XD.street.data.UserInfo;
import com.XD.street.model.LoginModel;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationActivity extends SherlockActivity{
	
	private static final String USER_HOME_PAGE_URL = "http://my.hupu.com/";
	
	private TextView mUserName;
	private ImageView mUserPic;
	private LoginModel loginModel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_frame);
		setTitle("半场提醒");
		
		loginModel = LoginModel.getInstance(this);
		
		mUserName = (TextView)findViewById(R.id.user_name);
		mUserPic = (ImageView)findViewById(R.id.user_pic);
		
		mUserPic.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				jumpToLogin();
			}			
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	private void refresh() {
		if (loginModel.isLogedin) {
			UserInfo userInfo = loginModel.userInfo;
			mUserName.setText(userInfo.userName);
		}
		else {
			mUserName.setText(R.string.not_logedin);
		}
		super.invalidateOptionsMenu();
	}
	
	private void jumpToLogin() {
		// TODO Auto-generated method stub
		System.out.println("NotificationActivity: toggle()");
		if (loginModel.isLogedin) {
			Intent intent = new Intent(this, BrowserActivity.class);
			intent.putExtra("url", USER_HOME_PAGE_URL);
			startActivity(intent);
			Toast.makeText(getApplicationContext(), "我的主页", Toast.LENGTH_LONG).show();
		}
		else {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (loginModel.getToken() != null) {
			getSupportMenuInflater().inflate(R.menu.notification_menu, menu);
		}		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.notification_logout:
			logout();
			break;
		case R.id.notification_refresh:
			if (loginModel.getToken() != null) {
				// loged in
//				mLoadingView.show();
//				model.load();
				Toast.makeText(getApplicationContext(), "刷新", Toast.LENGTH_LONG).show();
			}
			break;
		}
		return true;
	}
	
	private void logout() {
		loginModel.logout();
		refresh();
	}
}

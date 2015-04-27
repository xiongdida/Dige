package com.XD.street.activity;

import com.XD.street.R;
import com.XD.street.model.LoginModel;
import com.XD.street.model.MyBoardsModel;
import com.XD.street.view.LoadingView;
import com.actionbarsherlock.app.SherlockActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends SherlockActivity{
	
	private LoginModel inModel;
	private LoadingView mLoading;
	private EditText userName;
	private EditText password;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	

		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	
		setTitle(R.string.login);
		setContentView(R.layout.login_frame);
	
		userName = (EditText) findViewById(R.id.username_edit);
		password = (EditText) findViewById(R.id.password_edit);
		
		inModel = LoginModel.getInstance(this);
	
		mLoading = new LoadingView(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		try {
			IntentFilter filter = new IntentFilter();
			filter.addAction(LoginModel.LOGIN_FINISHED);
			filter.addAction(LoginModel.LOGIN_FAILED);
			filter.addAction(LoginModel.WRONG_PASSWORD);
			registerReceiver(mBoradcastReceiver, filter);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void login(View v)
	{
		String user = userName.getText().toString();
		String pwd = password.getText().toString();
		
		if (user.matches("")) {
			Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_LONG).show();
			return;
		} 
		else if (pwd.matches("")) {
			Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_LONG).show();
			return;
		}
		mLoading.show("正在登录...");
		inModel.login(user, pwd);
	}
	
	private BroadcastReceiver mBoradcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			mLoading.hide();
			String action = (String) intent.getAction();
			if (action.equals(LoginModel.LOGIN_FINISHED)) {
				
				MyBoardsModel boardModel = MyBoardsModel.getInstance(context);
				boardModel.load();
				
				Toast.makeText(context, "登录成功", Toast.LENGTH_LONG).show();
				Handler handler = new Handler(getMainLooper());
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						finish();
					}
				}, 1000);
			} else if (action.equals(LoginModel.LOGIN_FAILED)) {
				Toast.makeText(context, "登录失败", Toast.LENGTH_LONG).show();
			}
			else if (action.equals(LoginModel.WRONG_PASSWORD)) {
				Toast.makeText(context, "账号或密码不正确", Toast.LENGTH_LONG).show();
			}
		}

	};
}

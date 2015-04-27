package com.XD.street.activity;
 
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.Toast;


import com.XD.street.R;
import com.XD.street.fragment.MenuFragment;
import com.XD.street.fragment.ThreadsFragment;
import com.XD.street.view.LoadingView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {
	
	private LoadingView mLoading;
	private Fragment mContent;	
	private String mid;
	MenuFragment mMenuFragment;
	private long exitTime = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.main_frame);
		mLoading = new LoadingView(this);
		
		//主界面论坛显示
		ThreadsFragment threadsFragment = new ThreadsFragment();
		mContent = threadsFragment;
		setTitle("步行街");
		threadsFragment.fid = "34";
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_frame, threadsFragment).commit();
		
		//sliding菜单设置
		setBehindContentView(R.layout.menu_frame);
		SlidingMenu sm = getSlidingMenu();
		mMenuFragment = new MenuFragment();
		getSupportFragmentManager().beginTransaction()
	    		.replace(R.id.menu_frame, mMenuFragment).commit();
		
		sm.setSlidingEnabled(true);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);					    	      		
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.threads_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			break;
		case R.id.notification:
			startActivity(new Intent(this, NotificationActivity.class));
			break;
		case R.id.menu_refresh:
			refresh();
			break;
		}
		return true;
	}
	
	public void switchContent(Fragment fragment, String title) {
		mContent = fragment;
		setTitle(title);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.main_frame, fragment).commit();
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 30);
	}
	
	private void refresh() {
		// TODO Auto-generated method stub
		mLoading.show();
		((ThreadsFragment)mContent).refresh();
	}


	public void showLoadingView(boolean show) {
		// TODO Auto-generated method stub
		if(show)
		{
			mLoading.show();
		}
		else {
			mLoading.hide();
		}
	}
	
	public boolean isLoading() {
		return mLoading.isLoading;
	}

	public void onThreadClicked(String fid, String tid) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, ContentActivity.class);
	    intent.putExtra("fid", fid);
	    intent.putExtra("tid", tid);
	    intent.putExtra("page", "1");
	    startActivity(intent);
	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) 
		{		
			if ((System.currentTimeMillis() - exitTime) > 2000) 
			{
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} 
			else 
			{
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void noNet() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_LONG).show();
	}

}

package com.XD.street.activity;

import java.util.ArrayList;
import java.util.Locale;

import com.XD.street.R;
import com.XD.street.adapter.ThreadDataAdapter;
import com.XD.street.data.ThreadData;
import com.XD.street.data.ThreadReply;
import com.XD.street.model.LoginModel;
import com.XD.street.model.commentModel;
import com.XD.street.model.contentModel;
import com.XD.street.view.CounterBar;
import com.XD.street.view.CounterBar.OnJumpListener;
import com.XD.street.view.LoadingView;
import com.XD.street.view.MenuBar;
import com.XD.street.view.MenuBar.OnItemClickListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContentActivity extends Activity {

	private String mFid;
	private String mTid;
	private contentModel ctModel;
	private commentModel comModel;

	private PullToRefreshListView mThreadDataListView;
	private ThreadDataAdapter mThreadDataAdapter;
	private LoadingView mLoading;
	private Context mContext;
	private ListView conListView;
	private MenuBar mMenuBar;
	private TextView mCounter;
	private CounterBar mCounterBar;
	private boolean isFirstTime;

	private static final String ThreadDataURL = "http://bbs.hupu.com/";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题栏
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
		              WindowManager.LayoutParams. FLAG_FULLSCREEN);//全屏
		setContentView(R.layout.content_frame);
		
		Intent intent = getIntent();
		mFid = intent.getStringExtra("fid");
		mTid = intent.getStringExtra("tid");
		
		ctModel = new contentModel(this, mTid);
		comModel = new commentModel(this, mFid, mTid);
		
		mThreadDataAdapter = new ThreadDataAdapter(this);
		mThreadDataAdapter.threadTopic = new ThreadData();
		mThreadDataAdapter.array = new ArrayList<ThreadReply>();

		mThreadDataListView = (PullToRefreshListView) findViewById(R.id.replies_list);
		mThreadDataListView.setOnRefreshListener(new OnRefreshListener2<ListView>()
		    {
		    	@Override
		    	public void onPullDownToRefresh(PullToRefreshBase<ListView> paramAnonymousPullToRefreshBase)
		    	{
		    		ctModel.refresh();
			    }
			    	
			   	@Override
			   	public void onPullUpToRefresh(PullToRefreshBase<ListView> paramAnonymousPullToRefreshBase)
			   	{
			   		nextPage();
			   	}
		    });
		conListView = (ListView)mThreadDataListView.getRefreshableView();
		
		mCounter = (TextView) findViewById(R.id.counter_btn);
		
		isFirstTime = true;
		registerBroadcast();

		mMenuBar = MenuBar.getInstance(this);
		mMenuBar.setOnItemClickListener(mMenuListener);
		
		mCounterBar = CounterBar.getInstance(this);
		mCounterBar.setOnJumpListener(new OnJumpListener() {				
			@Override
			public void OnJump(int page) {					
				jump(page);
			}
		});
		
		mLoading = new LoadingView(this);
		mLoading.show("正在加载...");
		loadThreadData();
	}

	private void loadThreadData() {
		// TODO Auto-generated method stub
		if (ctModel != null) {
			mThreadDataAdapter.topicFirst = true;
			ctModel.loadThreadData();
		}
	}
	
	@Override
	protected void onPause() {
		try {
			unregisterReceiver(mBroadcastReceiver);
		} catch (Exception e) {
			// mBroadCastReceiver is not registered in some case
			e.printStackTrace();
		}

		super.onPause();
	}

	@Override
	protected void onResume() {
		if (!isFirstTime) {
			registerBroadcast();
		}
		isFirstTime = false;
		super.onResume();
	}
	
	private void registerBroadcast() {
		try {
			IntentFilter filter = new IntentFilter();

			// for content model
			filter.addAction(contentModel.ThreadData_FinishedLoad);
			filter.addAction(contentModel.ThreadData_FailedLoad);
			filter.addAction(contentModel.Replies_FinishedLoad);
			filter.addAction(contentModel.Replies_FailedLoad);
			
			// for comment model
			filter.addAction(commentModel.RECOMMEND_FAILED);
			filter.addAction(commentModel.RECOMMEND_FINISHED);
//			// for reply model
//			filter.addAction(ZenReplyModel.ZenThreadDataDidFinishedLoad);
//			filter.addAction(ZenReplyModel.ZenThreadDataDidFailedLoad);
//			filter.addAction(ZenReplyModel.ZenRepliesDidFinishedLoad);
//			filter.addAction(ZenReplyModel.ZenRepliesDidFailedLoad);
//			filter.addAction(ZenReplyModel.ZenRepliesEmpty);
//			
//			// for comment model
//			filter.addAction(ZenCommentModel.ZEN_LIGHT_FINISHED);
//			filter.addAction(ZenCommentModel.ZEN_LIGHT_FAILED);
//			filter.addAction(ZenCommentModel.ZEN_RECOMMEND_FINISHED);
//			filter.addAction(ZenCommentModel.ZEN_RECOMMEND_FAILED);
//			
//			// for archive model
//			filter.addAction(ZenArchiveModel.ZEN_ARCHIVE_FAILED);
//			filter.addAction(ZenArchiveModel.ZEN_ARCHIVE_SUCCESS);
//			
			registerReceiver(mBroadcastReceiver, filter);
		} catch (Exception e) {
			// mBroadcastReceiver register failed
			e.printStackTrace();
		}

	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
	{
		@Override  
	    public void onReceive(Context context, Intent intent) {
			// model finished load threads
			Log.d("XD", "onReceive");
			String action = intent.getAction();
			if (action.equals(contentModel.ThreadData_FinishedLoad)) {
				onThreadDataFinished(ctModel.threadData);
			}
			else if (action.equals(contentModel.Replies_FinishedLoad)) {
				onRepliesFinished();
			}
			else if (action.equals(contentModel.RepliesEmpty)) {
				mLoading.hide();
				mThreadDataAdapter.notifyDataSetChanged();
			}
			else if (action.equals(commentModel.RECOMMEND_FINISHED)) {
				mLoading.hide();
				String msg = intent.getStringExtra("msg");
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
			else if (action.equals(commentModel.RECOMMEND_FAILED)) {
				mLoading.hide();
				String msg = intent.getStringExtra("msg");
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
			}
		}

	};

	private void onThreadDataFinished(ThreadData t) {
		// TODO Auto-generated method stub
		mThreadDataAdapter.threadTopic = t;
		conListView.setAdapter(mThreadDataAdapter); //获取数据之后再设置adapter 不会先有静态变量显示
		loadThreadReplies();
	}

	private void loadThreadReplies() {
		// TODO Auto-generated method stub
		if (ctModel != null) {
			ctModel.loadReplies(ctModel.currentPage);
		}
	}
	
	private void onRepliesFinished() {
		// TODO Auto-generated method stub
		mLoading.hide();
		mThreadDataListView.onRefreshComplete();
		mThreadDataAdapter.array = ctModel.replies;				
		mThreadDataAdapter.notifyDataSetChanged();
		// refresh counter
		String count = String.format(Locale.getDefault(), "%d/%d",
			ctModel.currentPage, ctModel.pageCount);
		mCounter.setText(count);
		if(ctModel.currentPage > 1) {
			mThreadDataListView.getRefreshableView().setSelection(0);
		}
//		else {
//			mThreadDataListView.scrollTo(0, 0);
//		}
		InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	public void OnBottomItemClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.prev_btn:
	   		prevPage();
			break;
		case R.id.next_btn:
			nextPage();
			break;
		case R.id.more_btn:
			mMenuBar.show();
			break;
		case R.id.counter_btn:
			mCounterBar.show();
			mCounterBar.maxPage = ctModel.pageCount;
			break;
		}
	}

	private void jump(int page) {
		mLoading.show("正在加载...");
		if (page == 1) {
			loadThreadData();
		}
		
		else  if (page > ctModel.pageCount){
//			ctModel.currentPage = ctModel.pageCount;
//			mThreadDataAdapter.topicFirst = false;
//			ctModel.loadReplies(ctModel.currentPage);
			Toast.makeText(getApplicationContext(), "页码太大", Toast.LENGTH_LONG).show();
			mLoading.hide();
		}
		else {
			ctModel.currentPage = page;
			mThreadDataAdapter.topicFirst = false;
			ctModel.loadReplies(ctModel.currentPage);
		}
	}
		
	public void nextPage()
	{
		if(ctModel.currentPage < ctModel.pageCount)
   		{
			mLoading.show("正在加载...");
			mThreadDataAdapter.topicFirst = false;			   			
   			ctModel.next();
   		}
   		else {
   			ctModel.refresh();
   			Toast.makeText(getApplicationContext(), "已经是最后一页了", Toast.LENGTH_LONG).show();
   		}
	}
	
	public void prevPage()
	{
		switch(ctModel.currentPage)
		{
		case 1:
			Toast.makeText(getApplicationContext(), "已经是第一页了", Toast.LENGTH_LONG).show();
			break;
		case 2:
			mLoading.show("正在加载...");
			ctModel.currentPage --;
			loadThreadData();
			break;
		default: 
			mLoading.show("正在加载...");
			ctModel.prev();
			break;
		}
	}
	
	private OnItemClickListener mMenuListener = new OnItemClickListener() {
		@Override
		public void OnMenuItemClick(int type) {
			// TODO Auto-generated method stub
			switch (type) {
			case MenuBar.MENU_REPLY:
				comment();
				break;
			case MenuBar.MENU_RECOMMEND:
				recommend();
				break;
			case MenuBar.MENU_REFRESH:
				refresh();
				break;
			}
		}
	};

	private void comment() {
		// TODO Auto-generated method stub
		if (ctModel.threadData.subject == null) {
			Toast.makeText(getApplicationContext(), "主贴还未加载完成", Toast.LENGTH_LONG).show();
			return;
		}
		if (isLogin()) {
			Intent intent = new Intent(this, PostActivity.class);
//			intent.putExtra("type", PostActivity.TYPE_COMMENT);
//			intent.putExtra("title", ctModel.threadData.subject);
			intent.putExtra("fid", mFid);
			intent.putExtra("tid", mTid);
			intent.putExtra("subject", ctModel.threadData.subject);
//			intent.putExtra("pid", "");
			startActivity(intent);
		}
	}

	protected void recommend() {
		// TODO Auto-generated method stub
		if (isLogin()) {
			mLoading.show("正在推荐...");
			comModel.recommend("recommend", "from zen for android",
					LoginModel.getInstance(this).getToken());
		}
	}

	private boolean isLogin() {
		LoginModel inModel = LoginModel.getInstance(this);
		if (!inModel.isLogedin) {
			startLoginActivity();
		}
		return inModel.isLogedin;
	}
	
	private void startLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	
	private void refresh() {
		// TODO Auto-generated method stub
		mLoading.show();
		ctModel.refresh();
	}
}

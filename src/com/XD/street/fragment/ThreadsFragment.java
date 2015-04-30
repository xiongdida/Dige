package com.XD.street.fragment;

import java.util.ArrayList;

import com.XD.street.R;
import com.XD.street.activity.MainActivity;
import com.XD.street.adapter.ThreadsAdapter;
import com.XD.street.data.ThreadData;
import com.XD.street.model.ThreadsModel;
import com.XD.street.view.RefreshLayout;
import com.XD.street.view.RefreshLayout.OnLoadListener;
//import com.handmark.pulltorefresh.library.PullToRefreshBase;
//import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
//import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;



public class ThreadsFragment extends Fragment{

	public String fid;
	private Context mContext;
	private ThreadsAdapter mThreadsAdapter;
	private ListView mThreadsListView;
	private RefreshLayout threadsRefresh;
	private boolean isFirstTime;
	private ThreadsModel mModel;
	private ListView mList;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	    mContext = getActivity();
	    mThreadsAdapter = new ThreadsAdapter(mContext);
	    mThreadsAdapter.array = new ArrayList<ThreadData>();
	    threadsRefresh = (RefreshLayout)getView().findViewById(R.id.threadsRefreshLayout);
	    mThreadsListView = (ListView)getView().findViewById(R.id.threads_list);
	    
	    threadsRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,  
	            android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
	    threadsRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
            	MainActivity ac = (MainActivity)mContext;
	    		if(ac.isLoading())
	    		{
//	    			threadsRefresh.setRefreshing(false);
	    			return;
	    		}
	    		mModel.refresh();
            }
	    });
	    threadsRefresh.setOnLoadListener(new OnLoadListener() {
	    	  
            @Override  
            public void onLoad() {
            	MainActivity ac = (MainActivity)mContext;
	    		if (ac.isLoading())
	    		{
//	    			threadsRefresh.setRefreshing(false);
	    			return;
	    		}
	    		Log.v("XD", "Pull To Load More Task Execute.");
	    		Toast.makeText(ac, "½Ó×ÅÍùÏÂ»¬°É ^_^o~", Toast.LENGTH_SHORT).show();
	    		mModel.loadMore();
            }  
        }); 
//	    mThreadsListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
//	    	@Override
//	    	public void onPullDownToRefresh(PullToRefreshBase<ListView> paramAnonymousPullToRefreshBase)
//	    	{
//	    		MainActivity ac = (MainActivity)mContext;
//	    		if(ac.isLoading())
//	    		{
//	    			mThreadsListView.onRefreshComplete();
//	    			return;
//	    		}
//	    		mModel.refresh();
//	    	}
//	    	
//	    	@Override
//	    	public void onPullUpToRefresh(PullToRefreshBase<ListView> paramAnonymousPullToRefreshBase)
//	    	{
//	    		MainActivity ac = (MainActivity)mContext;
//	    		if (ac.isLoading())
//	    		{
//	    			mThreadsListView.onRefreshComplete();
//	    			return;
//	    		}
//	    		Log.v("XD", "Pull To Load More Task Execute.");
//	    		mModel.loadMore();
//	    	}
//
//	    });
//	    ListView actureListView = (ListView)mThreadsListView.getRefreshableView();
	    mList = mThreadsListView;
//	    AdView localAdView = new AdView(this.mContext);
//	    localAdView.setAdUnitId("ca-app-pub-8494263015060478/5574028341");
//	    localAdView.setAdSize(AdSize.BANNER);
//	    this.mAds = localAdView;
//	    this.mList.addFooterView(localAdView);
//	    localAdView.loadAd(new AdRequest.Builder().build());
	    mThreadsListView.setAdapter(mThreadsAdapter);
	    mThreadsListView.setOnItemClickListener(new OnItemClickListener()
	    {
	    	public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
	    	{
	    		try
	    		{
	    			System.out.println("position: " + position);
	    			if((getActivity() instanceof MainActivity))
	    			{
	    				MainActivity ac = (MainActivity)getActivity();
	    				ThreadData da = (ThreadData)parent.getAdapter().getItem(position);
	    				if (da != null)
	    					ac.onThreadClicked(da.fid, da.tid);
	    			}
	    			return;
	    		}
	    		catch (Exception e)
	    		{
	    			e.printStackTrace();
	    		}
	    	}
	    });
	    mModel = new ThreadsModel(mContext,fid);
	    isFirstTime = true;
	    threadsRefresh.setEnabled(true);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(ThreadsModel.FinishedLoad);
		filter.addAction(ThreadsModel.FailedLoad);
		mContext.registerReceiver(mBroadcastReceiver, filter);
		
		if (isFirstTime) {
			mModel.refresh();
			((MainActivity)mContext).showLoadingView(true);
			isFirstTime = false;
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.threads_list, null);
	}
	
	@Override
	public void onPause() {
		try {
			mModel.cancel();
			mContext.unregisterReceiver(mBroadcastReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		super.onPause();
	}
	
	public void refresh()
	{
		mModel.refresh();
		mList.setSelection(0);
	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
	{
		@Override  
	    public void onReceive(Context context, Intent intent) {
			// model finished load threads
			Log.d("XD", "onReceive");
			((MainActivity)mContext).showLoadingView(false);
			threadsRefresh.setRefreshing(false);
			threadsRefresh.setLoading(false);
			String action = intent.getAction();
			if (action.equals(ThreadsModel.FinishedLoad)) {				
				mThreadsAdapter.array = mModel.threads;
				mThreadsAdapter.notifyDataSetChanged();
//				mList.scrollTo(0, 0);
			}
			else{
				MainActivity ac = (MainActivity)getActivity();
				ac.noNet();
			}
		}

	};
}

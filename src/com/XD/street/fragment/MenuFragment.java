package com.XD.street.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.XD.street.R;
import com.XD.street.activity.MainActivity;
import com.XD.street.adapter.MenuAdapter;
import com.XD.street.data.ThreadData;
import com.XD.street.model.MyBoardsModel;
import com.XD.street.model.ThreadsModel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class MenuFragment extends Fragment{

	private Context mContext;
	private MenuAdapter mMenuAdapter;
	private ListView mListview;
	private MyBoardsModel menuModel;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	    mContext = getActivity();
	    
	    menuModel = MyBoardsModel.getInstance(mContext);
	    menuModel.reloadMyBoards();
	    
	    mMenuAdapter = new MenuAdapter(mContext);
	    mMenuAdapter.menuList = new ArrayList<Map<String, String>>();
	    
	    mMenuAdapter.menuList = menuModel.list;
	    
	    mListview = (ListView)getView().findViewById(R.id.my_boards);
	    mListview.setAdapter(mMenuAdapter);
	    
	    mListview.setOnItemClickListener(new OnItemClickListener()
	    {
	    	public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
	    	{
	    		Map<String, String> board = (Map<String, String>) parent.getAdapter().getItem(position);
	    		switchContent(board.get("fid"), board.get("name"));
	    	}
	    });
	    		
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyBoardsModel.Finish);
		mContext.registerReceiver(mBroadcastReceiver, filter);
	}
	
	private void switchContent(String fid, String name) {
		// TODO Auto-generated method stub
		if (fid != null && name != null) {
			ThreadsFragment fragment = new ThreadsFragment();
			fragment.fid = fid;
			MainActivity ac = (MainActivity)getActivity();
//			ac.setFid(fid);
			ac.switchContent(fragment, name);
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.menu_list, null);
	}
	
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
	{
		@Override  
	    public void onReceive(Context context, Intent intent) {
			// model finished load threads
			String action = intent.getAction();
			if (action.equals(MyBoardsModel.Finish)) {				
			    mMenuAdapter.menuList = menuModel.list;
			    mMenuAdapter.notifyDataSetChanged();
			}
		}
	};
}

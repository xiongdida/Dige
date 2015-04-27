package com.XD.street.view;

import com.XD.street.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class MenuBar {

	public static final int MENU_NONE = 0xFF00;
	public static final int MENU_REPLY = 0xFF01;
	public static final int MENU_REFRESH = 0xFF02;
	
	private Animation SlideInAnimation;
	private Animation SlideOutAnimation;
	
	private View mMenuView;
	private View mMenuBar;
	private View mMenuBg;
	
	private Button mButtonReply;
	private Button mButtonRefresh;
	private Context mContext;
	private OnItemClickListener mListener;
	private int selected;
	
	public static synchronized MenuBar getInstance(Context c) {
		MenuBar instance = new MenuBar();
		instance.mContext = c;
		instance.load();
		return instance;
	}

	private void load() {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.menu_bar, null);
		mMenuBar = mMenuView.findViewById(R.id.menu_bar);				
		mMenuBg = mMenuView.findViewById(R.id.menu_bg);
		
		mButtonReply = (Button) mMenuView.findViewById(R.id.menu_reply);
		mButtonReply.setOnClickListener(mOnClickListener);
		mButtonRefresh = (Button) mMenuView.findViewById(R.id.menu_refresh);
		mButtonRefresh.setOnClickListener(mOnClickListener);
		
		mMenuBg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hide();
			}
		});
		
		SlideInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_bottom);
		SlideOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_to_bottom);
		SlideOutAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				removeFromSuperView(mMenuView);
				if (selected != MenuBar.MENU_NONE && mListener != null) {
					mListener.OnMenuItemClick(selected);
					selected = MenuBar.MENU_NONE;
				}
			}
		});
//		removeFromSuperView(mMenuView);
		((Activity) mContext).addContentView(mMenuView, new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.menu_reply:
//				Toast.makeText(mContext, "»Ø¸´", Toast.LENGTH_LONG).show();
				selected = MENU_REPLY;
				break;
			case R.id.menu_refresh:
				selected = MENU_REFRESH;
				break;
			}
			hide();
		}
		
	};


	public void show() {
		// TODO Auto-generated method stub
//		((Activity)getContext()).addContentView(this, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mMenuBar.startAnimation(SlideInAnimation);
	}
	
	public void hide() {
		mMenuBar.startAnimation(SlideOutAnimation);
	}

	private void removeFromSuperView(View view) {
		ViewGroup superView = (ViewGroup) view.getParent();
		if (superView != null) {
			superView.removeView(view);
		}
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mListener = listener;
	}
	
	public interface OnItemClickListener {
		public void OnMenuItemClick(int type);
	}
}

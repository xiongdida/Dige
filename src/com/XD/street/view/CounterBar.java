package com.XD.street.view;

import java.io.IOException;

import com.XD.street.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CounterBar extends RelativeLayout {
	private Animation mSlideInAnimation;
	private Animation mSlideOutAnimation;
	private Button mCancel;
	private Button mEnter;
	private Button mEnd;
	private EditText mCounterNum;
	private Context mContext;
	private OnJumpListener mListener;
	
	public boolean isLoading;
	public int maxPage;


	public static synchronized CounterBar getInstance(Context c) {
		CounterBar instance = new CounterBar(c);
		instance.mContext = c;
		instance.load();
		return instance;
	}
	
	public CounterBar(Context context) {
		super(context);
	}
	
	private void load() {
		LayoutInflater.from(mContext).inflate(R.layout.counter_view, this, true);
		mSlideInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_bottom);
		mSlideOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_to_bottom);
		mSlideOutAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				CounterBar.this.setVisibility(View.GONE);
			}
		});
		
		mCancel = (Button)findViewById(R.id.counter_cancel);
		mEnter = (Button)findViewById(R.id.counter_enter);
		mEnd = (Button)findViewById(R.id.counter_end);
		mCounterNum = (EditText)findViewById(R.id.counter_num);
		
		mCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				hide();
			}
		});
		
		mEnter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				try {
					int page = Integer.parseInt(mCounterNum.getText().toString());
					if (mListener != null) {
						mListener.OnJump(page);
					}
					mCounterNum.setText("");
					hide();
				} catch (Exception e) {
					Toast.makeText(getContext(), "«Î ‰»Î“≥¬Î", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		mEnd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				end();
			}
		});
		
		((Activity)getContext()).addContentView(this, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.setVisibility(View.INVISIBLE);
		isLoading = false;
	}
	
	protected void end() {
		// TODO Auto-generated method stub
		int page = maxPage;
		if (mListener != null) {
			mListener.OnJump(page);
		}
		hide();
	}

	public void show() {
		isLoading = true;
		this.setVisibility(View.VISIBLE);
		this.startAnimation(mSlideInAnimation);
	}
	
	public void hide() {
		isLoading = false;
		if (this.getVisibility() == View.GONE) {
			return;
		}
		this.startAnimation(mSlideOutAnimation);		
	}
	
	public void setOnJumpListener(OnJumpListener listener) {
		mListener = listener;
	}
		
	public interface OnJumpListener {
		public void OnJump(int page);
	};
}

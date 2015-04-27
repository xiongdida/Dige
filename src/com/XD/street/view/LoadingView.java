package com.XD.street.view;

import com.XD.street.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.TextView;

public class LoadingView extends FrameLayout {
	private Animation mSlideInAnimation;
	private Animation mSlideOutAnimation;
	private TextView mTitle;
	public boolean isLoading;
	
	public LoadingView(Context context) {
		super(context);
		load();
	}
	
	private void load() {
		Context context = getContext();
		LayoutInflater.from(context).inflate(R.layout.loading_view, this, true);
		mSlideInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_top);
		mSlideOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_to_top);
		mSlideOutAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				LoadingView.this.setVisibility(View.GONE);
			}
		});
		
		mTitle = (TextView)findViewById(R.id.loading_title);
		
		((Activity)getContext()).addContentView(this, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.setVisibility(View.INVISIBLE);
		isLoading = false;
	}
	
	public void show() {
		isLoading = true;
		mTitle.setText("ÕýÔÚ¼ÓÔØ...");
		this.setVisibility(View.VISIBLE);
		this.startAnimation(mSlideInAnimation);
	}
	
	public void show(String title) {
		mTitle.setText(title);
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
}

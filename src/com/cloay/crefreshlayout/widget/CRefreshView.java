package com.cloay.crefreshlayout.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

/**
 * 
 * @ClassName: CRefreshLayout 
 * @author cloay Email:shangrody@gmail.com 
 * @date 2014-11-10 下午6:35:22 
 *
 */
public class CRefreshView extends RelativeLayout{
	
    private static final long kloadingIndividualAnimationTiming = 800;
    private static final float kbarDarkAlpha = 0.55f;
    private static final long kloadingTimingOffset = 300;
    private static final float kdisappearDuration = 0.8f;
    
    private int dropHeight = 64;
    private int lineColor = Color.BLACK;
    private float lineWidth = 3f;
    private float disappearProgress;
    private boolean reverseLoadingAnimation = false;
    private float internalAnimationFactor = 0.6f;
    private int horizontalRandomness = 150;
    public CRefreshLayoutState state = CRefreshLayoutState.CRefreshLayoutStateIdle;
    
    private List<BarItem> barItems;
    private Context mContext;
	public CRefreshView(Context context) {
		super(context);
		init(context);
	}
	
	public CRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public CRefreshView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	
	public void init(Context context){
		mContext = context;
		barItems = new ArrayList<BarItem>();
		
		List<Point> startPoints = new ArrayList<Point>();
		//加
		startPoints.add(new Point(240, 85));
		startPoints.add(new Point(255, 70));
		startPoints.add(new Point(270, 85));
		startPoints.add(new Point(265, 108));
		startPoints.add(new Point(275, 85));
		startPoints.add(new Point(275, 85));
		startPoints.add(new Point(275, 112));
		startPoints.add(new Point(302, 85));
		
		//载
		startPoints.add(new Point(320, 75));
		startPoints.add(new Point(313, 85));
		startPoints.add(new Point(330, 68));
		startPoints.add(new Point(315, 92));
		startPoints.add(new Point(330, 85));
		startPoints.add(new Point(315, 105));
		startPoints.add(new Point(330, 95));
		startPoints.add(new Point(315, 115));
		startPoints.add(new Point(345, 70));
		startPoints.add(new Point(357, 72));
		startPoints.add(new Point(363, 108));
		//中
		startPoints.add(new Point(375, 85));
		startPoints.add(new Point(375, 85));
		startPoints.add(new Point(425, 85));
		startPoints.add(new Point(380, 100));
		startPoints.add(new Point(400, 68));
		
		
		List<Point> endPoints = new ArrayList<Point>();
		endPoints.add(new Point(270, 85));
		endPoints.add(new Point(250, 115));
		endPoints.add(new Point(270, 115));
		endPoints.add(new Point(270, 115));
		endPoints.add(new Point(302, 85));
		endPoints.add(new Point(275, 112));
		endPoints.add(new Point(302, 112));
		endPoints.add(new Point(302, 112));
		//载
		endPoints.add(new Point(340, 75));
		endPoints.add(new Point(360, 85));
		endPoints.add(new Point(330, 85));
		endPoints.add(new Point(340, 92));
		endPoints.add(new Point(315, 105));
		endPoints.add(new Point(345, 103));
		endPoints.add(new Point(330, 125));
		endPoints.add(new Point(345, 113));
		endPoints.add(new Point(360, 125));
		endPoints.add(new Point(363, 80));
		endPoints.add(new Point(345, 122));
		//中
		endPoints.add(new Point(380, 100));
		endPoints.add(new Point(425, 85));
		endPoints.add(new Point(420, 100));
		endPoints.add(new Point(420, 100));
		endPoints.add(new Point(400, 125));
		
		for(int i = 0; i < startPoints.size(); i++){
			Point startP = startPoints.get(i);
			Point endP = endPoints.get(i);
			BarItem item = new BarItem(getContext(), startP, endP, lineColor, lineWidth);
			item.setTag(Integer.valueOf(i));
			item.setBackgroundColor(Color.TRANSPARENT);
			item.setAlpha(0f);
			barItems.add(item);
			this.addView(item);
			item.setHorizontalRandomness(this.horizontalRandomness, this.dropHeight);
		}
		
		for (BarItem barItem : this.barItems) {
			barItem.setupFrame();
	    }
	}
	
	public void updateBarItemsWithProgress(float progress){
		Log.v("CRefreshLayout", "updateBarItemsWithProgress dragPercent" + progress);
	    for (BarItem barItem : this.barItems) {
	        int index = this.barItems.indexOf(barItem);
	        float startPadding = (1 - this.internalAnimationFactor) / this.barItems.size() * index;
	        float endPadding = 1 - this.internalAnimationFactor - startPadding;
	        
	        barItem.resetMatrix();
	        if (progress == 1 || progress >= 1 - endPadding) {
	            barItem.setAlpha(kbarDarkAlpha);
	        }else if (progress == 0) {
	            barItem.setHorizontalRandomness(this.horizontalRandomness, this.dropHeight);	        
	        }else {
	            float realProgress;
	            if (progress <= startPadding)
	                realProgress = 0;
	            else
	                realProgress = Math.min(1, (progress - startPadding)/this.internalAnimationFactor);
	            barItem.preMatrixTranslate(barItem.translationX*(1-realProgress), -this.dropHeight*(1-realProgress));
	            barItem.preMatrixScale(1.0f*realProgress, 1.0f*realProgress);
	            barItem.preMatrixRotate(-(float)Math.PI*realProgress);
	            barItem.invalidate();
	            barItem.setAlpha(realProgress*kbarDarkAlpha);
	        }
	    }
	}

	public void startLoadingAnimation(){
	    if (this.reverseLoadingAnimation) {
	        int count = (int)this.barItems.size();
	        for (int i= count-1; i>=0; i--) {
	        	final BarItem barItem = this.barItems.get(i);
	            new Handler().postDelayed(new Runnable(){  
	                public void run() {  
	                	CRefreshView.this.barItemAnimation(barItem);
	                }  
	             }, (this.barItems.size()-i-1)*kloadingTimingOffset);
	        }
	    }else {
	        for (int i=0; i<this.barItems.size(); i++) {
	        	final BarItem barItem = this.barItems.get(i);
	            new Handler().postDelayed(new Runnable(){  
	                public void run() {  
	                	CRefreshView.this.barItemAnimation(barItem);
	                }  
	             }, i*kloadingTimingOffset);
	        }
	    }
	}

	private void barItemAnimation(BarItem barItem){
	    if (this.state == CRefreshLayoutState.CRefreshLayoutStateRefreshing){
	        barItem.clearAnimation();
	        
	        Animation alphaA = new AlphaAnimation(1f, kbarDarkAlpha);
	        alphaA.setDuration(kloadingIndividualAnimationTiming);
	        barItem.startAnimation(alphaA);
	        boolean isLastOne;
	        if (this.reverseLoadingAnimation)
	            isLastOne = (Integer)barItem.getTag() == 0;
	        else
	            isLastOne = (Integer)barItem.getTag() == this.barItems.size()-1;
	            
	        if (isLastOne && this.state == CRefreshLayoutState.CRefreshLayoutStateRefreshing) {
	            this.startLoadingAnimation();
	        }
	    }
	}

	public void updateDisappearAnimation(){
	    if (this.disappearProgress >= 0 && this.disappearProgress <= 1) {
	        this.disappearProgress -= 1/30.f/kdisappearDuration;
	        this.updateBarItemsWithProgress(this.disappearProgress);
	    }
	}
    
	public void finishingLoading(){
	    this.state = CRefreshLayoutState.CRefreshLayoutStateDisappearing;
        this.disappearProgress = 1;
	    for (BarItem barItem : this.barItems) {
	        barItem.clearAnimation();
	        barItem.setAlpha(kbarDarkAlpha);
	    }
	    
	    updateDisappearProgress();
	}
	
	public void updateDisappearProgress(){
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if(disappearProgress <= 0){
					timer.cancel();
					timer.purge();
				}
				synchronized (this) {//must make sure thread is synchronize
					((Activity) mContext).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							updateDisappearAnimation();
						}
					});
				}
			}
		}, 0, 1000/30);
	}
	
    public enum CRefreshLayoutState{
    	CRefreshLayoutStateIdle,
    	CRefreshLayoutStateRefreshing,
    	CRefreshLayoutStateDisappearing
    }
    
    public void setDisappearProgress(float disappearProgress){
    	this.disappearProgress = disappearProgress;
    }
}

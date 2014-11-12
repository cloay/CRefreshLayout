package com.cloay.crefreshlayout.widget;

import java.util.ArrayList;
import java.util.List;

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
 * @date 2014-11-10 ÏÂÎç6:35:22 
 *
 */
public class CRefreshView extends RelativeLayout{
	
    private static final long kloadingIndividualAnimationTiming = 800;
    private static final float kbarDarkAlpha = 0.6f;
    private static final long kloadingTimingOffset = 250;
    private static final float kdisappearDuration = 0.8f;
    
    private int dropHeight = 180;
    private int lineColor = Color.BLACK;
    private float lineWidth = 6f;
    private float disappearProgress;
    private boolean reverseLoadingAnimation = true;
    private float internalAnimationFactor = 0.5f;
    private int horizontalRandomness = 120;
    public CRefreshLayoutState state = CRefreshLayoutState.CRefreshLayoutStateIdle;
    
    private List<BarItem> barItems;
	 
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
		barItems = new ArrayList<BarItem>();
		
		List<Point> startPoints = new ArrayList<Point>();
		startPoints.add(new Point(335, 50));
		startPoints.add(new Point(385, 50));
		startPoints.add(new Point(335, 50));
		startPoints.add(new Point(335, 100));
		
		List<Point> endPoints = new ArrayList<Point>();
		endPoints.add(new Point(385, 50));
		endPoints.add(new Point(385, 100));
		endPoints.add(new Point(335, 100));
		endPoints.add(new Point(385, 100));
		
		
		
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
	    for (BarItem barItem : this.barItems) {
	        int index = this.barItems.indexOf(barItem);
	        float startPadding = (1 - this.internalAnimationFactor) / this.barItems.size() * index;
	        float endPadding = 1 - this.internalAnimationFactor - startPadding;
	        
	        if (progress == 1 || progress >= 1 - endPadding) {
	        	barItem.resetMatrix();
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
	            barItem.preMatrixRotate((float)Math.PI*realProgress);
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
	    	Log.v("CRefreshLayout", "barItemAnimation...");
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
	        this.disappearProgress -= 1/60.f/kdisappearDuration;
	        //60.f means this method get called 60 times per second
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
	    updateDisappearAnimation();
	}
	
    public enum CRefreshLayoutState{
    	CRefreshLayoutStateIdle,
    	CRefreshLayoutStateRefreshing,
    	CRefreshLayoutStateDisappearing
    }
    
}

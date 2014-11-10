package com.cloay.crefreshlayout.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * 
 * @ClassName: CRefreshLayout 
 * @author cloay Email:shangrody@gmail.com 
 * @date 2014-11-10 ÏÂÎç6:35:22 
 *
 */
public class CRefreshLayout extends LinearLayout implements OnScrollListener{
    
    private static final float kloadingIndividualAnimationTiming = 0.8f;
    private static final float kbarDarkAlpha = 0.4f;
    private static final float kloadingTimingOffset = 0.1f;
    private static final float kdisappearDuration = 1.2f;
    private static final float krelativeHeightFactor = 2.f/5.f;
    
    private CRefreshLayoutState state;
    
    private int dropHeight;
    private float originalMarginTop;
    private float disappearProgress;
    private float internalAnimationFactor;
    private int horizontalRandomness;
    private List<BarItem> barItems;
    
	private ListView mListView;
	 
	public CRefreshLayout(Context context) {
		super(context);
	}
	
	public CRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	
	public void init(ListView listView, int color, float lineWidth, int dropHeight, 
			int horizontalRandomness, float internalAnimationFactor){
		mListView = listView;
		
		this.dropHeight = dropHeight;
		this.horizontalRandomness = horizontalRandomness;
		this.internalAnimationFactor = internalAnimationFactor;
		
		mListView.addView(this);
		
		float width = 0;
		float height = 0;
		List<Point> startPoints = new ArrayList<Point>();
		startPoints.add(new Point(100, 100));
		startPoints.add(new Point(200, 100));
		startPoints.add(new Point(100, 100));
		startPoints.add(new Point(100, 200));
		
		List<Point> endPoints = new ArrayList<Point>();
		endPoints.add(new Point(200, 100));
		endPoints.add(new Point(200, 200));
		endPoints.add(new Point(100, 200));
		endPoints.add(new Point(200, 200));
		
		for(int i = 0; i < startPoints.size(); i++){
			Point startP = startPoints.get(i);
			Point endP = endPoints.get(i);
			if(startP.x > width) width = startP.x;
			if(endP.x > width) width = endP.x;
			if(startP.y > height) height = startP.y;
			if(endP.y > height) height = endP.y;
		}
		this.layout(0, 0, (int)width, (int)height);
		
		for(int i = 0; i < startPoints.size(); i++){
			Point startP = startPoints.get(i);
			Point endP = endPoints.get(i);
			BarItem item = new BarItem(getContext(), startP, endP, color, lineWidth);
			item.setTag(i);
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
	
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}

	
	
	public enum CRefreshLayoutState{
		CRefreshLayoutStateIdle,
		CRefreshLayoutStateIdleRefreshing,
		CRefreshLayoutStateIdleDisappearing
	}
}

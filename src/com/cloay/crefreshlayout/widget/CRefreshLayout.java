package com.cloay.crefreshlayout.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

/**
 * 
 * @ClassName: CRefreshLayout 
 * @author cloay Email:shangrody@gmail.com 
 * @date 2014-11-10 ÏÂÎç6:35:22 
 *
 */
public class CRefreshLayout extends View implements OnScrollListener{
	private static final int    PULL_TO_REFRESH      = 1;
    private static final int    RELEASE_TO_REFRESH   = 2;
    private static final int    REFRESHING           = 3;
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

	
	public CRefreshLayout(Context context, ListView listView){
		super(context);
		mListView = listView;
		mListView.addView(this);
	}
	
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}

	
	
}

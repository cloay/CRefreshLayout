package com.cloay.crefreshlayout;

import com.cloay.crefreshlayout.widget.CRefreshLayout;
import com.cloay.crefreshlayout.widget.CRefreshLayout.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @ClassName: MainActivity 
 * @author cloay Email:shangrody@gmail.com 
 * @date 2014-11-10 ÏÂÎç6:33:36 
 *
 */
public class MainActivity extends Activity {

	private ListView listView;  
    private MyAdapter adapter;
    private CRefreshLayout refreshLayout;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listView = (ListView) this.findViewById(R.id.listview);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
		
	    refreshLayout = (CRefreshLayout) findViewById(R.id.crefreshLayout);
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(8*1000); //sleep 8 seconds 
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						adapter.count = 15;
						adapter.notifyDataSetChanged();
						refreshLayout.setRefreshing(false);
						super.onPostExecute(result);
					}
					
				}.execute();
			}
		});
		
//		test();
	}

	/*
	private void test(){
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.test_layout);
		List<BarItem> barItems = new ArrayList<BarItem>();
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
			BarItem item = new BarItem(this, startP, endP, Color.BLACK, 10);
			item.setTag(Integer.valueOf(i));
			item.setBackgroundColor(Color.TRANSPARENT);
//			item.setAlpha(0f);
			barItems.add(item);
			item.setHorizontalRandomness(150, 80);
			layout.addView(item);
		}
		
		for (BarItem barItem : barItems) {
			barItem.setupFrame();
	    }
	}*/
	
	private class MyAdapter extends BaseAdapter{
		public int count = 15;
		@Override
		public int getCount() {
			return count;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(MainActivity.this).inflate(android.R.layout.simple_list_item_1, null);
				holder.textV = (TextView) convertView.findViewById(android.R.id.text1);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textV.setText("This is " + (position + 1) + " line.");
			return convertView;
		}
		
		private class ViewHolder{
			TextView textV;
		}
		
	}
}

CRefreshLayout
==============
A stunning android pull refresh layout inspired by <a href="https://github.com/coolbeet/CBStoreHouseRefreshControl">CBStoreHouseRefreshControl</a>.
How to use:
==============
1, in layout
<com.cloay.crefreshlayout.widget.CRefreshLayout
        android:id="@+id/crefreshLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <ListView android:id="@+id/listview"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>    
</com.cloay.crefreshlayout.widget.CRefreshLayout>
2, And init
refreshLayout = (CRefreshLayout) findViewById(R.id.crefreshLayout);
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				  //refresh data;
				  
			}
});
Thanks
==============
<a href="https://github.com/coolbeet/CBStoreHouseRefreshControl">CBStoreHouseRefreshControl</a>
<a href="https://github.com/baoyongzhang/android-PullRefreshLayout">android-PullRefreshLayout</a>
About me
==============
Email:shangrody@gmail.com
blog:www.cloay.com
License
==============
The MIT License (MIT)
Copyright (c) 2014 <a href="http://www.cloay.com">Cloay</a>
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

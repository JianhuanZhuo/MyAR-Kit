package recentnews.hit.edu.cn;

import hit.edu.cn.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import main.hit.edu.cn.main_activity;

import recentnews.hit.edu.cn.MyRefreshListView.OnRefreshListener;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class recentNews_activity extends ListActivity implements OnScrollListener{   
	MyRefreshListView listView;
	SimpleAdapter adapter;
	
	private static final String TAG="recent news activity";
	static List<Map<String, Object>> list=null;
	static List<Map<String, Object>> loadList=null;
	
	static ArrayList<String> newsidList=null;   //存储新闻id的list
	static int pageNum=1;   //存储当前新闻网页的页码
	
	String url="http://www.gucas.ac.cn/site/157?c=103&pn=";
	
	static String lastRefreshTime="2012-01-01 00:00:00";
	static String lastRefreshTimeForDisplay="2012-01-01 00:00:00";
	
	public static final String wsUrl=main_activity.HOST+"activityInfo?wsdl";
	public static final String wsNamespace="http://ws.apache.org/axis2";
	int activityNum;
 
	protected static final int NETWORK_ERROR = 1;
	protected static final int LOAD_MORE = 2;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v(TAG, "in onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_news_activity);

        networkCheck(this);
        
        listView = (MyRefreshListView) getListView();
        listView.setSelector(R.drawable.remove_listview_default_selector_color); //去掉ListView Selector选中时默认黄色底纹一闪的效果 
        // Set a listener to be invoked when the list should be refreshed.
        
        listView.setOnRefreshListener(new OnRefreshListener() {
        	//设置刷新的接口，即刷新要执行什么操作
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				Toast.makeText(MyActivitiesRefreshActivity.this, ""+position, Toast.LENGTH_SHORT).show();
    			
				Intent  intent=new Intent(recentNews_activity.this, newsDetail.class);
				intent.putExtra("url", "http://www.gucas.ac.cn/site/157?u="+list.get(position-1).get("newsid").toString());
				startActivity(intent);
			}
		});
        listView.setOnScrollListener(this);
        
        adapter = new SimpleAdapter(this, getData(), R.layout.listviewitemlayout,
        		new String[]{"title", "time"},
        		new int[]{R.id.title, R.id.time});

        setListAdapter(adapter);
        
        listView.setWHETHER_LOAD_MORE(false);
        listView.beginLoadData();
    }

    private List<Map<String, Object>> getData(){
    	if(list==null){
    		Log.v(TAG, "in getData, in if");
    		
    		list=new ArrayList<Map<String, Object>>();
    		loadList=new ArrayList<Map<String, Object>>();
    		
    		newsidList=new ArrayList<String>();
    		
//        	Map<String, Object> map=new HashMap<String, Object>();
//        	map.put("title", "欢迎使用 ~");
//        	map.put("time", "提示:点击列表条目可以查看详细信息. ");
//        	map.put("content", "欢迎使用全能出行助手！");
//        	list.add(0, map);
    	}
    	else{
    		Log.v(TAG, "in getData, in else");
    		listView.onRefreshComplete("最后更新时间："+lastRefreshTimeForDisplay);
    	}    	
    	return list;
    }
    
    private class GetDataTask extends AsyncTask<Void, Void, String> {

    	@Override
        protected String doInBackground(Void... params) {
            // Simulates a background job.
    		try {
    			loadList.clear();
				return getHtmlCode(url+pageNum);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        	if(result!=null){
        		parseHtmlCode(result);
        		
        		DateFormat df=new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        		lastRefreshTimeForDisplay=df.format(new Date());
        		
        		listView.onRefreshComplete("最后更新时间："+lastRefreshTimeForDisplay);
        		
        		
        		super.onPostExecute(result);
        	}
        	else{
        		Log.v(TAG, "Network is error!!!");
        	}
        }
        
        //以下三个函数是抓取并解析网站
        private String parseHtmlCode(String result) {
			// TODO Auto-generated method stub
        	
        	int whetherNeedRefresh=1;
			Pattern pattern=Pattern.
					compile("<img\\s*.*\"/images/43.gif\"\\s*.*\\s*<a\\s*.*href=\"(.*)\"\\s*target.*\\s*title=\"(.*)\".*>(.*)</a>\\s*.*\\s*<div\\s*.*\\s*>\\s*(.*)\\s*</div>");
			Matcher matcher=pattern.matcher(result);
			String temp="";
			String fullTitle="";
			String newstime="";
			String newsid="";
			
			int count=0;
			while(matcher.find()){
				for(int j=1; j<=matcher.groupCount(); j++){
					temp=matcher.group(j);
					//System.out.println("temp is "+temp);
					Pattern p=Pattern.
							compile("u=(.*)");
					if(j==1){
						Matcher m=p.matcher(temp);
						while(m.find()){
							newsid=m.group(1);
							count++;
							System.out.println("NO."+count+" newsid is "+newsid);
							if(newsidList.contains(newsid)){
								whetherNeedRefresh=0;
								break;
							}
						}
					}
					if(j==2){
						fullTitle=temp;
						System.out.println("newstitle is "+fullTitle);
					}
					if(j==4){
						newstime=temp;
						System.out.println("newstime is "+newstime);
					}
					
					if(whetherNeedRefresh==0){
						break;
					}
				}
				
				if(whetherNeedRefresh==0){
					break;
				}
				
				newsidList.add(newsid);
				HashMap<String, Object> map=new HashMap<String, Object>();
				map.put("title", fullTitle);
				map.put("newsid", newsid);
				map.put("time", "发布时间："+newstime);
				map.put("content", fullTitle);
				
//				loadList.add(map);
				
				list.add(map);   //加在最后
				//adapter.notifyDataSetChanged();   //不能加，否则下拉刷新项就被显示了
			}
			return null;
		}
        
        private String getHtmlCode(String urlStr) throws Exception {
    		// TODO Auto-generated method stub
    		URL url;
    		try {
    			url = new URL(urlStr);
    			HttpURLConnection conn=(HttpURLConnection) url.openConnection();
    			conn.setConnectTimeout(10000);
    			conn.setRequestMethod("GET");
    			if(conn.getResponseCode()==200){
    				InputStream inStream = conn.getInputStream();
    				byte[] data=readStream(inStream);
    				String result=new String(data);
    				return result;
    			}
    			
    		} catch (MalformedURLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return null;
    	}

    	private byte[] readStream(InputStream inStream) throws Exception {
    		// TODO Auto-generated method stub
    		byte[] buffer = new byte[1024];
    		int len = -1;
    		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    		while ((len = inStream.read(buffer)) != -1) {
    		byteArrayOutputStream.write(buffer, 0, len);
    		}

    		inStream.close();
    		byteArrayOutputStream.close();
    		return byteArrayOutputStream.toByteArray();
    	}
    	
    }

	/*检查网络是否开启的函数*/
	public boolean networkCheck(Context context){
		if(context==null)
			return false;
		
		boolean state = false;
		Log.v(main_activity.TAG, "poi onStart networkCheck");
		ConnectivityManager connect = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
	      
	    if (connect == null)  
	    	state = false;  
	    else{
	    	NetworkInfo mobile = connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	    	NetworkInfo wifi = connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	        
	    	if(mobile==null&&wifi==null){
	    		state=false;
	    	}
	    	else{
	    		//如果3G网络和WIFI网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
	    		State ms=mobile.getState();
	    		State ws=wifi.getState();
		        if(ms==State.CONNECTED||ws==State.CONNECTED){
		        	return true;
		        }
		        else{
		        	state=false;
		        }
	    	}
	    }
	    if(state){}
		else
		{
			Builder b = new AlertDialog.Builder(context).setTitle("网络连接未开启")
			.setMessage("为了本功能的正常使用,建议您打开网络连接,是否现在去打开？");            
			b.setPositiveButton("是", new DialogInterface.OnClickListener() 
			{                
				public void onClick(DialogInterface dialog, int whichButton) {    
					Intent myIntent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
					startActivity(myIntent);
					}            
			  }
			).setNeutralButton("否", new DialogInterface.OnClickListener() 
				{        
					public void onClick(DialogInterface dialog, int whichButton) 
					{
						dialog.cancel();
					}            
				}).show();
		}
		return state;
	}
	
    private boolean isScrollLastRow = false;   //是否滚动到最后一行
    
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		Log.v(TAG, "firstVisibleItem="+firstVisibleItem+", visibleItemCount="+visibleItemCount+", totalItemCount="+totalItemCount);

		
		if(visibleItemCount+firstVisibleItem==totalItemCount && totalItemCount>2){
			//滑到底部了	
			isScrollLastRow=true;
		}
		else if(totalItemCount>2){
			if(listView.getFooterViewsCount()==0){
				Log.v(TAG, "add footer view here..............");
				listView.addFooterView();
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		//正在滚动时回调，回调2-3次，手指没抛则回调2次。scrollState = 2的这次不回调    
        //回调顺序如下    
        //第1次：scrollState = SCROLL_STATE_TOUCH_SCROLL(1) 正在滚动    
        //第2次：scrollState = SCROLL_STATE_FLING(2) 手指做了抛的动作（手指离开屏幕前，用力滑了一下）    
        //第3次：scrollState = SCROLL_STATE_IDLE(0) 停止滚动             
        //当屏幕停止滚动时为0；当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；  
        //由于用户的操作，屏幕产生惯性滑动时为2  
    
//		Log.v(TAG, "scroll state is "+scrollState);
		//当滚到最后一行且停止滚动时，执行加载  
		if(isScrollLastRow && scrollState==AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
			Log.v(TAG, "okkkkkkkkkkkkk,到底部了！！！！！！！！！！！！！！！");
				
			listView.setWHETHER_LOAD_MORE(true);		
        	
			pageNum++;   //web页码加一    
			new GetDataTask().execute();   //执行加载
			
			isScrollLastRow=false;
		}
	}
}

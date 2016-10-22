/**
 *这是本应用的主界面类 
 * 
 * @author JiaDebin
 * @version 1.0, 20/3/2012
 */

package main.hit.edu.cn;


import hit.edu.cn.R;
import poi.hit.edu.cn.poi_activity;
import recentnews.hit.edu.cn.recentNews_activity;

import alarm.hit.edu.cn.setTimeActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.ColorMatrixColorFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;
import arCompass.hit.edu.cn.AR_CompassMainActivity;
import arNavigator.hit.edu.cn.ARNavigatorActivity;

public class main_activity extends Activity {
	/*
	 * 用虚拟器运行时，IP可以是电脑的真实IP或者10.0.2.2；用真机运行时，要用真实IP而且与电脑要在同一个网段
	 * 127.0.0.1和local host代表虚拟器本身,这里我们在ARNavigator里定义一个静态常量存储这个URL
	 */
//	public static final String HOST="http://192.168.166.1:8080/axis2/services/";
	public static final String HOST="http://photo.hit.edu.cn/axis2/services/";
	public static float DENSITY=2.0F;
	private Intent intent=null;
	public static final String TAG = "main_activity";
	Intent click_intent;
	private ImageButton ib_alarm;   //现在已经变成了AR指南针
	private ImageButton ib_poi;
	private ImageButton ib_activity;
	private ImageButton ib_ar;
	private ImageButton ib_addthing;   //添加事件提醒
	private ImageButton ib_classnews;   //班级通知

	private myReceiver receiver;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder;
		if (item.getItemId() == R.id.item_help_main) {
			builder=new Builder(this);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
			builder.setMessage(R.string.help);
			builder.setTitle("使用帮助");
			builder.create().show();
		} else if (item.getItemId() == R.id.item_exit) {
			this.finish();
			/*
			 *我尝试用以下代码来完全退出程序的进程，但是service仍然会运行，并且点击报时通知时会出错，因为所需的activity都被关闭了
			 *所以还是不完全退出了，这样用户体验更好一些。 
			 
			ActivityManager activityMgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(getPackageName());
			activityMgr.killBackgroundProcesses(getPackageName());
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
			*/
		} else if (item.getItemId() == R.id.item_ablout) {
			builder=new Builder(this);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
			builder.setMessage(R.string.about);
			builder.setTitle("关于我");
			builder.create().show();
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		this.isExiting=false;
		super.onStop();
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
//		networkCheck(this);    //发这里不好，因为每次从其他功能返回到主界面时都会执行，用户体验不好,故改在onCreate里了
		IntentFilter filter=new IntentFilter();
		filter.addAction("main_activity.myReceiver");
		receiver=new myReceiver();
		registerReceiver(receiver, filter);
		super.onStart();
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();   
        //取得DisplayMetrics对象的值
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
        DENSITY = dm.density;
        Log.v(TAG, "density is "+DENSITY);
        if(DENSITY<2.0){
        	setContentView(R.layout.main_mdpi);        	
        }
        else{
        	setContentView(R.layout.main_hdpi);        	
        }
        Log.v(TAG, "main create");

//        Toast.makeText(this, "请确保网络连接和GPS已打开,以便我更好滴为您服务~", Toast.LENGTH_LONG).show();
        
        intent = this.getIntent();  /*解决重复返回main界面时是否该直接进入POI地图的问题*/
        ib_alarm = (ImageButton)findViewById(R.id.ib_alarm);
        ib_poi = (ImageButton) findViewById(R.id.ib_poi);
        ib_ar = (ImageButton) findViewById(R.id.ib_ar);
        ib_activity = (ImageButton) findViewById(R.id.ib_activity);
        ib_addthing = (ImageButton) findViewById(R.id.ib_addthing);
        ib_classnews = (ImageButton) findViewById(R.id.ib_classnews);
        
        ib_alarm.setOnTouchListener(touch);
        ib_alarm.setOnClickListener(mylistener);
        ib_poi.setOnTouchListener(touch);
        ib_poi.setOnClickListener(mylistener);
        ib_ar.setOnTouchListener(touch);
        ib_ar.setOnClickListener(mylistener);
        ib_activity.setOnTouchListener(touch);
        ib_activity.setOnClickListener(mylistener);
        
        ib_addthing.setOnTouchListener(touch);
        ib_addthing.setOnClickListener(mylistener);
        ib_classnews.setOnTouchListener(touch);
        ib_classnews.setOnClickListener(mylistener);
        
		if(intent!=null&&intent.getStringExtra("require_name")!=null){
//			Bundle bundle=intent.getExtras();    //注意这里取不到service中传过来的动态参数，所以改用广播和静态变量了
//			String thing_desc=bundle.getString("thing_desc");
			Log.v(TAG, "in main, intent is not null, thing_desc is "+main_activity.thing_desc+", search word is "+main_activity.serch_word);	
			this.getApplicationContext().stopService(intent);   //停止调用它的intent，但是这里不能实现停止service
			intent = null;
			Toast.makeText(this, "Guide Dog提示您执行预设事件-"+main_activity.thing_desc+"-的时间到了.", Toast.LENGTH_LONG).show();
		}
    }
    
    private boolean isExiting=false;
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
    	if(keyCode==KeyEvent.KEYCODE_BACK){
    		if(isExiting==false){
    			isExiting=true;
    			Toast.makeText(this, "再次点击返回键退出程序", Toast.LENGTH_SHORT).show();
    		}
    		else{
    			this.finish();
    			java.lang.System.exit(0);
    		}
    	}
		return false;
	}

	OnTouchListener touch = new OnTouchListener() {
    	public final float[] BT_SELECTED = new float[] {1,0,0,0,50,0,1,0,0,50,0,0,1,0,50,0,0,0,1,0};  
    	public final float[] BT_NOT_SELECTED = new float[] {1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0};  
		
    	@Override
    	public boolean onTouch(View v, MotionEvent event) {  

			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				v.getBackground().setColorFilter(

				new ColorMatrixColorFilter(BT_SELECTED));

				v.setBackgroundDrawable(v.getBackground());

			} 
			else if (event.getAction() == MotionEvent.ACTION_UP) {

				v.getBackground().setColorFilter(

				new ColorMatrixColorFilter(BT_NOT_SELECTED));

				v.setBackgroundDrawable(v.getBackground());
			}
			return false;
    	}  
	};
    OnClickListener mylistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.ib_alarm) {
				click_intent = new Intent(main_activity.this, setTimeActivity.class);
				startActivity(click_intent);
			}

			else if (v.getId() == R.id.ib_poi) {
				click_intent = new Intent(main_activity.this, poi_activity.class);
				click_intent.putExtra("require_name", "main_activity");
				startActivity(click_intent);
			}  
			else if (v.getId() == R.id.ib_activity) {
				click_intent = new Intent(main_activity.this, recentNews_activity.class);
				startActivity(click_intent);
			} 
			else if (v.getId() == R.id.ib_ar) {
				click_intent = new Intent(main_activity.this, ARNavigatorActivity.class);
				click_intent.putExtra("require_name", "main_activity");
				startActivity(click_intent);
			} 
			else if (v.getId() == R.id.ib_addthing) {
				click_intent = new Intent(main_activity.this, AR_CompassMainActivity.class);
				startActivity(click_intent);
			}
			else if (v.getId() == R.id.ib_classnews) {
				click_intent = new Intent(main_activity.this, classnews_activity.class);
				click_intent.putExtra("url", "http://tongzhifabu.duapp.com/");
				startActivity(click_intent);
			}
			else {
			}
		}
	};

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
			.setMessage("Guide Dog发现您的网络连接未开启,是否现在去打开？");            
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
	
	public static String serch_word="";
	public static String thing_desc="";
	
	public static class myReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			Bundle bundle=arg1.getExtras();
			String search_word=bundle.getString("search_word");
			String thing_desc=bundle.getString("thing_desc");
			Log.v(TAG, "in myReceiver, thing_desc is "+thing_desc+", search word is "+search_word);
			
			main_activity.serch_word=search_word;   //用main_activity的静态变量保存值
			main_activity.thing_desc=thing_desc;
			
//			Toast.makeText(arg0, "Guide Dog提示您执行预设事件--"+thing_desc+"的时间到了.", Toast.LENGTH_LONG).show();
			
		}
		
	}
}
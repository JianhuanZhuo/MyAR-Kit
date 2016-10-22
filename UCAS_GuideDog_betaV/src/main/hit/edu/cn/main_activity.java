/**
 *���Ǳ�Ӧ�õ��������� 
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
	 * ������������ʱ��IP�����ǵ��Ե���ʵIP����10.0.2.2�����������ʱ��Ҫ����ʵIP���������Ҫ��ͬһ������
	 * 127.0.0.1��local host��������������,����������ARNavigator�ﶨ��һ����̬�����洢���URL
	 */
//	public static final String HOST="http://192.168.166.1:8080/axis2/services/";
	public static final String HOST="http://photo.hit.edu.cn/axis2/services/";
	public static float DENSITY=2.0F;
	private Intent intent=null;
	public static final String TAG = "main_activity";
	Intent click_intent;
	private ImageButton ib_alarm;   //�����Ѿ������ARָ����
	private ImageButton ib_poi;
	private ImageButton ib_activity;
	private ImageButton ib_ar;
	private ImageButton ib_addthing;   //����¼�����
	private ImageButton ib_classnews;   //�༶֪ͨ

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
			builder.setTitle("ʹ�ð���");
			builder.create().show();
		} else if (item.getItemId() == R.id.item_exit) {
			this.finish();
			/*
			 *�ҳ��������´�������ȫ�˳�����Ľ��̣�����service��Ȼ�����У����ҵ����ʱ֪ͨʱ�������Ϊ�����activity�����ر���
			 *���Ի��ǲ���ȫ�˳��ˣ������û��������һЩ�� 
			 
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
			builder.setTitle("������");
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
//		networkCheck(this);    //�����ﲻ�ã���Ϊÿ�δ��������ܷ��ص�������ʱ����ִ�У��û����鲻��,�ʸ���onCreate����
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
        //ȡ��DisplayMetrics�����ֵ
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

//        Toast.makeText(this, "��ȷ���������Ӻ�GPS�Ѵ�,�Ա��Ҹ��õ�Ϊ������~", Toast.LENGTH_LONG).show();
        
        intent = this.getIntent();  /*����ظ�����main����ʱ�Ƿ��ֱ�ӽ���POI��ͼ������*/
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
//			Bundle bundle=intent.getExtras();    //ע������ȡ����service�д������Ķ�̬���������Ը��ù㲥�;�̬������
//			String thing_desc=bundle.getString("thing_desc");
			Log.v(TAG, "in main, intent is not null, thing_desc is "+main_activity.thing_desc+", search word is "+main_activity.serch_word);	
			this.getApplicationContext().stopService(intent);   //ֹͣ��������intent���������ﲻ��ʵ��ֹͣservice
			intent = null;
			Toast.makeText(this, "Guide Dog��ʾ��ִ��Ԥ���¼�-"+main_activity.thing_desc+"-��ʱ�䵽��.", Toast.LENGTH_LONG).show();
		}
    }
    
    private boolean isExiting=false;
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
    	if(keyCode==KeyEvent.KEYCODE_BACK){
    		if(isExiting==false){
    			isExiting=true;
    			Toast.makeText(this, "�ٴε�����ؼ��˳�����", Toast.LENGTH_SHORT).show();
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

	/*��������Ƿ����ĺ���*/
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
	    		//���3G�����WIFI���綼δ���ӣ��Ҳ��Ǵ�����������״̬ �����Network Setting���� ���û�������������
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
			Builder b = new AlertDialog.Builder(context).setTitle("��������δ����")
			.setMessage("Guide Dog����������������δ����,�Ƿ�����ȥ�򿪣�");            
			b.setPositiveButton("��", new DialogInterface.OnClickListener() 
			{                
				public void onClick(DialogInterface dialog, int whichButton) {    
					Intent myIntent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
					startActivity(myIntent);
					}            
			  }
			).setNeutralButton("��", new DialogInterface.OnClickListener() 
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
			
			main_activity.serch_word=search_word;   //��main_activity�ľ�̬��������ֵ
			main_activity.thing_desc=thing_desc;
			
//			Toast.makeText(arg0, "Guide Dog��ʾ��ִ��Ԥ���¼�--"+thing_desc+"��ʱ�䵽��.", Toast.LENGTH_LONG).show();
			
		}
		
	}
}
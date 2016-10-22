/**
 *这是周边搜索的兴趣点显示界面类 
 * 
 * @author JiaDebin
 * @version 1.0, 20/3/2012
 */

package poi.hit.edu.cn;


import main.hit.edu.cn.main_activity;
import hit.edu.cn.R;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.PoiOverlay;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class poi_activity extends MapActivity implements LocationListener{
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.poi_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.item_help_poi) {
			AlertDialog.Builder builder=new Builder(this);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
			builder.setMessage(R.string.poi_help);
			builder.setTitle("使用帮助");
			builder.create().show();
		} else if (item.getItemId() == R.id.item_changeMode_poi) {
			if(mapMode==0){
				mapView.setSatellite(true);
				mapMode=1;
				item.setTitle("切换到常规模式");
			}
			else{
				mapView.setSatellite(false);
				mapMode=0;
				item.setTitle("切换到卫星地图");
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	private int mapMode=0;
	private EditText search_word;
	private Button btn_search;
	
    static final String TAG = "POI_Activity";
	private InputMethodManager imm;  
	private BMapManager mapManager = null;
	private MapView mapView;
	private MapController mapControl;
	
	private MKLocationManager locationManager=null;
	private MyLocationOverlay locationOverlay;
	private PoiOverlay poiOverlay;
	private MKSearch mksearch = null;
	private GeoPoint point = null;
	
	Intent intent;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
			
		super.onCreate(arg0);
		setContentView(R.layout.poi_activity);
		
		networkCheck(this);
		gpsCheck(this);
		
		mapView = (MapView) findViewById(R.id.poi_mapview);
		mapManager = new BMapManager(getApplication());
		mapManager.init("D52F4D689069CE4D8CC4271C2D4F7CA10D1B4F89", null);
		
		Log.v(main_activity.TAG, "poi create");
		search_word = (EditText) findViewById(R.id.search_word);
		btn_search = (Button) findViewById(R.id.btn_search);
		intent = this.getIntent();
		if(intent.getStringExtra("require_name").equals("notification")){
			search_word.setVisibility(View.GONE);
			btn_search.setVisibility(View.GONE);
		}
		else{}
		btn_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(search_word.getText().toString().equals(""))
				{
					Log.v(TAG,"word is null!");
					return;
				}
				mksearch.poiSearchNearBy(search_word.getText().toString(), point, 1000);
				search_word.setText("");
				/*隐藏输入法*/
				imm.hideSoftInputFromWindow(search_word.getWindowToken(), 0); 
				mapView.findFocus();
				Log.v(TAG, "in onclick!");
			}
		});
	    imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		super.initMapActivity(mapManager);
		
		locationManager = mapManager.getLocationManager();
		locationManager.requestLocationUpdates(this);
			
		locationManager.enableProvider(MKLocationManager.MK_GPS_PROVIDER);  //第二次执行时到此停止
		if(mksearch==null){
			mksearch = new MKSearch();
			mksearch.init(mapManager, new MKSearchListener() {
				
				@Override
				public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onGetPoiResult(MKPoiResult result, int arg1, int arg2) {
					// TODO Auto-generated method stub
					Log.v(TAG, "in search result.........!");
					if(result==null)
					{
						Toast.makeText(getApplicationContext(), "对不起,附近没有搜寻到该类地点\n请尝试用其他关键词搜索", Toast.LENGTH_LONG).show();
						return;
					}
					if(mapView.getOverlays().size()>2)
					{
						Log.v(TAG, "Has several overlays!");
						mapView.getOverlays().remove(poiOverlay);
					}
				    poiOverlay = new PoiOverlay(poi_activity.this, mapView);
				    poiOverlay.setData(result.getAllPoi());
					mapView.getOverlays().add(poiOverlay);
					
				    if(result.getNumPois()<10){
				    	Toast.makeText(getApplicationContext(), "助手为您在附近找到了"+result.getNumPois()+"个可行地点\n"+
				    			"请查看地图上的红色小标记", Toast.LENGTH_LONG).show();
				    	mapControl.setZoom(17);
				    }
				    else{
						Toast.makeText(getApplicationContext(), "助手为您在附近找到了10个可行地点\n"+
								"请查看地图上的红色小标记", Toast.LENGTH_LONG).show();
						mapControl.setZoom(15);
				    }
				}
				
				@Override
				public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onGetAddrResult(MKAddrInfo result, int arg1) {
					// TODO Auto-generated method stub
					if(result!=null){
						Toast.makeText(poi_activity.this, result.strAddr, Toast.LENGTH_LONG).show();
						return;
					}
					return;
				}

				@Override
				public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onGetPoiDetailSearchResult(int arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onGetRGCShareUrlResult(String arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onGetSuggestionResult(MKSuggestionResult arg0,
						int arg1) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		mapView.setTraffic(true);
		mapView.setSatellite(false);
		mapMode=0;
		mapView.setBuiltInZoomControls(true);
		mapView.setDrawOverlayWhenZooming(true);
		
		point = new GeoPoint((int) (39.908422 * 1E6), (int) (116.397483 * 1E6));  //初始位置
		mapControl = mapView.getController();
		mapControl.setCenter(point);
		mapControl.setZoom(18);
		
		locationOverlay = new MyLocationOverlay(this, mapView);
		locationOverlay.enableMyLocation();
		locationOverlay.enableCompass();
		mapView.getOverlays().add(locationOverlay);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isLocationDisplayed() {
		// TODO Auto-generated method stub
		return locationOverlay.isMyLocationEnabled();
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if(location!=null)
		{
			point.setLatitudeE6((int) (location.getLatitude()*1E6));
			point.setLongitudeE6((int) (location.getLongitude()*1e6));
			Log.v(TAG, "location changeed "+location.getLatitude()+"  "+location.getLongitude());
			if(!intent.getStringExtra("require_name").equals("notification")){
				Log.v(TAG, "normal mode");
				mapControl.setCenter(point);
			}
			else
				Log.v(TAG, "notification mode");
				mapControl.setCenter(point);
		}
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.v(main_activity.TAG, "poi onRestart");
		super.onRestart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.v(main_activity.TAG, "poi onStop");
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.v(main_activity.TAG, "poi destroy");
		locationManager.disableProvider(MKLocationManager.MK_GPS_PROVIDER);
		if(mapManager!=null)
		{
			mapManager.destroy();
			mapManager = null;
		}
		locationManager = null;
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.v(main_activity.TAG, "poi pause");
		if(mapManager!=null)
		{
			mapManager.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.v(main_activity.TAG, "poi ressume");
		if(mapManager!=null)
		{
			mapManager.start();
		}
		
		super.onResume();
	}
	
	/*检查GPS是否开启的函数*/
	public boolean gpsCheck(Context context){
		if(context==null)
			return false;
		
		boolean state = false;
		Log.v(main_activity.TAG, "poi onStart gpsCheck");
		LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE );
		state = lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER );
		if(state){}
		else
		{
			Builder b = new AlertDialog.Builder(context).setTitle("GPS未开启")
			.setMessage("为了获得更精确的位置信息,建议您打开GPS,是否现在进行设置？");            
			b.setPositiveButton("是", new DialogInterface.OnClickListener() 
			{                
				public void onClick(DialogInterface dialog, int whichButton) {    
					Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS );
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
}
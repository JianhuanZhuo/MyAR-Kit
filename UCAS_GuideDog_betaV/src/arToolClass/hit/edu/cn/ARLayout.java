package arToolClass.hit.edu.cn;

import hit.edu.cn.R;

import java.util.Iterator;
import java.util.Vector;




import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import arLocationTool.hit.edu.cn.LocCalculateTool;
import arNavigator.hit.edu.cn.ARDetailWithLocalDataActivity;
import arNavigator.hit.edu.cn.ARNavigatorActivity;
import arToolView.hit.edu.cn.MyARView;
import arToolView.hit.edu.cn.MyCameraView;

public class ARLayout extends FrameLayout implements SensorEventListener, LocationListener, OnClickListener{

	private Context context;
	private boolean Debug;
	private int width;
	private int height;
	
	private float dirOfCamera;
	private float dirOfScreen;
	private int upOrDown;
	
	public final float H_ANGLEBUF = (float) 15.0;   //摄像头左右可视角度
	public final float V_ANGLEBUF = (float) 30.0;   //摄像头上下可视角度
	public final float V_ANGLE_SCREEN = (float) 30.0;   //屏幕竖向半面总度数
	
	private SensorManager sensorManager;
	private LocationManager locationManager;
	
	private MyCameraView cameraView;
	private MyARView arView;
	
	public volatile static Vector<ARPoint> vecViews;
	private Location deviceLocation;
	private boolean locationChanged;
	private boolean sensorChanged;
	
	private ARPointClickListener clickListener;
	private TextView tvDetailInfo;
	private boolean tvDetailInfoState;
	private int curClickViewId;
	
	public ARLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ARLayout(Context context, int width, int height) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.width = width;  //设置屏幕宽和高
		this.height = height;
		
		this.setDebug(true);
		
		this.deviceLocation = new Location("testLocation");   //测试用*************************************************
		this.deviceLocation.setLatitude(40.41322514d);                 //测试用*************************************************
		this.deviceLocation.setLongitude(116.67205785d);               //测试用*************************************************
		
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
//		this.deviceLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
       
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), sensorManager.SENSOR_DELAY_NORMAL);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
		
		this.cameraView = new MyCameraView(context);
		this.arView = new MyARView(context);
		this.arView.setDebug(true);
		
        this.addCustomView(this.cameraView);   //在addARView()之前调用本句,且在设置width和height之后
        this.addARView();   //注意这句话的调用必须放到添加cameraView的后面，否则会被cameraView的view覆盖，显示不出来
        
		this.vecViews = new Vector<ARPoint>();
		this.locationChanged = false;
		this.sensorChanged = false;
		
		FrameLayout.LayoutParams tvLP = new FrameLayout.LayoutParams(
        		5, 
        		5
        );
		tvDetailInfo = new TextView(context);
		tvDetailInfo.setTextColor(Color.YELLOW);
        
		tvDetailInfo.setLayoutParams(tvLP);
		tvDetailInfo.setOnClickListener(this);
		tvDetailInfoState = false;
		curClickViewId = -1;   //存储着当前正在tv中显示的view的id
        
		this.setOnClickListener(this);
	}
	
	//用于添加ARView
	public void addARView() {
		// TODO Auto-generated method stub
		if(this.isDebug()){    //Debug开启时才显示
			this.addView(this.arView, this.width, this.height);
		}
	}
	
	//用于添加cameraView
	public void addCustomView(View view) {
		// TODO Auto-generated method stub
		this.addView(view, this.width, this.height);
		
	}

	public void addARPoint(ARPoint point) {
		// TODO Auto-generated method stub
		this.vecViews.add(point);
	}

	public void addARPointDev(ARPoint point) {
		//测试用*************************************************
		//开发调试用临时方法，直接显示地点
		// TODO Auto-generated method stub
//		Toast.makeText(this.context, "Has "+this.getChildCount()+" children.", Toast.LENGTH_SHORT).show();
		this.vecViews.add(point);
	}
	
	public void updateARPointState(){
		if(vecViews.size()>0){
			int size = vecViews.size();
			
			for(int i=0; i<size; i++){
				
				ARPoint tempPoint = vecViews.elementAt(i);
				if(this.deviceLocation!=null){
					vecViews.elementAt(i).bearingMe = LocCalculateTool.calculate(this.deviceLocation, tempPoint.location);
					vecViews.elementAt(i).offsetCenter = vecViews.elementAt(i).bearingMe - this.dirOfCamera;
					vecViews.elementAt(i).distance = this.deviceLocation.distanceTo(tempPoint.location);
				}
				//下面计算偏移量
				float offset = vecViews.elementAt(i).offsetCenter;
				float leftMarginF;
				float topMarginF;
				if(Math.abs(offset)<this.H_ANGLEBUF){
					leftMarginF = (offset/this.H_ANGLEBUF)*(this.width/2);
					vecViews.elementAt(i).leftMargin = (int)(leftMarginF+0.5);   //四舍五入转int
					
					if(Math.abs(this.dirOfScreen)<this.V_ANGLEBUF){
						topMarginF = (this.dirOfScreen/this.V_ANGLE_SCREEN)*(this.height/2)*-1;
						vecViews.elementAt(i).visible = true;  //设为可见
					}
					else{
						vecViews.elementAt(i).visible = false; //设为不可见
					}
				}
				else{
					vecViews.elementAt(i).visible = false; //设为不可见
				}
			
			}
			showARPoint();   //显示该显示的point
			
			this.locationChanged = false;
			this.sensorChanged = false;
		}
		else{
			Log.v(ARNavigatorActivity.TAG, "vector size is 0.");
		}
	}
	
	public void TestUpdatePointState(){
		//测试用*************************************************
//		Toast.makeText(this.context, "Has "+this.getChildCount()+" children.", Toast.LENGTH_SHORT).show();
//		Toast.makeText(this.context, "vector size is "+vecViews.size(), Toast.LENGTH_SHORT).show();
		
		if(vecViews.size()>0){
			int size = vecViews.size();
			
			for(int i=0; i<size; i++){
				
				ARPoint tempPoint = vecViews.elementAt(i);
				if(this.deviceLocation!=null){
					vecViews.elementAt(i).bearingMe = LocCalculateTool.calculate(this.deviceLocation, tempPoint.location);
//					Toast.makeText(this.context, "value is "+vecViews.elementAt(i).bearingMe, Toast.LENGTH_SHORT).show();
					vecViews.elementAt(i).offsetCenter = vecViews.elementAt(i).bearingMe - this.dirOfCamera;
					vecViews.elementAt(i).distance = this.deviceLocation.distanceTo(tempPoint.location);
				}
				//下面计算偏移量
				float offset = vecViews.elementAt(i).offsetCenter;
				float leftMarginF;
				float topMarginF;
				if(Math.abs(offset)<this.H_ANGLEBUF){
					leftMarginF = (offset/this.H_ANGLEBUF)*(this.width/2);
					vecViews.elementAt(i).leftMargin = (int)(leftMarginF+0.5);   //四舍五入转int
					
					if(Math.abs(this.dirOfScreen)<this.V_ANGLEBUF){
						topMarginF = (this.dirOfScreen/this.V_ANGLE_SCREEN)*(this.height/2);
						vecViews.elementAt(i).topMargin = (int)(topMarginF+0.5);   //四舍五入转int
						vecViews.elementAt(i).visible = true;  //设为可见
					}
					else{
						vecViews.elementAt(i).visible = false; //设为不可见
					}
				}
				else if(offset>-360&&offset<(-360+this.H_ANGLEBUF)){  
					//处理dirOfCamere为360时的情况，因为摄像头朝向北方时，是0和360的交界处，所以dirOfCamere有可能是大于0也有可能是小于360一些，这两种情况都是北方
					offset=360+offset;
					leftMarginF = (offset/this.H_ANGLEBUF)*(this.width/2);
					vecViews.elementAt(i).leftMargin = (int)(leftMarginF+0.5);   //四舍五入转int
					
					if(Math.abs(this.dirOfScreen)<this.V_ANGLEBUF){
						topMarginF = (this.dirOfScreen/this.V_ANGLE_SCREEN)*(this.height/2);
						vecViews.elementAt(i).topMargin = (int)(topMarginF+0.5);   //四舍五入转int
						vecViews.elementAt(i).visible = true;  //设为可见
						
					}
					else{
						vecViews.elementAt(i).visible = false; //设为不可见
					}
				}
				else{
					vecViews.elementAt(i).visible = false; //设为不可见
				}
			
			}
			showARPoint();   //显示该显示的point
			
			this.locationChanged = false;
			this.sensorChanged = false;
		}
		else{
			Log.v(ARNavigatorActivity.TAG, "vector size is 0.");
		}
	}
	
	public void showARPoint(){
		int childNum = this.getChildCount();
		if(childNum>2){
			if(this.tvDetailInfoState == true){
				this.removeViews(2, childNum-3);   //注意这里是减3了，不是减2
			}
			else{
				this.removeViews(2, childNum-2);
				
			}
		}
		if(vecViews.size()>0){
			int size = vecViews.size();
			
			for(int i=0; i<size; i++){		
				ARPoint tempPoint = vecViews.elementAt(i);
				if(tempPoint.visible){   //如果可见
					showPoint(tempPoint, tempPoint.getId());
				}
			}
		}
	}
	
	public void showPoint(ARPoint point, int id) {
		// TODO Auto-generated method stub
		FrameLayout.LayoutParams viewLP = new FrameLayout.LayoutParams(
        		ViewGroup.LayoutParams.WRAP_CONTENT, 
        		ViewGroup.LayoutParams.WRAP_CONTENT
        );
		
		ImageView imageView = new ImageView(context);
		
		switch(point.getType()){
			case ARPoint.TYPE_TECHING:
				imageView.setImageResource(R.drawable.marker1);
				break;
			case ARPoint.TYPE_SERVICE:
				imageView.setImageResource(R.drawable.marker2);
				break;	
			case ARPoint.TYPE_OTHERS:
				imageView.setImageResource(R.drawable.marker3);
				break;	
			default:
				break;
		}
        imageView.setLayoutParams(viewLP);
        
        imageView.setId(id);   //设置一个id，即该点的id值
        imageView.setOnClickListener(this);
       
        FrameLayout.LayoutParams viewPosition1 = new FrameLayout.LayoutParams(
        		ViewGroup.LayoutParams.WRAP_CONTENT, 
        		ViewGroup.LayoutParams.WRAP_CONTENT, 
        		Gravity.CENTER);   //或者这里不加gravity属性也行，但是如果设置为center了，那么leftMargin就应该是相对于屏幕中心的值了(负数)，就不是相对于屏幕左上角了
        viewPosition1.leftMargin = point.leftMargin;
        viewPosition1.topMargin = point.topMargin;
        this.addView(imageView, 2, viewPosition1);

	 	TextView tv = new TextView(context);
        tv.setText(point.getName()+"("+generateDistanceStr(point.distance)+")");
        tv.setTextColor(Color.YELLOW);
        
        tv.setLayoutParams(viewLP);
        
        FrameLayout.LayoutParams viewPosition2 = new FrameLayout.LayoutParams(
        		ViewGroup.LayoutParams.WRAP_CONTENT, 
        		ViewGroup.LayoutParams.WRAP_CONTENT, 
        		Gravity.CENTER);   //或者这里不加gravity属性也行，但是如果设置为center了，那么leftMargin就应该是相对于屏幕中心的值了(负数)，就不是相对于屏幕左上角了
        viewPosition2.leftMargin = point.leftMargin;
        viewPosition2.topMargin = point.topMargin + 64;
        this.addView(tv, 3, viewPosition2);
	}
	
	private String generateDistanceStr(float distance) {
		// TODO Auto-generated method stub
		/*对distance进行四舍五入并格式化，生成km或m的串*/
		StringBuffer str = new StringBuffer();
		if(distance > 1000){
			distance = distance/1000;
			distance = Math.round(distance*10);
			distance = distance/10;
			str.append(distance);
			str.append("km");
		}
		else{
			distance = Math.round(distance*10);
			distance = distance/10;
			str.append(distance);
			str.append("m");
		}
		return str.toString();
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float values[] = event.values;
		
		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
			this.sensorChanged = true;
			float x=(values[0]+90)%360;  //手机摄像头与正北方向夹角,0-360
			this.dirOfCamera = x;
			this.dirOfScreen = this.upOrDown*(values[2]-90);   //屏幕朝上为正，朝下为负，范围-45~45
			
			this.arView.dirOfCamera = this.dirOfCamera;
			this.arView.dirOfScreen = this.dirOfScreen;
		}
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			this.sensorChanged = true;
			if(values[2]<0){
//				deOfScreen *= (-1);       //不要放这里，因为放这里的话显示时会频繁变换正负，所以放到上面方向传感器事件里
				this.upOrDown = -1;
			}
			else{
				this.upOrDown = 1;
			}
			//屏幕垂直于水平面对准自己时，Z为0，屏幕向上倾斜时Z为正，向下倾斜时Z为负数
		}
//		Log.v(AR_ARProjectMainActivity.TAG, "In MyARLayout");
		this.arView.postInvalidate();  //强制要求arView重绘
		
//		updateARPointState();
		TestUpdatePointState();  //测试用*************************************************
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public boolean isDebug() {
		return Debug;
	}

	public void setDebug(boolean debug) {
		Debug = debug;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if(this.deviceLocation==null){
			this.locationChanged = true;
		}
		else {
			float distance = this.deviceLocation.distanceTo(location);
			if(distance<5){   //5m以内视为不变
				this.locationChanged = false;
			}
			else{
				this.locationChanged = true;
				
				this.deviceLocation = location;   //更新位置
//				TestUpdatePointState();
			}
		}
		
		this.deviceLocation = location;
		
//		updateARPointState();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
//		Toast.makeText(this.context, "请打开手机的GPS定位功能，以便更好地为您服务。", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	public static ARPoint getARPointById(int id){
		if(id>0){
			Iterator<ARPoint> tempIterator;
			tempIterator=vecViews.iterator();
			while(tempIterator.hasNext()){
				ARPoint temp=tempIterator.next();
				if(temp.getId()==id){
					return temp;
				}
			}
		}
		
		return null;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Log.v(ARNavigatorActivity.TAG, "yes, click id is "+view.getId());
		
		//下面的if是后来加的，直接跳转到detail界面----------------------------------
		if(view.getId()>0&&view.getId()<10000){
			Intent intent = new Intent();
			intent.setClass(this.context, ARDetailWithLocalDataActivity.class);
			intent.putExtra("ARPOINT_ID", view.getId());
			intent.putExtra("ARPOINT_TITLE", "");
			intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);   //需要加上这句，否则跳转失败，因为不是在activity里
			this.context.startActivity(intent);   //进入detail界面
			
			return;
		}
		
		//上面的if是后来加的，直接跳转到detail界面,下面的代码暂时弃用-------------------
		
		if(view.getId()>0){
			Log.v(ARNavigatorActivity.TAG, "in on click listener............"+this.getChildCount()+", "+this.vecViews.size());
			if(this.tvDetailInfoState == false){
				FrameLayout.LayoutParams tvPosition = new FrameLayout.LayoutParams(
						this.width/5, 
						ViewGroup.LayoutParams.WRAP_CONTENT, 
						Gravity.RIGHT|Gravity.CENTER_VERTICAL);   //或者这里不加gravity属性也行，但是如果设置为center了，那么leftMargin就应该是相对于屏幕中心的值了(负数)，就不是相对于屏幕左上角了
				tvDetailInfo.setText("点此查看该地点详细信息..>>");
				
				tvDetailInfo.setId(view.getId()+10000);  //设tv的id为10000+view的id，便于区分
				this.addView(tvDetailInfo, tvPosition);
				this.tvDetailInfoState = true;
				this.curClickViewId = view.getId();
			}
			else if(view.getId()==this.curClickViewId){   //点击第二次时移除
				this.removeViewAt(this.getChildCount()-1);  //移除最后的tv
				this.tvDetailInfoState = false;
				tvDetailInfo.setId(-1);
				this.curClickViewId = -1;
			}
			else if(view.getId()>10000){  //说明用户点击的是textView
				Log.v(ARNavigatorActivity.TAG, "yes, click tv.......");
				Intent intent = new Intent();
				intent.setClass(this.context, ARDetailWithLocalDataActivity.class);
				intent.putExtra("ARPOINT_ID", (view.getId()-10000));
				intent.putExtra("ARPOINT_TITLE", "");
				intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);   //需要加上这句，否则跳转失败，因为不是在activity里
				this.context.startActivity(intent);   //进入detail界面
			}
			else{     									  //如果点的是另外一个图标，那么应该更新tv的信息
				tvDetailInfo.setText("点此查看该地点详细信息..>>");
				tvDetailInfo.setId(view.getId()+10000);  //设tv的id为10000+view的id，便于区分
				this.tvDetailInfoState = true;
				this.curClickViewId = view.getId();
			}
		}
		else if(view == this){   //如果点击的是空白处,也即对象本身
			Log.v(ARNavigatorActivity.TAG, "this is myself。。。。。。。。。。。。。。");
			if(this.tvDetailInfoState){
				this.removeViewAt(this.getChildCount()-1);  //移除最后一个view
				this.tvDetailInfoState = false;
				tvDetailInfo.setId(-1);
				this.curClickViewId = -1;
			}
		}
	}
	
}

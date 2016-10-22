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
	
	public final float H_ANGLEBUF = (float) 15.0;   //����ͷ���ҿ��ӽǶ�
	public final float V_ANGLEBUF = (float) 30.0;   //����ͷ���¿��ӽǶ�
	public final float V_ANGLE_SCREEN = (float) 30.0;   //��Ļ��������ܶ���
	
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
		this.width = width;  //������Ļ��͸�
		this.height = height;
		
		this.setDebug(true);
		
		this.deviceLocation = new Location("testLocation");   //������*************************************************
		this.deviceLocation.setLatitude(40.41322514d);                 //������*************************************************
		this.deviceLocation.setLongitude(116.67205785d);               //������*************************************************
		
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
//		this.deviceLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
       
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), sensorManager.SENSOR_DELAY_NORMAL);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
		
		this.cameraView = new MyCameraView(context);
		this.arView = new MyARView(context);
		this.arView.setDebug(true);
		
        this.addCustomView(this.cameraView);   //��addARView()֮ǰ���ñ���,��������width��height֮��
        this.addARView();   //ע����仰�ĵ��ñ���ŵ����cameraView�ĺ��棬����ᱻcameraView��view���ǣ���ʾ������
        
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
		curClickViewId = -1;   //�洢�ŵ�ǰ����tv����ʾ��view��id
        
		this.setOnClickListener(this);
	}
	
	//�������ARView
	public void addARView() {
		// TODO Auto-generated method stub
		if(this.isDebug()){    //Debug����ʱ����ʾ
			this.addView(this.arView, this.width, this.height);
		}
	}
	
	//�������cameraView
	public void addCustomView(View view) {
		// TODO Auto-generated method stub
		this.addView(view, this.width, this.height);
		
	}

	public void addARPoint(ARPoint point) {
		// TODO Auto-generated method stub
		this.vecViews.add(point);
	}

	public void addARPointDev(ARPoint point) {
		//������*************************************************
		//������������ʱ������ֱ����ʾ�ص�
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
				//�������ƫ����
				float offset = vecViews.elementAt(i).offsetCenter;
				float leftMarginF;
				float topMarginF;
				if(Math.abs(offset)<this.H_ANGLEBUF){
					leftMarginF = (offset/this.H_ANGLEBUF)*(this.width/2);
					vecViews.elementAt(i).leftMargin = (int)(leftMarginF+0.5);   //��������תint
					
					if(Math.abs(this.dirOfScreen)<this.V_ANGLEBUF){
						topMarginF = (this.dirOfScreen/this.V_ANGLE_SCREEN)*(this.height/2)*-1;
						vecViews.elementAt(i).visible = true;  //��Ϊ�ɼ�
					}
					else{
						vecViews.elementAt(i).visible = false; //��Ϊ���ɼ�
					}
				}
				else{
					vecViews.elementAt(i).visible = false; //��Ϊ���ɼ�
				}
			
			}
			showARPoint();   //��ʾ����ʾ��point
			
			this.locationChanged = false;
			this.sensorChanged = false;
		}
		else{
			Log.v(ARNavigatorActivity.TAG, "vector size is 0.");
		}
	}
	
	public void TestUpdatePointState(){
		//������*************************************************
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
				//�������ƫ����
				float offset = vecViews.elementAt(i).offsetCenter;
				float leftMarginF;
				float topMarginF;
				if(Math.abs(offset)<this.H_ANGLEBUF){
					leftMarginF = (offset/this.H_ANGLEBUF)*(this.width/2);
					vecViews.elementAt(i).leftMargin = (int)(leftMarginF+0.5);   //��������תint
					
					if(Math.abs(this.dirOfScreen)<this.V_ANGLEBUF){
						topMarginF = (this.dirOfScreen/this.V_ANGLE_SCREEN)*(this.height/2);
						vecViews.elementAt(i).topMargin = (int)(topMarginF+0.5);   //��������תint
						vecViews.elementAt(i).visible = true;  //��Ϊ�ɼ�
					}
					else{
						vecViews.elementAt(i).visible = false; //��Ϊ���ɼ�
					}
				}
				else if(offset>-360&&offset<(-360+this.H_ANGLEBUF)){  
					//����dirOfCamereΪ360ʱ���������Ϊ����ͷ���򱱷�ʱ����0��360�Ľ��紦������dirOfCamere�п����Ǵ���0Ҳ�п�����С��360һЩ��������������Ǳ���
					offset=360+offset;
					leftMarginF = (offset/this.H_ANGLEBUF)*(this.width/2);
					vecViews.elementAt(i).leftMargin = (int)(leftMarginF+0.5);   //��������תint
					
					if(Math.abs(this.dirOfScreen)<this.V_ANGLEBUF){
						topMarginF = (this.dirOfScreen/this.V_ANGLE_SCREEN)*(this.height/2);
						vecViews.elementAt(i).topMargin = (int)(topMarginF+0.5);   //��������תint
						vecViews.elementAt(i).visible = true;  //��Ϊ�ɼ�
						
					}
					else{
						vecViews.elementAt(i).visible = false; //��Ϊ���ɼ�
					}
				}
				else{
					vecViews.elementAt(i).visible = false; //��Ϊ���ɼ�
				}
			
			}
			showARPoint();   //��ʾ����ʾ��point
			
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
				this.removeViews(2, childNum-3);   //ע�������Ǽ�3�ˣ����Ǽ�2
			}
			else{
				this.removeViews(2, childNum-2);
				
			}
		}
		if(vecViews.size()>0){
			int size = vecViews.size();
			
			for(int i=0; i<size; i++){		
				ARPoint tempPoint = vecViews.elementAt(i);
				if(tempPoint.visible){   //����ɼ�
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
        
        imageView.setId(id);   //����һ��id�����õ��idֵ
        imageView.setOnClickListener(this);
       
        FrameLayout.LayoutParams viewPosition1 = new FrameLayout.LayoutParams(
        		ViewGroup.LayoutParams.WRAP_CONTENT, 
        		ViewGroup.LayoutParams.WRAP_CONTENT, 
        		Gravity.CENTER);   //�������ﲻ��gravity����Ҳ�У������������Ϊcenter�ˣ���ôleftMargin��Ӧ�����������Ļ���ĵ�ֵ��(����)���Ͳ����������Ļ���Ͻ���
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
        		Gravity.CENTER);   //�������ﲻ��gravity����Ҳ�У������������Ϊcenter�ˣ���ôleftMargin��Ӧ�����������Ļ���ĵ�ֵ��(����)���Ͳ����������Ļ���Ͻ���
        viewPosition2.leftMargin = point.leftMargin;
        viewPosition2.topMargin = point.topMargin + 64;
        this.addView(tv, 3, viewPosition2);
	}
	
	private String generateDistanceStr(float distance) {
		// TODO Auto-generated method stub
		/*��distance�����������벢��ʽ��������km��m�Ĵ�*/
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
			float x=(values[0]+90)%360;  //�ֻ�����ͷ����������н�,0-360
			this.dirOfCamera = x;
			this.dirOfScreen = this.upOrDown*(values[2]-90);   //��Ļ����Ϊ��������Ϊ������Χ-45~45
			
			this.arView.dirOfCamera = this.dirOfCamera;
			this.arView.dirOfScreen = this.dirOfScreen;
		}
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			this.sensorChanged = true;
			if(values[2]<0){
//				deOfScreen *= (-1);       //��Ҫ�������Ϊ������Ļ���ʾʱ��Ƶ���任���������Էŵ����淽�򴫸����¼���
				this.upOrDown = -1;
			}
			else{
				this.upOrDown = 1;
			}
			//��Ļ��ֱ��ˮƽ���׼�Լ�ʱ��ZΪ0����Ļ������бʱZΪ����������бʱZΪ����
		}
//		Log.v(AR_ARProjectMainActivity.TAG, "In MyARLayout");
		this.arView.postInvalidate();  //ǿ��Ҫ��arView�ػ�
		
//		updateARPointState();
		TestUpdatePointState();  //������*************************************************
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
			if(distance<5){   //5m������Ϊ����
				this.locationChanged = false;
			}
			else{
				this.locationChanged = true;
				
				this.deviceLocation = location;   //����λ��
//				TestUpdatePointState();
			}
		}
		
		this.deviceLocation = location;
		
//		updateARPointState();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
//		Toast.makeText(this.context, "����ֻ���GPS��λ���ܣ��Ա���õ�Ϊ������", Toast.LENGTH_LONG).show();
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
		
		//�����if�Ǻ����ӵģ�ֱ����ת��detail����----------------------------------
		if(view.getId()>0&&view.getId()<10000){
			Intent intent = new Intent();
			intent.setClass(this.context, ARDetailWithLocalDataActivity.class);
			intent.putExtra("ARPOINT_ID", view.getId());
			intent.putExtra("ARPOINT_TITLE", "");
			intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);   //��Ҫ������䣬������תʧ�ܣ���Ϊ������activity��
			this.context.startActivity(intent);   //����detail����
			
			return;
		}
		
		//�����if�Ǻ����ӵģ�ֱ����ת��detail����,����Ĵ�����ʱ����-------------------
		
		if(view.getId()>0){
			Log.v(ARNavigatorActivity.TAG, "in on click listener............"+this.getChildCount()+", "+this.vecViews.size());
			if(this.tvDetailInfoState == false){
				FrameLayout.LayoutParams tvPosition = new FrameLayout.LayoutParams(
						this.width/5, 
						ViewGroup.LayoutParams.WRAP_CONTENT, 
						Gravity.RIGHT|Gravity.CENTER_VERTICAL);   //�������ﲻ��gravity����Ҳ�У������������Ϊcenter�ˣ���ôleftMargin��Ӧ�����������Ļ���ĵ�ֵ��(����)���Ͳ����������Ļ���Ͻ���
				tvDetailInfo.setText("��˲鿴�õص���ϸ��Ϣ..>>");
				
				tvDetailInfo.setId(view.getId()+10000);  //��tv��idΪ10000+view��id����������
				this.addView(tvDetailInfo, tvPosition);
				this.tvDetailInfoState = true;
				this.curClickViewId = view.getId();
			}
			else if(view.getId()==this.curClickViewId){   //����ڶ���ʱ�Ƴ�
				this.removeViewAt(this.getChildCount()-1);  //�Ƴ�����tv
				this.tvDetailInfoState = false;
				tvDetailInfo.setId(-1);
				this.curClickViewId = -1;
			}
			else if(view.getId()>10000){  //˵���û��������textView
				Log.v(ARNavigatorActivity.TAG, "yes, click tv.......");
				Intent intent = new Intent();
				intent.setClass(this.context, ARDetailWithLocalDataActivity.class);
				intent.putExtra("ARPOINT_ID", (view.getId()-10000));
				intent.putExtra("ARPOINT_TITLE", "");
				intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);   //��Ҫ������䣬������תʧ�ܣ���Ϊ������activity��
				this.context.startActivity(intent);   //����detail����
			}
			else{     									  //������������һ��ͼ�꣬��ôӦ�ø���tv����Ϣ
				tvDetailInfo.setText("��˲鿴�õص���ϸ��Ϣ..>>");
				tvDetailInfo.setId(view.getId()+10000);  //��tv��idΪ10000+view��id����������
				this.tvDetailInfoState = true;
				this.curClickViewId = view.getId();
			}
		}
		else if(view == this){   //���������ǿհ״�,Ҳ��������
			Log.v(ARNavigatorActivity.TAG, "this is myself����������������������������");
			if(this.tvDetailInfoState){
				this.removeViewAt(this.getChildCount()-1);  //�Ƴ����һ��view
				this.tvDetailInfoState = false;
				tvDetailInfo.setId(-1);
				this.curClickViewId = -1;
			}
		}
	}
	
}

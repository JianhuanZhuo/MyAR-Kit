package arToolClass.hit.edu.cn;

import hit.edu.cn.R;

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
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import arCompass.hit.edu.cn.AR_CompassMainActivity;
import arLocationTool.hit.edu.cn.LocCalculateTool;
import arNavigator.hit.edu.cn.ARPointDetailActivity;
import arToolView.hit.edu.cn.MyARView;
import arToolView.hit.edu.cn.MyCameraView;

public class ARLayoutForCompass extends FrameLayout implements SensorEventListener, LocationListener, OnClickListener{

	public static final String TAG="AR_COMPASS";
	private Context context;
	private boolean Debug;
	private int width;
	public int getScreenWidth() {
		return width;
	}

	public void setScreenWidth(int width) {
		this.width = width;
	}

	public int getScreenHeight() {
		return height;
	}

	public void setScreenHeight(int height) {
		this.height = height;
	}

	private int height;
	
	private float dirOfCamera;
	private float dirOfScreen;
	private int upOrDown;
	
	public final float H_ANGLEBUF = (float) 15.0;   //����ͷ���ҿ��ӽǶ�
	public final float V_ANGLEBUF = (float) 30.0;   //����ͷ���¿��ӽǶ�
	public final float V_ANGLE_SCREEN = (float) 30.0;   //��Ļ��������ܶ���
	
	private SensorManager sensorManager;
	private LocationManager locationManager;
	
	public MyCameraView cameraView;
	private MyARView arView;
	
	private volatile Vector<ARPoint> vecViews;
	private Location deviceLocation;
	private boolean locationChanged;
	private boolean sensorChanged;
	
	private ARPointClickListener clickListener;
	private TextView tvDetailInfo;
	private boolean tvDetailInfoState;
	private int curClickViewId;
	
	public ARLayoutForCompass(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ARLayoutForCompass(Context context, int width, int height) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.width = width;  //������Ļ��͸�
		this.height = height;
		
		this.setDebug(true);
		this.vecViews = new Vector<ARPoint>();
		
		this.deviceLocation = new Location("testLocation");   //������*************************************************
		this.deviceLocation.setLatitude(39.908255d);                 //������*************************************************
		this.deviceLocation.setLongitude(116.397482d);               //������*************************************************
		addTestARPoints();   //���붫�������ĸ���
		
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
       
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
		
		Location lastLoc;
		
		lastLoc=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);  //Ϊnull
//		this.deviceLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(lastLoc==null){
			Log.v(AR_CompassMainActivity.TAG, "last loc is null");
		}
		else{
			Log.v(AR_CompassMainActivity.TAG, "last loc is not null");
		}
		
		this.cameraView = new MyCameraView(context);
		this.arView = new MyARView(context);
		this.arView.setDebug(true);
		
        this.addCustomView(this.cameraView);   //��addARView()֮ǰ���ñ���,��������width��height֮��
        this.addARView();   //ע����仰�ĵ��ñ���ŵ����cameraView�ĺ��棬����ᱻcameraView��view���ǣ���ʾ������
        
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
		
		Toast.makeText(this.context, "�뽫�ֻ������ճ֣�����ͷ����ǰ����ԭ��ת�����ҵ�����", Toast.LENGTH_LONG).show();
		
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

	public void addARPointDev(ARPoint point) {
		//������*************************************************
		//������������ʱ������ֱ����ʾ�ص�
		// TODO Auto-generated method stub
		this.vecViews.add(point);
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
					
					vecViews.elementAt(i).offsetCenter = vecViews.elementAt(i).bearingMe - this.dirOfCamera;
					
					if(i==3)
					{
//						Log.v(AR_ARProjectMainActivity.TAG, "bearingMe is "+vecViews.elementAt(i).bearingMe+", dirOfCamera is "+this.dirOfCamera+", offsetCenter is "+vecViews.elementAt(i).offsetCenter);
					}
					
					vecViews.elementAt(i).distance = 0f;
				}
				//�������ƫ����
				float offset = vecViews.elementAt(i).offsetCenter;
				float leftMarginF;
				float topMarginF;
				if(Math.abs(offset)<this.H_ANGLEBUF){    //15������
					leftMarginF = (offset/this.H_ANGLEBUF)*(this.width/2);
					vecViews.elementAt(i).leftMargin = (int)(leftMarginF+0.5);   //��������תint
					
					if(Math.abs(this.dirOfScreen)<this.V_ANGLEBUF){
						topMarginF = (this.dirOfScreen/this.V_ANGLE_SCREEN)*(this.height/2);
						vecViews.elementAt(i).topMargin = (int)(topMarginF+0.5);   //��������תint
						vecViews.elementAt(i).visible = true;  //��Ϊ�ɼ�
//						Log.v(AR_ARProjectMainActivity.TAG, "i is "+i);
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
//						Log.v(AR_ARProjectMainActivity.TAG, "i is "+i);
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
			Log.v(AR_CompassMainActivity.TAG, "vector size is 0.");
		}
	}
	
	private void addTestARPoints() {
		// TODO Auto-generated method stub
		this.vecViews.clear();
		
		ARPoint point1 = new ARPoint();
		point1.setId(1);
		point1.setType(ARPoint.TYPE_SERVICE);
		point1.setName("��");
		point1.location.setLatitude(this.deviceLocation.getLatitude());
		point1.location.setLongitude(this.deviceLocation.getLongitude()+3);
		this.addARPointDev(point1);
		
		ARPoint point2 = new ARPoint();
		point2.setId(2);
		point2.setType(ARPoint.TYPE_SERVICE);
		point2.setName("��");
		point2.location.setLatitude(this.deviceLocation.getLatitude()-3);
		point2.location.setLongitude(this.deviceLocation.getLongitude());
		this.addARPointDev(point2);
		
		ARPoint point3 = new ARPoint();
		point3.setId(3);
		point3.setType(ARPoint.TYPE_SERVICE);
		point3.setName("��");
		point3.location.setLatitude(this.deviceLocation.getLatitude());
		point3.location.setLongitude(this.deviceLocation.getLongitude()-3);
		this.addARPointDev(point3);
		
		ARPoint point4 = new ARPoint();
		point4.setId(4);
		point4.setType(ARPoint.TYPE_SERVICE);
		point4.setName("��");
		point4.location.setLatitude(this.deviceLocation.getLatitude()+3);
		point4.location.setLongitude(this.deviceLocation.getLongitude());
		this.addARPointDev(point4);
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
					showPoint(tempPoint, i);
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
		
		switch(point.getId()){
			case 1:
				imageView.setImageResource(R.drawable.east);
				break;
			case 2:
				imageView.setImageResource(R.drawable.south);
				break;	
			case 3:
				imageView.setImageResource(R.drawable.west);
				break;	
			case 4:
				imageView.setImageResource(R.drawable.north);
				break;	
			default:
				break;
		}
        imageView.setLayoutParams(viewLP);
        
//        imageView.setId(id);   //����һ��id����0��ʼ������point��vector�е�����ֵ
        imageView.setId(point.getId());   //����һ��id����0��ʼ������point��id
        
        imageView.setOnClickListener(this);
       
        FrameLayout.LayoutParams viewPosition1 = new FrameLayout.LayoutParams(
        		ViewGroup.LayoutParams.WRAP_CONTENT, 
        		ViewGroup.LayoutParams.WRAP_CONTENT, 
        		Gravity.CENTER);   //�������ﲻ��gravity����Ҳ�У������������Ϊcenter�ˣ���ôleftMargin��Ӧ�����������Ļ���ĵ�ֵ��(����)���Ͳ����������Ļ���Ͻ���
        viewPosition1.leftMargin = point.leftMargin;
        viewPosition1.topMargin = point.topMargin;
        this.addView(imageView, 2, viewPosition1);

	 	TextView tv = new TextView(context);
        tv.setText(point.getName());
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
			}
		}
		Log.v(AR_CompassMainActivity.TAG, "IN onLocationChanged, add test points");
		this.deviceLocation = location;
		addTestARPoints();   //���붫�������ĸ���
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

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Log.v(TAG, "in onClick, view id is "+view.getId());
		//�����ж��Ƿ�Ϊimageview
		if(view.getId()>0&&view.getId()<=4){
			Log.v(TAG, "in onClick, start new intent to show detail");
			
			Intent intent = new Intent();
			intent.setClass(this.context, ARPointDetailActivity.class);
			intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);   //��Ҫ������䣬������תʧ�ܣ���Ϊ������activity��
			intent.putExtra("ARPOINT_ID", view.getId());
			intent.putExtra("ARPOINT_TITLE", "");
			
			this.context.startActivity(intent);
			
			return;
		}
		if(view.getId()>=0 && view.getId()<this.vecViews.size()){
			//Log.v(TAG, "in on click listener............"+this.getChildCount()+", "+this.vecViews.size());
			if(this.tvDetailInfoState == false){
				FrameLayout.LayoutParams tvPosition = new FrameLayout.LayoutParams(
						this.width/5, 
						ViewGroup.LayoutParams.WRAP_CONTENT, 
						Gravity.RIGHT|Gravity.CENTER_VERTICAL);   //�������ﲻ��gravity����Ҳ�У������������Ϊcenter�ˣ���ôleftMargin��Ӧ�����������Ļ���ĵ�ֵ��(����)���Ͳ����������Ļ���Ͻ���
				tvDetailInfo.setText("����鿴AR�ص�"+view.getId()+"����ϸ��Ϣ...>>");
				tvDetailInfo.setId(view.getId()+10000);  //��tv��idΪ10000+view��id����������
				
				this.addView(tvDetailInfo, tvPosition);
				this.tvDetailInfoState = true;
				this.curClickViewId = view.getId();
			}
			else if(view.getId()==this.curClickViewId){   //����ڶ���ʱ�Ƴ�
				Log.v(TAG, "click it again");
				this.removeViewAt(this.getChildCount()-1);  //�Ƴ�����tv
				this.tvDetailInfoState = false;
				tvDetailInfo.setId(-1);
				this.curClickViewId = -1;
			}
			else{     									  //������������һ��ͼ�꣬��ôӦ�ø���tv����Ϣ
				tvDetailInfo.setText("����鿴AR�ص�"+view.getId()+"����ϸ��Ϣ...>>");
				tvDetailInfo.setId(view.getId()+10000);  //��tv��idΪ10000+view��id����������
				this.tvDetailInfoState = true;
				this.curClickViewId = view.getId();
			}
		}
		else if(view == this){   //���������ǿհ״�,Ҳ��������
			Log.v(TAG, "this is myself");
			if(this.tvDetailInfoState){
				this.removeViewAt(this.getChildCount()-1);  //�Ƴ����һ��view
				this.tvDetailInfoState = false;
				tvDetailInfo.setId(-1);
				this.curClickViewId = -1;
			}
		}
		else if(view.getId()>=10000){  //˵���û��������textView
			Log.v(TAG, "click TextView");
			Intent intent = new Intent();
			intent.setClass(this.context, ARPointDetailActivity.class);
			intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);   //��Ҫ������䣬������תʧ�ܣ���Ϊ������activity��
			intent.putExtra("ARPOINT_ID", (view.getId()-10000));
			intent.putExtra("ARPOINT_TITLE", "");
			
			this.context.startActivity(intent);
		}
		
	}

}

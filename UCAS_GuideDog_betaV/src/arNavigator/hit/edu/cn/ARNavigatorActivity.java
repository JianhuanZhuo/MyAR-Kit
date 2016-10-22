/**
 *这是实景导航主界面类 
 * 
 * @author JiaDebin
 * @version 1.0, 20/3/2012
 */

package arNavigator.hit.edu.cn;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import arToolClass.hit.edu.cn.ARLayout;
import arToolClass.hit.edu.cn.ARPoint;


public class ARNavigatorActivity extends Activity{
	
	public static final String TAG = "MyARDemo";
	
	private ARLayout arLayout;
	private WindowManager winManager;
	private Display display;
	private int width = 0;
	private int height = 0;
	
	private Context context;
	SensorManager sensorManager;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        context = getApplicationContext();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  
        //设置横屏,此时的宽和高正好与竖屏时相反
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置全屏
        
        winManager = getWindowManager();
        display = winManager.getDefaultDisplay();
        width = display.getWidth();    //1196
        height = display.getHeight();  //720
        
        arLayout = new ARLayout(context, width, height);
        
        setContentView(this.arLayout);
        
        Toast.makeText(context, "请将手机横屏握持，摄像头朝向周围，并确保开启了GPS定位，以便更准确地为您导航(此功能仅限在雁栖湖校区内使用)", Toast.LENGTH_LONG).show();
        addTestARPlaces();
    }

	private void addTestARPlaces() {
		// TODO Auto-generated method stub
		ARPoint point1 = new ARPoint();
		point1.setType(ARPoint.TYPE_SERVICE);
		point1.setName("校医院");
		point1.setId(1);
		point1.setDetailInfo("这是国科大雁栖湖校区的校医院。(图片待更新)");
		point1.location.setLatitude(40.41228805d);
		point1.location.setLongitude(116.67470519d);
		this.arLayout.addARPointDev(point1);
		
		ARPoint point2 = new ARPoint();
		point2.setType(ARPoint.TYPE_OTHERS);
		point2.setName("过街天桥");
		point2.setId(2);
		point2.setDetailInfo("这是国科大雁栖湖校区的过街天桥，通往东区。");
		point2.location.setLatitude(40.40845295d);
		point2.location.setLongitude(116.67543383d);
		this.arLayout.addARPointDev(point2);
		
		ARPoint point3 = new ARPoint();
		point3.setId(3);
		point3.setType(ARPoint.TYPE_OTHERS);
		point3.setName("国科大正门");
		point3.setDetailInfo("这是国科大雁栖湖校区的正门。");
		point3.location.setLatitude(40.40705807d);
		point3.location.setLongitude(116.67578562d);
		this.arLayout.addARPointDev(point3);
		
		ARPoint point4 = new ARPoint();
		point4.setId(4);
		point4.setType(ARPoint.TYPE_TECHING);
		point4.setName("国际会议中心");
		point4.setDetailInfo("这是国科大雁栖湖校区的国际会议中心。");
		point4.location.setLatitude(40.40653664d);
		point4.location.setLongitude(116.67448843d);
		this.arLayout.addARPointDev(point4);
		
		ARPoint point5 = new ARPoint();
		point5.setId(5);
		point5.setType(ARPoint.TYPE_SERVICE);
		point5.setName("图书馆");
		point5.setDetailInfo("这是国科大雁栖湖校区的图书馆。");
		point5.location.setLatitude(40.40723682d);
		point5.location.setLongitude(116.67348696d);
		this.arLayout.addARPointDev(point5);
		
		ARPoint point6 = new ARPoint();
		point6.setId(6);
		point6.setType(ARPoint.TYPE_TECHING);
		point6.setName("教一楼");
		point6.setDetailInfo("这是国科大雁栖湖校区的教一楼。");
		point6.location.setLatitude(40.40774187d);
		point6.location.setLongitude(116.67432473d);
		this.arLayout.addARPointDev(point6);
		
		ARPoint point7 = new ARPoint();
		point7.setId(7);
		point7.setType(ARPoint.TYPE_TECHING);
		point7.setName("学园一");
		point7.setDetailInfo("这是国科大雁栖湖校区的学园一。");
		point7.location.setLatitude(40.40982976d);
		point7.location.setLongitude(116.67396062d);
		this.arLayout.addARPointDev(point7);
		
		ARPoint point8 = new ARPoint();
		point8.setId(8);
		point8.setType(ARPoint.TYPE_TECHING);
		point8.setName("学园二");
		point8.setDetailInfo("这是国科大雁栖湖校区的学园二。");
		point8.location.setLatitude(40.40922069d);
		point8.location.setLongitude(116.67437377d);
		this.arLayout.addARPointDev(point8);
		
		ARPoint point9 = new ARPoint();
		point9.setId(9);
		point9.setType(ARPoint.TYPE_SERVICE);
		point9.setName("西A公寓");
		point9.setDetailInfo("这是国科大雁栖湖校区的西A公寓。");
		point9.location.setLatitude(40.41322514d);
		point9.location.setLongitude(116.67205785d);
		this.arLayout.addARPointDev(point9);
		
		ARPoint point10 = new ARPoint();
		point10.setId(10);
		point10.setType(ARPoint.TYPE_SERVICE);
		point10.setName("体育场");
		point10.setDetailInfo("这是国科大雁栖湖校区的体育场。(图片待更新)");
		point10.location.setLatitude(40.41456512d);
		point10.location.setLongitude(116.67107432d);
		this.arLayout.addARPointDev(point10);
		
		ARPoint point11 = new ARPoint();
		point11.setId(11);
		point11.setType(ARPoint.TYPE_SERVICE);
		point11.setName("一食堂");
		point11.setDetailInfo("这是国科大雁栖湖校区的一食堂，旁边是雁栖超市。");
		point11.location.setLatitude(40.41226248d);
		point11.location.setLongitude(116.67390212d);
		this.arLayout.addARPointDev(point11);
		
		ARPoint point12 = new ARPoint();
		point12.setId(12);
		point12.setType(ARPoint.TYPE_SERVICE);
		point12.setName("二食堂");
		point12.setDetailInfo("这是国科大雁栖湖校区的二食堂。");
		point12.location.setLatitude(40.41001601d);
		point12.location.setLongitude(116.67243009d);
		this.arLayout.addARPointDev(point12);
		
		ARPoint point13 = new ARPoint();
		point13.setId(13);
		point13.setType(ARPoint.TYPE_SERVICE);
		point13.setName("西B公寓");
		point9.setDetailInfo("  这是国科大雁栖湖校区的西B公寓。");
		point13.location.setLatitude(40.41247069d);
		point13.location.setLongitude(116.67267258d);
		this.arLayout.addARPointDev(point13);
		
		ARPoint point14 = new ARPoint();
		point14.setId(14);
		point14.setType(ARPoint.TYPE_SERVICE);
		point14.setName("西C公寓");
		point14.setDetailInfo("  这是国科大雁栖湖校区的西C公寓。");
		point14.location.setLatitude(40.41411027d);
		point14.location.setLongitude(116.67336702d);
		this.arLayout.addARPointDev(point14);
		
		ARPoint point15 = new ARPoint();
		point15.setId(15);
		point15.setType(ARPoint.TYPE_SERVICE);
		point15.setName("西D公寓");
		point15.setDetailInfo("  这是国科大雁栖湖校区的西D公寓。");
		point15.location.setLatitude(40.41318613d);
		point15.location.setLongitude(116.67400731d);
		this.arLayout.addARPointDev(point15);
	}
}

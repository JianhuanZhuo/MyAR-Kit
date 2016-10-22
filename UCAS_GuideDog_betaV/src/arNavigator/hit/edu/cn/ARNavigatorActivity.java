/**
 *����ʵ�������������� 
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
        //���ú���,��ʱ�Ŀ�͸�����������ʱ�෴
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //����ȫ��
        
        winManager = getWindowManager();
        display = winManager.getDefaultDisplay();
        width = display.getWidth();    //1196
        height = display.getHeight();  //720
        
        arLayout = new ARLayout(context, width, height);
        
        setContentView(this.arLayout);
        
        Toast.makeText(context, "�뽫�ֻ������ճ֣�����ͷ������Χ����ȷ��������GPS��λ���Ա��׼ȷ��Ϊ������(�˹��ܽ��������ܺ�У����ʹ��)", Toast.LENGTH_LONG).show();
        addTestARPlaces();
    }

	private void addTestARPlaces() {
		// TODO Auto-generated method stub
		ARPoint point1 = new ARPoint();
		point1.setType(ARPoint.TYPE_SERVICE);
		point1.setName("УҽԺ");
		point1.setId(1);
		point1.setDetailInfo("���ǹ��ƴ����ܺ�У����УҽԺ��(ͼƬ������)");
		point1.location.setLatitude(40.41228805d);
		point1.location.setLongitude(116.67470519d);
		this.arLayout.addARPointDev(point1);
		
		ARPoint point2 = new ARPoint();
		point2.setType(ARPoint.TYPE_OTHERS);
		point2.setName("��������");
		point2.setId(2);
		point2.setDetailInfo("���ǹ��ƴ����ܺ�У���Ĺ������ţ�ͨ��������");
		point2.location.setLatitude(40.40845295d);
		point2.location.setLongitude(116.67543383d);
		this.arLayout.addARPointDev(point2);
		
		ARPoint point3 = new ARPoint();
		point3.setId(3);
		point3.setType(ARPoint.TYPE_OTHERS);
		point3.setName("���ƴ�����");
		point3.setDetailInfo("���ǹ��ƴ����ܺ�У�������š�");
		point3.location.setLatitude(40.40705807d);
		point3.location.setLongitude(116.67578562d);
		this.arLayout.addARPointDev(point3);
		
		ARPoint point4 = new ARPoint();
		point4.setId(4);
		point4.setType(ARPoint.TYPE_TECHING);
		point4.setName("���ʻ�������");
		point4.setDetailInfo("���ǹ��ƴ����ܺ�У���Ĺ��ʻ������ġ�");
		point4.location.setLatitude(40.40653664d);
		point4.location.setLongitude(116.67448843d);
		this.arLayout.addARPointDev(point4);
		
		ARPoint point5 = new ARPoint();
		point5.setId(5);
		point5.setType(ARPoint.TYPE_SERVICE);
		point5.setName("ͼ���");
		point5.setDetailInfo("���ǹ��ƴ����ܺ�У����ͼ��ݡ�");
		point5.location.setLatitude(40.40723682d);
		point5.location.setLongitude(116.67348696d);
		this.arLayout.addARPointDev(point5);
		
		ARPoint point6 = new ARPoint();
		point6.setId(6);
		point6.setType(ARPoint.TYPE_TECHING);
		point6.setName("��һ¥");
		point6.setDetailInfo("���ǹ��ƴ����ܺ�У���Ľ�һ¥��");
		point6.location.setLatitude(40.40774187d);
		point6.location.setLongitude(116.67432473d);
		this.arLayout.addARPointDev(point6);
		
		ARPoint point7 = new ARPoint();
		point7.setId(7);
		point7.setType(ARPoint.TYPE_TECHING);
		point7.setName("ѧ԰һ");
		point7.setDetailInfo("���ǹ��ƴ����ܺ�У����ѧ԰һ��");
		point7.location.setLatitude(40.40982976d);
		point7.location.setLongitude(116.67396062d);
		this.arLayout.addARPointDev(point7);
		
		ARPoint point8 = new ARPoint();
		point8.setId(8);
		point8.setType(ARPoint.TYPE_TECHING);
		point8.setName("ѧ԰��");
		point8.setDetailInfo("���ǹ��ƴ����ܺ�У����ѧ԰����");
		point8.location.setLatitude(40.40922069d);
		point8.location.setLongitude(116.67437377d);
		this.arLayout.addARPointDev(point8);
		
		ARPoint point9 = new ARPoint();
		point9.setId(9);
		point9.setType(ARPoint.TYPE_SERVICE);
		point9.setName("��A��Ԣ");
		point9.setDetailInfo("���ǹ��ƴ����ܺ�У������A��Ԣ��");
		point9.location.setLatitude(40.41322514d);
		point9.location.setLongitude(116.67205785d);
		this.arLayout.addARPointDev(point9);
		
		ARPoint point10 = new ARPoint();
		point10.setId(10);
		point10.setType(ARPoint.TYPE_SERVICE);
		point10.setName("������");
		point10.setDetailInfo("���ǹ��ƴ����ܺ�У������������(ͼƬ������)");
		point10.location.setLatitude(40.41456512d);
		point10.location.setLongitude(116.67107432d);
		this.arLayout.addARPointDev(point10);
		
		ARPoint point11 = new ARPoint();
		point11.setId(11);
		point11.setType(ARPoint.TYPE_SERVICE);
		point11.setName("һʳ��");
		point11.setDetailInfo("���ǹ��ƴ����ܺ�У����һʳ�ã��Ա������ܳ��С�");
		point11.location.setLatitude(40.41226248d);
		point11.location.setLongitude(116.67390212d);
		this.arLayout.addARPointDev(point11);
		
		ARPoint point12 = new ARPoint();
		point12.setId(12);
		point12.setType(ARPoint.TYPE_SERVICE);
		point12.setName("��ʳ��");
		point12.setDetailInfo("���ǹ��ƴ����ܺ�У���Ķ�ʳ�á�");
		point12.location.setLatitude(40.41001601d);
		point12.location.setLongitude(116.67243009d);
		this.arLayout.addARPointDev(point12);
		
		ARPoint point13 = new ARPoint();
		point13.setId(13);
		point13.setType(ARPoint.TYPE_SERVICE);
		point13.setName("��B��Ԣ");
		point9.setDetailInfo("  ���ǹ��ƴ����ܺ�У������B��Ԣ��");
		point13.location.setLatitude(40.41247069d);
		point13.location.setLongitude(116.67267258d);
		this.arLayout.addARPointDev(point13);
		
		ARPoint point14 = new ARPoint();
		point14.setId(14);
		point14.setType(ARPoint.TYPE_SERVICE);
		point14.setName("��C��Ԣ");
		point14.setDetailInfo("  ���ǹ��ƴ����ܺ�У������C��Ԣ��");
		point14.location.setLatitude(40.41411027d);
		point14.location.setLongitude(116.67336702d);
		this.arLayout.addARPointDev(point14);
		
		ARPoint point15 = new ARPoint();
		point15.setId(15);
		point15.setType(ARPoint.TYPE_SERVICE);
		point15.setName("��D��Ԣ");
		point15.setDetailInfo("  ���ǹ��ƴ����ܺ�У������D��Ԣ��");
		point15.location.setLatitude(40.41318613d);
		point15.location.setLongitude(116.67400731d);
		this.arLayout.addARPointDev(point15);
	}
}

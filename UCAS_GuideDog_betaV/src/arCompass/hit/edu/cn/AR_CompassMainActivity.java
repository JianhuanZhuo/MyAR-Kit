package arCompass.hit.edu.cn;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import arToolClass.hit.edu.cn.ARLayoutForCompass;

public class AR_CompassMainActivity extends Activity {
	
	public static final String TAG = "MyARDemo";
	
	public static final int LANDSCAPE=2;
	public static final int PORTRAIT=1;
	
	public static int orientation=0;
	
	private ARLayoutForCompass arLayout;
	private WindowManager winManager;
	private Display display;
	private int width = 0;
	private int height = 0;
	
	private Context context;
	SensorManager sensorManager;
	
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		Log.v(TAG, "in main, width is "+width+", height is "+height);
	}

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
        if(width>height){
        	orientation=LANDSCAPE;
        }
        else{
        	orientation=PORTRAIT;
        }
        
        arLayout = new ARLayoutForCompass(context, width, height);
        
        setContentView(this.arLayout);

    }
    
}
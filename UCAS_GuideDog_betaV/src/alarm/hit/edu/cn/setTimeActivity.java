package alarm.hit.edu.cn;

import hit.edu.cn.R;

import java.util.Calendar;
import java.util.Date;

import alarm.toolview.hit.edu.cn.NumericWheelAdapter;
import alarm.toolview.hit.edu.cn.OnWheelChangedListener;
import alarm.toolview.hit.edu.cn.OnWheelScrollListener;
import alarm.toolview.hit.edu.cn.WheelView;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class setTimeActivity extends Activity implements OnClickListener{
	// Time changed flag
	private boolean timeChanged = false;
	private boolean timeScrolled = false;
	public static final String TAG="SET_TIME_ACTIVITY";
	
	private TimePicker picker;
	private Button btn_ok;
	private EditText et_thingdesc;
	
	private String thingdesc="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settime_activity_layout);
	
		btn_ok=(Button) findViewById(R.id.btn_settimeok);
		et_thingdesc=(EditText) findViewById(R.id.settime_thingdesc);
		btn_ok.setOnClickListener(this);
		
		final WheelView hours = (WheelView) findViewById(R.id.hour);
		hours.setAdapter(new NumericWheelAdapter(0, 23));
		hours.setLabel("时");
	
		final WheelView mins = (WheelView) findViewById(R.id.mins);
		mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		mins.setLabel("分");
		mins.setCyclic(true);
	
		picker = (TimePicker) findViewById(R.id.time);
		picker.setIs24HourView(true);
	
		// set current time
		Calendar c = Calendar.getInstance();
		int curHours = c.get(Calendar.HOUR_OF_DAY);
		int curMinutes = c.get(Calendar.MINUTE);
	
		hours.setCurrentItem(curHours);
		mins.setCurrentItem(curMinutes);
	
		picker.setCurrentHour(curHours);
		picker.setCurrentMinute(curMinutes);
	
		// add listeners
		addChangingListener(mins, "分");
		addChangingListener(hours, "时");
	
		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!timeScrolled) {
					timeChanged = true;
					picker.setCurrentHour(hours.getCurrentItem());
					picker.setCurrentMinute(mins.getCurrentItem());
					timeChanged = false;
				}
			}
		};

		hours.addChangingListener(wheelListener);
		mins.addChangingListener(wheelListener);

		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				timeScrolled = true;
			}
			public void onScrollingFinished(WheelView wheel) {
				timeScrolled = false;
				timeChanged = true;
				picker.setCurrentHour(hours.getCurrentItem());
				picker.setCurrentMinute(mins.getCurrentItem());
				timeChanged = false;
			}
		};
		
		hours.addScrollingListener(scrollListener);
		mins.addScrollingListener(scrollListener);
		
		picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			public void onTimeChanged(TimePicker  view, int hourOfDay, int minute) {
				if (!timeChanged) {
					hours.setCurrentItem(hourOfDay, true);
					mins.setCurrentItem(minute, true);
				}
			}
		});
	}

	/**
	 * Adds changing listener for wheel that updates the wheel label
	 * @param wheel the wheel
	 * @param label the wheel label
	 */
	private void addChangingListener(final WheelView wheel, final String label) {
		wheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				wheel.setLabel(label);
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_settimeok) {
			thingdesc = et_thingdesc.getText().toString();
			
			if(thingdesc.equals("")){
				Toast.makeText(getApplicationContext(), "您还未填写描述信息！", Toast.LENGTH_SHORT).show();
				return;
			}
			
			int hour_set = picker.getCurrentHour();
			int min_set = picker.getCurrentMinute();
			Log.v(TAG, "hour is "+hour_set+", min is "+min_set);
			
			if(hour_set==0&&min_set==0){
				Toast.makeText(getApplicationContext(), "您还未设置时间！", Toast.LENGTH_SHORT).show();
				return;
			}
			
			Date setDate = new Date();
			setDate.setHours(hour_set);
			setDate.setMinutes(min_set);
			
			Date curDate = new Date();
			Log.v(TAG, "setDate is "+setDate.getTime()+", curDate is "+curDate.getTime());
			if(setDate.getTime()<=curDate.getTime()){
				Toast.makeText(getApplicationContext(), "您设置的时间已过，请重试", Toast.LENGTH_SHORT).show();
				return;
			}
			
			AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(getBaseContext(), notification_service.class);
			intent.putExtra("thing_desc", this.thingdesc);
			
			PendingIntent pend = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
			alarm.set(AlarmManager.RTC_WAKEUP, setDate.getTime(), pend);   //计时
			Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
			
			this.finish();
		}
	}
}

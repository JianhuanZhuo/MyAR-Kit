/**
 *�����ṩ��ʱ���ܵĺ�̨������ 
 * 
 * @author JiaDebin
 * @version 1.0, 20/3/2012
 */

package alarm.hit.edu.cn;

import main.hit.edu.cn.main_activity;
import hit.edu.cn.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class notification_service extends Service{

	static final String TAG = "NOTIFICATION_SERVICE";
	Notification notification = null;
	NotificationManager nmanager = null;
	private static final int notification_id = 1234567;
	private String search_word="";
	private String thing_desc="";
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		nmanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}
		
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Bundle bundle=intent.getExtras();
		Log.v(TAG, "in onStart, search word is "+bundle.getString("search_word")+", desc is "+bundle.getString("thing_desc"));
		this.search_word=bundle.getString("search_word");
		this.thing_desc=bundle.getString("thing_desc");
		show();
//		this.stopSelf();   //�ŵ�show����
	}


	public void show() {
		// TODO Auto-generated method stub
		Intent next_intent = new Intent(getApplicationContext(), main_activity.class);
		Log.v(TAG, "search word is "+this.search_word+", desc is "+this.thing_desc);
		
		next_intent.putExtra("require_name", "notification");    //����������ֵ����
//		next_intent.putExtra("thing_desc", this.thing_desc); //������ֵ�����ԣ��ղ���,���ù㲥��
		
		PendingIntent pend = PendingIntent.getActivity(getApplicationContext(), 0, next_intent, 0);
		
		notification = new Notification(R.drawable.pop, "GuideDog����", System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		
		notification.ledARGB = 0xff00ff00; // LED�Ƶ���ɫ���̵�
		notification.ledOnMS = 300; // LED����ʾ�ĺ�������300����
		notification.ledOffMS = 1000; // LED�ƹرյĺ�������1000����
		notification.flags |= Notification.FLAG_SHOW_LIGHTS; // ������������־,����ע�������|=���������٣�������Ч
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(getApplicationContext(), "GuideDog����", "�����µ����ѣ�����鿴", pend);
		nmanager.notify(notification_id, notification);
		
		Intent broadcastIntent=new Intent();
		broadcastIntent.setAction("main_activity.myReceiver");
		broadcastIntent.putExtra("search_word", this.search_word);   //�ù㲥��ֵ����
		broadcastIntent.putExtra("thing_desc", this.thing_desc);
		sendBroadcast(broadcastIntent);
		
		this.stopSelf();  //ֹͣservice����,������ں�̨һֱ����
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

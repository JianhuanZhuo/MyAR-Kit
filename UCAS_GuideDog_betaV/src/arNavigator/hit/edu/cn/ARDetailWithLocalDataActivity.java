package arNavigator.hit.edu.cn;

import java.io.File;


import hit.edu.cn.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import arToolClass.hit.edu.cn.ARLayout;
import arToolClass.hit.edu.cn.ARPoint;

public class ARDetailWithLocalDataActivity extends Activity {

	private Intent intent;
	private int pointId;
	private ARPoint arpoint;
	private Gallery gallery;
	private TextView title;
	private TextView info;
	private ImageView imageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ardetailwithlocaldata);
		imageView=(ImageView) findViewById(R.id.imageView1);
		title=(TextView) findViewById(R.id.title);
		info=(TextView) findViewById(R.id.info);
		
		intent=this.getIntent();
		if(intent!=null){
			pointId=intent.getIntExtra("ARPOINT_ID", -1);
		}
		Log.v(ARNavigatorActivity.TAG, "in ardetail, point id is "+pointId);
		if(pointId!=-1){
			arpoint=ARLayout.getARPointById(pointId);
			if(arpoint!=null){
				title.setText(arpoint.getName());
				info.setText(arpoint.getDetailInfo());
				switch(pointId){
					case 1:
						imageView.setImageResource(R.drawable.p1);
						break;
					case 2:
						imageView.setImageResource(R.drawable.p2);
						break;
					case 3:
						imageView.setImageResource(R.drawable.p3);
						break;
					case 4:
						imageView.setImageResource(R.drawable.p4);
						break;
					case 5:
						imageView.setImageResource(R.drawable.p5);
						break;
					case 6:
						imageView.setImageResource(R.drawable.p6);
						break;
					case 7:
						imageView.setImageResource(R.drawable.p7);
						break;
					case 8:
						imageView.setImageResource(R.drawable.p8);
						break;
					case 9:
						imageView.setImageResource(R.drawable.p9);
						break;
					case 10:
						imageView.setImageResource(R.drawable.p10);
						break;
					case 11:
						imageView.setImageResource(R.drawable.p11);
						break;
					case 12:
						imageView.setImageResource(R.drawable.p12);
						break;
					
				}
				
			}
		}
		
		
	}
}

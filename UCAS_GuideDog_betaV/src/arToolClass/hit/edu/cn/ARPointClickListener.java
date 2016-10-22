package arToolClass.hit.edu.cn;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import arNavigator.hit.edu.cn.ARNavigatorActivity;

public class ARPointClickListener implements OnClickListener {

	private Context context;
	
	public ARPointClickListener(Context context) {
		super();
		this.context = context;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
//		Toast.makeText(context, "id is "+view.getId(), Toast.LENGTH_LONG).show();
		Log.v(ARNavigatorActivity.TAG, "in on click listener................id is "+view.getId());
	}

}

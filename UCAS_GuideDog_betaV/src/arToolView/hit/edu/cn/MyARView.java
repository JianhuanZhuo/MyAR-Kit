package arToolView.hit.edu.cn;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class MyARView extends View {

	private boolean Debug;
	private Paint paint;
	public float dirOfCamera;
	public float dirOfScreen;
	
	public MyARView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.paint = new Paint();
		
		this.dirOfCamera = 0f;
		this.dirOfScreen = 90;
	}

	public boolean isDebug() {
		return Debug;
	}

	public void setDebug(boolean debug) {
		Debug = debug;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		if(this.Debug){
			this.paint.setColor(Color.WHITE);
			canvas.drawText("Camera£º"+dirOfCamera, 20, 20, paint);
			canvas.drawText("Screen£º"+dirOfScreen, 220, 20, paint);
		}
	}
}

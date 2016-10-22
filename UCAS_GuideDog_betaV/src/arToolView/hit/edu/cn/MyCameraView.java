package arToolView.hit.edu.cn;

import java.io.IOException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyCameraView extends SurfaceView implements SurfaceHolder.Callback {

	private Context context;
	private Camera camera;
	private SurfaceHolder surHolder;
	
	public MyCameraView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.surHolder = this.getHolder();
		surHolder.addCallback(this);
		surHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
		this.camera = Camera.open();
		try {
			this.camera.setPreviewDisplay(this.surHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		this.camera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		this.camera.stopPreview();
		this.camera.release();
	}

}

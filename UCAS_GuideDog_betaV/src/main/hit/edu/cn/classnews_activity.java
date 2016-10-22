package main.hit.edu.cn;

import hit.edu.cn.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

public class classnews_activity extends Activity {
	
	private WebView webView;
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.classnews_activity);
		
		webView=(WebView) findViewById(R.id.webView1);
		intent=this.getIntent();
		
		WebSettings ws=webView.getSettings();
		ws.setSupportZoom(true);
		ws.setBuiltInZoomControls(true);   //只有网页宽度大于屏幕宽度时才显示
		
		webView.loadUrl(intent.getStringExtra("url"));
		
	}
	
}

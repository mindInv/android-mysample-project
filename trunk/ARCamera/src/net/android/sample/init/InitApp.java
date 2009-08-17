package net.android.sample.init;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InitApp extends Activity implements OnClickListener {

	private Button cameraBtn;
	private Button imageViewer;
	private Button wikitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		cameraBtn = (Button)findViewById(R.id.cameraMap);
		imageViewer = (Button)findViewById(R.id.imageViewer);
		wikitude = (Button)findViewById(R.id.wikitude);
		cameraBtn.setOnClickListener(this);
		imageViewer.setOnClickListener(this);
		wikitude.setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent intent = new Intent();
		if ( v == cameraBtn ) {
			intent.setClassName(this, "net.android.sample.cameramap.CameraMap");
			startActivity(intent);
		} else if ( v == imageViewer ) {
			intent.setClassName(this, "net.android.sample.imageviewer.ImageViewer");
			startActivity(intent);
		} else if ( v == wikitude ) {
			intent.setClassName(this, "net.android.sample.wikitude.WikitudeViewer");
			startActivity(intent);
		}
		finish();
	}
}

package net.android.sample.cameramap;

import android.widget.LinearLayout;

public class CameraMap extends CameraMapView {
	
	private CameraView mCamera = null;
	
    // カメラビューの生成
    protected void cameraViewBuilder(LinearLayout layout) {
        mCamera = new CameraView(this);
        mCamera.setLayoutParams(new LinearLayout.LayoutParams(280, 210));
        layout.addView(mCamera);
	}

	@Override
	protected int setZoomLevel() {
		return 17;
	}   
}

package net.android.sample.cameramap;

import net.android.sample.imageviewer.CameraMapView;
import android.view.Menu;
import android.view.MenuItem;
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
	
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return true;
	}
}

package net.android.sample.cameramap;

import net.android.sample.init.R;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class CameraMapView extends MapActivity {
	private MapView mMap = null;
	private MyLocationOverlay mMyLocationOverlay;
	private ImagePointOverlay mImagePointOverlay;
	private int now_lat;
	private int now_lng;
	private boolean mMyLocation = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //レイアウトの生成
        LinearLayout layout= new LinearLayout(this);
        layout.setBackgroundColor(Color.BLACK);
        setContentView(layout);

        // マップビューの生成
        mMap = new MapView(this, "0Y_VNXj7CxhvaJSe0hdIijkxS2G2_nEtWhFfNMA");

        mMap.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mMap.setBuiltInZoomControls(true);
        mMap.setClickable(true);
       
        // 現在地オーバーレイの生成
        mMyLocationOverlay = new MyLocationOverlay(this, mMap);
        mMyLocationOverlay.runOnFirstFix(new Runnable() { 
        	public void run() {
	        	mMap.getController().animateTo(mMyLocationOverlay.getMyLocation());
	    		mMyLocation = true;
        }});
        
        mMap.getOverlays().add(mMyLocationOverlay);
      
        // 画像オーバーレイの生成
        Drawable marker = getResources().getDrawable(R.drawable.red_pin);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
//        mImagePointOverlay = new ImagePointOverlay(marker, this);
        mMap.getOverlays().add(mImagePointOverlay);
        
        layout.addView(mMap);

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}

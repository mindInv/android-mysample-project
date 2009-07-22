package com.sample.location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

public class LocationBase extends MapActivity implements LocationListener {
	private static final String TAG = "LocationBase";
	// GPSの受信間隔の設定用
	private static final String MIN_TIME = "MIN_TIME";
	private static final String MIN_DISTANCE = "MIN_DISTANCE";
	// メニュー項目
	private static final int MENU_INTERVAL = 0;
	private static final int MENU_MYLOCATION = 1;
	
	private MapView mMap;
	private MyLocationOverlay mMyLocationOverlay;
	private LocationManager mLocManager;
	private SharedPreferences mPref;
	
	private boolean mMylocStatus = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		// プレファレンスの生成
		mPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		// Mapオブジェクトの取得
        mMap = (MapView)findViewById(R.id.mapview);
        // ズームコントロールを追加
        mMap.setBuiltInZoomControls(true);
        
        // 現在地オーバーレイの生成
        mMyLocationOverlay = new MyLocationOverlay(this, mMap);
        mMyLocationOverlay.runOnFirstFix(new Runnable() { 
        	public void run() {
        		mMap.getController().animateTo(mMyLocationOverlay.getMyLocation());
        }});
        
        mMap.getOverlays().add(mMyLocationOverlay);
    }

	@Override
	protected void onStart() {
		super.onStart();
		// ロケーションマネージャの取得
		mLocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// 現在の設定を読み出す(初回起動は"-")
		String minTimeText = mPref.getString(MIN_TIME, "-");
		String minDistanceText = mPref.getString(MIN_DISTANCE, "-");
	
		if ( minTimeText.equals("-") || minDistanceText.equals("-") ) {
			// 間隔設定がされていない場合は，設定用アクティビティを起動
			startActivity( new Intent(this, Setting.class) );
			return;
		}

		// GPSの受信を開始する
		mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Long.parseLong(minTimeText), Float.parseFloat(minDistanceText), this);
		
		// 現在地表示を有効にする
		mMyLocationOverlay.enableMyLocation();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 現在地表示の解除
		mMyLocationOverlay.disableMyLocation();
		// GPSの受信を解除する
		mLocManager.removeUpdates(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_MYLOCATION, 0, "現在地");
		menu.add(0, MENU_INTERVAL, 1, "更新間隔の変更");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch ( item.getItemId() ) {
			case MENU_MYLOCATION:
				mMylocStatus = true;
				return true;
			case MENU_INTERVAL:
				startActivity( new Intent(this, Setting.class) );
				return true;
			default:
		}
		
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mMylocStatus = false;
		return super.onTouchEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mMylocStatus = false;
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onLocationChanged(Location loc) {
		Toast.makeText(this, "Loc Changed", Toast.LENGTH_SHORT);
		if ( !mMylocStatus ) {
			return;
		}
		// 現在地を更新して中心に移動する
		mMap.getController().animateTo(mMyLocationOverlay.getMyLocation());
	}

	public void onProviderDisabled(String arg0) {
	}

	public void onProviderEnabled(String arg0) {
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}
}
package com.gclue.SampleAR;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;

public class SampleAR extends Activity implements SensorEventListener, LocationListener {
	// 富士山の緯度経度・高さ
	private static final double lat_fuji = 35.362176;
	private static final double lng_fuji = 138.730888;
	private static final double alt_fuji = 3776.0;
	
	private Preview mPreview;
	private MyView mMview;
	private SensorManager mSensorManager = null;
	private boolean mRegisterSensor = false;
	private LocationManager mLocationManager = null;
	private Location mFujiLoc;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // センサーマネージャの取得
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mRegisterSensor = false;
        
        // ロケーションマネージャの取得
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        // タイトルを消す
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // カメラビュー用のサーフェスを生成
        mPreview = new Preview(this);
        setContentView(mPreview);

        // 富士山の位置情報を作成
		mFujiLoc = new Location(LocationManager.GPS_PROVIDER);
		mFujiLoc.setLatitude(lat_fuji);
		mFujiLoc.setLongitude(lng_fuji);
		mFujiLoc.setAltitude(alt_fuji);
        
        mMview = new MyView(this);
        addContentView(mMview, new LayoutParams(LayoutParams.WRAP_CONTENT,
        										LayoutParams.WRAP_CONTENT));
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		
		if ( sensors.size() > 0 ) {
			Sensor sensor = sensors.get(0);
			mRegisterSensor = mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
		}
		
		// GPSサービスの登録
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if ( mRegisterSensor ) {
			mSensorManager.unregisterListener(this);
			mRegisterSensor = false;
		}
		
		mLocationManager.removeUpdates(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mSensorManager.unregisterListener(this);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		if ( event.sensor.getType() == Sensor.TYPE_ORIENTATION ) {
			mMview.setOrientationString(event.values[0], event.values[1], event.values[2]);
		}
	}

	public void onLocationChanged(Location location) {
		mMview.setLocationString("" + location.getLatitude(), "" + location.getLongitude());
		float distance = location.distanceTo(mFujiLoc);
		float bearing = location.bearingTo(mFujiLoc);
		mMview.setDistanceAndBearingString("" + distance / 1000, "" + bearing);
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}


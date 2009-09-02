package net.android.sample.cameramap;

import java.util.List;
import net.android.sample.imageviewer.PopImageListener;
import net.android.sample.init.R;
import net.android.sample.lib.Shortto;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.view.MotionEvent;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CameraMap extends MapActivity implements LocationListener, PopImageListener, SensorEventListener {
	
    private static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;      
    private Uri[] mUriIds;
    private String[] mTitles;
    
    // Map関連のオブジェクト
    private MapView mMap = null;
    private MyLocationOverlay mMyLocationOverlay;
    private ImagePointOverlay mImagePointOverlay;
    
    // 位置情報関連のオブジェクト
    private LocationManager mLocManager;
    private int now_lat;
    private int now_lng;
    private boolean mMyLocation = false;
    private boolean mIsMyLocation = false;

    // コンパスセンサ関連のオブジェクト
	private SensorManager mSensorManager;
	
    // カメラ関連のオブジェクト
    private CameraView mCamera = null;
	private CameraOverlayView mOverlay = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       
        //レイアウトの生成
        LinearLayout layout= new LinearLayout(this);
        layout.setBackgroundColor(Color.BLACK);
        setContentView(layout);

        // カメラビューの生成
        cameraViewBuilder(layout);
        
    	// ロケーションマネージャの取得
    	mLocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        // センサーマネージャの取得
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

    	// マップビューの生成
        mMap = new MapView(this, "0Y_VNXj7CxhvaJSe0hdIijkxS2G2_nEtWhFfNMA");

        mMap.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mMap.setBuiltInZoomControls(true);
        // ズームレベルの初期設定
        mMap.getController().setZoom(17);
        mMap.setClickable(true);
       
        // 現在地オーバーレイの生成
        mMyLocationOverlay = new MyLocationOverlay(this, mMap);
        mMyLocationOverlay.runOnFirstFix(new Runnable() { 
                public void run() {
                        mMap.getController().animateTo(mMyLocationOverlay.getMyLocation());
        }});
        
        mMap.getOverlays().add(mMyLocationOverlay);
      
        // 画像オーバーレイの生成
        Drawable marker = getResources().getDrawable(R.drawable.red_pin);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        mImagePointOverlay = new ImagePointOverlay(marker, this);
        mMap.getOverlays().add(mImagePointOverlay);
        
        layout.addView(mMap);        

        // 地図上の画像の位置を取得する
        getImageMaps();
    }
	
    // カメラビューの生成
    protected void cameraViewBuilder(LinearLayout layout) {
        mCamera = new CameraView(this);
        mCamera.setLayoutParams(new LinearLayout.LayoutParams(280, 210));
        layout.addView(mCamera);
        
        mOverlay = new CameraOverlayView(this);
        addContentView(mOverlay, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
	}

	public GeoPoint getLocation() {
        return new GeoPoint( now_lat, now_lng );
    }

    @Override
    protected void onResume() {
        super.onResume();
        // GPSの受信を開始する
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        // センサーの受信を開始する
		List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		if ( sensors.size() > 0 ) {
			Sensor sensor = sensors.get(0);
			mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
		}
        
        // 現在地表示を有効にする
        mMyLocationOverlay.enableMyLocation();    
    }	

    @Override
    protected void onPause() {
    	super.onPause();
    	// 現在地表示を無効にする
        mMyLocationOverlay.disableMyLocation();
        mLocManager.removeUpdates(this);
        mSensorManager.unregisterListener(this);
    }
    
    /**
     * Image Mapを追加
     * 
     * @param lat
     * @param lng
     */
    public void addImagePoint(int lat, int lng) {
        GeoPoint point = new GeoPoint(lat, lng);
        OverlayItem item = new OverlayItem(point, "PICT", "");
        mImagePointOverlay.addPoint(item);
        mMap.invalidate();
    }
    
    public void getImageMaps() {
        final String[] col = { "_id", Media.DISPLAY_NAME, Media.DESCRIPTION, Media.MIME_TYPE, Media.LATITUDE, Media.LONGITUDE };
        Cursor cursor = managedQuery(IMAGE_URI, col, Media.DESCRIPTION + "='Image Map'", null, "_id" + " DESC");
        mUriIds = new Uri[cursor.getCount()];
        mTitles = new String[cursor.getCount()];
        int i = 0;
        while ( cursor.moveToNext() ) {                      
            int lat = (int)( cursor.getDouble(cursor.getColumnIndex(Media.LATITUDE)) );
            int lng = (int)( cursor.getDouble(cursor.getColumnIndex(Media.LONGITUDE)) );
            mUriIds[i] = Uri.parse(IMAGE_URI.toString() + "/" + cursor.getString(cursor.getColumnIndex("_id")));
            mTitles[i++] = cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME));
            addImagePoint(lat, lng);
        }
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mMyLocation = false;
        return super.dispatchTouchEvent(ev);
    }

    public void onLocationChanged(Location location) {
    	// 現在地を更新
    	now_lat = (int) (location.getLatitude()*1E6);
    	now_lng = (int) (location.getLongitude()*1E6);

		mOverlay.setLocationString(now_lat, now_lng);

		if ( mMyLocation == false ) {
    		return;
        }
        // 現在地の確定
        mIsMyLocation = true;
    	mMyLocationOverlay.getMyLocation();
        // 現在地を更新して中心に移動する
        mMap.getController().animateTo(new GeoPoint(now_lat, now_lng));
    }

	public void onProviderDisabled(String arg0) {
	}

	public void onProviderEnabled(String arg0) {
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

	public void popImageView(int index) {
	    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);        
	    ImageView image = new ImageView(this);
	    image.setImageURI(mUriIds[index]);
	    
	    // タイトルを設定
	    alertDialogBuilder.setTitle(mTitles[index]);
 	    // Imageを表示
	    alertDialogBuilder.setView(image);
	    // ダイアログを表示
	    alertDialogBuilder.create().show();  
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void onSensorChanged(SensorEvent event) {
		if ( event.sensor.getType() == Sensor.TYPE_ORIENTATION ) {
			mOverlay.setOrientationString(event.values[0], event.values[1], event.values[2]);
		}
	}
	
	public void sendTwitter(int lat, int lng) {
		String url = "http://maps.google.com/maps?q=" + (float)lat/1E6 + "," + (float)lng/1E6;
		Shortto.getShortUrl(url);
        
		// Twidroidにメッセージを送信
		Intent intent = new Intent("com.twidroid.SendTweet");
        intent.putExtra("com.twidroid.extra.MESSAGE", "Taken photo at " + Shortto.getShortUrl(url) + " photo ");
        try {
        	startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
        	/* Handle Exception if Twidroid is not installed */
        	Toast.makeText(this, "Twidroid Application not found.", Toast.LENGTH_LONG);
        }
	}	
}

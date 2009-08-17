package net.android.sample.cameramap;

import net.android.sample.init.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CameraMap extends MapActivity implements LocationListener {
	private static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;	
	private static final int DRAW_LOCATION = 0;
	private CameraView mCamera = null;
	private MapView mMap = null;
	private LocationManager mLocManager;
	private MyLocationOverlay mMyLocationOverlay;
	private ImagePointOverlay mImagePointOverlay;
	private int now_lat;
	private int now_lng;
	private boolean mMyLocation = false;
	private Uri[] mUriIds;
	private String[] mTitles;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       
        //レイアウトの生成
        LinearLayout layout= new LinearLayout(this);
        layout.setBackgroundColor(Color.BLACK);
        setContentView(layout);
        
        // カメラビューの生成
        mCamera = new CameraView(this);
        mCamera.setLayoutParams(new LinearLayout.LayoutParams(280, 210));
        layout.addView(mCamera);
        
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
        mImagePointOverlay = new ImagePointOverlay(marker, this);
        mMap.getOverlays().add(mImagePointOverlay);
        
        layout.addView(mMap);
	}

	public GeoPoint getLocation() {
		return new GeoPoint( now_lat, now_lng );
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
        // GPSの受信を開始する
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        
        // 現在地表示を有効にする
        mMyLocationOverlay.enableMyLocation();
        
        // 地図上の画像の位置を取得する
        getImageMaps();
    }
	
	@Override
	protected void onPause() {
		super.onPause();
        // 現在地表示を無効にする
		mMyLocationOverlay.disableMyLocation();
		mLocManager.removeUpdates(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		System.exit(0);
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
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

        if ( mMyLocation == false ) {
			return;
		}
		mMyLocationOverlay.getMyLocation();
        // 現在地を更新して中心に移動する
        mMap.getController().animateTo(new GeoPoint(now_lat, now_lng));
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
/*			
			Log.d("Image", "IMG "+ cursor.getString(cursor.getColumnIndex("_id")));			
			Log.d("Image", "IMG "+ cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME)));
			Log.d("Image", "IMG "+ cursor.getString(cursor.getColumnIndex(Media.DESCRIPTION)));
			Log.d("Image", "IMG "+ cursor.getString(cursor.getColumnIndex(Media.MIME_TYPE)));
			Log.d("Image", "IMG "+ cursor.getDouble(cursor.getColumnIndex(Media.LATITUDE)));
			Log.d("Image", "IMG "+ cursor.getDouble(cursor.getColumnIndex(Media.LONGITUDE)));
*/			
			int lat = (int)( cursor.getDouble(cursor.getColumnIndex(Media.LATITUDE)) );
			int lng = (int)( cursor.getDouble(cursor.getColumnIndex(Media.LONGITUDE)) );
			mUriIds[i] = Uri.parse(IMAGE_URI.toString() + "/" + cursor.getString(cursor.getColumnIndex("_id")));
			mTitles[i++] = cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME));
			addImagePoint(lat, lng);
		}
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
}
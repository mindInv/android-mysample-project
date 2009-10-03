package net.android.sample.imageviewer;

import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.android.sample.cameramap.ImagePointOverlay;
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
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CameraMapView extends MapActivity implements LocationListener, PopImageListener {
	
	private static final int MYLOCATION_ID = 0;
	private static final int IMAGEVIEW_ID = 1;
    private static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;      

    private MapView mMap = null;
    private LocationManager mLocManager;
    private MyLocationOverlay mMyLocationOverlay;
    private ImagePointOverlay mImagePointOverlay;
    private int now_lat;
    private int now_lng;
    private boolean mMyLocation = false;
    private boolean mIsMyLocation = false;
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
        
    	// ロケーションマネージャの取得
    	mLocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

    	// マップビューの生成
        mMap = new MapView(this, "0Y_VNXj7CxhvaJSe0hdIijkxS2G2_nEtWhFfNMA");

        mMap.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        mMap.setBuiltInZoomControls(true);
        // ズームレベルの初期設定
        mMap.getController().setZoom(15);
        mMap.setClickable(true);
       
        // 現在地オーバーレイの生成
        mMyLocationOverlay = new MyLocationOverlay(this, mMap);
        mMyLocationOverlay.runOnFirstFix(new Runnable() { 
                public void run() {
                        mMap.getController().animateTo(mMyLocationOverlay.getMyLocation());
        }});
        
        mMap.getOverlays().add(mMyLocationOverlay);
      
        // 画像オーバーレイの生成
        Drawable marker = getResources().getDrawable(R.drawable.blupushpin);
        marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
        mImagePointOverlay = new ImagePointOverlay(marker, this);
        mMap.getOverlays().add(mImagePointOverlay);
        
        layout.addView(mMap);        

        // 地図上の画像の位置を取得する
        getImageMaps();
    }

	public GeoPoint getLocation() {
        return new GeoPoint( now_lat, now_lng );
    }

    @Override
    protected void onResume() {
        super.onResume();
        // GPSの受信を開始する
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    
        // 現在地表示を有効にする
        mMyLocationOverlay.enableMyLocation();    
    }

    @Override
	public void onNewIntent(Intent intent) {
        String action = intent.getAction();
        if ( Intent.ACTION_VIEW.equals(action) ) {
        	String data = intent.getData().getSchemeSpecificPart();
        	StringTokenizer st = new StringTokenizer(data, ",");
        	float lat = Float.parseFloat(st.nextToken());
        	float lng = Float.parseFloat(st.nextToken());
        	mIsMyLocation = false;
        	mMyLocation = false;
            mMyLocationOverlay.getMyLocation();
            // 受け取った場所を中心に移動する
            mMap.getController().animateTo(new GeoPoint((int) (lat*1E6), (int) (lng*1E6)));
        }
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	// 現在地表示を無効にする
        mMyLocationOverlay.disableMyLocation();
        mLocManager.removeUpdates(this);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MYLOCATION_ID, 0, "My Location");
    	menu.add(0, IMAGEVIEW_ID, 0, "ImageView");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch ( item.getItemId() ) {
			case MYLOCATION_ID:
				// 現在地が確定していれば，現在地に張り付くように
				if ( mIsMyLocation ) {
					mMyLocation = true;
			        mMap.getController().animateTo(new GeoPoint(now_lat, now_lng));
				} else {
					Toast.makeText(this, "現在地を取得中...", Toast.LENGTH_SHORT).show();
				}
				break;
			case IMAGEVIEW_ID:
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClassName(this, "net.android.sample.imageviewer.ImageViewer");
				startActivity(intent);
				break;
		}
		return true;
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

    	if ( mMyLocation == false ) {
    		return;
        }
        // 現在地の確定
        mIsMyLocation = true;
    	mMyLocationOverlay.getMyLocation();
        // 現在地を更新して中心に移動する
        mMap.getController().animateTo(new GeoPoint(now_lat, now_lng));
    }

    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
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
  
	// Twitterへ投げるためのオブジェクト
	ExecutorService mExecutor = Executors.newFixedThreadPool(1);

	public void popImageView(int index, final int lat, final int lng) {
	    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);        
	    ImageView image = new ImageView(this);
	    image.setImageURI(mUriIds[index]);
	    image.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendTwitter(lat, lng);
				Toast.makeText(CameraMapView.this, "Twidroid起動中", Toast.LENGTH_SHORT).show();
			}
	    });
	    // タイトルを設定
	    alertDialogBuilder.setTitle(mTitles[index]);
 	    // Imageを表示
	    alertDialogBuilder.setView(image);
	    // ダイアログを表示
	    alertDialogBuilder.create().show();     
    
    }
    
	public void sendTwitter(int lat, int lng) {
		mExecutor.execute(new SendTwitterTask(lat, lng));
	}	

	private class SendTwitterTask implements Runnable {
		private String url;
		
		SendTwitterTask(int lat, int lng) {
			this.url = "http://maps.google.com/maps?q=" + (float)lat/1E6 + "," + (float)lng/1E6;
		}
		
		public void run() {
			// URL Short
			Shortto.getShortUrl(url);
			// Twidroidにメッセージを送信
			Intent intent = new Intent("com.twidroid.SendTweet");
	        intent.putExtra("com.twidroid.extra.MESSAGE", " L: " + Shortto.getShortUrl(url) + " photo ");
	        try {
	        	startActivityForResult(intent, 1);
	        } catch (ActivityNotFoundException e) {
	        	/* Handle Exception if Twidroid is not installed */
	        	Toast.makeText(CameraMapView.this, "Twidroid Application not found.", Toast.LENGTH_LONG);
	        }
		}
	}
}
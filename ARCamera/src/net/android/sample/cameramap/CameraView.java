package net.android.sample.cameramap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import com.google.android.maps.GeoPoint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class CameraView extends SurfaceView implements Callback {

	private SurfaceHolder holder;
	private Camera camera;
	private CameraMap ar;
	private static ContentResolver contentResolver = null;
	private int lat = 0;
	private int lng = 0;
	
	// コンストラクタ
	public CameraView(CameraMap context) {
		super(context);
		
		ar = context;
		
		// サーフェスホルダーの生成
		holder = getHolder();
		holder.addCallback(this);
		holder.setFixedSize(getWidth(), getHeight());
		
		// プッシュバッファの指定
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		contentResolver = context.getContentResolver();
	}

	// サーフェス変更イベントの処理
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// カメラのプレビュー開始
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(width, height);
		parameters.setPictureSize(800, 600); // WVGA Size
		camera.setParameters(parameters);
		camera.startPreview();
	}

	// サーフェス生成イベントの処理
	public void surfaceCreated(SurfaceHolder holder) {
		// カメラの初期化
		try {
			camera = Camera.open();
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// サーフェス解放イベントの処理
	public void surfaceDestroyed(SurfaceHolder holder) {
		// カメラのプレビュー停止
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	// タッチイベントの処理
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
			GeoPoint geo = ar.getLocation();
			lat = geo.getLatitudeE6();
			lng = geo.getLongitudeE6();
			takePicture();
			// 現在地にポイントを追加
			ar.addImagePoint(lat, lng);
			// Twitterに送る
//			ar.sendTwitter(lat, lng); なぜか1.6で動かなくなった
			// カメラを再スタート
			camera.startPreview();
		}
		return true;
	}

	private void takePicture() {
		// カメラのスクリーンショットの取得
		camera.takePicture(null, null, new Camera.PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				try {
					String dataName = "map_" + String.valueOf(Calendar.getInstance().getTime()) + ".jpg";
					saveDataToSdCard(data, dataName);
				} catch (Exception e) {
					camera.release();
					e.printStackTrace();
				}
			}
		});
	}

	// 画像をSDCARDに保存する
	protected void saveDataToSdCard(byte[] data, String dataName) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		ContentValues values = new ContentValues();
		values.put(Media.DISPLAY_NAME, dataName);
		values.put(Media.DESCRIPTION, "Image Map");
		values.put(Media.MIME_TYPE, "image/jpeg");
		values.put(Media.LATITUDE, (double)lat);
		values.put(Media.LONGITUDE, (double)lng);
		// イメージのContent URI
		Uri uri = contentResolver.insert(Media.EXTERNAL_CONTENT_URI, values);
		try {
			OutputStream outStream = contentResolver.openOutputStream(uri);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
			outStream.close();
		} catch (Exception e) {
			camera.release();
			e.printStackTrace();
		}
	}
}

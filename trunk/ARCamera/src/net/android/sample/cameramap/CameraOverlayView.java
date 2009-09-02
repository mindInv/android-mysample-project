package net.android.sample.cameramap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class CameraOverlayView extends View {
	
	private float mXaxis = 0;
	private float mYaxis = 0;
	private float mZaxis = 0;
	private int mLat = 0;
	private int mLng = 0;
	
	public CameraOverlayView(Context context) {
		super(context);
		setFocusable(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	
		// 背景色
		canvas.drawColor(Color.TRANSPARENT);
		// 文字の色を設定
		Paint textPaint = new Paint();
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setAntiAlias(true);
		textPaint.setARGB(255, 255, 255, 0);		
		textPaint.setTextSize(25);
	      
		String orientation = new String();

		if ( mXaxis > 67.5 && mXaxis <= 112.5 ) {
			orientation = "南";   // 90
		} else if ( mXaxis > 112.5 && mXaxis <= 157.5 ) {
			orientation = "南西";  // 135			
		} else if ( mXaxis > 157.5 && mXaxis <= 202.5 ) {
			orientation = "西";   // 180
		} else if ( mXaxis > 202.5 && mXaxis <= 247.5 ) {
			orientation = "北西";  // 225			
		} else if ( mXaxis > 247.5 && mXaxis <= 292.5 ) {
			orientation = "北";   // 270
		} else if ( mXaxis > 292.5 && mXaxis <= 337.5 ) {
			orientation = "北東";  // 315			
		} else if ( ( mXaxis > 337.5 && mXaxis < 360 ) || ( mXaxis >= 0 && mXaxis <= 22.5 ) ){
			orientation = "東";   // 0
		} else if ( mXaxis > 22.5 && mXaxis <= 67.5 ) {
			orientation = "南東";  // 45			
		}
				
		canvas.drawText(orientation, 120, 200, textPaint);

		String location = "位置未取得";
		if ( mLat != 0 || mLng != 0 ) {
			location = "lat:" + (float)mLat/1E6 + ", lng:" + (float)mLng/1E6;
		}
		textPaint.setTextSize(12);
		canvas.drawText(location, 20, 240, textPaint);
	}
	
	// 方角の描画
	public void setOrientationString(float new_x, float new_y, float new_z) {
		// x, y, zの値の設定
		mXaxis = new_x;
		mYaxis = new_y;
		mZaxis = new_z;
		
		// 再描画
		invalidate();
	}
	
	// 位置情報の描画
	public void setLocationString(int lat, int lng) {
		// 位置情報の設定
		mLat = lat;
		mLng = lng;
		
		// 再描画
		invalidate();
	}
}

package com.gclue.SampleAR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public class MyView extends View {

	private int x;
	private int y;
	private float ori_x;
	private float ori_y;
	private float ori_z;
	private String latString;
	private String lngString;
	private String distanceString;
	private String bearingString;
	
	public MyView(Context context) {
		super(context);

		setFocusable(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	
		// 背景色
		canvas.drawColor(Color.TRANSPARENT);
		
		// 描画するための線の色
		Paint mainPaint = new Paint();
		mainPaint.setStyle(Paint.Style.FILL);
		mainPaint.setARGB(255, 255, 255, 100);
		
		// 線で描画
		canvas.drawLine(x, y, 50, 50, mainPaint);
		
		// 文字の色を設定
		Paint textPaint = new Paint();
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setARGB(255, 255, 0, 0);
		
		// 文字描画
		canvas.drawText("x " + ori_x, 20, 20, textPaint);
		canvas.drawText("y " + ori_y, 20, 40, textPaint);
		canvas.drawText("z " + ori_z, 20, 60, textPaint);
	     
		String status;		
		if ((ori_y > -30 && ori_y < 30) &&  ( ori_z > - 30 && ori_z < 30)) {
			status = "床";
		} else if(( ori_y > 150 || ori_y < -150 ) &&  ( ori_z > - 30 && ori_z < 30)){
			status = "天井";
		} else {
        	status = "景色";
        }
		// 仰角を表示
		canvas.drawText(status, 20, 80, textPaint);

		// ターゲットとの距離と方角
		canvas.drawText("富士山まで " + distanceString + " km", 20, 100, textPaint);
		// Bearingは動作を検証していない・・・
		canvas.drawText("富士山の方角 " + bearingString, 20, 120, textPaint);
	      
		String orientation = new String();

		if ( ori_x > 67.5 && ori_x <= 112.5 ) {
			orientation = "南";   // 90
		} else if ( ori_x > 112.5 && ori_x <= 157.5 ) {
			orientation = "南西";  // 135			
		} else if ( ori_x > 157.5 && ori_x <= 202.5 ) {
			orientation = "西";   // 180
		} else if ( ori_x > 202.5 && ori_x <= 247.5 ) {
			orientation = "北西";  // 225			
		} else if ( ori_x > 247.5 && ori_x <= 292.5 ) {
			orientation = "北";   // 270
		} else if ( ori_x > 292.5 && ori_x <= 337.5 ) {
			orientation = "北東";  // 315			
		} else if ( ( ori_x > 337.5 && ori_x < 360 ) || ( ori_x >= 0 && ori_x <= 22.5 ) ){
			orientation = "東";   // 0
		} else if ( ori_x > 22.5 && ori_x <= 67.5 ) {
			orientation = "南東";  // 45			
		}
		
		// 文字の色を設定
		Paint oriPaint = new Paint();
		oriPaint.setStyle(Paint.Style.FILL);
		oriPaint.setAntiAlias(true);
		oriPaint.setARGB(255, 255, 255, 0);
		
		oriPaint.setTextSize(50);
		canvas.drawText(orientation, 80, 80, oriPaint);

		// 文字の色を設定
		Paint locPaint = new Paint();
		locPaint.setStyle(Paint.Style.FILL);
		locPaint.setAntiAlias(true);
		locPaint.setARGB(255, 0, 0, 255);
		
		locPaint.setTextSize(20);
		canvas.drawText("LAT: " + latString + ", LNG: " + lngString, 100, 200, locPaint);
	}

	// タッチイベント
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// X, Y座標の取得
		x = (int)event.getX();
		y = (int)event.getY();
		// 再描画
		invalidate();
		
		return true;
	}
	
	// 方角の描画
	public void setOrientationString(float new_x, float new_y, float new_z) {
		
		// x, y, zの値の設定
		ori_x = new_x;
		ori_y = new_y;
		ori_z = new_z;
		
		// 再描画
		invalidate();
	}

	// 位置情報の描画
	public void setLocationString(String latitude, String longitude) {
		latString = latitude;
		lngString = longitude;
		
		// 再描画
		invalidate();
	}
	
	// ターゲットとの距離と方角の描画
	public void setDistanceAndBearingString(String distance, String bearing) {
		distanceString = distance;
		bearingString = bearing;
		
		// 再描画
		invalidate();
	}
}

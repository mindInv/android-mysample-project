package net.android.sample.cameramap;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class ImagePointOverlay extends ItemizedOverlay<OverlayItem> {

	private static final int UNKOWN_INDEX = -1;
	private List<OverlayItem> items = new ArrayList<OverlayItem>();
	private OverlayItem selectedItem;
	private CameraMap mCameraMap;
	private int mSelectedIndex = UNKOWN_INDEX;
	
	public ImagePointOverlay(Drawable defaultMarker, CameraMap context) {
		super(defaultMarker);
		
		this.mCameraMap = context;
		
		populate();
	}

	public void addPoint(OverlayItem item) {
		items.add(item);
		populate();
	}
	
	public void clear() {
		items.clear();
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return items.get(i);
	}

	@Override
	public int size() {
		return items.size();
	}

	@Override
	protected boolean onTap(int index) {
		mSelectedIndex = index;
		return false;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		
		if ( !shadow && mSelectedIndex != UNKOWN_INDEX ) {
			mCameraMap.popImageView(mSelectedIndex);
			mSelectedIndex = UNKOWN_INDEX;
		}
	}	
}

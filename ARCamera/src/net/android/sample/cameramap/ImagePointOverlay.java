package net.android.sample.cameramap;

import java.util.ArrayList;
import java.util.List;
import net.android.sample.imageviewer.PopImageListener;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class ImagePointOverlay extends ItemizedOverlay<OverlayItem> {

	private static final int UNKOWN_INDEX = -1;
	private List<OverlayItem> items = new ArrayList<OverlayItem>();
	private OverlayItem selectedItem;
	private PopImageListener listener;
	private int mSelectedIndex = UNKOWN_INDEX;
	
	public ImagePointOverlay(Drawable defaultMarker, PopImageListener listener) {
		super(boundCenterBottom(defaultMarker));
		
		this.listener = listener;
		
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

	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		
		if ( !shadow && mSelectedIndex != UNKOWN_INDEX ) {
			int lat = items.get(mSelectedIndex).getPoint().getLatitudeE6();
			int lng = items.get(mSelectedIndex).getPoint().getLongitudeE6();
			listener.popImageView(mSelectedIndex, lat, lng);
			mSelectedIndex = UNKOWN_INDEX;
		}
	}
}
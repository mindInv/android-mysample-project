package net.android.sample.wikitude;

import java.util.ArrayList;
import java.util.Collection;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import org.openintents.intents.WikitudeARIntent;
import org.openintents.intents.WikitudePOI;

public class WikitudeViewer extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create the basic intent
		WikitudeARIntent intent = prepareIntent();

		// Optionally add a title
		intent.addTitleText("View By Wikitude");

		// And launch the intent
		startActivity(intent);
	}
	
	/**
	 * prepares a Wikitude AR Intent (e.g. adds the POIs to the view)
	 * 
	 * @return the prepared intent
	 */
	private WikitudeARIntent prepareIntent() {
		// create the intent
		WikitudeARIntent intent = new WikitudeARIntent(this.getApplication(), null, null);
		// add the POIs
		this.addPois(intent);
		return intent;
	}
	
	/**
	 * adds hard-coded POIs to the intent
	 * 
	 * @param intent
	 *            the intent
	 */
	private void addPois(WikitudeARIntent intent) {
		
		final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		final String[] col = { "_id", Media.DISPLAY_NAME, Media.DESCRIPTION, Media.MIME_TYPE, Media.LATITUDE, Media.LONGITUDE };
		Cursor cursor = managedQuery(IMAGE_URI, col, Media.DESCRIPTION + "='Image Map'", null, "_id" + " DESC");
		int i = 0;
		WikitudePOI[] wikitudePoi = new WikitudePOI[cursor.getColumnCount()]; 
		Collection<WikitudePOI> pois = new ArrayList<WikitudePOI>();
		while ( cursor.moveToNext() ) {

			float lat = (float)( cursor.getDouble(cursor.getColumnIndex(Media.LATITUDE))/1E6 );
			float lng = (float)( cursor.getDouble(cursor.getColumnIndex(Media.LONGITUDE))/1E6 );

			wikitudePoi[i] = new WikitudePOI(lat, lng, 0,
											 cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME)),
											 "");
			pois.add(wikitudePoi[i]);
		}
		intent.addPOIs(pois);
	}	
}

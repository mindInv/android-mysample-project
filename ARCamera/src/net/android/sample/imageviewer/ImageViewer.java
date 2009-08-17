package net.android.sample.imageviewer;

import net.android.sample.init.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ViewSwitcher.ViewFactory;

public class ImageViewer extends Activity implements OnItemSelectedListener, ViewFactory {
	
	private static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	private static final Uri IMAGE_THUM = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;

    private ImageSwitcher mSwitcher;
    private Cursor mCursor;
	private int mPosition = 0;
	private Uri[] mUriIds;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.viewer);

        mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
        mSwitcher.setFactory(this);
        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));
        mSwitcher.setClickable(true);
	}
	
    public void onItemSelected(AdapterView parent, View v, int position, long id) {
        mSwitcher.setImageURI(mUriIds[position]);    	
        mPosition = position;
    }

    public void onNothingSelected(AdapterView parent) {
    }

    public View makeView() {
        ImageView i = new ImageView(this);
        i.setBackgroundColor(0xFF000000);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        i.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.i("Image", "ID = " + mPosition);
				mCursor.moveToPosition(mPosition);
				int lat = (int)( mCursor.getDouble(mCursor.getColumnIndex(Media.LATITUDE)) );
				int lng = (int)( mCursor.getDouble(mCursor.getColumnIndex(Media.LONGITUDE)) );
				Toast.makeText(ImageViewer.this, "LOC: " + lat + ", " + lng, Toast.LENGTH_LONG).show();
				
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + lat/1E6 + "," + lng/1E6));
				startActivity(intent);
			}        	
        });
        i.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
		        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ImageViewer.this);
		        final EditText editor = new EditText(ImageViewer.this);
		        // タイトルを設定
		        alertDialogBuilder.setTitle("ファイル名の変更");
		        // ファイル名の変更
		        mCursor.moveToPosition(mPosition);
		        editor.setText(mCursor.getString(mCursor.getColumnIndex(Media.DISPLAY_NAME)));
		        alertDialogBuilder.setView(editor);
		        // OKボタンとリスナを設定
		        alertDialogBuilder.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						// ファイル名の書き換え
						String newName = editor.getText().toString();
						String uriNumber = mCursor.getString(mCursor.getColumnIndex("_id"));
						ContentValues value = new ContentValues();
						value.put(Media.DISPLAY_NAME, newName);
						getContentResolver().update(Uri.withAppendedPath(IMAGE_URI, uriNumber), value, null, null);
				        mCursor.requery();
					}
		        });
		        // Cancelボタンとリスナを設定
		        alertDialogBuilder.setNegativeButton("CANCEL", new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
		        });
		        // ダイアログを表示
		        alertDialogBuilder.create().show(); 	
				return true;
			}
        });
        
        return i;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        
        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
        	return mCursor.getCount();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);

            i.setImageURI(mUriIds[position]);
			i.setScaleType(ImageView.ScaleType.FIT_XY);
            i.setAdjustViewBounds(true);
            i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            i.setBackgroundResource(R.drawable.picture_frame);
            return i;
        }
    }

    @Override
	protected void onStart() {
		super.onStart();

		final String[] col = { "_id", Media.DISPLAY_NAME, Media.DESCRIPTION, Media.MIME_TYPE, Media.LATITUDE, Media.LONGITUDE };
		mCursor = managedQuery(IMAGE_URI, col, Media.DESCRIPTION + "='Image Map'", null, "_id" + " DESC");
		mUriIds = new Uri[mCursor.getCount()];
		int i = 0;
		while ( mCursor.moveToNext() ) {
/*
 			Log.d("Image", "IMG "+ mCursor.getString(mCursor.getColumnIndex("_id")));			
			Log.d("Image", "IMG "+ mCursor.getString(mCursor.getColumnIndex(Media.DISPLAY_NAME)));
			Log.d("Image", "IMG "+ mCursor.getString(mCursor.getColumnIndex(Media.DESCRIPTION)));
			Log.d("Image", "IMG "+ mCursor.getString(mCursor.getColumnIndex(Media.MIME_TYPE)));
			Log.d("Image", "IMG "+ mCursor.getDouble(mCursor.getColumnIndex(Media.LATITUDE)));
			Log.d("Image", "IMG "+ mCursor.getDouble(mCursor.getColumnIndex(Media.LONGITUDE)));
*/
			mUriIds[i++] = Uri.parse(IMAGE_URI.toString() + "/" + mCursor.getString(mCursor.getColumnIndex("_id")));
		}

        Gallery g = (Gallery) findViewById(R.id.gallery);
        g.setAdapter(new ImageAdapter(this));
        g.setOnItemSelectedListener(this);
    }
}
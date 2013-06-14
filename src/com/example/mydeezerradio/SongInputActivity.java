package com.example.mydeezerradio;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.deezer.sdk.DeezerConnect;
import com.deezer.sdk.DeezerConnectImpl;
import com.deezer.sdk.DeezerError;
import com.deezer.sdk.DeezerRequest;
import com.deezer.sdk.OAuthException;
import com.deezer.sdk.RequestListener;
import com.deezer.sdk.SessionStore;

public class SongInputActivity extends Activity {

	public static final String EXTRA_SONGINPUT_SEARCH = "songInput_search_song";
	/** DeezerConnect object used for auhtentification or request. */
	private DeezerConnect deezerConnect = new DeezerConnectImpl(
			MainActivity.APP_ID);
	/** DeezerRequestListener object used to handle requests. */
	RequestListener requestHandler = new SongInputRequestHandler();
	public static List<Track> listTracks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_input);
		// Show the Up button in the action bar.
		setupActionBar();
		
		SessionStore sessionStore = new SessionStore();
		sessionStore.restore(deezerConnect, this);
		
		listTracks = null;
	}

	public void songInput_onClick_return(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	public void songInput_onClick_search(View view) {

		String song = String
				.valueOf((((TextView) findViewById(R.id.songInput_editText_song))
						.getText()));

		if (song.length() > 0) {
			Bundle bundle = new Bundle();
			bundle.putString("q", song);
			bundle.putString("nb_items", "20");
			DeezerRequest request_songs = new DeezerRequest("search/", bundle);
			deezerConnect.requestAsync(request_songs, requestHandler);
		}
	}

	private void TrackSearchComplete(List<Track> listReceived) {
		Log.w("SongInput / TrackSearchComplete","done");
	}
	
	private class SongInputRequestHandler implements RequestListener {
		public void onComplete(String response, Object requestId) {
			try {
//				Log.w("SongInput / onComplete", "receive json : "
//						+ response);
				listTracks = new ListDeezerDataReader<Track>(Track.class)
						.readList(response);
				Log.w("SongInput / onComplete", "received Track list : "
						+ listTracks);
				TrackSearchComplete(listTracks);
			} catch (IllegalStateException e) {
				Log.e("SongInput / onComplete", "IllegalStateException : "
						+ e);
				e.printStackTrace();
			}// catch

			Intent intent = new Intent(getApplicationContext(),
					SongSelectionActivity.class);
			// intent.putExtra(EXTRA_SONGINPUT_SEARCH, response);

			startActivity(intent);
		}

		public void onIOException(IOException e, Object requestId) {
			Log.w("Main / requestHandler", "IOException");
		}

		public void onMalformedURLException(MalformedURLException e,
				Object requestId) {
			Log.w("Main / requestHandler", "onMalformedURLException");
		}

		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			Log.w("Main / requestHandler", "onDeezerError : " + arg0.toString());
		}

		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			Log.w("Main / requestHandler", "onOAuthException" + arg0 + " / "
					+ arg1);
		}
	}// class

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.song_input, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}//

package com.example.mydeezerradio;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
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
import android.widget.Toast;

import com.deezer.sdk.AsyncDeezerTask;
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
	RequestListener songInputRequestHandler = new SongInputRequestHandler();
	RequestListener songInputFavHandler = new SongInputFavHandler();
	RequestListener previewRequestHandler = new PreviewRequestHandler();
	public static List<Track> listTracks;
	private List<Track> listFav;
	private int songInput_compteur_i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_input);
		// Show the Up button in the action bar.
		setupActionBar();

		SessionStore sessionStore = new SessionStore();
		Log.i("SongInput / onCreate",
				"restore : " + sessionStore.restore(deezerConnect, this));
		Log.i("SongInput / onCreate", "user : " + MainActivity.user_data);

		listTracks = new ArrayList<Track>();
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
			AsyncDeezerTask searchAsyncTracks = new AsyncDeezerTask(
					deezerConnect, songInputRequestHandler);
			searchAsyncTracks.execute(request_songs);
		}
	} // songInput_onClick_search

	public void songInput_onClick_goToFavorite(View view) {
		AsyncDeezerTask searchAsyncFav = new AsyncDeezerTask(deezerConnect,
				songInputFavHandler);
		DeezerRequest request_favorite = new DeezerRequest("/user/"
				+ MainActivity.userId + "/tracks");
		searchAsyncFav.execute(request_favorite);
		Toast.makeText(this, "Loading your fav", Toast.LENGTH_SHORT).show();
	}

	private void FavTrackSearchComplete() {

		AsyncDeezerTask searchAsyncFav = new AsyncDeezerTask(deezerConnect,
				previewRequestHandler);
		DeezerRequest request_preview = new DeezerRequest("/track/"
				+ listFav.get(songInput_compteur_i).getId());
		searchAsyncFav.execute(request_preview);
		++songInput_compteur_i;
	}

	private class SongInputRequestHandler implements RequestListener {
		public void onComplete(String response, Object requestId) {
			try {
				listTracks = new ListDeezerDataReader<Track>(Track.class)
						.readList(response);
				Log.w("SongInput / onComplete", "received Track list : "
						+ listTracks);

			} catch (IllegalStateException e) {
				Log.e("SongInput / onComplete", "IllegalStateException : " + e);
				e.printStackTrace();
			}// catch

			Log.w("SongInput / TrackSearchComplete", "done");

			Intent intent = new Intent(getApplicationContext(),
					SongSelectionActivity.class);
			startActivity(intent);
		}

		public void onIOException(IOException e, Object requestId) {
			Log.w("SongInputActivity / requestHandler", "IOException");
		}

		public void onMalformedURLException(MalformedURLException e,
				Object requestId) {
			Log.w("SongInputActivity / requestHandler",
					"onMalformedURLException");
		}

		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			Log.w("SongInputActivity / requestHandler", "onDeezerError : "
					+ arg0.toString());
		}

		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			Log.w("SongInputActivity / requestHandler", "onOAuthException"
					+ arg0 + " / " + arg1);
		}
	}// class SongInputRequestHandler

	private class SongInputFavHandler implements RequestListener {
		public void onComplete(String response, Object requestId) {

			listFav = new ListDeezerDataReader<Track>(Track.class)
					.readList(response);
			Log.w("SongInput / FavHandler", "received Track list : " + listFav);
			FavTrackSearchComplete();
		}

		public void onIOException(IOException e, Object requestId) {
			Log.w("SongInputActivity / FavHandler", "IOException");
		}

		public void onMalformedURLException(MalformedURLException e,
				Object requestId) {
			Log.w("SongInputActivity / FavHandler", "onMalformedURLException");
		}

		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			Log.w("SongInputActivity / FavHandler",
					"onDeezerError : " + arg0.toString());
		}

		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			Log.w("SongInputActivity / FavHandler", "onOAuthException" + arg0
					+ " / " + arg1);
		}
	}// class SongInputFavHandler

	private class PreviewRequestHandler implements RequestListener {
		public void onComplete(String response, Object requestId) {

			Track temp_track = new DeezerDataReader<Track>(Track.class)
					.read(response);
			Log.w("SongInput / PreviewHandler", "received Track : "
					+ temp_track);
			listTracks.add(temp_track);

			if (songInput_compteur_i < listFav.size()) {
				FavTrackSearchComplete();
			} else {
				Log.i("SongInput / PreviewRequestHandler",
						"size Fav : " + listFav.size() + " / size list : "
								+ listTracks.size());
				Intent intent = new Intent(getApplicationContext(),
						SongSelectionActivity.class);
				startActivity(intent);
			}

		}

		public void onIOException(IOException e, Object requestId) {
			Log.w("SongInputActivity / PreviewHandler", "IOException");
		}

		public void onMalformedURLException(MalformedURLException e,
				Object requestId) {
			Log.w("SongInputActivity / PreviewHandler",
					"onMalformedURLException");
		}

		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			Log.w("SongInputActivity / PreviewHandler", "onDeezerError : "
					+ arg0.toString());
		}

		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			Log.w("SongInputActivity / PreviewHandler", "onOAuthException"
					+ arg0 + " / " + arg1);
		}
	}// class PreviewRequestHandler

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

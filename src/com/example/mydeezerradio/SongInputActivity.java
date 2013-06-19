package com.example.mydeezerradio;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
	RequestListener songInputTrackFavRequestHandler = new SongInputTrackFavRequestHandler();
	RequestListener songInputFavHandler_v2 = new SongInputFavHandler_v2();
	public static List<Track> listTracks;
	public static List<Track> songInput_listTrack_listFav;

	// TODO : remplir la list listFav static (complete) même si on ne passe pas
	// par les favoris

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_input);

		SessionStore sessionStore = new SessionStore();
		Log.i("SongInput / onCreate",
				"restore : " + sessionStore.restore(deezerConnect, this));
		Log.i("SongInput / onCreate", "user : " + MainActivity.user_data);

		listTracks = new ArrayList<Track>();
		songInput_listTrack_listFav = new ArrayList<Track>();
		songInput_lookForFav();
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

	public void songInput_onClick_goToFavorite_v2(View view) {
		listTracks = songInput_listTrack_listFav;

		Intent intent = new Intent(getApplicationContext(),
				SongSelectionActivity.class);
		startActivity(intent);

	}

	public void songInput_lookForFav() {
		AsyncDeezerTask searchAsyncFav = new AsyncDeezerTask(deezerConnect,
				songInputFavHandler_v2);
		DeezerRequest request_favorite_playlist = new DeezerRequest("/user/"
				+ MainActivity.userId + "/playlists");
		searchAsyncFav.execute(request_favorite_playlist);
		Toast.makeText(this, "Loading your fav", Toast.LENGTH_SHORT).show();
	}

	public void songInput_lookForFav_finished() {
		Toast.makeText(this, "Fav loaded", Toast.LENGTH_SHORT).show();
	}

	private class SongInputRequestHandler implements RequestListener {
		public void onComplete(String response, Object requestId) {

			listTracks = new ListDeezerDataReader<Track>(Track.class)
					.readList(response);
			Log.w("SongInput / onComplete", "received Track list : "
					+ listTracks);

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

	private class SongInputFavHandler_v2 implements RequestListener {
		public void onComplete(String response, Object requestId) {

			List<Playlist> list_playlists = new ListDeezerDataReader<Playlist>(
					Playlist.class).readList(response);

			Log.i("SongInput / SongInputFavHandler_v2", "list playlist : "
					+ list_playlists);

			long fav_id = -1;
			Iterator<Playlist> it_playlist = list_playlists.iterator();
			while (it_playlist.hasNext()) {
				Playlist temp_playlist = it_playlist.next();
				if (temp_playlist.isIs_loved_track()) {
					fav_id = temp_playlist.getId();
				}
			}

			AsyncDeezerTask searchAsyncFav = new AsyncDeezerTask(deezerConnect,
					songInputTrackFavRequestHandler);
			DeezerRequest request_favorite_tracks = new DeezerRequest(
					"/playlist/" + fav_id + "/tracks");
			searchAsyncFav.execute(request_favorite_tracks);

		} // onComplete

		public void onIOException(IOException e, Object requestId) {
			Log.w("SongInputActivity / SongInputFavHandler_v2", "IOException");
		}

		public void onMalformedURLException(MalformedURLException e,
				Object requestId) {
			Log.w("SongInputActivity / SongInputFavHandler_v2",
					"onMalformedURLException");
		}

		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			Log.w("SongInputActivity / SongInputFavHandler_v2",
					"onDeezerError : " + arg0.toString());
		}

		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			Log.w("SongInputActivity / SongInputFavHandler_v2",
					"onOAuthException" + arg0 + " / " + arg1);
		}
	}// class SongInputFavHandler_v2

	private class SongInputTrackFavRequestHandler implements RequestListener {
		public void onComplete(String response, Object requestId) {

			songInput_listTrack_listFav = new ListDeezerDataReader<Track>(
					Track.class).readList(response);
			Log.w("SongInput / onComplete", "received Track list : "
					+ songInput_listTrack_listFav);

			Log.w("SongInput / TrackSearchComplete", "done");

			songInput_lookForFav_finished();
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

}// songInputActivity

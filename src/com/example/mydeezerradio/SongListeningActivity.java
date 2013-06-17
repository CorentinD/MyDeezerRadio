package com.example.mydeezerradio;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

import com.deezer.sdk.AsyncDeezerTask;
import com.deezer.sdk.DeezerConnect;
import com.deezer.sdk.DeezerConnectImpl;
import com.deezer.sdk.DeezerError;
import com.deezer.sdk.DeezerRequest;
import com.deezer.sdk.OAuthException;
import com.deezer.sdk.RequestListener;
import com.deezer.sdk.SessionStore;
import com.deezer.sdk.player.Player;
import com.deezer.sdk.player.TooManyPlayersExceptions;
import com.deezer.sdk.player.event.BufferState;
import com.deezer.sdk.player.event.OnBufferErrorListener;
import com.deezer.sdk.player.event.OnBufferProgressListener;
import com.deezer.sdk.player.event.OnBufferStateChangeListener;
import com.deezer.sdk.player.event.OnPlayerErrorListener;
import com.deezer.sdk.player.event.OnPlayerProgressListener;
import com.deezer.sdk.player.event.OnPlayerStateChangeListener;
import com.deezer.sdk.player.event.PlayerState;
import com.deezer.sdk.player.impl.DefaultPlayerFactory;
import com.deezer.sdk.player.networkcheck.WifiOnlyNetworkStateChecker;

public class SongListeningActivity extends Activity {

	private TextView songListening_textView_author;
	// private SimpleAdapter songListening_adapter_textAuthor;
	private Player songListening_player_songPlayer;
	private DeezerConnect deezerConnect = new DeezerConnectImpl(
			MainActivity.APP_ID);
	private PlayerHandler playerHandler = new PlayerHandler();
	private List<Artist> songListening_list_futureArtists = new ArrayList<Artist>();
	List<Track> songListening_list_futureSongs = new ArrayList<Track>();
	private RequestListener nextSongRequestHandler = new NextSongSearchHandler();
	private RequestListener topSongRequestHandler = new TopSongSearchHandler();
	private Track songListening_track_trackToAdd;
	private Track temp_track;
	private Track songListening_Track_currentTrack;
	private int i = 0;
	String TAG = "par ou passes tu ?";
	String TAG2 = "SongListening";

	protected void onPause() {
		super.onPause();
		songListening_player_songPlayer.stop();
		songListening_player_songPlayer.release();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_listening);

		songListening_Track_currentTrack = SongSelectionActivity.trackSelected;

		songListening_textView_author = (TextView) findViewById(R.id.songListening_textView_author);

		songListening_textView_author.setText(songListening_Track_currentTrack
				.toString());

		// deezerConnect.setAccessToken(getApplicationContext(),
		// MainActivity.access_token);
		SessionStore sessionStore = new SessionStore();
		sessionStore.restore(deezerConnect, this);

		Log.i("SongListening / onCreate", "track preview :"
				+ songListening_Track_currentTrack.getPreview());

		try {
			songListening_player_songPlayer = new DefaultPlayerFactory(
					getApplication(), deezerConnect,
					new WifiOnlyNetworkStateChecker()).createPlayer();

			songListening_player_songPlayer
					.addOnBufferErrorListener(playerHandler);
			songListening_player_songPlayer
					.addOnBufferStateChangeListener(playerHandler);
			songListening_player_songPlayer
					.addOnBufferProgressListener(playerHandler);
			songListening_player_songPlayer
					.addOnPlayerErrorListener(playerHandler);
			songListening_player_songPlayer
					.addOnPlayerStateChangeListener(playerHandler);
			songListening_player_songPlayer
					.addOnPlayerProgressListener(playerHandler);

			Log.i("SongListening / onCreate", "network available : "
					+ new WifiOnlyNetworkStateChecker().isNetworkAvailable());

		} catch (OAuthException e) {
			Log.e("SongListening / onCreate", "OAuthException : " + e);
		} catch (DeezerError e) {
			Log.e("SongListening / onCreate", "DeezerError : " + e);
		}// catch
		catch (TooManyPlayersExceptions e) {
			Log.e("SongListening / onCreate", "TooManyPlayersExceptions : " + e);
		}

		songListening_nextSongs(songListening_Track_currentTrack);

		// adapter à faire

	}

	public void songListening_onClick_play(View view) {
		// player.release();

		Log.i("SongListening / songListening_onClick_play", "Song : "
				+ songListening_Track_currentTrack);
		//
		// if (SongSelectionActivity.trackSelected.hasStream()) {
		// songListening_player_songPlayer.init(
		// SongSelectionActivity.trackSelected.getId(),
		// SongSelectionActivity.trackSelected.getStream());
		// } else {
		songListening_player_songPlayer.init(
				songListening_Track_currentTrack.getId(),
				songListening_Track_currentTrack.getPreview());
		// }
		songListening_player_songPlayer.play();

	}

	public void songListening_onClick_pause(View view) {
		songListening_player_songPlayer.pause();
	}

	public void songListening_onClick_return(View view) {
		Intent intent = new Intent(this, SongInputActivity.class);
		startActivity(intent);
	}

	private void sendMessageShowPlayerProgress(long timePosition) {
		Log.w("SongListening / sendMessageShowPlayerProgress",
				"timePosition : " + timePosition);
	}// met

	private void sendMessageShowPlayerState(PlayerState state) {
		Log.w("SongListening / sendMessageShowPlayerState", "state : " + state);
	}// met

	private void sendMessageShowBufferProgress(double percent) {
		Log.w("SongListening / sendMessageShowBufferProgress", "percent : "
				+ percent);
	}// met

	private void sendMessageShowBufferState(BufferState state) {
		Log.w("SongListening / sendMessageShowBufferState", "percent : "
				+ state);
	}// met

	private void sendMessageShowError(String error) {
		Log.w("SongListening / sendMessageShowError", "percent : " + error);
	}// met

	private class PlayerHandler implements OnPlayerProgressListener,
			OnBufferProgressListener, OnPlayerStateChangeListener,
			OnPlayerErrorListener, OnBufferStateChangeListener,
			OnBufferErrorListener {

		@Override
		public void onBufferError(Exception ex, double percent) {
			if (ex.getMessage() != null) {
				sendMessageShowError(ex.getMessage());
			} else {
				sendMessageShowError(ex.getClass().getName());
			}// else
			Log.e("SongListening / PlayerHandler", "Buffer error:", ex);
		}// met

		@Override
		public void onBufferStateChange(BufferState state, double percent) {
			sendMessageShowBufferProgress(percent);
			sendMessageShowBufferState(state);
		}// met

		@Override
		public void onPlayerError(Exception ex, long timePosition) {
			sendMessageShowError(ex.getMessage());
			Log.e("SongListening / PlayerHandler", "Buffer error:", ex);
		}// met

		@Override
		public void onPlayerStateChange(PlayerState state, long timePosition) {
			sendMessageShowPlayerState(state);
			sendMessageShowPlayerProgress(timePosition);
		}// met

		@Override
		public void onBufferProgress(double percent) {
			sendMessageShowBufferProgress(percent);
		}// met

		@Override
		public void onPlayerProgress(long timePosition) {
			sendMessageShowPlayerProgress(timePosition);
		}// met
	}// inner class

	void songListening_nextSongs(Track current_track) {

		temp_track = current_track;

		for (int j = 0; j < 5; ++j) {
			// TODO : one asyncTask for the artist then one for the top songs of
			// each artists
			// AsyncTask asyncRequestArtist = new AsyncRequestArtist();
			// asyncRequestArtist.
			DeezerRequest request_songs = new DeezerRequest("artist/"
					+ temp_track.getArtist().getId() + "/related");

			AsyncDeezerTask searchAsyncArtist = new AsyncDeezerTask(
					deezerConnect, nextSongRequestHandler);
			// try {
			// searchAsyncArtist.get(500, TimeUnit.MILLISECONDS);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// } catch (ExecutionException e) {
			// e.printStackTrace();
			// } catch (TimeoutException e) {
			// Log.w("SongListening / nextSongs", "Time Out");
			// }
			searchAsyncArtist.execute(request_songs);
			// deezerConnect.requestAsync(request_songs,
			// nextSongRequestHandler);
		}
		// Log.i("SongListening / nextSongs",
		// "list artists : "+songListening_list_futureArtists);
		i = 5;
	} // songListening_nextSongs

	private class NextSongSearchHandler implements RequestListener {
		@SuppressWarnings("unchecked")
		public void onComplete(String response, Object requestId) {
			try {
				ArrayList<Integer> temp_parsed = parseResult(response);
				List<Artist> temp_list = new ListDeezerDataReader<Artist>(
						Artist.class).readList(response);

				// adding informations to the artist for the sorting
				add_nbFans(temp_list, temp_parsed);
				// Sorting by decreasing number of fans
				Collections.sort(temp_list);
				// Take one of the 5 best
				Artist temp_artist = temp_list.get((int) (Math.random() * 5));
				Log.e(TAG, TAG2 + " sel temp");

				while (containsArtist(songListening_list_futureArtists,
						temp_artist)) {
					Log.e(TAG, TAG2 + " while");
					temp_artist = temp_list.get((int) (Math.random() * 5));
				}

				songListening_list_futureArtists.add(temp_artist);
				Log.i("SongListening / NextSongSearchHandler",
						"list artists : " + songListening_list_futureArtists);
				// TODO : fill the artist list and THEN search for the top
				// songs.
				// DeezerRequest request_top = new DeezerRequest("/artist/"
				// + temp_artist.getId() + "/top");
				// deezerConnect.requestAsync(request_top,
				// topSongRequestHandler);

			} catch (IllegalStateException e) {
				Log.e("SongListening / onComplete", "IllegalStateException : "
						+ e);
				e.printStackTrace();
			}// catch
		}

		public void onIOException(IOException e, Object requestId) {
			Log.w("SongListening / NextSongSearchHandler", "IOException");
		}

		public void onMalformedURLException(MalformedURLException e,
				Object requestId) {
			Log.w("SongListening / NextSongSearchHandler",
					"onMalformedURLException");
		}

		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			Log.w("SongListening / NextSongSearchHandler", "onDeezerError : "
					+ arg0.toString());
		}

		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			Log.w("SongListening / NextSongSearchHandler", "onOAuthException"
					+ arg0 + " / " + arg1);
		}
	}// class

	private class TopSongSearchHandler implements RequestListener {
		public void onComplete(String response, Object requestId) {
			try {

				List<Track> temp_trackList = new ListDeezerDataReader<Track>(
						Track.class).readList(response);

				songListening_track_trackToAdd = temp_trackList.get((int) (Math
						.random() * 5));

				songListening_list_futureSongs
						.add(songListening_track_trackToAdd);

				temp_track = songListening_track_trackToAdd;

				Log.i("SongListening / TopSongSearchHandler", "List : "
						+ songListening_list_futureSongs);

			} catch (IllegalStateException e) {
				Log.e("SongListening / onComplete", "IllegalStateException : "
						+ e);
				e.printStackTrace();
			}// catch

		}

		public void onIOException(IOException e, Object requestId) {
			Log.w("SongListening / NextSongSearchHandler", "IOException");
		}

		public void onMalformedURLException(MalformedURLException e,
				Object requestId) {
			Log.w("SongListening / NextSongSearchHandler",
					"onMalformedURLException");
		}

		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			Log.w("SongListening / NextSongSearchHandler", "onDeezerError : "
					+ arg0.toString());
		}

		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			Log.w("SongListening / NextSongSearchHandler", "onOAuthException"
					+ arg0 + " / " + arg1);
		}
	}// class

	public ArrayList<Integer> parseResult(String informations) {

		ArrayList<Integer> res = new ArrayList<Integer>();

		// Log.w("SongListening / parseResult", "informations : "+informations);

		if (informations.contains("total\":0")) {
			res.add(-1);
			return res;
		}

		String[] info_tab = informations.split("(?<=\\}),(?=\\{)");
		// Log.w("SongListening / parseResult", "nb_cases : " +
		// info_tab.length);

		for (int i = 0; i < info_tab.length; ++i) {
			String current = info_tab[i];
			// song
			int index_nbFans = current.indexOf("nb_fan\":") + 8;
			int index_radio = current.indexOf(",\"radio");
			String current_nbFan = current.substring(index_nbFans, index_radio);
			res.add(Integer.parseInt(current_nbFan));

		}

		// Log.i("SongListening / parseResults", "res :" + res);
		return res;
	}

	public void add_nbFans(List<Artist> temp_list, ArrayList<Integer> list_fans) {

		int i = 0;
		for (Artist a : temp_list) {
			a.setNbFan(list_fans.get(i));
			++i;
		}

	}

	public void tri_ArrayList(List<Integer> list) {
		Collections.sort(list);
		// Log.w("SongListening / triArrayList", "list triée : " + list);
	}

	public boolean containsArtist(List<Artist> listT, Artist art) {
		Iterator<Artist> it = listT.iterator();
		int i = 0;
		Log.e("SongListening / containsArtist", "size list : " + listT.size());
		while (it.hasNext()) {
			Artist temp_art = it.next();
			if (temp_art.getId() == art.getId()) {
				return true;
			}
			Log.w("SongListening / containsArtist", "Artist " + i + " : "
					+ temp_art.toString() + " / New Artist : " + art.toString());
			Log.w("SongListening / containsArtist",
					"res = " + (temp_art.getId() == art.getId()));
			++i;
		}

		return false;
	}

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
		getMenuInflater().inflate(R.menu.song_listening, menu);
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

}

package com.example.mydeezerradio.activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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
import com.example.mydeezerradio.R;
import com.example.mydeezerradio.deezerclasses.Artist;
import com.example.mydeezerradio.deezerclasses.DeezerDataReader;
import com.example.mydeezerradio.deezerclasses.ListDeezerDataReader;
import com.example.mydeezerradio.deezerclasses.Track;

public class SongListeningActivity extends Activity {
	private TextView songListening_textView_author;

	private DeezerConnect deezerConnect = new DeezerConnectImpl(
			MainActivity.APP_ID);

	private Player songListening_player_songPlayer;
	private PlayerHandler songListening_playerHandler = new PlayerHandler();
	private RequestListener songListening_nextArtistRequestHandler = new SongListening_NextArtistSearchHandler();
	private RequestListener songListening_topSongRequestHandler = new SongListening_TopSongSearchHandler();
	private RequestListener songListening_trackInfoRequestHandler = new SongListening_TrackInfoRequestHandler();
	private RequestListener songListening_addFavHandler = new SongListening_AddFavHandler();
	private RequestListener songListening_delFavHandler = new SongListening_DelFavHandler();

	private List<Artist> songListening_list_futureArtists = new ArrayList<Artist>();
	private List<Track> songListening_list_futureSongs = new ArrayList<Track>();
	private Track songListening_track_trackToAdd;
	private Track songListening_Track_currentTrack;
	private boolean songListening_boolean_currentTrack_isFav = false;

	private int songListening_numberOfArtists_i = 1;
	private int songListening_trackBeingListened = 0;

	private boolean songListening_boolean_songSearched = false;
	private boolean songListening_boolean_toDo = false;
	private boolean songListening_boolean_buttons_areClickable = false;

	protected void onPause() {
		super.onPause();
		songListening_player_songPlayer.stop();
		songListening_player_songPlayer.release();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_listening);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		Toast.makeText(this, "Buffering, please wait", Toast.LENGTH_SHORT)
				.show();
		songListening_Track_currentTrack = SongSelectionActivity.songSelection_track_trackSelected;

		songListening_textView_author = (TextView) findViewById(R.id.songListening_textView_author);

		songListening_textView_author.setText(songListening_Track_currentTrack
				.toString());

		SessionStore sessionStore = new SessionStore();
		sessionStore.restore(deezerConnect, this);

		Log.i("SongListening / onCreate", "track preview :"
				+ songListening_Track_currentTrack.getPreview());

		songListening_setImage();
		set_currentTrack_isFav();

		try {
			songListening_player_songPlayer = new DefaultPlayerFactory(
					getApplication(), deezerConnect,
					new WifiOnlyNetworkStateChecker()).createPlayer();

			songListening_player_songPlayer
					.addOnBufferErrorListener(songListening_playerHandler);
			songListening_player_songPlayer
					.addOnBufferStateChangeListener(songListening_playerHandler);
			songListening_player_songPlayer
					.addOnBufferProgressListener(songListening_playerHandler);
			songListening_player_songPlayer
					.addOnPlayerErrorListener(songListening_playerHandler);
			songListening_player_songPlayer
					.addOnPlayerStateChangeListener(songListening_playerHandler);
			songListening_player_songPlayer
					.addOnPlayerProgressListener(songListening_playerHandler);

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

		songListening_list_futureSongs.add(songListening_Track_currentTrack);
		songListening_nextArtist(songListening_Track_currentTrack.getArtist());

	}

	public void songListening_onClick_play(View view) {
		if (songListening_boolean_buttons_areClickable) {
			play();
		}
	}

	public void songListening_onClick_pause(View view) {
		if (songListening_boolean_buttons_areClickable) {
			songListening_player_songPlayer.pause();
		}
	}

	public void songListening_onClick_next(View view) {
		if (songListening_boolean_buttons_areClickable) {
			goTo_nextSong();
		}
	}

	public void songListening_onClick_return(View view) {
		if (songListening_boolean_buttons_areClickable) {
			songListening_player_songPlayer.stop();
			songListening_player_songPlayer.release();
			Intent intent = new Intent(this, SongInputActivity.class);
			startActivity(intent);
		}
	}

	public void songListening_onClick_fav(View view) {
		if (songListening_boolean_buttons_areClickable) {
			if (!songListening_boolean_currentTrack_isFav) {
				addCurrentToFav();
			} else {
				removeCurrentFromFav();
			}
		}
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
			if (state.compareTo(PlayerState.PLAYBACK_COMPLETED) == 0) {
				goTo_nextSong();
			}
			sendMessageShowPlayerState(state);
			sendMessageShowPlayerProgress(timePosition);
		}// met

		@Override
		public void onBufferProgress(double percent) {
			sendMessageShowBufferProgress(percent);
		}// met

		@Override
		public void onPlayerProgress(long timePosition) {
			((TextView) findViewById(R.id.songListening_textView_progression))
					.setText(timePosition
							/ (songListening_Track_currentTrack.getDuration() * 10)
							+ " %");
			sendMessageShowPlayerProgress(timePosition);
		}// met
	}// inner class

	private void songListening_nextArtist(Artist prev_artist) {
		DeezerRequest request_artists = new DeezerRequest("artist/"
				+ prev_artist.getId() + "/related");
		AsyncDeezerTask searchAsyncArtist = new AsyncDeezerTask(deezerConnect,
				songListening_nextArtistRequestHandler);
		searchAsyncArtist.execute(request_artists);
	}

	private void songListening_nextSongs(List<Artist> listArtists) {
		songListening_list_futureSongs = new ArrayList<Track>();
		songListening_trackBeingListened = 0;
		for (Artist a : listArtists) {
			DeezerRequest request_songs = new DeezerRequest("artist/"
					+ a.getId() + "/top");
			AsyncDeezerTask searchAsyncArtist = new AsyncDeezerTask(
					deezerConnect, songListening_topSongRequestHandler);
			searchAsyncArtist.execute(request_songs);
		}
		songListening_boolean_songSearched = true;
	} // songListening_nextSongs

	private ArrayList<Integer> parseResult(String informations) {
		ArrayList<Integer> res = new ArrayList<Integer>();

		if (informations.contains("total\":0")) {
			res.add(-1);
			return res;
		}

		String[] info_tab = informations.split("(?<=\\}),(?=\\{)");

		for (int i = 0; i < info_tab.length; ++i) {
			String current = info_tab[i];
			// song
			int index_nbFans = current.indexOf("nb_fan\":") + 8;
			int index_radio = current.indexOf(",\"radio");
			String current_nbFan = current.substring(index_nbFans, index_radio);
			res.add(Integer.parseInt(current_nbFan));

		}
		return res;
	} // parseResult

	private void add_nbFans(List<Artist> temp_list, ArrayList<Integer> list_fans) {
		int i = 0;
		for (Artist a : temp_list) {
			a.setNbFan(list_fans.get(i));
			++i;
		}
	} // add_nbFans



	private boolean containsArtist(List<Artist> listT, Artist art) {
		Iterator<Artist> it = listT.iterator();
		while (it.hasNext()) {
			Artist temp_art = it.next();
			if (temp_art.getId() == art.getId()) {
				return true;
			}
		}
		return false;
	} // containsArtist

	private void goTo_nextSong() {

		if (songListening_boolean_songSearched) {

			songListening_Track_currentTrack = songListening_list_futureSongs
					.get(songListening_trackBeingListened);
			++songListening_trackBeingListened;
			--songListening_numberOfArtists_i;
			songListening_player_songPlayer.stop();
			songListening_textView_author
					.setText(songListening_Track_currentTrack.toString());
			play();

			if (songListening_boolean_toDo) {
				Log.i("SongListening / goTo_NextSong", "toDo : " + songListening_boolean_toDo);
				songListening_nextSongs(songListening_list_futureArtists);
				songListening_boolean_toDo = false;
			}
			if (songListening_numberOfArtists_i == 2) {
				songListening_list_futureArtists = new ArrayList<Artist>();
				songListening_nextArtist(songListening_Track_currentTrack
						.getArtist());
				songListening_boolean_toDo = true;
			}

		}

	}// goTo_nextSong

	private void play() {

		Log.i("SongListening / play", "Song : "
				+ songListening_Track_currentTrack);

		Log.i("SongListening / play ", "stream() : "
				+ songListening_Track_currentTrack.getStream());

		if (songListening_Track_currentTrack.hasStream()) {
			songListening_player_songPlayer.init(
					songListening_Track_currentTrack.getId(),
					songListening_Track_currentTrack.getStream());
		} else {
			songListening_player_songPlayer.init(
					songListening_Track_currentTrack.getId(),
					songListening_Track_currentTrack.getPreview());
		}

		set_currentTrack_isFav();

		songListening_player_songPlayer.play();

		Log.i("SongListening / play", "cover set : " + songListening_setImage());

		if (!songListening_boolean_songSearched) {
			songListening_nextSongs(songListening_list_futureArtists);
		}
	} // play

	private void set_currentTrack_isFav() {
		songListening_boolean_currentTrack_isFav = SongInputActivity.songInput_listTrack_listFav
				.contains(songListening_Track_currentTrack);
		if (songListening_boolean_currentTrack_isFav) {
			((ImageView) findViewById(R.id.songListening_button_fav))
					.setImageResource(R.drawable.deezer_button_fav_yes);
		} else {
			((ImageView) findViewById(R.id.songListening_button_fav))
					.setImageResource(R.drawable.deezer_button_fav_no);
		}

		Log.i("SongListening / set_currentTrack_isFav", "list fav id : "
				+ SongInputActivity.songInput_listTrack_listFav.get(0).getId());
		Log.i("SongListening / set_currentTrack_isFav", "current id : "
				+ songListening_Track_currentTrack.getId());

	} // set_currentTrack_isFav

	private boolean songListening_setImage() {
		boolean rep = false;
		try {
			URL url = new URL(songListening_Track_currentTrack.getAlbum()
					.getCover());
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			InputStream input = connection.getInputStream();
			((ImageView) findViewById(R.id.songListening_imageView_cover))
					.setImageBitmap(BitmapFactory.decodeStream(input));
			((ImageView) findViewById(R.id.songListening_imageView_cover))
					.setScaleType(ScaleType.FIT_XY);
			rep = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rep;
	}

	private void addCurrentToFav() {

		Bundle bundle = new Bundle();
		bundle.putString("p", songListening_Track_currentTrack.toString());
		bundle.putString("track_id",
				String.valueOf(songListening_Track_currentTrack.getId()));
		DeezerRequest addFav_request = new DeezerRequest("/user/"
				+ MainActivity.main_int_userId + "/tracks", bundle, "POST");
		AsyncDeezerTask asyncDeezerTask = new AsyncDeezerTask(deezerConnect,
				songListening_addFavHandler);
		asyncDeezerTask.execute(addFav_request);
	}

	private void removeCurrentFromFav() {
		Bundle bundle = new Bundle();
		bundle.putString("d", songListening_Track_currentTrack.toString());
		bundle.putString("track_id",
				String.valueOf(songListening_Track_currentTrack.getId()));
		DeezerRequest delFav_request = new DeezerRequest("/user/"
				+ MainActivity.main_int_userId + "/tracks", bundle, "DELETE");
		AsyncDeezerTask asyncDeezerTask = new AsyncDeezerTask(deezerConnect,
				songListening_delFavHandler);
		asyncDeezerTask.execute(delFav_request);
	}

	private void getWholeTrackInfo(long trackId) {
		DeezerRequest requestTrackInfo = new DeezerRequest("track/" + trackId);
		AsyncDeezerTask searchAsyncArtist = new AsyncDeezerTask(deezerConnect,
				songListening_trackInfoRequestHandler);
		searchAsyncArtist.execute(requestTrackInfo);
	}

	private class SongListening_NextArtistSearchHandler implements RequestListener {
		@SuppressWarnings("unchecked")
		public void onComplete(String response, Object requestId) {
			try {
				ArrayList<Integer> temp_parsed = parseResult(response);
				List<Artist> temp_list = new ListDeezerDataReader<Artist>(
						Artist.class).readList(response);

				// if there's less than 5 related artists, new random :
				int random_number = 5;
				if (temp_list.size() < 5) {
					random_number = temp_list.size();
				}
				// adding informations to the artist for the sorting
				add_nbFans(temp_list, temp_parsed);
				// Sorting by decreasing number of fans
				Collections.sort(temp_list);
				// Take one of the 5 best
				Artist temp_artist = temp_list
						.get((int) (Math.random() * random_number));

				while (containsArtist(songListening_list_futureArtists,
						temp_artist)) {
					// not twice the same artist
					temp_artist = temp_list.get((int) (Math.random() * 5));
				}

				songListening_list_futureArtists.add(temp_artist);
				++songListening_numberOfArtists_i;

				Log.i("SongListening / NextSongSearchHandler",
						"list artists : " + songListening_list_futureArtists);

				// recursion until 6 artists are found
				if (songListening_numberOfArtists_i < 6) {
					songListening_nextArtist(temp_artist);
				} else {
					Toast.makeText(getApplicationContext(),
							"Buffering done, enjoy !", Toast.LENGTH_SHORT)
							.show();
					songListening_boolean_buttons_areClickable = true;
				}
			} catch (IllegalStateException e) {
				Log.e("SongListening / onComplete", "IllegalStateException : "
						+ e);
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

	private class SongListening_TopSongSearchHandler implements RequestListener {
		public void onComplete(String response, Object requestId) {
			try {

				List<Track> temp_trackList = new ListDeezerDataReader<Track>(
						Track.class).readList(response);

				int random_number = 5;
				if (temp_trackList.size() < 5) {
					random_number = temp_trackList.size();
				}

				// get a random track of TOP and complete it with infos
				getWholeTrackInfo((temp_trackList
						.get((int) (Math.random() * random_number))).getId());
			} catch (IllegalStateException e) {
				Log.e("SongListening / onComplete", "IllegalStateException : "
						+ e);
				e.printStackTrace();
			}// catch

		}

		public void onIOException(IOException e, Object requestId) {
			Log.w("SongListening / TopSongSearchHandler", "IOException");
		}

		public void onMalformedURLException(MalformedURLException e,
				Object requestId) {
			Log.w("SongListening / TopSongSearchHandler",
					"onMalformedURLException");
		}

		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			Log.w("SongListening / TopSongSearchHandler", "onDeezerError : "
					+ arg0.toString());
		}

		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			Log.w("SongListening / TopSongSearchHandler", "onOAuthException"
					+ arg0 + " / " + arg1);
		}
	}// class TopSongSearchHandler

	private class SongListening_AddFavHandler implements RequestListener {

		@Override
		public void onComplete(String arg0, Object arg1) {
			Toast.makeText(getApplicationContext(), "Track added to fav",
					Toast.LENGTH_SHORT).show();
			SongInputActivity.songInput_listTrack_listFav
					.add(songListening_Track_currentTrack);
			((ImageView) findViewById(R.id.songListening_button_fav))
					.setImageResource(R.drawable.deezer_button_fav_yes);
			songListening_boolean_currentTrack_isFav = true;
			Log.i("SongListening / addFavHandler", "Track added to fav : "
					+ songListening_Track_currentTrack);
		}

		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			Log.w("SongListening / AddFavHandler", "DeezerError : " + arg0);
		}

		@Override
		public void onIOException(IOException arg0, Object arg1) {
			Log.w("SongListening / AddFavHandler", "IOException : " + arg0);
		}

		@Override
		public void onMalformedURLException(MalformedURLException arg0,
				Object arg1) {
			Log.w("SongListening / AddFavHandler", "MalformedURLException : "
					+ arg0);
		}

		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			Log.w("SongListening / AddFavHandler", "OAuthException : " + arg0);
		}

	}

	private class SongListening_DelFavHandler implements RequestListener {

		@Override
		public void onComplete(String arg0, Object arg1) {
			Toast.makeText(getApplicationContext(), "Track deleted from fav",
					Toast.LENGTH_SHORT).show();
			SongInputActivity.songInput_listTrack_listFav
					.remove(songListening_Track_currentTrack);
			((ImageView) findViewById(R.id.songListening_button_fav))
					.setImageResource(R.drawable.deezer_button_fav_no);
			songListening_boolean_currentTrack_isFav = false;
			Log.i("SongListening / addFavHandler", "Track deleted from fav : "
					+ songListening_Track_currentTrack);
		}

		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			Log.w("SongListening / AddFavHandler", "DeezerError : " + arg0);
		}

		@Override
		public void onIOException(IOException arg0, Object arg1) {
			Log.w("SongListening / AddFavHandler", "IOException : " + arg0);
		}

		@Override
		public void onMalformedURLException(MalformedURLException arg0,
				Object arg1) {
			Log.w("SongListening / AddFavHandler", "MalformedURLException : "
					+ arg0);
		}

		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			Log.w("SongListening / AddFavHandler", "OAuthException : " + arg0);
		}

	}

	private class SongListening_TrackInfoRequestHandler implements RequestListener {
		@Override
		public void onComplete(String response, Object arg1) {
			songListening_track_trackToAdd = new DeezerDataReader<Track>(
					Track.class).read(response);

			songListening_list_futureSongs.add(songListening_track_trackToAdd);

			Log.i("SongListening / TopSongSearchHandler", "List : "
					+ songListening_list_futureSongs);
		}

		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			Log.w("SongListening / TrackInfoRequestHandler", "onDeezerError"
					+ arg0 + " / " + arg1);
		}

		@Override
		public void onIOException(IOException arg0, Object arg1) {
			Log.w("SongListening / TrackInfoRequestHandler", "IOException"
					+ arg0 + " / " + arg1);
		}

		@Override
		public void onMalformedURLException(MalformedURLException arg0,
				Object arg1) {
			Log.w("SongListening / TrackInfoRequestHandler",
					"onMalformedURLException" + arg0 + " / " + arg1);
		}

		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			Log.w("SongListening / TrackInfoRequestHandler", "onOAuthException"
					+ arg0 + " / " + arg1);
		}
	} // class TrackInfoRequestHandler

} // songListeningActivity

package com.example.mydeezerradio;

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

public class SongListeningActivity extends Activity {
	private TextView songListening_textView_author;

	private DeezerConnect deezerConnect = new DeezerConnectImpl(
			MainActivity.APP_ID);

	private Player songListening_player_songPlayer;
	private PlayerHandler playerHandler = new PlayerHandler();
	private RequestListener nextArtistRequestHandler = new NextArtistSearchHandler();
	private RequestListener topSongRequestHandler = new TopSongSearchHandler();

	private List<Artist> songListening_list_futureArtists = new ArrayList<Artist>();
	private List<Track> songListening_list_futureSongs = new ArrayList<Track>();
	private Track songListening_track_trackToAdd;
	private Track songListening_Track_currentTrack;

	private int songListening_numberOfTracks_i = 1;
	private int songListening_trackBeingListened = 0;

	private boolean songListening_boolean_songSearched = false;
	private boolean toDo = false;

	String TAG2 = "par ou passes tu ?";
	String TAG = "SongListening";

	protected void onPause() {
		super.onPause();
		songListening_player_songPlayer.stop();
		songListening_player_songPlayer.release();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_listening);

		Toast.makeText(this, "Buffering, please wait", Toast.LENGTH_SHORT)
				.show();
		songListening_Track_currentTrack = SongSelectionActivity.trackSelected;

		songListening_textView_author = (TextView) findViewById(R.id.songListening_textView_author);

		songListening_textView_author.setText(songListening_Track_currentTrack
				.toString());

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

		songListening_list_futureSongs.add(songListening_Track_currentTrack);
		songListening_nextArtist(songListening_Track_currentTrack.getArtist());

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

	}

	public void songListening_onClick_play(View view) {
		play();
	}

	public void songListening_onClick_pause(View view) {
		songListening_player_songPlayer.pause();
	}

	public void songListening_onClick_next(View view) {
		goTo_nextSong();
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
					.setText(timePosition / 300 + " %");
			sendMessageShowPlayerProgress(timePosition);
		}// met
	}// inner class

	public void songListening_nextArtist(Artist prev_artist) {
		DeezerRequest request_artists = new DeezerRequest("artist/"
				+ prev_artist.getId() + "/related");
		AsyncDeezerTask searchAsyncArtist = new AsyncDeezerTask(deezerConnect,
				nextArtistRequestHandler);
		searchAsyncArtist.execute(request_artists);
	}

	void songListening_nextSongs(List<Artist> listArtists) {
		songListening_list_futureSongs = new ArrayList<Track>();
		songListening_trackBeingListened = 0;
		for (Artist a : listArtists) {
			DeezerRequest request_songs = new DeezerRequest("artist/"
					+ a.getId() + "/top");
			AsyncDeezerTask searchAsyncArtist = new AsyncDeezerTask(
					deezerConnect, topSongRequestHandler);
			searchAsyncArtist.execute(request_songs);
		}
		songListening_boolean_songSearched = true;
	} // songListening_nextSongs

	private class NextArtistSearchHandler implements RequestListener {
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
				++songListening_numberOfTracks_i;

				Log.i("SongListening / NextSongSearchHandler",
						"list artists : " + songListening_list_futureArtists);

				if (songListening_numberOfTracks_i < 6) {
					songListening_nextArtist(temp_artist);
				} else {
					Toast.makeText(getApplicationContext(),
							"Buffering done, enjoy !", Toast.LENGTH_SHORT)
							.show();
				}
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

	public void add_nbFans(List<Artist> temp_list, ArrayList<Integer> list_fans) {

		int i = 0;
		for (Artist a : temp_list) {
			a.setNbFan(list_fans.get(i));
			++i;
		}
	} // add_nbFans

	public void tri_ArrayList(List<Integer> list) {
		Collections.sort(list);
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

	public void goTo_nextSong() {

		if (songListening_boolean_songSearched) {

			songListening_Track_currentTrack = songListening_list_futureSongs
					.get(songListening_trackBeingListened);
			++songListening_trackBeingListened;
			--songListening_numberOfTracks_i;
			songListening_player_songPlayer.stop();
			songListening_textView_author
					.setText(songListening_Track_currentTrack.toString());
			play();

			if (toDo) {
				Log.i("SongListening / goTo_NextSong", "toDo : " + toDo);
				songListening_nextSongs(songListening_list_futureArtists);
				toDo = false;
			}
			if (songListening_numberOfTracks_i == 2) {
				songListening_list_futureArtists = new ArrayList<Artist>();
				songListening_nextArtist(songListening_Track_currentTrack
						.getArtist());
				toDo = true;
			}

		}

	}// goTo_nextSong

	public void play() {

		Log.i("SongListening / play", "Song : "
				+ songListening_Track_currentTrack);

		Log.i("SongListening / play ", "stream() : "
				+ songListening_Track_currentTrack.getStream());

		// TODO : si vient des fav : stream==false / si recherche stream==null

		if (songListening_Track_currentTrack.hasStream()) {
			songListening_player_songPlayer.init(
					songListening_Track_currentTrack.getId(),
					songListening_Track_currentTrack.getStream());
		} else {
			songListening_player_songPlayer.init(
					songListening_Track_currentTrack.getId(),
					songListening_Track_currentTrack.getPreview());
		}

		songListening_player_songPlayer.play();

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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!songListening_boolean_songSearched) {
			songListening_nextSongs(songListening_list_futureArtists);
		}
	}

} // songListeningActivity

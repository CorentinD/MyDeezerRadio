package com.example.mydeezerradio;

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
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.deezer.sdk.DeezerConnect;
import com.deezer.sdk.DeezerConnectImpl;
import com.deezer.sdk.DeezerError;
import com.deezer.sdk.OAuthException;
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

	TextView songListening_textView_author;
	SimpleAdapter songListening_adapter_textAuthor;
	private Player player;
	private DeezerConnect deezerConnect = new DeezerConnectImpl(
			MainActivity.APP_ID);
	private PlayerHandler playerHandler = new PlayerHandler();

	protected void onPause() {
		super.onPause();
		player.stop();
		player.release();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_listening);

		songListening_textView_author = (TextView) findViewById(R.id.songListening_textView_author);

		songListening_textView_author
				.setText(SongSelectionActivity.trackSelected.toString());

		deezerConnect.setAccessToken(getApplicationContext(),
				MainActivity.access_token);

		Log.i("SongListening / onCreate", "track preview :"
				+ SongSelectionActivity.trackSelected.getPreview());

		try {
			player = new DefaultPlayerFactory(getApplication(), deezerConnect,
					new WifiOnlyNetworkStateChecker()).createPlayer();

			player.addOnBufferErrorListener(playerHandler);
			player.addOnBufferStateChangeListener(playerHandler);
			player.addOnBufferProgressListener(playerHandler);
			player.addOnPlayerErrorListener(playerHandler);
			player.addOnPlayerStateChangeListener(playerHandler);
			player.addOnPlayerProgressListener(playerHandler);

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

		// adapter à faire

	}

	public void songListening_onClick_play(View view) {
		// player.release();

		Log.i("SongListening / songListening_onClick_play", "Song : "
				+ SongSelectionActivity.trackSelected);

		player.init(SongSelectionActivity.trackSelected.getId(),
				SongSelectionActivity.trackSelected.getPreview());
		player.play();
		
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

package com.example.mydeezerradio.activities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.deezer.sdk.AsyncDeezerTask;
import com.deezer.sdk.DeezerConnect;
import com.deezer.sdk.DeezerConnectImpl;
import com.deezer.sdk.DeezerError;
import com.deezer.sdk.DeezerRequest;
import com.deezer.sdk.OAuthException;
import com.deezer.sdk.RequestListener;
import com.deezer.sdk.SessionStore;
import com.example.mydeezerradio.R;
import com.example.mydeezerradio.deezerclasses.DeezerDataReader;
import com.example.mydeezerradio.deezerclasses.Track;

public class SongSelectionActivity extends Activity {

	private ListView songSelection_listView_songList;
	private ArrayAdapter<String> songSelection_adapter_songListAdapter;
	private ArrayList<String> songSelection_list_searchResults;
	public final static String songSelection_string_noResults = "No results";
	public static Track songSelection_track_trackSelected = new Track();
	private DeezerConnect deezerConnect = new DeezerConnectImpl(
			MainActivity.APP_ID);
	private SongSelection_TrackInfoRequestHandler songSelection_TrackInfoRequestHandler = new SongSelection_TrackInfoRequestHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_selection);

		new SessionStore().restore(deezerConnect, this);

		songSelection_listView_songList = (ListView) findViewById(R.id.songSelection_listView_songList);
		songSelection_list_searchResults = new ArrayList<String>();

		songSelection_adapter_songListAdapter = new ArrayAdapter<String>(this,
				R.layout.songselection_listview,
				songSelection_list_searchResults);

		songSelection_listView_songList
				.setAdapter(songSelection_adapter_songListAdapter);

		Iterator<Track> it_list_searchedSongs = SongInputActivity.songInput_list_listTracks
				.iterator();
		while (it_list_searchedSongs.hasNext()) {
			Track temp_track = it_list_searchedSongs.next();
			songSelection_adapter_songListAdapter.add(temp_track.toString());
		}

		songSelection_listView_songList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View v,
							int position, long id) {

						songSelection_getTrackInfos(SongInputActivity.songInput_list_listTracks
								.get(position));

					}
				});
	}// onCreate

	public void songSelection_onClick_return(View view) {
		finish();
	}

	public void songSelection_getTrackInfos(Track track) {
		DeezerRequest getTrackInfos_request = new DeezerRequest("/track/"
				+ track.getId());
		AsyncDeezerTask asyncDeezerTask = new AsyncDeezerTask(deezerConnect,
				songSelection_TrackInfoRequestHandler);
		asyncDeezerTask.execute(getTrackInfos_request);
	}

	public void songSelection_TrackSearchComplete() {

		Intent intent = new Intent(getApplicationContext(),
				SongListeningActivity.class);

		startActivity(intent);

		Log.w("SongSelection / onComplete", "received Track stream : "
				+ songSelection_track_trackSelected.getStream());
	}

	private class SongSelection_TrackInfoRequestHandler implements
			RequestListener {
		@Override
		public void onComplete(String response, Object arg1) {
			songSelection_track_trackSelected = new DeezerDataReader<Track>(
					Track.class).read(response);
			songSelection_TrackSearchComplete();
		}

		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			Log.w("SongSelection / TrackInfoRequestHandler", "onDeezerError"
					+ arg0 + " / " + arg1);
		}

		@Override
		public void onIOException(IOException arg0, Object arg1) {
			Log.w("SongSelection / TrackInfoRequestHandler", "IOException"
					+ arg0 + " / " + arg1);
		}

		@Override
		public void onMalformedURLException(MalformedURLException arg0,
				Object arg1) {
			Log.w("SongSelection / TrackInfoRequestHandler",
					"onMalformedURLException" + arg0 + " / " + arg1);
		}

		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			Log.w("SongSelection / TrackInfoRequestHandler", "onOAuthException"
					+ arg0 + " / " + arg1);
		}
	} // class TrackInfoRequestHandler

} // SongSelectionActivity

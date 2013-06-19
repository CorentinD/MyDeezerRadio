package com.example.mydeezerradio;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.deezer.sdk.DeezerConnect;
import com.deezer.sdk.DeezerConnectImpl;
import com.deezer.sdk.SessionStore;

public class SongSelectionActivity extends Activity {

	ListView songSelection_listView_songList;
	ArrayAdapter<String> songSelection_adapter_songListAdapter;
	public static final String EXTRA_SONGSELECTION_SELECTION = "songSelection_clicked_song";
	SharedPreferences sharedPref;
	SharedPreferences.Editor sharedPref_editor;
	ArrayList<String> SongSelection_list_searchResults;
	public final static String songSelection_string_noResults = "No results";
	public static Track trackSelected = new Track();
	private DeezerConnect deezerConnect = new DeezerConnectImpl(
			MainActivity.APP_ID);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_selection);

		sharedPref = getSharedPreferences(MainActivity.name_sharedPref,
				Context.MODE_PRIVATE);
		sharedPref_editor = sharedPref.edit();
		new SessionStore().restore(deezerConnect, this);

		songSelection_listView_songList = (ListView) findViewById(R.id.songSelection_listView_songList);
		SongSelection_list_searchResults = new ArrayList<String>();

		songSelection_adapter_songListAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				SongSelection_list_searchResults);

		songSelection_listView_songList
				.setAdapter(songSelection_adapter_songListAdapter);

		Iterator<Track> it_list_searchedSongs = SongInputActivity.listTracks
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

						trackSelected = SongInputActivity.listTracks
								.get(position);
						TrackSearchComplete();
					}
					// TODO : problem with the fav songs : not initialised;

				});
	}// onCreate

	public void songSelection_onClick_return(View view) {
		finish();
	}

	public void TrackSearchComplete() {

		Intent intent = new Intent(getApplicationContext(),
				SongListeningActivity.class);

		startActivity(intent);

		Log.w("SongSelection / onComplete",
				"received Track : " + trackSelected.getPreview());
	}

} // SongSelectionActivity

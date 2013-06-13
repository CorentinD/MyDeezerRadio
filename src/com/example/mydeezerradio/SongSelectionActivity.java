package com.example.mydeezerradio;

import java.util.ArrayList;
import java.util.Iterator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SongSelectionActivity extends Activity {

	ListView songSelection_listView_songList;
	ArrayAdapter<String> songSelection_adapter_songListAdapter;
	public static final String EXTRA_SONGSELECTION_SELECTION = "songSelection_clicked_song";
	SharedPreferences sharedPref;
	SharedPreferences.Editor sharedPref_editor;
	ArrayList<String> SongSelection_list_searchResults;
	public final static String songSelection_string_noResults = "No results";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_selection);
		// Show the Up button in the action bar.
		setupActionBar();

		sharedPref = getSharedPreferences(MainActivity.name_sharedPref,
				Context.MODE_PRIVATE);
		sharedPref_editor = sharedPref.edit();

		songSelection_listView_songList = (ListView) findViewById(R.id.songSelection_listView_songList);
		SongSelection_list_searchResults = new ArrayList<String>();

		songSelection_adapter_songListAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				SongSelection_list_searchResults);

		songSelection_listView_songList
				.setAdapter(songSelection_adapter_songListAdapter);

		// Intent parent_intent = getIntent();

		// String temp_searched_song = parent_intent
		// .getStringExtra(SongInputActivity.EXTRA_SONGINPUT_SEARCH);

		// SongSelection_list_searchResults = parseResult(temp_searched_song);

		// Iterator<String> it_list_searchedSongs =
		// SongSelection_list_searchResults
		// .iterator();

		Iterator<Track> it_list_searchedSongs = SongInputActivity.listTracks
				.iterator();

		while (it_list_searchedSongs.hasNext()) {
			songSelection_adapter_songListAdapter.add(it_list_searchedSongs
					.next().toString());
		}

		songSelection_listView_songList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View v,
							int position, long id) {

						String temp_clicked_song = String
								.valueOf(((TextView) v).getText());

						Intent intent = new Intent(getApplicationContext(),
								SongListeningActivity.class);
						intent.putExtra(EXTRA_SONGSELECTION_SELECTION,
								temp_clicked_song);
						startActivity(intent);
					}
				});
	} // onCreate

	public void songSelection_onClick_return(View view) {
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.song_selection, menu);
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

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	public ArrayList<String> parseResult(String informations) {

		ArrayList<String> res = new ArrayList<String>();

		if (informations.contains("total\":0")) {
			res.add(songSelection_string_noResults);
			return res;
		}

		String[] info_tab = informations.split("(?<=\\}),(?=\\{)");
		Log.w("SongSelection / parseResult", "nb_cases : " + info_tab.length);

		for (int i = 0; i < info_tab.length; ++i) {
			String current = info_tab[i];
			// song
			int index_title = current.indexOf("title\":") + 8;
			int index_link = current.indexOf("\",\"link");
			String current_title = current.substring(index_title, index_link);

			// artist
			int index_artist = current.indexOf("name\":") + 7;
			int index_link2 = current.indexOf("\",\"link", index_artist);
			String current_artist = current
					.substring(index_artist, index_link2);

			res.add(current_title + " by " + current_artist);

		}

		Log.i("SongSelection / parseResults", "res :" + res);
		return res;
	}

} // SongSelectionActivity

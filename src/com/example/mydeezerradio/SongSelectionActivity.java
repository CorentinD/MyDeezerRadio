package com.example.mydeezerradio;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_selection);
		// Show the Up button in the action bar.
		setupActionBar();

		// Log.w("SongSelection / onCreate()", "création");

		songSelection_listView_songList = (ListView) findViewById(R.id.songSelection_listView_songList);

		// Log.w("SongSelection / onCreate()", "init list");

		songSelection_adapter_songListAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);

		// Log.w("SongSelection / onCreate()", "init Adapter on :"
		// + R.id.songSelection_listView_songList);

		Intent parent_intent = getIntent();

		// Log.w("SongSelection / onCreate()", "init INTENT");

		String temp_searched_song = parent_intent
				.getStringExtra(SongInputActivity.EXTRA_SONGINPUT_SEARCH);

		// Log.w("SongSelection / onCreate()", "init string song : "
		// + temp_searched_song);

		songSelection_adapter_songListAdapter.add(temp_searched_song);

		// Log.w("SongSelection / onCreate()", "add song to list");

		songSelection_listView_songList
				.setAdapter(songSelection_adapter_songListAdapter);

		// Log.w("SongSelection / onCreate()", "set Adapters");

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

	}

	public void songSelection_onClick_return(View view) {
		finish();
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

}

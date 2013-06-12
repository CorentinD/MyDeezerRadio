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
	// /** DeezerConnect object used for auhtentification or request. */
	// private DeezerConnect deezerConnect = new DeezerConnectImpl(
	// MainActivity.APP_ID);
	// /** DeezerRequestListener object used to handle requests. */
	// RequestListener requestHandler = new SongSelectionRequestHandler();
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

		// Log.w("SongSelection / onCreate()", "création");

		songSelection_listView_songList = (ListView) findViewById(R.id.songSelection_listView_songList);
		SongSelection_list_searchResults = new ArrayList<String>();

		// Log.w("SongSelection / onCreate()", "init list");

		songSelection_adapter_songListAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				SongSelection_list_searchResults);

		songSelection_listView_songList
				.setAdapter(songSelection_adapter_songListAdapter);

		// Log.w("SongSelection / onCreate()", "init Adapter on :"
		// + R.id.songSelection_listView_songList);

		Intent parent_intent = getIntent();

		// Log.w("SongSelection / onCreate()", "init INTENT");

		String temp_searched_song = parent_intent
				.getStringExtra(SongInputActivity.EXTRA_SONGINPUT_SEARCH);
		
		Log.i("SongSelection / onCreate", "autre :"+SongInputActivity.song_search_data);
		Log.i("SongSelection / onCreate", "temp :"+temp_searched_song);
		

		SongSelection_list_searchResults = parseResult(temp_searched_song);

		// Bundle bundle = new Bundle();
		// bundle.putString("q", temp_searched_song);
		// bundle.putString("nb_items", "20");
		// DeezerRequest request_songs = new DeezerRequest("search/", bundle);
		// deezerConnect.requestAsync(request_songs, requestHandler);

		Iterator<String> it_list_searchedSongs = SongSelection_list_searchResults
				.iterator();

		while (it_list_searchedSongs.hasNext()) {
			songSelection_adapter_songListAdapter.add(it_list_searchedSongs
					.next());
		}

		// songSelection_adapter_songListAdapter.notifyDataSetChanged();

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

	// private class SongSelectionRequestHandler implements RequestListener {
	// public void onComplete(String response, Object requestId) {
	// // Warning code is not executed in UI Thread
	//
	// Log.w("SongSelection / requestHandler", "list of song : "
	// + response);
	//
	// Log.w("SongSelection / onComplete", "before parsing");
	// SongSelection_list_searchResults = parseResult(response);
	//
	// Log.w("SongSelection / onComplete", "after parsing");
	//
	// Log.w("SongSelection / onCreate", "taille listRes :"
	// + SongSelection_list_searchResults.size());
	//
	// }
	//
	// public void onIOException(IOException e, Object requestId) {
	// Log.w("Main / requestHandler", "IOException");
	// // TODO. Implement some code to handle error. Warning code is not
	// // executed in UI Thread
	// }
	//
	// public void onMalformedURLException(MalformedURLException e,
	// Object requestId) {
	// Log.w("Main / requestHandler", "onMalformedURLException");
	// // TODO. Implement some code to handle error. Warning code is not
	// // executed in UI Thread
	// }
	//
	// @Override
	// public void onDeezerError(DeezerError arg0, Object arg1) {
	// Log.w("Main / requestHandler", "onDeezerError : " + arg0.toString());
	//
	// }
	//
	// @Override
	// public void onOAuthException(OAuthException arg0, Object arg1) {
	// Log.w("Main / requestHandler", "onOAuthException" + arg0 + " / "
	// + arg1);
	//
	// }
	// }// class

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

			int index_name = current.indexOf("title\":");
			int index_userName = index_name + 8;

			int index_link = current.indexOf("\",\"link");

			current = current.substring(index_userName, index_link);

			res.add(current);

		}

		Log.i("SongSelection / parseResults", "res :" + res);
		return res;
	}

	// public void handleError(Object e) {
	// Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG)
	// .show();
	// }

}

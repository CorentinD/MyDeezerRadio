package com.example.mydeezerradio.activities;

import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.deezer.sdk.AsyncDeezerTask;
import com.deezer.sdk.DeezerConnect;
import com.deezer.sdk.DeezerConnectImpl;
import com.deezer.sdk.DeezerError;
import com.deezer.sdk.DeezerRequest;
import com.deezer.sdk.DialogError;
import com.deezer.sdk.DialogListener;
import com.deezer.sdk.OAuthException;
import com.deezer.sdk.RequestListener;
import com.deezer.sdk.SessionStore;
import com.example.mydeezerradio.R;
import com.example.mydeezerradio.deezerclasses.DeezerDataReader;
import com.example.mydeezerradio.deezerclasses.User;

public class MainActivity extends Activity {
	public final static String TAG = "com.example.mydeezerradio.Mainactivity";
	final String defValue = "erreur recuperation données";
	public static int main_int_userId = 0;
	public final static String name_sharedPref = "com.example.mydeezerradio";
	SharedPreferences sharedPref;
	SharedPreferences.Editor sharedPref_editor;
	public static String access_token = null;
	public final static String sharedPref_string_userName = "string_userName";
	public static User main_user_currentUser = new User();
	private boolean main_boolean_isConnected;

	/** Your app Deezer appId. */
	public final static String APP_ID = "119355";
	/** Permissions requested on Deezer accounts. */
	private final static String[] PERMISSIONS = new String[] { "basic_access",
			"delete_library", "manage_library" };
	/** DeezerConnect object used for authentification or request. */
	private DeezerConnect deezerConnect = new DeezerConnectImpl(APP_ID);

	/** DeezerRequestListener object used to handle requests. */
	private RequestListener main_requestListener_userRequestListenerHandler = new Main_UserRequestHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_v2);

		sharedPref = getSharedPreferences(name_sharedPref, Context.MODE_PRIVATE);
		sharedPref_editor = sharedPref.edit();

		SessionStore sessionStore = new SessionStore();
		main_boolean_isConnected = sessionStore.restore(deezerConnect, this);

		if (main_boolean_isConnected) {
			Toast.makeText(this, "Already logged in !", Toast.LENGTH_SHORT)
					.show();
		}
	}

	protected void onResume() {
		super.onResume();

		if (main_boolean_isConnected) {
			// if the user is connected, get the user info
			((TextView) findViewById(R.id.mainV2_textView_nameUser))
					.setText("Connected, welcome "
							+ sharedPref.getString(sharedPref_string_userName,
									"NO ONE"));
		} else {
			((TextView) findViewById(R.id.mainV2_textView_nameUser))
					.setText("Not connected, please log in");
		}
		Log.i("MainActivity / onResume", "user data : " + main_user_currentUser);
	}

	public void mainV2_onClick_connect(View view) {
		// Attempt to connect
		deezerConnect.authorize(MainActivity.this, PERMISSIONS,
				new Main_LoginDialogHandler());
	}

	public void mainV2_onClick_disconnect(View view) {

		new SessionStore().clear(this);

		deezerConnect.logout(MainActivity.this);
		main_boolean_isConnected = false;
		access_token = null;
		main_user_currentUser = new User();

		Toast.makeText(this, "disconnected", Toast.LENGTH_SHORT).show();
		((TextView) findViewById(R.id.mainV2_textView_nameUser))
				.setText("Déconnecté");

		sharedPref_editor.putString(sharedPref_string_userName, "NO ONE");
		sharedPref_editor.commit();

	}

	/** Handle DeezerConnect callbacks. */
	private class Main_LoginDialogHandler implements DialogListener {
		@Override
		public void onComplete(final Bundle values) {

			MainActivity.access_token = values.getString("access_token");

			SessionStore sessionStore = new SessionStore();
			sessionStore.save(deezerConnect, MainActivity.this);

			Toast.makeText(getApplicationContext(), "Connected",
					Toast.LENGTH_SHORT).show();
			Log.w("MainActivity / onComplete", access_token);

			main_searchUser();
		}// met

		@Override
		public void onDeezerError(final DeezerError deezerError) {
			Log.w("Main / onDeezerError", deezerError);

		}// met

		@Override
		public void onError(final DialogError dialogError) {
			Log.w("Main / onError", dialogError);
		}// met

		@Override
		public void onCancel() {
			Log.w("Main / onCancel", "ERROR");
		}// met

		@Override
		public void onOAuthException(OAuthException oAuthException) {
			Log.w("Main / onOAuthException", oAuthException);
		}// met
	}// inner class

	public void main_searchUser() {
		DeezerRequest request = new DeezerRequest("user/me");
		AsyncDeezerTask searchAsyncUser = new AsyncDeezerTask(deezerConnect,
				main_requestListener_userRequestListenerHandler);
		searchAsyncUser.execute(request);

	}

	public void main_searchUserFinish() {
		sharedPref_editor.putString(sharedPref_string_userName,
				main_user_currentUser.getFirstname());
		sharedPref_editor.commit();

		// go to the inputSong activity
		Intent intent = new Intent(getApplicationContext(),
				SongInputActivity.class);
		startActivity(intent);
	}

	private class Main_UserRequestHandler implements RequestListener {
		@Override
		public void onComplete(String response, Object arg1) {
			try {
				main_user_currentUser = new DeezerDataReader<User>(User.class)
						.read(response);
				main_int_userId = main_user_currentUser.getId();
				main_searchUserFinish();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onDeezerError(DeezerError e, Object arg1) {
			Log.w("userRequesthandler / onDeezerError", e);
		}

		@Override
		public void onIOException(IOException e, Object arg1) {
			Log.w("userRequesthandler / onIOException", e);
		}

		@Override
		public void onMalformedURLException(MalformedURLException e, Object arg1) {
			Log.w("userRequesthandler / onMalformedURLException", e);
		}

		@Override
		public void onOAuthException(OAuthException e, Object arg1) {
			Log.w("userRequesthandler / onOAuthException", e);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

} // MainActivity

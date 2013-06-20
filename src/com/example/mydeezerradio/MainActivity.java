package com.example.mydeezerradio;

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

public class MainActivity extends Activity {
	public final static String TAG = "com.example.mydeezerradio.Mainactivity";
	final String defValue = "erreur recuperation données";
	public static int userId = 0;
	public final static String name_sharedPref = "com.example.mydeezerradio";
	SharedPreferences sharedPref;
	SharedPreferences.Editor sharedPref_editor;
	public static String access_token = null;
	public final static String sharedPref_string_userName = "string_userName";
	public static User user_data = new User();
	boolean isConnected;

	/** Your app Deezer appId. */
	public final static String APP_ID = "119355";
	/** Permissions requested on Deezer accounts. */
	private final static String[] PERMISSIONS = new String[] { "basic_access",
			"delete_library", "manage_library" };
	/** DeezerConnect object used for authentification or request. */
	private DeezerConnect deezerConnect = new DeezerConnectImpl(APP_ID);

	/** DeezerRequestListener object used to handle requests. */
	private RequestListener userRequestListenerHandler = new UserRequestHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_v2);

		sharedPref = getSharedPreferences(name_sharedPref, Context.MODE_PRIVATE);
		sharedPref_editor = sharedPref.edit();

		SessionStore sessionStore = new SessionStore();
		isConnected = sessionStore.restore(deezerConnect, this);

		if (isConnected) {
			Toast.makeText(this, "Already logged in !", Toast.LENGTH_SHORT)
					.show();
		}
	}

	protected void onResume() {
		super.onResume();

		if (isConnected) {
			// if the user is connected, get the user info
			((TextView) findViewById(R.id.mainV2_textView_nameUser))
					.setText("Connected, welcome "
							+ sharedPref.getString(sharedPref_string_userName,
									"NO ONE"));
		} else {
			((TextView) findViewById(R.id.mainV2_textView_nameUser))
					.setText("Not connected, please log in");
		}
		Log.i("MainActivity / onResume", "user data : " + user_data);
	}

	public void mainV2_onClick_connect(View view) {
		// Attempt to connect
		deezerConnect.authorize(MainActivity.this, PERMISSIONS,
				new LoginDialogHandler());
	}

	public void mainV2_onClick_disconnect(View view) {

		new SessionStore().clear(this);

		deezerConnect.logout(MainActivity.this);
		isConnected = false;
		access_token = null;
		user_data = new User();

		Toast.makeText(this, "disconnected", Toast.LENGTH_SHORT).show();
		((TextView) findViewById(R.id.mainV2_textView_nameUser))
				.setText("Déconnecté");

		sharedPref_editor.putString(sharedPref_string_userName, "NO ONE");
		sharedPref_editor.commit();

	}

	/** Handle DeezerConnect callbacks. */
	private class LoginDialogHandler implements DialogListener {
		@Override
		public void onComplete(final Bundle values) {

			MainActivity.access_token = values.getString("access_token");

			SessionStore sessionStore = new SessionStore();
			sessionStore.save(deezerConnect, MainActivity.this);

			Toast.makeText(getApplicationContext(), "Connected",
					Toast.LENGTH_SHORT).show();
			Log.w("MainActivity / onComplete", access_token);

			searchUser();
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

	public void searchUser() {
		DeezerRequest request = new DeezerRequest("user/me");
		AsyncDeezerTask searchAsyncUser = new AsyncDeezerTask(deezerConnect,
				userRequestListenerHandler);
		searchAsyncUser.execute(request);

	}

	public void searchUserFinish(User user) {
		sharedPref_editor.putString(sharedPref_string_userName,
				user.getFirstname());
		sharedPref_editor.commit();
		user_data = user;
		Log.i("MainActivity / searchUserFinish", "user request : " + user);
		// go to the inputSong activity
		Intent intent = new Intent(getApplicationContext(),
				SongInputActivity.class);
		startActivity(intent);
	}

	private class UserRequestHandler implements RequestListener {
		@Override
		public void onComplete(String response, Object arg1) {
			try {
				userId = Integer.parseInt(getId(response));
				User user = new DeezerDataReader<User>(User.class)
						.read(response);
				Log.i("MainActivity / onComplete", "user request : " + user);
				Log.i("MainActivity / onComplete", "user ID : " + userId);
				searchUserFinish(user);
			} catch (IllegalStateException e) {
				handleError(e);
				e.printStackTrace();
			}
		}

		@Override
		public void onDeezerError(DeezerError e, Object arg1) {
			Log.w("userRequesthandler / onDeezerError", e);
			handleError(e);
		}

		@Override
		public void onIOException(IOException e, Object arg1) {
			Log.w("userRequesthandler / onIOException", e);
			handleError(e);
		}

		@Override
		public void onMalformedURLException(MalformedURLException e, Object arg1) {
			Log.w("userRequesthandler / onMalformedURLException", e);
			handleError(e);

		}

		@Override
		public void onOAuthException(OAuthException e, Object arg1) {
			Log.w("userRequesthandler / onOAuthException", e);
			handleError(e);
		}

	}

	public void handleError(Object e) {
		Log.e("MainActivity / Error :", e.toString());
	}

	public String getId(String data) {
		int index_id = data.indexOf("id\":") + 5;
		int index_name = data.indexOf(",\"name") - 1;
		return data.substring(index_id, index_name);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

} // MainActivity

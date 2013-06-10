package com.example.mydeezerradio;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class MainActivity extends Activity {

	public final static String TAG = "com.example.mydeezerradio.Mainactivity";
	final String defValue = "erreur recuperation données";
	String userId = "8399072";
	public final static String name_sharedPref = "com.example.mydeezerradio";
	SharedPreferences sharedPref;
	SharedPreferences.Editor sharedPref_editor;
	final String sharedPref_boolean_isConnected = "boolean_isConnected";
	public static String access_token = "";

	/**
	 * v1 : with shared pref
	 * 
	 * public final static String name_sharedPref = "com.example.mydeezerradio";
	 * SharedPreferences sharedPref; SharedPreferences.Editor sharedPref_editor;
	 * final String sharedPref_string_email = "string_email"; final String
	 * sharedPref_string_password = "string_password";
	 */

	/** Your app Deezer appId. */
	public final static String APP_ID = "119355";
	/** Permissions requested on Deezer accounts. */
	private final static String[] PERMISSIONS = new String[] {
			Manifest.permission.ACCESS_NETWORK_STATE,
			Manifest.permission.INTERNET, Manifest.permission.WAKE_LOCK,
			Manifest.permission.READ_PHONE_STATE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE

	};
	/** DeezerConnect object used for auhtentification or request. */
	private DeezerConnect deezerConnect = new DeezerConnectImpl(APP_ID);

	/** DeezerRequestListener object used to handle requests. */
	RequestListener requestHandler = new MyDeezerRequestHandler();
	/** DeezerTaskRequestListener object used to handle requests. */
	RequestListener deezerTaskHandler = new RequestForNameTaskHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_v2);
		sharedPref = getSharedPreferences(name_sharedPref, Context.MODE_PRIVATE);
		sharedPref_editor = sharedPref.edit();
		if (!sharedPref.contains(sharedPref_boolean_isConnected)) {
			sharedPref_editor.putBoolean(sharedPref_boolean_isConnected, false);
		}

		/**
		 * v1 : with shared pref
		 * 
		 * // init sharedPref sharedPref = getSharedPreferences(name_sharedPref,
		 * Context.MODE_PRIVATE); sharedPref_editor = sharedPref.edit();
		 * 
		 * // if sharedPref have been stocked, retrieve them if
		 * (sharedPref.contains(sharedPref_string_email) &&
		 * sharedPref.contains(sharedPref_string_password)) { ((TextView)
		 * findViewById(R.id.main_editText_email))
		 * .setText(sharedPref.getString(sharedPref_string_email, defValue));
		 * ((TextView) findViewById(R.id.main_editText_password))
		 * .setText(sharedPref.getString(sharedPref_string_password, defValue));
		 * }
		 */

	}

	protected void onResume() {
		super.onResume();

		Log.w("TAG / connected",
				""
						+ sharedPref.getBoolean(sharedPref_boolean_isConnected,
								false));

		if (sharedPref.getBoolean(sharedPref_boolean_isConnected, false)) {

			DeezerRequest request_name = new DeezerRequest("/user/" + userId
					+ "/name/");

			deezerConnect.requestAsync(request_name, requestHandler);

			// AsyncDeezerTask asyncDeezerTask = new AsyncDeezerTask(
			// deezerConnect, request_name, requestHandler, "name_user");

			Log.w("Main / onResume", "already connected");
			((TextView) findViewById(R.id.mainV2_textView_nameUser))
					.setText("Connected");
		} else {
			((TextView) findViewById(R.id.mainV2_textView_nameUser))
					.setText("Not connected, please log in");
		}
	}

	public void main_onClick_connect(View view) {
		// TODO à refaire avec une vraie version

		/**
		 * v1 : with shared pref
		 * 
		 * // Retrieve data TextView main_textView_email = (TextView)
		 * findViewById(R.id.main_editText_email); TextView
		 * main_textView_password = (TextView)
		 * findViewById(R.id.main_editText_password);
		 * 
		 * String main_string_email = String
		 * .valueOf(main_textView_email.getText()); String main_string_password
		 * = String.valueOf(main_textView_password .getText());
		 * 
		 * // if the user checked "remember me", remember him (stock data in //
		 * sharedPref) if (((CheckBox)
		 * findViewById(R.id.main_checkBox_rememberMe)) .isChecked()) {
		 * sharedPref_editor.putString(sharedPref_string_email,
		 * main_string_email);
		 * sharedPref_editor.putString(sharedPref_string_password,
		 * main_string_password); sharedPref_editor.commit(); }
		 */

		Log.w("Main / onConnect", "isConnected : " + isConnected());

		// Attempt to connect
		deezerConnect.authorize(MainActivity.this, PERMISSIONS,
				new LoginDialogHandler());
		Log.w("Main / onConnect", "after authorizing");

	}

	public void mainV2_onClick_connect(View view) {
		// Attempt to connect

		deezerConnect.authorize(MainActivity.this, PERMISSIONS,
				new LoginDialogHandler());

	}

	public void mainV2_onClick_disconnect(View view) {
		sharedPref_editor.putBoolean(sharedPref_boolean_isConnected, false);

		deezerConnect.logout(MainActivity.this);

		Toast.makeText(getApplicationContext(), "disconnected",
				Toast.LENGTH_SHORT).show();
		((TextView) findViewById(R.id.mainV2_textView_nameUser))
				.setText("Déconnecté");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean isLogged() {
		return deezerConnect.isSessionValid();
	}

	public boolean isConnected() {
		ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net = conn.getActiveNetworkInfo();
		return (net.isConnected() && (net != null));
	}

	/** Handle DeezerConnect callbacks. */
	private class LoginDialogHandler implements DialogListener {
		@Override
		public void onComplete(final Bundle values) {
			Log.w("Main / onComplete", "OK");

			MainActivity.access_token = values.getString("access_token");

			sharedPref_editor.putBoolean(sharedPref_boolean_isConnected, true);
			sharedPref_editor.commit();

			Toast.makeText(getApplicationContext(), "Connected",
					Toast.LENGTH_SHORT).show();

			Log.w("oncomplete", access_token);

			// go to the inputSong activity
			Intent intent = new Intent(getApplicationContext(),
					SongInputActivity.class);
			startActivity(intent);

		}// met

		@Override
		public void onDeezerError(final DeezerError deezerError) {
			// Toast toast_error = new Toast(getApplicationContext());
			// toast_error.setText("Connection error");
			// toast_error.show();

			Log.w("Main / onDeezerError", "ERROR");
		}// met

		@Override
		public void onError(final DialogError dialogError) {
			Log.w("Main / onError", "ERROR");
			// Toast toast_error = new Toast(getApplicationContext());
			// toast_error.setText("Connection error");
			// toast_error.show();
		}// met

		@Override
		public void onCancel() {
			Log.w("Main / onCancel", "ERROR");
		}// met

		@Override
		public void onOAuthException(OAuthException oAuthException) {
			Log.w("Main / onOAuthException", "ERROR");

		}// met
	}// inner class

	private class MyDeezerRequestHandler implements RequestListener {
		public void onComplete(String response, Object requestId) {
			// Warning code is not executed in UI Thread
			if ("user_name".equals(requestId)) {
				Toast.makeText(getApplicationContext(), "user !",
						Toast.LENGTH_SHORT).show();

			}
		}

		public void onIOException(IOException e, Object requestId) {
			Log.w("Main / requestHandler", "IOException");
			// TODO. Implement some code to handle error. Warning code is not
			// executed in UI Thread
		}

		public void onMalformedURLException(MalformedURLException e,
				Object requestId) {
			Log.w("Main / requestHandler", "onMalformedURLException");
			// TODO. Implement some code to handle error. Warning code is not
			// executed in UI Thread
		}

		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			Log.w("Main / requestHandler", "onDeezerError : " + arg0.toString());

		}

		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			Log.w("Main / requestHandler", "onOAuthException");

		}
	}// class

	class RequestForNameTaskHandler implements RequestListener {
		public void onComplete(String response, Object requestId) {
			// code is executed in UI Thread
			Toast.makeText(getApplicationContext(),
					"onComplete deezer task:" + response, Toast.LENGTH_SHORT)
					.show();
			if ("list-playlists".equals(requestId)) {
				// TODO. Implement some code to parse the answer as detailled in
				// http://developers.deezer.com/api/user/playlists
			}// if
		}

		public void onIOException(IOException e, Object requestId) {
			// TODO. Implement some code to handle error. Code is executed in UI
			// Thread
		}

		public void onMalformedURLException(MalformedURLException e,
				Object requestId) {
			// TODO. Implement some code to handle error. Code is executed in UI
			// Thread
		}

		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			// TODO Auto-generated method stub
		}
	}// inner class

	public void btDeezerASyncTaskRequest(View view) {
		// sending requests

		String userId = "2529";
		DeezerRequest request = new DeezerRequest("/user/" + userId
				+ "/playlists");
		AsyncDeezerTask asyncDeezerTask = new AsyncDeezerTask(deezerConnect,
				deezerTaskHandler);
		// optinionally, use an executor
		BlockingQueue<Runnable> worksQueue = new ArrayBlockingQueue<Runnable>(2);
		Executor executor = new ThreadPoolExecutor(3, 3, 10, TimeUnit.SECONDS,
				worksQueue);
		// execute the AsyncTask with the executor
		asyncDeezerTask.executeOnExecutor(executor, request);
	}

}

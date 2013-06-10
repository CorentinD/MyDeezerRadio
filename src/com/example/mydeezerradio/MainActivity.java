package com.example.mydeezerradio;

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
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.deezer.sdk.DeezerConnect;
import com.deezer.sdk.DeezerConnectImpl;
import com.deezer.sdk.DeezerError;
import com.deezer.sdk.DialogError;
import com.deezer.sdk.DialogListener;
import com.deezer.sdk.OAuthException;

public class MainActivity extends Activity {

	public final static String TAG = "com.example.mydeezerradio.Mainactivity";
	final String defValue = "erreur recuperation données";
	public final static String name_sharedPref = "com.example.mydeezerradio";
	SharedPreferences sharedPref;
	SharedPreferences.Editor sharedPref_editor;
	final String sharedPref_string_email = "string_email";
	final String sharedPref_string_password = "string_password";

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

	// /** DeezerRequestListener object used to handle requests. */
	// RequestListener handler = new MyDeezerRequestHandler();
	// /** DeezerTaskRequestListener object used to handle requests. */
	// RequestListener deezerTaskHandler = new MyDeezerTaskHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_v2);

		// init sharedPref
		sharedPref = getSharedPreferences(name_sharedPref, Context.MODE_PRIVATE);
		sharedPref_editor = sharedPref.edit();

		// if sharedPref have been stocked, retrieve them
		if (sharedPref.contains(sharedPref_string_email)
				&& sharedPref.contains(sharedPref_string_password)) {
			((TextView) findViewById(R.id.main_editText_email))
					.setText(sharedPref.getString(sharedPref_string_email,
							defValue));
			((TextView) findViewById(R.id.main_editText_password))
					.setText(sharedPref.getString(sharedPref_string_password,
							defValue));
		}

	}

	public void main_onClick_connect(View view) {
		// TODO à refaire avec une vraie version

		// Retrieve data
		TextView main_textView_email = (TextView) findViewById(R.id.main_editText_email);
		TextView main_textView_password = (TextView) findViewById(R.id.main_editText_password);

		String main_string_email = String
				.valueOf(main_textView_email.getText());
		String main_string_password = String.valueOf(main_textView_password
				.getText());

		// if the user checked "remember me", remember him (stock data in
		// sharedPref)
		if (((CheckBox) findViewById(R.id.main_checkBox_rememberMe))
				.isChecked()) {
			sharedPref_editor.putString(sharedPref_string_email,
					main_string_email);
			sharedPref_editor.putString(sharedPref_string_password,
					main_string_password);
			sharedPref_editor.commit();
		}

		Log.w("Main / onConnect", "isConnected : " + isConnected());

		// Attempt to connect
		deezerConnect.authorize(MainActivity.this, PERMISSIONS,
				new MyDialogHandler());
		Log.w("Main / onConnect", "after authorizing");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean isConnected() {
		ConnectivityManager conn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net = conn.getActiveNetworkInfo();
		return (net.isConnected() && (net != null));
	}

	/** Handle DeezerConnect callbacks. */
	private class MyDialogHandler implements DialogListener {
		@Override
		public void onComplete(final Bundle values) {
			Log.w("Main / onComplete", "OK");

			Toast toast_complete = new Toast(getApplicationContext());
			toast_complete.setText("Connected");
			toast_complete.show();
			
			// go to the inputSong activity
			Intent intent = new Intent(getApplicationContext(),
					SongInputActivity.class);
			startActivity(intent);

		}// met

		@Override
		public void onDeezerError(final DeezerError deezerError) {
			Toast toast_error = new Toast(getApplicationContext());
			toast_error.setText("Connection error");
			toast_error.show();
			
			Log.w("Main / onDeezerError", "ERROR");
		}// met

		@Override
		public void onError(final DialogError dialogError) {
			Log.w("Main / onError", "ERROR");
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

}

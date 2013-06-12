package com.example.mydeezerradio;

import java.io.IOException;
import java.net.MalformedURLException;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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

public class MainActivity extends Activity {

	public final static String TAG = "com.example.mydeezerradio.Mainactivity";
	final String defValue = "erreur recuperation données";
	String userId = "8399072";
	public final static String name_sharedPref = "com.example.mydeezerradio";
	SharedPreferences sharedPref;
	SharedPreferences.Editor sharedPref_editor;
	public final static String sharedPref_boolean_isConnected = "boolean_isConnected";
	public static String access_token = "";
	public final static String sharedPref_string_userName = "string_userName";
	public static User user_data;

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
	private RequestListener requestHandler = new MyDeezerRequestHandler();

	private RequestListener userRequestListenerHandler = new UserRequestHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_v2);

		sharedPref = getSharedPreferences(name_sharedPref, Context.MODE_PRIVATE);
		sharedPref_editor = sharedPref.edit();
		if (!sharedPref.contains(sharedPref_boolean_isConnected)) {
			sharedPref_editor.putBoolean(sharedPref_boolean_isConnected, false);
		}

		user_data = new User();
	}

	protected void onResume() {
		super.onResume();

		if (sharedPref.getBoolean(sharedPref_boolean_isConnected, false)) {

			DeezerRequest request_name = new DeezerRequest("/user/me");
			deezerConnect.setAccessToken(getApplicationContext(), access_token);
			deezerConnect.requestAsync(request_name, requestHandler);

			((TextView) findViewById(R.id.mainV2_textView_nameUser))
					.setText("Connected, welcome "
							+ sharedPref.getString(sharedPref_string_userName,
									"User"));
		} else {
			((TextView) findViewById(R.id.mainV2_textView_nameUser))
					.setText("Not connected, please log in");
		}

		searchUser();
	}

	public void mainV2_onClick_connect(View view) {
		// Attempt to connect
		deezerConnect.authorize(MainActivity.this, PERMISSIONS,
				new LoginDialogHandler());

	}

	public void mainV2_onClick_disconnect(View view) {

		sharedPref_editor.putBoolean(sharedPref_boolean_isConnected, false);
		sharedPref_editor.putString(sharedPref_string_userName, "NO ONE");
		sharedPref_editor.commit();

		deezerConnect.logout(MainActivity.this);

		Toast.makeText(getApplicationContext(), "disconnected",
				Toast.LENGTH_SHORT).show();
		((TextView) findViewById(R.id.mainV2_textView_nameUser))
				.setText("Déconnecté");
	}

	/** Handle DeezerConnect callbacks. */
	private class LoginDialogHandler implements DialogListener {
		@Override
		public void onComplete(final Bundle values) {

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

	private class MyDeezerRequestHandler implements RequestListener {
		public void onComplete(String response, Object requestId) {
			// Warning code is not executed in UI Thread
			sharedPref_editor.putString(sharedPref_string_userName,
					getUserName(response));
			sharedPref_editor.commit();

			Log.w("Main / requestHandler", "username : " + response);

		}

		public void onIOException(IOException e, Object requestId) {
			Log.w("Main / requestHandler", "IOException");
		}

		public void onMalformedURLException(MalformedURLException e,
				Object requestId) {
			Log.w("Main / requestHandler", "onMalformedURLException");
		}

		@Override
		public void onDeezerError(DeezerError arg0, Object arg1) {
			Log.w("Main / requestHandler", "onDeezerError : " + arg0.toString());
		}

		@Override
		public void onOAuthException(OAuthException arg0, Object arg1) {
			Log.w("Main / requestHandler", "onOAuthException" + arg0 + " / "
					+ arg1);

		}
	}// class

	public void searchUser() {
		AsyncDeezerTask searchAsyncUser = new AsyncDeezerTaskWithDialog(this,
				deezerConnect, userRequestListenerHandler);
		DeezerRequest request = new DeezerRequest("user/me");
		searchAsyncUser.execute(request);

	}

	public void searchUserFinish(User user) {
		user_data.setFirstname(user.getFirstname());
		user_data.setLastname(user.getLastname());
		user_data.setBirthday(user.getBirthday());
		user_data.setPicture(user.getPicture());
	}

	private class UserRequestHandler implements RequestListener {
		@Override
		public void onComplete(String response, Object arg1) {
			try {
				User user = new DeezerDataReader<User>(User.class)
						.read(response);

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
		Log.e("Error :", e.toString());
	}

	public class AsyncDeezerTaskWithDialog extends AsyncDeezerTask {
		/** Progress dialog to show user that the request is beeing processed. */
		private ProgressDialog progressDialog;

		public AsyncDeezerTaskWithDialog(Context context,
				DeezerConnect deezerConnect, RequestListener listener) {
			super(deezerConnect, listener);
			progressDialog = new ProgressDialog(context);
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelHandler());
		}// met

		@Override
		protected void onPreExecute() {
			progressDialog.setMessage("Contacting Deezer...");
			progressDialog.show();
			super.onPreExecute();
		}// met

		@Override
		public void onPostExecute(String s) {
			try {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}// if
			} catch (IllegalArgumentException e) {
				// can happen sometimes, and nothing to get against it
			}// catch
			super.onPostExecute(s);
		}// met

		@Override
		protected void onCancelled() {
			try {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}// if
			} catch (IllegalArgumentException e) {
				// can happen sometimes, and nothing to get against it
			}// catch
			super.onCancelled();
		}// met

		private class OnCancelHandler implements OnCancelListener {
			@Override
			public void onCancel(DialogInterface dialog) {
				cancel(true);
			}// met
		}// inner class

	}// AsyncDeezerTaskWithDialog

	public String getUserName(String informations) {
		String res = new String();

		int index_name = informations.indexOf("firstname\":");
		int index_userName = index_name + 12;

		int index_link = informations.indexOf("\",\"birthday");

		res = informations.substring(index_userName, index_link);

		Log.i("MainActivity / getUserName", res);

		return res;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

} // MainActivity

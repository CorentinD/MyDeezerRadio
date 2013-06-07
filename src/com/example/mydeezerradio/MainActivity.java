package com.example.mydeezerradio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.deezer.sdk.DeezerConnect;
import com.deezer.sdk.DeezerConnectImpl;
import com.deezer.sdk.DeezerError;
import com.deezer.sdk.DialogError;
import com.deezer.sdk.DialogListener;
import com.deezer.sdk.OAuthException;

public class MainActivity extends Activity {

	public final static String TAG = "com.example.mydeezerradio.Mainactivity";
	
	/** Your app Deezer appId. */
	public final static String APP_ID = "119355";
	/** Permissions requested on Deezer accounts. */
	private final static String[] PERMISSIONS = new String[] {};
	/** DeezerConnect object used for auhtentification or request. */
	private DeezerConnect deezerConnect = new DeezerConnectImpl( APP_ID );

	// /** DeezerRequestListener object used to handle requests. */
	// RequestListener handler = new MyDeezerRequestHandler();
	// /** DeezerTaskRequestListener object used to handle requests. */
	// RequestListener deezerTaskHandler = new MyDeezerTaskHandler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
		
	public void main_onClick_connect(View view) {
		//TODO à refaire avec une vraie version
		Intent intent = new Intent(this, SongInputActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/** Handle DeezerConnect callbacks. */
	private class MyDialogHandler implements DialogListener {
		@Override
			public void onComplete(final Bundle values) {
	 
		}//met
		 
		@Override
			public void onDeezerError(final DeezerError deezerError) {
	 
		}//met
		 
		@Override
			public void onError(final DialogError dialogError) {
	 
		}//met
		 
		@Override
			public void onCancel() {
	 
		}//met
		 
		@Override
			public void onOAuthException(OAuthException oAuthException) {
	 
		}//met
	}//inner class
	
	



}

package com.whaleoftime.rasduino;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

	private static final String CLIENT_ID = "972c5e47fc604d619e4b5b0ba508ead9";
	private static final String REDIRECT_URI = "http://justlooking.mybluemix.net/callback";
	private static final int REQUEST_CODE = 1337;
	private SpotifyAppRemote mSpotifyAppRemote;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onStart() {
		super.onStart();

		DialogFragment newFragment = new DeviceSelectorDialogFragment();
		newFragment.show(getSupportFragmentManager(), "device");
	}

	public void wasSelected() {
		AuthenticationRequest.Builder builder =
				new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

		builder.setScopes(new String[]{"streaming playlist-modify-private user-top-read"});
		AuthenticationRequest request = builder.build();

		AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		// Check if result comes from the correct activity
		if (requestCode == REQUEST_CODE) {
			final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

			switch (response.getType()) {
				// Response was successful and contains auth token
				case TOKEN:
					// Handle successful response

					System.out.println("TOK" + response.getAccessToken());
					ConnectionParams connectionParams =
							new ConnectionParams.Builder(CLIENT_ID)
									.setRedirectUri(REDIRECT_URI)
									.showAuthView(true)
									.build();

					SpotifyAppRemote.connect(this, connectionParams,
							new Connector.ConnectionListener() {

								@Override
								public void onConnected(SpotifyAppRemote spotifyAppRemote) {
									findViewById(R.id.progressBar).setVisibility(View.GONE);
									findViewById(R.id.buttons).setVisibility(View.VISIBLE);
									mSpotifyAppRemote = spotifyAppRemote;

									// Now you can start interacting with App Remote
									connected(response.getAccessToken());
								}

								@Override
								public void onFailure(Throwable throwable) {
									Log.e("MainActivity", throwable.getMessage(), throwable);

									// Something went wrong when attempting to connect! Handle errors here
								}
							});
					break;

				// Auth flow returned an error
				case ERROR:
					// Handle error response
					break;

				// Most likely auth flow was cancelled
				default:
					// Handle other cases
			}
		}
	}

	private void connected(final String token) {
		// Then we will write some more code here.
		findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							HttpURLConnection urlConnection = (HttpURLConnection) new URL("https://justlooking.mybluemix.net/next?tok=" + token).openConnection();
							urlConnection.setRequestMethod("GET");
							urlConnection.connect();
							BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
							mSpotifyAppRemote.getPlayerApi().play(bufferedReader.readLine());
						} catch (Exception e) {
							System.out.println("TOK" + e.getMessage());
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
		findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {//all
							HttpURLConnection urlConnection = (HttpURLConnection) new URL("https://justlooking.mybluemix.net/next?tok=" + token).openConnection();
							urlConnection.setRequestMethod("GET");
							urlConnection.connect();
							BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
							mSpotifyAppRemote.getPlayerApi().play(bufferedReader.readLine());
						} catch (Exception e) {
							System.out.println("TOK" + e.getMessage());
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Aaand we will finish off here.
	}
}
